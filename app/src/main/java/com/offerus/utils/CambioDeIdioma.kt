package com.offerus.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.offerus.Idioma
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Class to manage the language change
 * It is a Hilt Singleton app so we only have one instance on the whole application
 */
@Singleton
class CambioDeIdioma @Inject constructor() {

    // get the code of the current language
    var idiomaActual: Idioma = when (Locale.getDefault().language.lowercase()){
        "es" -> Idioma.Castellano
        "en" -> Idioma.English
        "eu" -> Idioma.Euskera
        else -> {
            Idioma.Castellano}
    }

    /**
     * Changes app language if the new language is different of the current one
     * @param idioma Target language
     * @param context App context
     */
    fun cambiarIdioma(idioma: Idioma, context: Context, recreate: Boolean = false) {

        if (idioma != idiomaActual || idiomaActual.codigo != Locale.getDefault().language) {

            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(idioma.codigo))

            idiomaActual = idioma
        }
    }
}