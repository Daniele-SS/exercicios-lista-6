package com.naotendo.pokedex.ui

import androidx.annotation.StringRes
import com.naotendo.pokedex.R
import retrofit2.HttpException
import java.io.IOException

/** Mensagem de erro já resolvida para recurso de string, pronta para a View exibir. */
data class ErrorMessage(@StringRes val resId: Int, val arg: Int? = null)

fun Throwable.toErrorMessage(): ErrorMessage = when (this) {
    is IOException -> ErrorMessage(R.string.error_network)
    is HttpException -> if (code() == 404) {
        ErrorMessage(R.string.error_not_found)
    } else {
        ErrorMessage(R.string.error_server, code())
    }
    else -> ErrorMessage(R.string.error_unknown)
}
