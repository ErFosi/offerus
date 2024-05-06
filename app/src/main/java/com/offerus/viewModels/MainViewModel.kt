package com.offerus.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.offerus.data.ServicioPeticion
import com.offerus.data.Usuario
import com.offerus.data.UsuarioCred
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
    //------------------------- CREAR OFERTAS ----------------------//
    //--------------------------------------------------------------//
    fun createOffer() {

    }


    //--------------------------------------------------------------//
    //--------------------- CREAR SOLICITUDES ----------------------//
    //--------------------------------------------------------------//
    fun createRequest() {

    }



    //--------------------------------------------------------------//
    //-------------------------- BUSCAR ----------------------------//
    //--------------------------------------------------------------//


    //--------------------------------------------------------------//
    //------------------------- OFFERS -----------------------------//
    //--------------------------------------------------------------//

    fun getMyOffers(): List<ServicioPeticion> {
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


}