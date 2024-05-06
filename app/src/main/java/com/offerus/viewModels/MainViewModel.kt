package com.offerus.viewModels

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.offerus.data.Deal
import com.offerus.data.ServicioPeticion
import com.offerus.model.repositories.UserDataRepository
import com.offerus.utils.AuthClient
import com.offerus.utils.UserClient
import com.offerus.utils.createDealListExample
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val httpAuthClient: AuthClient,
    private val httpUserClient: UserClient,
) : ViewModel() {

    var listaDeals = mutableListOf<Deal>()
    var listaEntrantes = mutableListOf<Deal>()
    var listaSalientes = mutableListOf<Deal>()

    init {
        actualizarListaDeals()
    }


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
    fun register(
        username: String,
        fullName: String,
        age: Int,
        email: String,
        phone: String,
        sex: String
    ) {
        //val user = Usuario(username = username, nombre_apellido = fullName, edad = age, mail = email, telefono = phone, sexo = sex)
    }
    //----------------- HOME SCREEN ---------------//

    // Recordar subpestañas
    var selectedTabIndexHome = mutableIntStateOf(0) // Estado para almacenar la pestaña seleccionada

    // servicio para mostrar detalles
    var servicioDetalle = mutableStateOf<ServicioPeticion?>(null)

    // gestor del dialogo para hacer la review
    var dialogoReview = mutableStateOf(false)
    var dealReview: Deal? = null
    fun actualizarListaDeals() {
        viewModelScope.launch {
            // Actualizar listaDeals
            //listaDeals = httpUserClient.obtenerDealsUsuario().toMutableList()
            listaDeals = createDealListExample().toMutableList()
            Log.d("lista", listaDeals.toString())
            // Filtrar deals entrantes y actualizar listaEntrantes
            listaEntrantes = listaDeals.filter { it.username_host == "cuadron11" }.toMutableList()

            // Filtrar deals salientes y actualizar listaSalientes
            listaSalientes =
                listaDeals.filter { it.username_cliente == "cuadron11" }.toMutableList()
        }
    }


    //metodo para actualizar (update) el deal cuando se hace una review
    fun enviarReview() {
        if (dealReview == null) return
        Log.d("review", dealReview.toString())
        //httpUserClient.enviarReview(dealReview!!)
    }

    fun cambiarServicioDetalle(idPeticion: Int) {
        viewModelScope.launch {
            //servicioDetalle.value = httpUserClient.verPeticion(idPeticion)
            servicioDetalle.value = ServicioPeticion(
                id = 1,
                username = "cuadron11",
                titulo = "Servicio Profesional de Desarrollo Web de paginas de deportes",
                descripcion = "Desarrollo de paginas web de deportes con las ultimas tecnologias",
                precio = 10.0,
                fecha = "12/12/2021",
                latitud = 43.2628005,
                longitud = -2.9479811,
                categorias = listOf("Deportes", "Gratis").toString()
            )
        }
    }

    // enviar notificacion
    fun crearDeal(idPeticion: Int, usernameHost: String, usernameCliente: String) {
        viewModelScope.launch {
            //httpUserClient.crearDeal(DealPeticion(1))
            Log.d("deal", "deal creado")
        }
    }


}