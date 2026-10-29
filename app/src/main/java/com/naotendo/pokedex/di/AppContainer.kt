package com.naotendo.pokedex.di

import com.naotendo.pokedex.data.remote.PokeApiService
import com.naotendo.pokedex.data.repository.PokemonRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Injeção de dependência manual: cria as instâncias compartilhadas pelo app. */
class AppContainer {

    private val api: PokeApiService = Retrofit.Builder()
        .baseUrl(PokeApiService.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(PokeApiService::class.java)

    val pokemonRepository = PokemonRepository(api)
}
