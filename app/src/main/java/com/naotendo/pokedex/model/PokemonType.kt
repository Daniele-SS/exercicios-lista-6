package com.naotendo.pokedex.model

import androidx.compose.ui.graphics.Color

/**
 * Tipos de Pokémon com a cor associada a cada um (enum com construtor).
 * O tipo principal define a cor dos cards e do fundo da tela de detalhes.
 */
enum class PokemonType(val color: Color) {
    NORMAL(Color(0xFFAAA67F)),
    FIRE(Color(0xFFF57D31)),
    WATER(Color(0xFF6493EB)),
    ELECTRIC(Color(0xFFF9CF30)),
    GRASS(Color(0xFF74CB48)),
    ICE(Color(0xFF9AD6DF)),
    FIGHTING(Color(0xFFC12239)),
    POISON(Color(0xFFA43E9E)),
    GROUND(Color(0xFFDEC16B)),
    FLYING(Color(0xFFA891EC)),
    PSYCHIC(Color(0xFFFB5584)),
    BUG(Color(0xFFA7B723)),
    ROCK(Color(0xFFB69E31)),
    GHOST(Color(0xFF70559B)),
    DRAGON(Color(0xFF7037FF)),
    DARK(Color(0xFF75574C)),
    STEEL(Color(0xFFB7B9D0)),
    FAIRY(Color(0xFFE69EAC));

    /** Nome usado pela PokéAPI (ex.: "grass"). */
    val apiName: String get() = name.lowercase()

    val displayName: String get() = apiName.replaceFirstChar(Char::titlecase)

    companion object {
        /** Cor usada enquanto o tipo ainda não é conhecido. */
        val DefaultColor = Color(0xFF666666)

        fun fromApiName(apiName: String): PokemonType? =
            entries.firstOrNull { it.apiName == apiName.lowercase() }
    }
}
