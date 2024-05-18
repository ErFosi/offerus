package com.offerus.model.repositories

import android.graphics.Bitmap
import android.util.Log
import com.offerus.MyPreferencesDataStore
import com.offerus.data.Usuario
import com.offerus.data.UsuarioCred
import com.offerus.model.database.daos.UserDao
import com.offerus.utils.UserClient
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataRepository @Inject constructor(
    private val userDao: UserDao,
    //private val datastore: PreferencesDataStore,
    //private val authenticationClient: AuthenticationClient,
    private val myPreferencesDataStore: MyPreferencesDataStore,
    private val userClient: UserClient
) {

    private lateinit var profileImage: Bitmap

    suspend fun addUserData(user: Usuario) = userDao.addUser(user)
    suspend fun updateUser(user: Usuario) {
        userDao.updateUser(user)
    }

    fun getUserData(username: String) = userDao.getUserData(username)

    fun checkUsernameExists(username: String) = userDao.checkUsernameExists(username)
@Serializable
    data class DealInfo(
        val nombrePeticion: String,
        val estado: String
    )
    @Serializable
    data class PendingDealInfo(
        val username: String,
        val titulo: String
    )
    suspend fun getDatosWidget(): Pair<List<DealInfo>?, List<PendingDealInfo>?> {
        val usuario: String = myPreferencesDataStore.getUsuarioLogeado().first()
        val contrasena: String = myPreferencesDataStore.getContrasenaUsuarioLogeado().first()
        Log.d("Widget", "metodo getDatosWidget"+ usuario+"passw: " +contrasena)
        if (usuario.isEmpty()) {
            return Pair(null, null)
        }
        userClient.authenticate(UsuarioCred(usuario, contrasena))
        val deals= userClient.obtenerDealsUsuario()
        Log.d("Widget", "metodo getDatosWidget"+ deals.toString())
        val clienteDeals = deals.filter { it.username_cliente == usuario }.map { deal ->
            DealInfo(
                nombrePeticion = userClient.verPeticion(deal.id_peticion).titulo,
                estado = deal.estado
            )
        }


        val pendingHostDeals = deals.filter { it.username_host == usuario && it.estado == "pendiente" }.map { deal ->
            PendingDealInfo(
                username = deal.username_cliente,
                titulo = userClient.verPeticion(deal.id_peticion).titulo
            )
        }

        return Pair(clienteDeals, pendingHostDeals)
    }
    //DATASTORE
    //change language preferences
    //suspend fun changeUserLanguage(username: String, lang: String) = datastore.setUserLanguage(username, lang)


    //API
    //@Throws(AuthenticationException::class)
    //suspend fun authenticateUser(user: AuthUser): Boolean {
    //    return try {
    //        authenticationClient.authenticate(user)
    //        true
    //    } catch (e: AuthenticationException) {
    //        false
    //    }
    //}
}