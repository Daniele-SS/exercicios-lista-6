package com.naotendo.pokedex.data.repository

import com.naotendo.pokedex.data.mapper.buildTypesById
import com.naotendo.pokedex.data.mapper.extractIdFromUrl
import com.naotendo.pokedex.data.mapper.toDetail
import com.naotendo.pokedex.data.remote.PokeApiService
import com.naotendo.pokedex.data.remote.dto.TypeResponse
import com.naotendo.pokedex.model.PokemonDetail
import com.naotendo.pokedex.model.PokemonSummary
import com.naotendo.pokedex.model.PokemonType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Fonte única de dados do app, com cache em memória das respostas da PokéAPI. */
class PokemonRepository(private val api: PokeApiService) {

    private val listMutex = Mutex()
    private var cachedList: List<PokemonSummary>? = null

    private val detailMutex = Mutex()
    private val detailCache = mutableMapOf<Int, PokemonDetail>()

    suspend fun getPokemonList(): List<PokemonSummary> = listMutex.withLock {
        cachedList ?: fetchPokemonList().also { cachedList = it }
    }

    suspend fun getPokemonDetail(id: Int): PokemonDetail {
        detailMutex.withLock { detailCache[id] }?.let { return it }

        val detail = coroutineScope {
            val pokemonCall = async { api.getPokemon(id) }
            val speciesCall = async { api.getPokemonSpecies(id) }
            val pokemon = pokemonCall.await()
            val species = speciesCall.await()
            val chain = species.evolutionChain?.url?.let { api.getEvolutionChain(it) }
            pokemon.toDetail(species, chain)
        }

        detailMutex.withLock { detailCache[id] = detail }
        return detail
    }

    private suspend fun fetchPokemonList(): List<PokemonSummary> = coroutineScope {
        val listCall = async { api.getPokemonList(limit = MAX_POKEMON_ID) }
        val typeCalls = PokemonType.entries.map { type ->
            async { type to fetchTypeOrNull(type) }
        }
        val typesById = buildTypesById(typeCalls.awaitAll())

        listCall.await().results.mapNotNull { resource ->
            val id = extractIdFromUrl(resource.url)
            if (id == null || id > MAX_POKEMON_ID) return@mapNotNull null
            PokemonSummary(id = id, name = resource.name, types = typesById[id].orEmpty())
        }
    }

    /** Falha em um tipo só deixa os cards sem cor; não deve derrubar a lista inteira. */
    private suspend fun fetchTypeOrNull(type: PokemonType): TypeResponse? =
        try {
            api.getType(type.apiName)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }

    companion object {
        /** Último Pokémon da Pokédex nacional; IDs acima disso são formas alternativas. */
        const val MAX_POKEMON_ID = 1025
    }
}
