package com.naotendo.pokedex.ui.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.naotendo.pokedex.R
import com.naotendo.pokedex.model.EvolutionSpecies
import com.naotendo.pokedex.model.PokemonDetail
import com.naotendo.pokedex.model.PokemonStat
import com.naotendo.pokedex.ui.list.PokemonImage
import com.naotendo.pokedex.ui.theme.PokedexGrayDark
import com.naotendo.pokedex.ui.theme.PokedexGrayLight
import com.naotendo.pokedex.ui.theme.PokedexGrayMedium
import java.util.Locale

@Composable
fun AboutSection(pokemon: PokemonDetail) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        AboutItem(
            value = stringResource(R.string.weight_value, pokemon.weightKg),
            label = stringResource(R.string.weight),
            modifier = Modifier.weight(1f),
        )
        VerticalDivider(color = PokedexGrayLight)
        AboutItem(
            value = stringResource(R.string.height_value, pokemon.heightMeters),
            label = stringResource(R.string.height),
            modifier = Modifier.weight(1f),
        )
        VerticalDivider(color = PokedexGrayLight)
        AboutItem(
            value = pokemon.abilities.joinToString("\n"),
            label = stringResource(R.string.abilities),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun AboutItem(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = PokedexGrayDark,
            textAlign = TextAlign.Center,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = PokedexGrayMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
fun BaseStatsSection(stats: List<PokemonStat>, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(horizontalAlignment = Alignment.End) {
            stats.forEach { stat ->
                Text(
                    text = stat.label,
                    color = color,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 2.dp),
                )
            }
        }
        VerticalDivider(color = PokedexGrayLight, modifier = Modifier.padding(horizontal = 12.dp))
        Column(modifier = Modifier.weight(1f)) {
            stats.forEach { stat ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = String.format(Locale.ROOT, "%03d", stat.value),
                        color = PokedexGrayDark,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .width(32.dp)
                            .padding(vertical = 2.dp),
                    )
                    StatBar(fraction = stat.fraction, color = color, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun StatBar(fraction: Float, color: Color, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .height(4.dp)
            .clip(shape)
            .background(color.copy(alpha = 0.2f)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction)
                .fillMaxHeight()
                .clip(shape)
                .background(color),
        )
    }
}

/** Desafio: exibe pré-evoluções e evoluções, agrupadas por estágio da cadeia evolutiva. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EvolutionSection(
    pokemon: PokemonDetail,
    color: Color,
    onEvolutionClick: (Int) -> Unit,
) {
    if (!pokemon.hasEvolutions) {
        Text(
            text = stringResource(R.string.no_evolutions),
            style = MaterialTheme.typography.bodyMedium,
            color = PokedexGrayMedium,
            textAlign = TextAlign.Center,
        )
        return
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        pokemon.evolutionStages.forEachIndexed { index, stage ->
            if (index > 0) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                stage.forEach { species ->
                    EvolutionItem(
                        species = species,
                        isCurrent = species.id == pokemon.id,
                        color = color,
                        onClick = { onEvolutionClick(species.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun EvolutionItem(
    species: EvolutionSpecies,
    isCurrent: Boolean,
    color: Color,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(88.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = !isCurrent, onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val border = if (isCurrent) BorderStroke(3.dp, color) else BorderStroke(1.dp, PokedexGrayLight)
        PokemonImage(
            url = species.imageUrl,
            contentDescription = species.displayName,
            modifier = Modifier
                .size(72.dp)
                .border(border, CircleShape)
                .padding(8.dp),
        )
        Text(
            text = species.displayName,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
            color = if (isCurrent) color else PokedexGrayDark,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(
            text = species.displayId,
            style = MaterialTheme.typography.labelSmall,
            color = PokedexGrayMedium,
        )
    }
}
