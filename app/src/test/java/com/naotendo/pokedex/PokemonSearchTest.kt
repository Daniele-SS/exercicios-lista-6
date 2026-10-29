package com.naotendo.pokedex

import com.naotendo.pokedex.model.PokemonSearch
import com.naotendo.pokedex.model.PokemonSummary
import com.naotendo.pokedex.model.PokemonType
import org.junit.Assert.assertEquals
import org.junit.Test

class PokemonSearchTest {

    private val pokemons = listOf(
        PokemonSummary(1, "bulbasaur", listOf(PokemonType.GRASS, PokemonType.POISON)),
        PokemonSummary(25, "pikachu", listOf(PokemonType.ELECTRIC)),
        PokemonSummary(122, "mr-mime", listOf(PokemonType.PSYCHIC, PokemonType.FAIRY)),
        PokemonSummary(250, "ho-oh", listOf(PokemonType.FIRE, PokemonType.FLYING)),
    )

    @Test
    fun `consulta vazia retorna todos`() {
        assertEquals(pokemons, PokemonSearch.filter(pokemons, "   "))
    }

    @Test
    fun `busca por nome parcial ignora maiusculas`() {
        assertEquals(listOf(25), PokemonSearch.filter(pokemons, "PIKA").map { it.id })
    }

    @Test
    fun `busca por id aceita zeros e cerquilha`() {
        assertEquals(listOf(1), PokemonSearch.filter(pokemons, "#001").map { it.id })
        assertEquals(listOf(25), PokemonSearch.filter(pokemons, "25").map { it.id })
    }

    @Test
    fun `espaco no nome casa com hifen da api`() {
        assertEquals(listOf(122), PokemonSearch.filter(pokemons, "mr mime").map { it.id })
    }

    @Test
    fun `busca sem resultado retorna lista vazia`() {
        assertEquals(emptyList<PokemonSummary>(), PokemonSearch.filter(pokemons, "agumon"))
    }
}
