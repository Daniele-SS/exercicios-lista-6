package com.naotendo.pokedex.model

import java.util.Locale

/** Item exibido na tela inicial. */
data class PokemonSummary(
    val id: Int,
    val name: String,
    val types: List<PokemonType>,
) {
    val primaryType: PokemonType? get() = types.firstOrNull()
    val displayName: String get() = formatPokemonName(name)
    val displayId: String get() = formatPokemonId(id)
    val imageUrl: String get() = officialArtworkUrl(id)
}

data class PokemonDetail(
    val id: Int,
    val name: String,
    val types: List<PokemonType>,
    val imageUrl: String,
    val heightMeters: Double,
    val weightKg: Double,
    val abilities: List<String>,
    val description: String?,
    val stats: List<PokemonStat>,
    /** Cadeia evolutiva agrupada por estágio (pré-evoluções, o próprio Pokémon e evoluções). */
    val evolutionStages: List<List<EvolutionSpecies>>,
) {
    val primaryType: PokemonType? get() = types.firstOrNull()
    val displayName: String get() = formatPokemonName(name)
    val displayId: String get() = formatPokemonId(id)
    val hasEvolutions: Boolean get() = evolutionStages.sumOf { it.size } > 1
}

data class PokemonStat(
    val label: String,
    val value: Int,
) {
    val fraction: Float get() = (value.toFloat() / MAX_BASE_STAT).coerceIn(0f, 1f)

    companion object {
        const val MAX_BASE_STAT = 255
    }
}

data class EvolutionSpecies(
    val id: Int,
    val name: String,
) {
    val displayName: String get() = formatPokemonName(name)
    val displayId: String get() = formatPokemonId(id)
    val imageUrl: String get() = officialArtworkUrl(id)
}

fun formatPokemonName(name: String): String =
    name.split('-').joinToString("-") { part -> part.replaceFirstChar(Char::titlecase) }

fun formatPokemonId(id: Int): String = "#%03d".format(Locale.ROOT, id)

fun officialArtworkUrl(id: Int): String =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
