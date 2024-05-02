package com.offerus.viewModels

import androidx.lifecycle.ViewModel
import com.offerus.data.Usuario
import com.offerus.model.repositories.UserDataRepository
import com.offerus.utils.AuthClient
import com.offerus.utils.UserClient
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.HttpClient
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val httpAuthClient: AuthClient,
    private val httpUserClient: UserClient,
) : ViewModel(){



//--------------------------------------------------------------//
//------------------------- CREAR OFERTAS ----------------------//
//--------------------------------------------------------------//



//--------------------------------------------------------------//
//--------------------- CREAR SOLICITUDES ----------------------//
//--------------------------------------------------------------//




//--------------------------------------------------------------//
//-------------------------- BUSCAR ----------------------------//
//--------------------------------------------------------------//



//--------------------------------------------------------------//
//-------------------------- LOGIN -----------------------------//
//--------------------------------------------------------------//
fun login() {}

//--------------------------------------------------------------//
//-------------------------- REGISTER --------------------------//
//--------------------------------------------------------------//
fun register(username:String, fullName: String, age: Int, email: String, phone: String, sex: String) {
    //val user = Usuario(username = username, nombre_apellido = fullName, edad = age, mail = email, telefono = phone, sexo = sex)
}


}