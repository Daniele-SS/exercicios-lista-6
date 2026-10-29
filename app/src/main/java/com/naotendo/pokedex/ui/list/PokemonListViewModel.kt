package com.naotendo.pokedex.ui.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.naotendo.pokedex.PokedexApplication
import com.naotendo.pokedex.data.repository.PokemonRepository
import com.naotendo.pokedex.model.PokemonSearch
import com.naotendo.pokedex.model.PokemonSummary
import com.naotendo.pokedex.ui.ErrorMessage
import com.naotendo.pokedex.ui.toErrorMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class LayoutMode { GRID, LIST }

data class PokemonListUiState(
    val isLoading: Boolean = true,
    val error: ErrorMessage? = null,
    val pokemons: List<PokemonSummary> = emptyList(),
    val layoutMode: LayoutMode = LayoutMode.GRID,
)

private sealed interface LoadState {
    data object Loading : LoadState
    data class Loaded(val pokemons: List<PokemonSummary>) : LoadState
    data class Failed(val error: ErrorMessage) : LoadState
}

class PokemonListViewModel(private val repository: PokemonRepository) : ViewModel() {

    /** Mantido como estado do Compose para o TextField ser atualizado de forma síncrona. */
    var query by mutableStateOf("")
        private set

    private val loadState = MutableStateFlow<LoadState>(LoadState.Loading)
    private val layoutMode = MutableStateFlow(LayoutMode.GRID)

    val uiState: StateFlow<PokemonListUiState> =
        combine(loadState, snapshotFlow { query }, layoutMode) { load, currentQuery, mode ->
            when (load) {
                LoadState.Loading -> PokemonListUiState(isLoading = true, layoutMode = mode)
                is LoadState.Failed -> PokemonListUiState(
                    isLoading = false,
                    error = load.error,
                    layoutMode = mode,
                )
                is LoadState.Loaded -> PokemonListUiState(
                    isLoading = false,
                    pokemons = PokemonSearch.filter(load.pokemons, currentQuery),
                    layoutMode = mode,
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PokemonListUiState(),
        )

    init {
        loadPokemons()
    }

    fun onQueryChange(newQuery: String) {
        query = newQuery
    }

    fun toggleLayoutMode() {
        layoutMode.value = if (layoutMode.value == LayoutMode.GRID) LayoutMode.LIST else LayoutMode.GRID
    }

    fun retry() = loadPokemons()

    private fun loadPokemons() {
        viewModelScope.launch {
            loadState.value = LoadState.Loading
            loadState.value = try {
                LoadState.Loaded(repository.getPokemonList())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                LoadState.Failed(e.toErrorMessage())
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as PokedexApplication
                PokemonListViewModel(application.container.pokemonRepository)
            }
        }
    }
}
