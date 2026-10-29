package com.naotendo.pokedex.model

/**
 * Filtro da tela inicial. A PokéAPI não oferece busca parcial por nome,
 * então a lista completa é carregada uma vez e filtrada localmente.
 */
object PokemonSearch {

    fun filter(pokemons: List<PokemonSummary>, query: String): List<PokemonSummary> {
        val normalized = query.trim().removePrefix("#").lowercase().replace(' ', '-')
        if (normalized.isEmpty()) return pokemons

        val id = normalized.toIntOrNull()
        return if (id != null) {
            pokemons.filter { it.id == id }
        } else {
            pokemons.filter { it.name.contains(normalized) }
        }
    }
}
