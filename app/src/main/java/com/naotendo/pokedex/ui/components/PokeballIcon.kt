package com.naotendo.pokedex.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

/** Pokébola em traço, usada no logo da barra superior e como marca d'água nos detalhes. */
@Composable
fun PokeballIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = size.minDimension * 0.12f
        val radius = size.minDimension / 2f
        val innerRadius = radius * 0.32f

        drawCircle(color = color, radius = radius - strokeWidth / 2f, style = Stroke(strokeWidth))
        drawCircle(color = color, radius = innerRadius - strokeWidth / 2f, style = Stroke(strokeWidth))
        drawLine(
            color = color,
            start = Offset(center.x - radius, center.y),
            end = Offset(center.x - innerRadius, center.y),
            strokeWidth = strokeWidth,
        )
        drawLine(
            color = color,
            start = Offset(center.x + innerRadius, center.y),
            end = Offset(center.x + radius, center.y),
            strokeWidth = strokeWidth,
        )
    }
}
