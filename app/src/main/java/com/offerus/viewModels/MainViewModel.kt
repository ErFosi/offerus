package com.offerus.viewModels

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.offerus.Idioma
import com.offerus.MyPreferencesDataStore
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
import com.offerus.utils.AuthenticationException
import com.offerus.utils.CambioDeIdioma
import com.offerus.utils.UserClient
import com.offerus.utils.UserExistsException
import com.offerus.utils.createDealListExample
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

interface LoginResultHandler {
    fun onLoginResult(success: Boolean)
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val httpAuthClient: AuthClient,
    private val httpUserClient: UserClient,
    private val myPreferencesDataStore: MyPreferencesDataStore,
    private val cambioDeIdioma: CambioDeIdioma,
) : ViewModel(){

    var listaDeals = mutableListOf<Deal>()
    var listaEntrantes = mutableListOf<Deal>()
    var listaSalientes = mutableListOf<Deal>()

    init {
        actualizarListaDeals()
    }
    // variable to store the current user name
    var usuario by mutableStateOf("")

    // get the theme and language from the data store
    val tema = myPreferencesDataStore.preferencesStatusFlow.map {
        it.temaClaro
    }
    val idioma = myPreferencesDataStore.preferencesStatusFlow.map {
        it.idioma
    }

    /**
     * get and set the user and password on the datastore
     * @return the user that is logged in
     */
    fun obtenerUsuarioLogeado(): String {
        Log.d("login", "obteniendo usuario logeado")
        var usuario = ""
        runBlocking{
            usuario = myPreferencesDataStore.getUsuarioLogeado().first()
            Log.d("login", "usuario logeado $usuario")
        }
        Log.d("login", "usuario logeado que se returnea $usuario")
        return usuario
    }

    /**
     * save the user and password on the datastore
     * @param usuario the user to be saved
     * @param contrasena the password to be saved
     */
    fun setUsuarioLogueado(usuario: String, contrasena: String) = viewModelScope.launch { myPreferencesDataStore.setUsuarioContraLogeado(usuario, contrasena) }

    /**
     * function to automatically get the token with the saved user credentials
     * if the user has saved credentials, the app will try to authenticate the user
     */
    fun loginUsuarioGuardado(){
        viewModelScope.launch {
            val usuariog: String = myPreferencesDataStore.getUsuarioLogeado().first()
            val contrasena: String = myPreferencesDataStore.getContrasenaUsuarioLogeado().first()
            Log.d("SeriesViewModel", "Usuario: $usuariog, Contrasena: $contrasena")
            usuario = usuariog
            if (usuariog != "" && contrasena != ""){
                try {
                    //authenticate(usuariog, contrasena)
                    Log.d("SeriesViewModel", "Usuario guardado autenticado")
                } catch (e: AuthenticationException){
                    Log.e("SeriesViewModel", "Error al autenticar usuario guardado")
                }
            }
        }
    }

    /**
     * logout the user
     */
    fun logout(){
        viewModelScope.launch {
            myPreferencesDataStore.setUsuarioContraLogeado("", "")
        }
    }

    /**
     * update the theme of the app
     * @param theme true if dark theme, false if white theme
     */
    fun updateTheme(theme: Boolean){
        viewModelScope.launch {
            myPreferencesDataStore.updateTheme(theme)
        }
    }

    /**
     * Update the selected language on the preferences data store and update the app language
     * @param idioma new language
     */
    fun updateIdioma(idioma: Idioma, context: Context){
        cambioDeIdioma.cambiarIdioma(idioma, context)
        viewModelScope.launch {
            myPreferencesDataStore.updateIdioma(idioma)
        }
    }



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
                peticion = true,
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