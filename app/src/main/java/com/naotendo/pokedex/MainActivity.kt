package com.naotendo.pokedex

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.naotendo.pokedex.ui.navigation.PokedexNavHost
import com.naotendo.pokedex.ui.theme.PokedexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // As duas telas têm topo colorido, então os ícones da status bar ficam claros.
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        super.onCreate(savedInstanceState)
        setContent {
            PokedexTheme {
                PokedexNavHost()
            }
        }
    }
}
