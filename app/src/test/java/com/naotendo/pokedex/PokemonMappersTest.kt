package com.naotendo.pokedex

import com.naotendo.pokedex.data.mapper.buildTypesById
import com.naotendo.pokedex.data.mapper.extractIdFromUrl
import com.naotendo.pokedex.data.mapper.selectDescription
import com.naotendo.pokedex.data.mapper.toEvolutionStages
import com.naotendo.pokedex.data.remote.dto.ChainLinkDto
import com.naotendo.pokedex.data.remote.dto.FlavorTextEntry
import com.naotendo.pokedex.data.remote.dto.NamedApiResource
import com.naotendo.pokedex.data.remote.dto.TypePokemonEntry
import com.naotendo.pokedex.data.remote.dto.TypeResponse
import com.naotendo.pokedex.model.PokemonType
import com.naotendo.pokedex.model.formatPokemonId
import com.naotendo.pokedex.model.formatPokemonName
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PokemonMappersTest {

    private fun species(id: Int, name: String) =
        NamedApiResource(name, "https://pokeapi.co/api/v2/pokemon-species/$id/")

    private fun pokemon(id: Int, name: String) =
        NamedApiResource(name, "https://pokeapi.co/api/v2/pokemon/$id/")

    @Test
    fun `extrai id da url`() {
        assertEquals(25, extractIdFromUrl("https://pokeapi.co/api/v2/pokemon/25/"))
        assertEquals(133, extractIdFromUrl("https://pokeapi.co/api/v2/pokemon-species/133"))
        assertNull(extractIdFromUrl("https://pokeapi.co/api/v2/pokemon/"))
    }

    @Test
    fun `tipos ficam ordenados pelo slot independente da ordem das respostas`() {
        val responses = listOf(
            PokemonType.POISON to TypeResponse("poison", listOf(TypePokemonEntry(2, pokemon(1, "bulbasaur")))),
            PokemonType.GRASS to TypeResponse("grass", listOf(TypePokemonEntry(1, pokemon(1, "bulbasaur")))),
            PokemonType.FIRE to null,
        )

        val typesById = buildTypesById(responses)

        assertEquals(listOf(PokemonType.GRASS, PokemonType.POISON), typesById[1])
    }

    @Test
    fun `cadeia ramificada vira estagios`() {
        val eevee = ChainLinkDto(
            species = species(133, "eevee"),
            evolvesTo = listOf(
                ChainLinkDto(species(134, "vaporeon"), emptyList()),
                ChainLinkDto(species(135, "jolteon"), emptyList()),
            ),
        )

        val stages = eevee.toEvolutionStages()

        assertEquals(listOf(listOf(133), listOf(134, 135)), stages.map { stage -> stage.map { it.id } })
    }

    @Test
    fun `cadeia linear de tres estagios`() {
        val chain = ChainLinkDto(
            species(1, "bulbasaur"),
            listOf(ChainLinkDto(species(2, "ivysaur"), listOf(ChainLinkDto(species(3, "venusaur"), emptyList())))),
        )

        assertEquals(listOf(listOf(1), listOf(2), listOf(3)), chain.toEvolutionStages().map { s -> s.map { it.id } })
    }

    @Test
    fun `descricao em ingles com quebras normalizadas`() {
        val entries = listOf(
            FlavorTextEntry("Texto\nantigo", NamedApiResource("en", "")),
            FlavorTextEntry("Une graine", NamedApiResource("fr", "")),
            FlavorTextEntry("A strange seed\u000cwas planted\non its back.", NamedApiResource("en", "")),
        )

        assertEquals("A strange seed was planted on its back.", selectDescription(entries))
    }

    @Test
    fun `formatacao de nome e id`() {
        assertEquals("Bulbasaur", formatPokemonName("bulbasaur"))
        assertEquals("Ho-Oh", formatPokemonName("ho-oh"))
        assertEquals("#001", formatPokemonId(1))
        assertEquals("#1025", formatPokemonId(1025))
    }

    @Test
    fun `enum resolve tipo pelo nome da api`() {
        assertEquals(PokemonType.ELECTRIC, PokemonType.fromApiName("electric"))
        assertNull(PokemonType.fromApiName("stellar"))
        assertEquals(18, PokemonType.entries.size)
    }
}
