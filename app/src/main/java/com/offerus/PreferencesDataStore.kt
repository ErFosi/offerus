package com.offerus

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.offerus.utils.CipherUtil
import com.offerus.utils.NoCryptographicKeyException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


/**
 * THE DATASTORE (preferencedatastore) IS USED TO STORE USERS SETTINGS OR PREFERENCES PERMANENTLY
 * the information is sotred as key-value pairs
 *
 * This datastore is used to store users language and theme selection
 */


val Context.myPreferencesDataStore : DataStore<Preferences> by preferencesDataStore("settings")

// definition of the available languages and their codes
enum class Idioma(val codigo:String){
    Castellano(codigo = "es"),
    English(codigo = "en"),
    Euskera(codigo = "eu")
}

// definition of the themes
data class Settings(
    val temaClaro : Boolean,
    val idioma: Idioma
)


/**
 * The preference data store is annotated as singleton so there is only one instance on the whole app
 * This class implements the data store
 */
@Singleton
class MyPreferencesDataStore @Inject constructor(
    @ApplicationContext context: Context,
    private val cipher: CipherUtil
) {


    private val myPreferencesDataStore : DataStore<Preferences> = context.myPreferencesDataStore
    private object PreferencesKeys{
        val IDIOMA_SELECCIONADO = stringPreferencesKey("idioma")
        val TEMA_CLARO_SELECCIONADO = booleanPreferencesKey("tema")
        val USUARIO_LOGEADO = stringPreferencesKey("usuario")
        val CONTRASENA_USUARIO_LOGEADO = stringPreferencesKey("contrasena")
    }
    val preferencesStatusFlow = myPreferencesDataStore.data
        .catch { exception ->
            if (exception is IOException){
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map {preferences ->
            val tema = preferences[PreferencesKeys.TEMA_CLARO_SELECCIONADO]?: true
            val idioma =
                Idioma.valueOf(
                    preferences[PreferencesKeys.IDIOMA_SELECCIONADO]?: Idioma.Castellano.name
                )
            Settings(tema, idioma)
        }

    suspend fun updateTheme(temaclaro: Boolean){
        myPreferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.TEMA_CLARO_SELECCIONADO] = temaclaro
        }
    }
    suspend fun updateIdioma(idioma: Idioma){
        myPreferencesDataStore.edit{preferences ->
            preferences[PreferencesKeys.IDIOMA_SELECCIONADO] = idioma.name
        }
    }

    // methods to store and retrieve the user and password of the last logged user

    //save the user and password of the logged user
    suspend fun setUsuarioContraLogeado(user: String, contrasena: String) {
        myPreferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.USUARIO_LOGEADO] = cipher.encryptData("usuario", user)
        }
        myPreferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.CONTRASENA_USUARIO_LOGEADO] = cipher.encryptData("contrasena", contrasena)
        }
    }
    // retrieve the logged user's username
    suspend fun getUsuarioLogeadoOLD(): String? {
        val encryptedData = myPreferencesDataStore.data.first()[PreferencesKeys.USUARIO_LOGEADO]
        return try {
            if (encryptedData != null) {
                val data = cipher.decryptData("usuario", encryptedData)
                data
            } else null
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        } catch (e: NoCryptographicKeyException) {
            e.printStackTrace()
            null
        }
    }
    fun getUsuarioLogeado(): Flow<String> = myPreferencesDataStore.data.map { preferences ->
        val encryptedUser = preferences[PreferencesKeys.USUARIO_LOGEADO] ?: ""
        if (encryptedUser.isNotEmpty()) {
            try {
                cipher.decryptData("usuario", encryptedUser)
            } catch (e: Exception) {
                Log.d("E","Error en el descifrado- usuario")
                ""
            }
        } else {
            ""
        }
    }
    // retrieve the logged user's password
    suspend fun getContrasenaUsuarioLogeadoOLD(): String? {
        val encryptedData = myPreferencesDataStore.data.first()[PreferencesKeys.CONTRASENA_USUARIO_LOGEADO]
        return try {
            if (encryptedData != null) {
                val data = cipher.decryptData("contrasena", encryptedData)
                data
            } else null
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        } catch (e: NoCryptographicKeyException) {
            e.printStackTrace()
            null
        }
    }
    fun getContrasenaUsuarioLogeado(): Flow<String> = myPreferencesDataStore.data.map { preferences ->
        val encryptedPassword = preferences[PreferencesKeys.CONTRASENA_USUARIO_LOGEADO] ?: ""
        if (encryptedPassword.isNotEmpty()) {
            try {
                cipher.decryptData("contrasena", encryptedPassword)
            } catch (e: Exception) {
                Log.d("E","Error en el descifrado - contraseña")
                ""

            }
        } else {
            ""
        }
    }

}

