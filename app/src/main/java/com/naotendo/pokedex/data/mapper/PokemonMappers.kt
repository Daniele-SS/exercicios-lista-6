package com.naotendo.pokedex.data.mapper

import com.naotendo.pokedex.data.remote.dto.ChainLinkDto
import com.naotendo.pokedex.data.remote.dto.EvolutionChainResponse
import com.naotendo.pokedex.data.remote.dto.FlavorTextEntry
import com.naotendo.pokedex.data.remote.dto.PokemonResponse
import com.naotendo.pokedex.data.remote.dto.PokemonSpeciesResponse
import com.naotendo.pokedex.data.remote.dto.TypeResponse
import com.naotendo.pokedex.model.EvolutionSpecies
import com.naotendo.pokedex.model.PokemonDetail
import com.naotendo.pokedex.model.PokemonStat
import com.naotendo.pokedex.model.PokemonType
import com.naotendo.pokedex.model.formatPokemonName
import com.naotendo.pokedex.model.officialArtworkUrl

/** Extrai o ID do final de URLs como "https://pokeapi.co/api/v2/pokemon/25/". */
fun extractIdFromUrl(url: String): Int? = url.trimEnd('/').substringAfterLast('/').toIntOrNull()

/**
 * Monta o mapa ID -> tipos (ordenados por slot) a partir das respostas de /type/{nome}.
 * Assim bastam 18 requisições para colorir todos os cards da lista.
 */
fun buildTypesById(responses: List<Pair<PokemonType, TypeResponse?>>): Map<Int, List<PokemonType>> {
    val slotsById = HashMap<Int, MutableList<Pair<Int, PokemonType>>>()
    for ((type, response) in responses) {
        response?.pokemon?.forEach { entry ->
            val id = extractIdFromUrl(entry.pokemon.url) ?: return@forEach
            slotsById.getOrPut(id) { mutableListOf() }.add(entry.slot to type)
        }
    }
    return slotsById.mapValues { (_, slots) -> slots.sortedBy { it.first }.map { it.second } }
}

/** Achata a árvore da cadeia evolutiva em estágios (profundidade 0, 1, 2...). */
fun ChainLinkDto.toEvolutionStages(): List<List<EvolutionSpecies>> {
    val stages = mutableListOf<MutableList<EvolutionSpecies>>()

    fun visit(link: ChainLinkDto, depth: Int) {
        if (stages.size <= depth) stages.add(mutableListOf())
        extractIdFromUrl(link.species.url)?.let { id ->
            stages[depth].add(EvolutionSpecies(id = id, name = link.species.name))
        }
        link.evolvesTo.forEach { visit(it, depth + 1) }
    }

    visit(this, 0)
    return stages
}

fun selectDescription(entries: List<FlavorTextEntry>, language: String = "en"): String? =
    entries.lastOrNull { it.language.name == language }
        ?.flavorText
        ?.replace(Regex("\\s+"), " ")
        ?.trim()

private val statLabels = mapOf(
    "hp" to "HP",
    "attack" to "ATK",
    "defense" to "DEF",
    "special-attack" to "SATK",
    "special-defense" to "SDEF",
    "speed" to "SPD",
)

fun PokemonResponse.toDetail(
    species: PokemonSpeciesResponse,
    evolutionChain: EvolutionChainResponse?,
): PokemonDetail = PokemonDetail(
    id = id,
    name = name,
    types = types.sortedBy { it.slot }.mapNotNull { PokemonType.fromApiName(it.type.name) },
    imageUrl = sprites?.other?.officialArtwork?.frontDefault
        ?: sprites?.frontDefault
        ?: officialArtworkUrl(id),
    heightMeters = height / 10.0,
    weightKg = weight / 10.0,
    abilities = abilities.sortedBy { it.slot }.map { formatPokemonName(it.ability.name) },
    description = selectDescription(species.flavorTextEntries),
    stats = stats.map { PokemonStat(statLabels[it.stat.name] ?: it.stat.name.uppercase(), it.baseStat) },
    evolutionStages = evolutionChain?.chain?.toEvolutionStages().orEmpty(),
)
