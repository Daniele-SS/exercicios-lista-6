package com.naotendo.pokedex.ui.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.naotendo.pokedex.model.PokemonSummary
import com.naotendo.pokedex.model.PokemonType
import com.naotendo.pokedex.ui.components.TypeChips

private val CardShape = RoundedCornerShape(8.dp)

@Composable
fun PokemonGridCard(pokemon: PokemonSummary, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val color = pokemon.primaryType?.color ?: PokemonType.DefaultColor

    Card(
        onClick = onClick,
        modifier = modifier,
        shape = CardShape,
        border = BorderStroke(1.dp, color),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column {
            Text(
                text = pokemon.displayId,
                color = color,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 4.dp, end = 8.dp),
            )
            PokemonImage(
                url = pokemon.imageUrl,
                contentDescription = pokemon.displayName,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(horizontal = 8.dp),
            )
            NameBar(name = pokemon.displayName, color = color)
        }
    }
}

@Composable
fun PokemonListCard(pokemon: PokemonSummary, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val color = pokemon.primaryType?.color ?: PokemonType.DefaultColor

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        border = BorderStroke(1.dp, color),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PokemonImage(
                url = pokemon.imageUrl,
                contentDescription = pokemon.displayName,
                modifier = Modifier
                    .padding(4.dp)
                    .size(80.dp),
            )
            Column(
                modifier = Modifier.padding(end = 8.dp, top = 4.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(text = pokemon.displayId, color = color, style = MaterialTheme.typography.labelSmall)
                NameBar(name = pokemon.displayName, color = color)
                TypeChips(types = pokemon.types)
            }
        }
    }
}

@Composable
private fun NameBar(name: String, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = name,
            color = Color.White,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun PokemonImage(url: String, contentDescription: String?, modifier: Modifier = Modifier) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
    )
}
