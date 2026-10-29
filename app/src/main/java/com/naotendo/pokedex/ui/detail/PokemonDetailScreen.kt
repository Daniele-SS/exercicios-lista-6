package com.naotendo.pokedex.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.naotendo.pokedex.R
import com.naotendo.pokedex.model.PokemonDetail
import com.naotendo.pokedex.model.PokemonType
import com.naotendo.pokedex.ui.components.ErrorView
import com.naotendo.pokedex.ui.components.LoadingView
import com.naotendo.pokedex.ui.components.PokeballIcon
import com.naotendo.pokedex.ui.components.TypeChips
import com.naotendo.pokedex.ui.list.PokemonImage
import com.naotendo.pokedex.ui.theme.PokedexGrayDark

@Composable
fun PokemonDetailScreen(
    onBack: () -> Unit,
    onEvolutionClick: (Int) -> Unit,
    viewModel: PokemonDetailViewModel = viewModel(factory = PokemonDetailViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        PokemonDetailUiState.Loading -> DetailPlaceholder(onBack) {
            LoadingView(color = Color.White)
        }
        is PokemonDetailUiState.Error -> DetailPlaceholder(onBack) {
            ErrorView(error = state.error, onRetry = viewModel::retry, textColor = Color.White)
        }
        is PokemonDetailUiState.Success -> PokemonDetailContent(
            pokemon = state.pokemon,
            onBack = onBack,
            onEvolutionClick = onEvolutionClick,
        )
    }
}

@Composable
private fun DetailPlaceholder(onBack: () -> Unit, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PokemonType.DefaultColor)
            .statusBarsPadding(),
    ) {
        DetailHeader(name = "", displayId = "", onBack = onBack)
        content()
    }
}

@Composable
private fun PokemonDetailContent(
    pokemon: PokemonDetail,
    onBack: () -> Unit,
    onEvolutionClick: (Int) -> Unit,
) {
    val typeColor = pokemon.primaryType?.color ?: PokemonType.DefaultColor

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(typeColor),
    ) {
        PokeballIcon(
            color = Color.White.copy(alpha = 0.12f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 8.dp, end = 8.dp)
                .size(208.dp),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState()),
        ) {
            DetailHeader(name = pokemon.displayName, displayId = pokemon.displayId, onBack = onBack)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 160.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                ) {
                    Column(
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp, top = 56.dp, bottom = 20.dp)
                            .navigationBarsPadding(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        TypeChips(types = pokemon.types)
                        SectionTitle(text = stringResource(R.string.about), color = typeColor)
                        AboutSection(pokemon)
                        pokemon.description?.let { description ->
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = PokedexGrayDark,
                                textAlign = TextAlign.Justify,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                            )
                        }
                        SectionTitle(text = stringResource(R.string.base_stats), color = typeColor)
                        BaseStatsSection(stats = pokemon.stats, color = typeColor)
                        SectionTitle(text = stringResource(R.string.evolutions), color = typeColor)
                        EvolutionSection(
                            pokemon = pokemon,
                            color = typeColor,
                            onEvolutionClick = onEvolutionClick,
                        )
                    }
                }

                PokemonImage(
                    url = pokemon.imageUrl,
                    contentDescription = pokemon.displayName,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .size(200.dp),
                )
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun DetailHeader(name: String, displayId: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 20.dp, top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = Color.White,
            )
        }
        Text(
            text = name,
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = displayId,
            color = Color.White,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun SectionTitle(text: String, color: Color) {
    Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 20.dp, bottom = 12.dp),
    )
}
