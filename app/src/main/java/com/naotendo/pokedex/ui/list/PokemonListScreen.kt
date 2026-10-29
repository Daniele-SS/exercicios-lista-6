package com.naotendo.pokedex.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.naotendo.pokedex.R
import com.naotendo.pokedex.model.PokemonSummary
import com.naotendo.pokedex.ui.components.ErrorView
import com.naotendo.pokedex.ui.components.LoadingView
import com.naotendo.pokedex.ui.components.PokeballIcon
import com.naotendo.pokedex.ui.theme.PokedexBackground
import com.naotendo.pokedex.ui.theme.PokedexGrayMedium
import com.naotendo.pokedex.ui.theme.PokedexRed

@Composable
fun PokemonListScreen(
    onPokemonClick: (Int) -> Unit,
    viewModel: PokemonListViewModel = viewModel(factory = PokemonListViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PokemonListContent(
        uiState = uiState,
        query = viewModel.query,
        onQueryChange = viewModel::onQueryChange,
        onToggleLayout = viewModel::toggleLayoutMode,
        onRetry = viewModel::retry,
        onPokemonClick = onPokemonClick,
    )
}

@Composable
private fun PokemonListContent(
    uiState: PokemonListUiState,
    query: String,
    onQueryChange: (String) -> Unit,
    onToggleLayout: () -> Unit,
    onRetry: () -> Unit,
    onPokemonClick: (Int) -> Unit,
) {
    Scaffold(
        topBar = { PokedexTopBar(layoutMode = uiState.layoutMode, onToggleLayout = onToggleLayout) },
        containerColor = PokedexBackground,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            SearchField(
                query = query,
                onQueryChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            )

            when {
                uiState.isLoading -> LoadingView()
                uiState.error != null -> ErrorView(error = uiState.error, onRetry = onRetry)
                uiState.pokemons.isEmpty() -> EmptySearchView(query)
                uiState.layoutMode == LayoutMode.GRID -> PokemonGrid(uiState.pokemons, onPokemonClick)
                else -> PokemonList(uiState.pokemons, onPokemonClick)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PokedexTopBar(layoutMode: LayoutMode, onToggleLayout: () -> Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PokeballIcon(color = Color.White, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(16.dp))
                Text(text = stringResource(R.string.app_name), fontWeight = FontWeight.Bold, fontSize = 24.sp)
            }
        },
        actions = {
            IconButton(onClick = onToggleLayout) {
                if (layoutMode == LayoutMode.GRID) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.List,
                        contentDescription = stringResource(R.string.layout_show_list),
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_grid),
                        contentDescription = stringResource(R.string.layout_show_grid),
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PokedexRed,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White,
        ),
    )
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text(stringResource(R.string.search_placeholder)) },
        trailingIcon = {
            if (query.isEmpty()) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = null)
            } else {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(imageVector = Icons.Filled.Clear, contentDescription = stringResource(R.string.search_clear))
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = PokedexRed,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = PokedexRed,
        ),
    )
}

@Composable
private fun PokemonGrid(pokemons: List<PokemonSummary>, onPokemonClick: (Int) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 104.dp),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(pokemons, key = { it.id }) { pokemon ->
            PokemonGridCard(pokemon = pokemon, onClick = { onPokemonClick(pokemon.id) })
        }
    }
}

@Composable
private fun PokemonList(pokemons: List<PokemonSummary>, onPokemonClick: (Int) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(pokemons, key = { it.id }) { pokemon ->
            PokemonListCard(pokemon = pokemon, onClick = { onPokemonClick(pokemon.id) })
        }
    }
}

@Composable
private fun EmptySearchView(query: String) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.empty_search, query.trim()),
            color = PokedexGrayMedium,
            textAlign = TextAlign.Center,
        )
    }
}
