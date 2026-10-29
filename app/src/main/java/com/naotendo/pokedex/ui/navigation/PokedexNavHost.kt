package com.naotendo.pokedex.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.naotendo.pokedex.ui.detail.PokemonDetailScreen
import com.naotendo.pokedex.ui.list.PokemonListScreen

object PokedexDestinations {
    const val LIST = "pokemon_list"
    const val POKEMON_ID_ARG = "pokemonId"
    const val DETAIL = "pokemon_detail/{$POKEMON_ID_ARG}"

    fun detail(id: Int) = "pokemon_detail/$id"
}

@Composable
fun PokedexNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = PokedexDestinations.LIST) {
        composable(PokedexDestinations.LIST) {
            PokemonListScreen(
                onPokemonClick = { id -> navController.navigate(PokedexDestinations.detail(id)) },
            )
        }
        composable(
            route = PokedexDestinations.DETAIL,
            arguments = listOf(navArgument(PokedexDestinations.POKEMON_ID_ARG) { type = NavType.IntType }),
        ) {
            PokemonDetailScreen(
                onBack = { navController.navigateUp() },
                onEvolutionClick = { id -> navController.navigate(PokedexDestinations.detail(id)) },
            )
        }
    }
}
