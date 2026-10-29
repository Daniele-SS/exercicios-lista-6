package com.naotendo.pokedex.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.naotendo.pokedex.PokedexApplication
import com.naotendo.pokedex.data.repository.PokemonRepository
import com.naotendo.pokedex.model.PokemonDetail
import com.naotendo.pokedex.ui.ErrorMessage
import com.naotendo.pokedex.ui.navigation.PokedexDestinations
import com.naotendo.pokedex.ui.toErrorMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PokemonDetailUiState {
    data object Loading : PokemonDetailUiState
    data class Success(val pokemon: PokemonDetail) : PokemonDetailUiState
    data class Error(val error: ErrorMessage) : PokemonDetailUiState
}

class PokemonDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: PokemonRepository,
) : ViewModel() {

    private val pokemonId: Int = checkNotNull(savedStateHandle[PokedexDestinations.POKEMON_ID_ARG])

    private val _uiState = MutableStateFlow<PokemonDetailUiState>(PokemonDetailUiState.Loading)
    val uiState: StateFlow<PokemonDetailUiState> = _uiState.asStateFlow()

    init {
        loadPokemon()
    }

    fun retry() = loadPokemon()

    private fun loadPokemon() {
        viewModelScope.launch {
            _uiState.value = PokemonDetailUiState.Loading
            _uiState.value = try {
                PokemonDetailUiState.Success(repository.getPokemonDetail(pokemonId))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                PokemonDetailUiState.Error(e.toErrorMessage())
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as PokedexApplication
                PokemonDetailViewModel(
                    savedStateHandle = createSavedStateHandle(),
                    repository = application.container.pokemonRepository,
                )
            }
        }
    }
}
