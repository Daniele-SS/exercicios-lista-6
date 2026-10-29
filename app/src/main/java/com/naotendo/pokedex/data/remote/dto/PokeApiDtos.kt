package com.naotendo.pokedex.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NamedApiResource(
    val name: String,
    val url: String,
)

data class ApiResource(
    val url: String,
)

data class NamedResourceListResponse(
    val count: Int,
    val results: List<NamedApiResource>,
)

data class TypeResponse(
    val name: String,
    val pokemon: List<TypePokemonEntry>,
)

/** [slot] indica se o tipo é o primário (1) ou o secundário (2) daquele Pokémon. */
data class TypePokemonEntry(
    val slot: Int,
    val pokemon: NamedApiResource,
)

data class PokemonResponse(
    val id: Int,
    val name: String,
    /** Em decímetros. */
    val height: Int,
    /** Em hectogramas. */
    val weight: Int,
    val types: List<PokemonTypeSlot>,
    val abilities: List<PokemonAbilitySlot>,
    val stats: List<PokemonStatEntry>,
    val sprites: SpritesDto?,
)

data class PokemonTypeSlot(
    val slot: Int,
    val type: NamedApiResource,
)

data class PokemonAbilitySlot(
    val slot: Int,
    val ability: NamedApiResource,
    @SerializedName("is_hidden") val isHidden: Boolean,
)

data class PokemonStatEntry(
    @SerializedName("base_stat") val baseStat: Int,
    val stat: NamedApiResource,
)

data class SpritesDto(
    @SerializedName("front_default") val frontDefault: String?,
    val other: OtherSpritesDto?,
)

data class OtherSpritesDto(
    @SerializedName("official-artwork") val officialArtwork: ArtworkDto?,
)

data class ArtworkDto(
    @SerializedName("front_default") val frontDefault: String?,
)

data class PokemonSpeciesResponse(
    val id: Int,
    @SerializedName("flavor_text_entries") val flavorTextEntries: List<FlavorTextEntry>,
    @SerializedName("evolution_chain") val evolutionChain: ApiResource?,
)

data class FlavorTextEntry(
    @SerializedName("flavor_text") val flavorText: String,
    val language: NamedApiResource,
)

data class EvolutionChainResponse(
    val id: Int,
    val chain: ChainLinkDto,
)

data class ChainLinkDto(
    val species: NamedApiResource,
    @SerializedName("evolves_to") val evolvesTo: List<ChainLinkDto>,
)
