package com.naotendo.pokedex

import android.app.Application
import com.naotendo.pokedex.di.AppContainer

class PokedexApplication : Application() {
    val container: AppContainer by lazy { AppContainer() }
}
