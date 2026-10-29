package com.naotendo.pokedex.data.remote

import com.naotendo.pokedex.data.remote.dto.EvolutionChainResponse
import com.naotendo.pokedex.data.remote.dto.NamedResourceListResponse
import com.naotendo.pokedex.data.remote.dto.PokemonResponse
import com.naotendo.pokedex.data.remote.dto.PokemonSpeciesResponse
import com.naotendo.pokedex.data.remote.dto.TypeResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

/** Endpoints da PokéAPI (https://pokeapi.co/docs/v2) utilizados pelo app. */
interface PokeApiService {

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int = 0,
    ): NamedResourceListResponse

    @GET("type/{name}")
    suspend fun getType(@Path("name") name: String): TypeResponse

    @GET("pokemon/{id}")
    suspend fun getPokemon(@Path("id") id: Int): PokemonResponse

    @GET("pokemon-species/{id}")
    suspend fun getPokemonSpecies(@Path("id") id: Int): PokemonSpeciesResponse

    /** A espécie informa a URL completa da cadeia evolutiva, por isso usamos @Url. */
    @GET
    suspend fun getEvolutionChain(@Url url: String): EvolutionChainResponse

    companion object {
        const val BASE_URL = "https://pokeapi.co/api/v2/"
    }
}
