package com.offerus.viewModels

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.offerus.data.BusquedaPeticionServicio
import com.offerus.data.ContraseñaChange
import com.offerus.data.Deal
import com.offerus.data.PeticionId
import com.offerus.data.ServicioPeticion
import com.offerus.data.ServicioPeticionCreate
import com.offerus.data.ServicioPeticionMod
import com.offerus.data.Usuario
import com.offerus.data.UsuarioCred
import com.offerus.data.UsuarioData
import com.offerus.data.UsuarioUpdate
import com.offerus.data.ValorarDeal
import com.offerus.model.repositories.UserDataRepository
import com.offerus.utils.AuthClient
import com.offerus.utils.UserClient
import com.offerus.utils.UserExistsException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

interface LoginResultHandler {
    fun onLoginResult(success: Boolean)
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val httpAuthClient: AuthClient,
    private val httpUserClient: UserClient,
) : ViewModel(){




    //--------------------------------------------------------------//
    //-------------------------- LOGIN -----------------------------//
    //--------------------------------------------------------------//
    fun login(username: String, password: String, handler: LoginResultHandler) {

        val userCred = UsuarioCred(usuario = username, contraseña = password)
        viewModelScope.launch {
            try {
                Log.d("KTOR", "Registering...")
                httpAuthClient.authenticate(userCred)
                handler.onLoginResult(true)

            } catch (e: UserExistsException){
                Log.e("KTOR", "Usuario ya existente")
                handler.onLoginResult(false)
            } catch (e: Exception) {
                Log.e("KTOR", "Error servidor")
                handler.onLoginResult(false)
            }
        }

    }

    //--------------------------------------------------------------//
    //-------------------------- REGISTER --------------------------//
    //--------------------------------------------------------------//
    fun register(username:String, password: String, fullName: String, age: Int, email: String, phone: String, sex: String) {
        val user = Usuario(username = username, nombre_apellido = fullName, edad = age, mail = email, telefono = phone, sexo = sex, contraseña = password, descripcion = "", latitud = 0.0, longitud = 0.0, suscripciones = "")

        viewModelScope.launch {
            try {
                Log.d("KTOR", "Registering...")
                httpAuthClient.register(user)

            } catch (e: UserExistsException){
                Log.e("KTOR", "Usuario ya existente")
            } catch (e: Exception) {
                Log.e("KTOR", "Error servidor" + e.toString())
            }
        }

    }


    //--------------------------------------------------------------//
    //---------------------------- USER ----------------------------//
    //--------------------------------------------------------------//

    fun getUserData(): UsuarioData {
        var respuesta = UsuarioData("","", 0,0.0,0.0,"","","","","")
        try {
            viewModelScope.launch {
                respuesta = httpUserClient.getDatosUsuario()
                Log.e("KTOR", "Datos usuario conseguidos")
            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())

        }
        return respuesta
    }
    fun modifyPassword(password: String, newPassword: String) {
        val passwordChange = ContraseñaChange(password, newPassword)
        try {
            viewModelScope.launch {
                httpUserClient.changePassword(passwordChange)
                Log.e("KTOR", "Cambio de contrasena completado")
            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())

        }
    }
    fun updateUserData(fullName: String, age: Int, email: String, phone: String, sex: String,lat: Double, lon: Double, descr: String, suscriptions: String) {
        val update = UsuarioUpdate(fullName, age, lat, lon, email, phone, sex, descr, suscriptions)
        try {
            viewModelScope.launch {
                httpUserClient.modifyUser(update)
                Log.e("KTOR", "Datos de usuario actualizados")
            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())

        }
    }

    fun getUserProfile(username: String): Bitmap {
        var bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        try {
            viewModelScope.launch {
                bitmap = httpUserClient.descargarImagenDeUsuario(username)
                Log.e("KTOR", "Perfil de usuario conseguida")
            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())

        }
        return bitmap
    }

    fun uploadUserProfile(bitmap: Bitmap) {
        try {
            viewModelScope.launch {
                httpUserClient.uploadUserProfile(bitmap)
                Log.e("KTOR", "Perfil subido con exito")
            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())

        }
    }


    //--------------------------------------------------------------//
    //------------------------- REQUESTS ---------------------------//
    //--------------------------------------------------------------//

    fun getUserRequests(): List<ServicioPeticion> {
        var respuesta: List<ServicioPeticion> = emptyList()
        try {
            viewModelScope.launch {
                respuesta = httpUserClient.obtenerPeticionesServicioUsuario()
                Log.e("KTOR", "Peticion getMyOffers completada")
            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())

        }
        return respuesta
    }

    fun getRequests(searchText: String, categories: String, maxDistance: Double, minPrice: Double, maxPrice: Double, lat: Double, lon: Double, asc: String): List<ServicioPeticion> {
        val filter = BusquedaPeticionServicio(searchText, categories, maxDistance, minPrice, maxPrice, lat, lon, asc)
        Log.e("KTOR", searchText + categories + maxDistance.toString())
        var respuesta: List<ServicioPeticion> = emptyList()
        try {
            viewModelScope.launch {
                respuesta = httpUserClient.buscarPeticionesServicio(filter)
                Log.e("KTOR", "Peticiones recogidas con exito")
            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())

        }
        return respuesta
    }

    //--------------------------------------------------------------//
    //------------------------- EDIT REQUESTS ----------------------//
    //--------------------------------------------------------------//

    fun createRequest(title: String, descr: String, peticion: Boolean, price: Double, date: String, lat: Double, lon: Double, categories: String) {
        val request = ServicioPeticionCreate(title, descr, peticion, price, date, lat, lon, categories)
        try {
            viewModelScope.launch {
                 httpUserClient.crearPeticionServicio(request)
                Log.e("KTOR", "Peticion creada con exito")
            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())

        }

    }

    fun updateRequest(id: Int, title: String, descr: String, peticion: Boolean, price: Double, date: String, lat: Double, lon: Double, categories: String) {
        val request = ServicioPeticionMod(id, title, descr, peticion, price, date, lat, lon, categories)
        try {
            viewModelScope.launch {
                httpUserClient.updateServiceRequest(request)
                Log.e("KTOR", "Peticion actualizada con exito")
            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())

        }

    }

    fun deleteRequest(requestId: Int) {
        try {
            viewModelScope.launch {
                httpUserClient.deleteServiceRequest(requestId)
                Log.e("KTOR", "Peticion borrada con exito")
            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())

        }
    }

    //--------------------------------------------------------------//
    //---------------------------- DEALS ---------------------------//
    //--------------------------------------------------------------//

    fun createDeal (requestId: Int) {
        val request = PeticionId(requestId)
        try {
            viewModelScope.launch {
                httpUserClient.crearDeal(request)
                Log.e("KTOR", "Deals acceptado o rechazado correctamente")
            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())

        }
    }
    fun getUserDeals(): List<Deal> {
        var respuesta: List<Deal> = emptyList()
        try {
            viewModelScope.launch {
                respuesta = httpUserClient.obtenerDealsUsuario()
                Log.e("KTOR", "Deals recogidos correctamente")
            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())

        }
        return respuesta
    }

    fun dealAcceptDeny(id: Int, accept: Boolean) {
        try {
            viewModelScope.launch {
                httpUserClient.aceptarDeal(id, accept)
                Log.e("KTOR", "Deals acceptado o rechazado correctamente")
            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())

        }
    }

    fun dealRate (id: Int, mark: Int) {
        val rate = ValorarDeal(id, mark)
        try {
            viewModelScope.launch {
                httpUserClient.rateDeal(rate)
                Log.e("KTOR", "Deal valorado correctamente")
            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())

        }
    }

    //--------------------------------------------------------------//
    //------------------------ FAVORITES ---------------------------//
    //--------------------------------------------------------------//

    fun getMyFavorites(): List<ServicioPeticion> {
        var respuesta: List<ServicioPeticion> = emptyList()
        try {
            viewModelScope.launch {
                respuesta = httpUserClient.obtenerPetFavoritasUsuario()
                Log.e("KTOR", "Peticion getMyOffers completada")
            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())

        }
        return respuesta
    }

    fun addFavorite(requestId: Int) {
        val request = PeticionId(requestId)
        try {
            viewModelScope.launch {
                httpUserClient.addFavorite(request)
                Log.e("KTOR", "Peticion Favorita anadida")
            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())

        }
    }
    fun deleteFavorite(requestId: Int) {
        val request = PeticionId(requestId)
        try {
            viewModelScope.launch {
                httpUserClient.deleteFavorite(request)
                Log.e("KTOR", "Peticion Favorita anadida")
            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())

        }
    }

}