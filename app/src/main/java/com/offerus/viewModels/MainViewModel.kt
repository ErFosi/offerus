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
import com.offerus.model.repositories.ServicioRepository
import com.offerus.model.repositories.UserDataRepository
import com.offerus.utils.AuthClient
import com.offerus.utils.AuthenticationException
import com.offerus.utils.BiometricAuthenticationStatus
import com.offerus.utils.CambioDeIdioma
import com.offerus.utils.ContraseñaNoCoincideException
import com.offerus.utils.UserClient
import com.offerus.utils.UserExistsException
import com.offerus.utils.showToastOnMainThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.math.*

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val httpAuthClient: AuthClient,
    private val httpUserClient: UserClient,
    private val myPreferencesDataStore: MyPreferencesDataStore,
    private val cambioDeIdioma: CambioDeIdioma,
    private val servicioRepository: ServicioRepository,
) : ViewModel() {


    // pull refresh states
    var isRefreshingHome = mutableStateOf(false)
    var isRefreshingMyOffers = mutableStateOf(false)
    var isRefreshingSearch = mutableStateOf(false)
    var isRefreshingFavorites = mutableStateOf(false)

    // Recordar subpestañas
    var selectedTabIndexHome =
        mutableIntStateOf(0) // Estados para almacenar la pestaña seleccionada
    var selectedTabIndexMyOffers = mutableIntStateOf(0)
    var editDialog = mutableStateOf(false)
    var editPeticion = mutableStateOf<ServicioPeticion?>(null)

    var listaDeals = mutableStateOf(emptyList<Deal>())
    var listaEntrantes = mutableStateOf(emptyList<Deal>())
    var listaSalientes = mutableStateOf(emptyList<Deal>())
    var listaValoracionesPendientes = mutableStateOf(emptyList<Deal>())

    var listaMisOfertas = mutableStateOf(emptyList<ServicioPeticion>())
    var listaMisPeticiones = mutableStateOf(emptyList<ServicioPeticion>())

    // Variables pantalla search
    var cargaInicialPeticiones = mutableStateOf(false)
    var listaSolicitudes = mutableStateOf(emptyList<ServicioPeticion>())
    var listaOfertas = mutableStateOf(emptyList<ServicioPeticion>())
    var ordenAscendenteSearch = mutableStateOf(true)

    // Variables pantalla favoritos
    var cargaInicialPeticionesFavoritas = mutableStateOf(false)
    var listaSolicitudesFavoritas = mutableStateOf(emptyList<ServicioPeticion>())
    var listaOfertasFavoritas = mutableStateOf(emptyList<ServicioPeticion>())
    var ordenAscendenteFavoritas = mutableStateOf(true)

    var selectedTabIndex by mutableStateOf(0)

    //huella

    var biometricAuthenticationStatus: BiometricAuthenticationStatus by mutableStateOf( BiometricAuthenticationStatus.NOT_AUTHENTICATED_YET)
    fun iniciarListas() {
        Log.d("iniciarListas", "INICIO - iniciando listas...")
        actualizarListaDeals()
        obtenerMisOfertas()
        Log.d("iniciarListas", "FIN - iniciado listas")
    }

    fun obtenerMisOfertas() {
        viewModelScope.launch {
            isRefreshingMyOffers.value = true
            delay(500)
            try {
                val listaServicios = httpUserClient.obtenerPeticionesServicioUsuario()
                Log.d("lista mis servicios", listaServicios.toString())

                val listaActualO = mutableListOf<ServicioPeticion>()
                val listaActualP = mutableListOf<ServicioPeticion>()

                listaServicios.forEach { servicio ->
                    if (servicio.peticion) {
                        listaActualP.add(servicio)
                    } else {
                        listaActualO.add(servicio)
                    }
                }

                listaMisOfertas.value = listaActualO
                listaMisPeticiones.value = listaActualP
                isRefreshingMyOffers.value = false

            } catch (e: Exception) {
                isRefreshingMyOffers.value = false

                // Manejar cualquier excepción
                Log.e("obtenerMisOfertas", "Error al obtener las ofertas: $e")
            }
        }
    }

    // variable to store the current user name
    var usuario by mutableStateOf("")
    var infoUsuario = mutableStateOf(UsuarioData("", "", 0, 0.0, 0.0, "", "", "", "", ""))

    // get the theme and language from the data store
    val tema = myPreferencesDataStore.preferencesStatusFlow.map {
        it.temaClaro
    }
    val idioma = myPreferencesDataStore.preferencesStatusFlow.map {
        it.idioma
    }

    // selected language
    val idiomaActual by cambioDeIdioma::idiomaActual

    /**
     * get and set the user and password on the datastore
     * @return the user that is logged in
     */
    fun obtenerUsuarioLogeado(): String {
        Log.d("login", "obteniendo usuario logeado")
        var usuario = ""
        runBlocking {
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
    fun setUsuarioLogueado(usuario: String, contrasena: String) = viewModelScope.launch {
        Log.d("login", "guardando usuario logeado")
        myPreferencesDataStore.setUsuarioContraLogeado(
            usuario,
            contrasena
        )
    }

    /**
     * function to automatically get the token with the saved user credentials
     * if the user has saved credentials, the app will try to authenticate the user
     */
    fun loginUsuarioGuardado() {
        viewModelScope.launch {
            val usuariog: String = myPreferencesDataStore.getUsuarioLogeado().first()
            val contrasena: String = myPreferencesDataStore.getContrasenaUsuarioLogeado().first()
            Log.d("SeriesViewModel", "Usuario: $usuariog, Contrasena: $contrasena")
            usuario = usuariog
            if (usuariog != "" && contrasena != "") {
                try {
                    //authenticate(usuariog, contrasena)
                    login(usuariog, contrasena)
                    Log.d("SeriesViewModel", "Usuario guardado autenticado")
                    actualizarInfoUsuario()
                    iniciarListas()
                } catch (e: AuthenticationException) {
                    Log.e("SeriesViewModel", "Error al autenticar usuario guardado")
                }
            }
        }
    }

    /**
     * logout the user
     */
    fun logout() {
        viewModelScope.launch {
            myPreferencesDataStore.setUsuarioContraLogeado("", "")
        }
    }

    /**
     * update the theme of the app
     * @param theme true if dark theme, false if white theme
     */
    fun updateTheme(theme: Boolean) {
        viewModelScope.launch {
            myPreferencesDataStore.updateTheme(theme)
        }
    }

    /**
     * Update the selected language on the preferences data store and update the app language
     * @param idioma new language
     */
    fun updateIdioma(idioma: Idioma, context: Context) {
        cambioDeIdioma.cambiarIdioma(idioma, context)
        viewModelScope.launch {
            myPreferencesDataStore.updateIdioma(idioma)
        }
    }

    /**
     * Update the selected language on the preferences data store and update the app language
     * @param idioma new language
     */
    fun reloadLang(idioma: Idioma, context: Context) = cambioDeIdioma.cambiarIdioma(idioma, context, false)




    //--------------------------------------------------------------//
    //-------------------------- LOGIN -----------------------------//
    //--------------------------------------------------------------//
    /**
     * Login function. Throws an exception if the credentials are not correct
     * @param username the username of the user
     * @param password the password of the user
     * @throws AuthenticationException if the credentials are not correct
     * @throws Exception if there is a problem with the server
     */
    @Throws(AuthenticationException::class, Exception::class)
    suspend fun login(username: String, password: String) {

        val userCred = UsuarioCred(usuario = username, contraseña = password)
        httpAuthClient.authenticate(userCred)
    }

    //--------------------------------------------------------------//
    //-------------------------- REGISTER --------------------------//
    //--------------------------------------------------------------//
    /**
     * Register function. Throws an exception if the user already exists
     * @param username the username of the user
     * @param password the password of the user
     * @param fullName the full name of the user
     * @param age the age of the user
     * @param email the email of the user
     * @param phone the phone of the user
     * @param sex users gender
     * @throws UserExistsException if the user already exists
     * @throws Exception if there is a problem with the server
     */
    @Throws(UserExistsException::class, Exception::class)
    suspend fun register(username:String, password: String, fullName: String, age: Int, email: String, phone: String, sex: String, latitud: Double, longitud: Double) {
        val user = Usuario(username = username, nombre_apellido = fullName, edad = age, mail = email, telefono = phone, sexo = sex, contraseña = password, descripcion = "", latitud = 0.0, longitud = 0.0, suscripciones = "")
        httpAuthClient.register(user)

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

    fun actualizarInfoUsuario(){
        try {
            viewModelScope.launch {
                infoUsuario.value = httpUserClient.getDatosUsuario()
                Log.e("KTOR", "Datos usuario conseguidos")
            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())

        }
    }

    @Throws(ContraseñaNoCoincideException::class, Exception::class)
    suspend fun modifyPassword(password: String, newPassword: String) {
        val passwordChange = ContraseñaChange(password, newPassword)

        httpUserClient.changePassword(passwordChange)
        Log.e("KTOR", "Cambio de contrasena completado")

    }
    fun updateUserData(fullName: String, age: Int, email: String, phone: String, sex: String,lat: Double, lon: Double, descr: String, suscriptions: String) {
        val update = UsuarioUpdate(fullName, age, lat, lon, email, phone, sex, descr, suscriptions)
        try {
            viewModelScope.launch {
                httpUserClient.modifyUser(update)
                Log.e("KTOR", "Datos de usuario actualizados")
                actualizarInfoUsuario()
            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())

        }
    }

    suspend fun getUserProfile(username: String): Bitmap {
        return suspendCancellableCoroutine { continuation ->
            try {
                viewModelScope.launch {
                    val bitmap = httpUserClient.descargarImagenDeUsuario(username)
                    Log.e("viewmodel", "Perfil de usuario conseguida")
                    continuation.resume(bitmap)
                }
            } catch (e: Exception) {
                Log.e("KTOR", e.toString())
                val defaultBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
                continuation.resume(defaultBitmap)
            }
        }
    }

    fun uploadUserProfile(bitmap: Bitmap) {
        try {
            viewModelScope.launch {
                Log.d("uploadUserProfile", "uploading profile")
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

    fun getRequests(searchText: String?, categories: String?, maxDistance: Double?, minPrice: Double?, maxPrice: Double?, asc: String) {
        val filter = BusquedaPeticionServicio(searchText, categories, maxDistance, minPrice, maxPrice, 0.0, 0.0, "precio_asc")
        Log.e("peticion", " $searchText , $maxDistance , $maxPrice")
        var respuesta: List<ServicioPeticion>
        try {
            viewModelScope.launch {
                isRefreshingSearch.value = true
                respuesta = httpUserClient.buscarPeticionesServicio(filter).toMutableList()
                respuesta = respuesta.sortedBy { it.precio }

                listaSolicitudes.value = respuesta.filter { it.peticion }
                listaOfertas.value = respuesta.filter { !it.peticion }

                ordenarServicios(ordenAscendenteSearch.value)


                Log.e("KTOR", "Peticiones recogidas con exito")
                Log.e("KTOR", listaOfertas.value.size.toString())
                Log.e("KTOR", listaSolicitudes.value.size.toString())

            isRefreshingSearch.value = false

            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())
            isRefreshingSearch.value = false

        }
    }

    fun cargarListasPeticiones(){
        val filter = BusquedaPeticionServicio(null, null, null, null, null, null, null, "precio_asc")
        var respuesta: List<ServicioPeticion>
        try {
            viewModelScope.launch {
                isRefreshingSearch.value = true
                respuesta = httpUserClient.buscarPeticionesServicio(filter).toMutableList()
                respuesta = respuesta.sortedBy { it.precio }

                listaSolicitudes.value = respuesta.filter { it.peticion }
                listaOfertas.value = respuesta.filter { !it.peticion }

                Log.e("KTOR", "Peticiones recogidas con exito")
                Log.e("KTOR", listaOfertas.value.size.toString())
                Log.e("KTOR", listaSolicitudes.value.size.toString())


                cargaInicialPeticiones.value = true
                isRefreshingSearch.value = false


            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())
            isRefreshingSearch.value = false

        }

    }

    fun ordenarServicios(asc: Boolean){

        if (asc){
            listaOfertas.value = listaOfertas.value.sortedBy { it.precio }
            listaSolicitudes.value = listaSolicitudes.value.sortedBy { it.precio }
        } else {
            listaOfertas.value = listaOfertas.value.sortedByDescending { it.precio }
            listaSolicitudes.value = listaSolicitudes.value.sortedByDescending { it.precio }
        }
    }

    fun ordenarServiciosFavoritos(asc: Boolean){

        if (asc){
            listaOfertasFavoritas.value = listaOfertasFavoritas.value.sortedBy { it.precio }
            listaSolicitudesFavoritas.value = listaSolicitudesFavoritas.value.sortedBy { it.precio }
        } else {
            listaOfertasFavoritas.value = listaOfertasFavoritas.value.sortedByDescending { it.precio }
            listaSolicitudesFavoritas.value = listaSolicitudesFavoritas.value.sortedByDescending { it.precio }
        }
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


    // servicio para mostrar detalles
    var servicioDetalle = mutableStateOf<ServicioPeticion?>(null)
    var infoUsuarioDetalle = mutableStateOf<UsuarioData?>(null)
    var listaServiciosApi = mutableStateOf(emptyList<ServicioPeticion>())

    // gestor del dialogo para hacer la review
    var dialogoReview = mutableStateOf(false)
    var dealReview: Deal? = null
    fun actualizarListaDeals() {

        viewModelScope.launch {
            isRefreshingHome.value = true
            delay(500)
            try {
                // Actualizar listaDeals
                val nuevasDeals = httpUserClient.obtenerDealsUsuario().toMutableList()

                // Obtener todas los id de las peticiones de servicio
                val listaIdPeticiones = nuevasDeals.map { it.id_peticion }
                Log.d("lista petis", listaIdPeticiones.toString())


                // Filtrar deals entrantes y actualizar listaEntrantes
                val nuevasEntrantes =
                    nuevasDeals.filter { it.username_host == usuario && it.estado == "pendiente" }
                        .toMutableList()
                val nuevasValoracionesPendientes =
                    nuevasDeals.filter { it.username_host == usuario && it.estado == "aceptado" }
                        .toMutableList()
                Log.d("lista entrantes", nuevasEntrantes.toString())

                // Filtrar deals salientes y actualizar listaSalientes
                val nuevasSalientes =
                    nuevasDeals.filter { it.username_cliente == usuario }.toMutableList()
                Log.d("lista salientes", nuevasSalientes.toString())

                // Actualizar los MutableState con las nuevas listas filtradas
                listaDeals.value = nuevasDeals
                listaEntrantes.value = nuevasEntrantes
                listaValoracionesPendientes.value = nuevasValoracionesPendientes
                listaSalientes.value = nuevasSalientes

                if (listaIdPeticiones.isNotEmpty()) {
                    withContext(Dispatchers.IO) {
                        servicioRepository.deleteServicio()
                        listaServiciosApi.value =
                            httpUserClient.obtenerPeticiones(listaIdPeticiones)
                        listaServiciosApi.value.forEach { peticion ->
                            Log.d("peticion add", peticion.toString())
                            servicioRepository.addServicio(peticion)
                        }
                        isRefreshingHome.value = false
                    }
                } else {
                    isRefreshingHome.value = false
                }

            } catch (e: Exception) {
                // Manejar cualquier excepción
                Log.e("actualizarListaDeals", "Error al actualizar las ofertas: $e")
                isRefreshingHome.value = false
            }

        }

    }

    var listaPeticiones = servicioRepository.getListaServicios()


    fun cambiarServicioDetalle(idPeticion: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                servicioDetalle.value = servicioRepository.getServicio(idPeticion)
                obtenerInfoUsuario(servicioDetalle.value!!.username)
            }
        }
    }

    fun cambiarServicioDetalle(servicioPeticion: ServicioPeticion) {
        servicioDetalle.value = servicioPeticion

    }
    //--------------------------------------------------------------//
    //------------------------- EDIT REQUESTS ----------------------//
    //--------------------------------------------------------------//

    fun createRequest(
        title: String,
        descr: String,
        peticion: Boolean,
        price: Double,
        date: String,
        lat: Double,
        lon: Double,
        categories: String,
        context: Context
    ) {
        val request =
            ServicioPeticionCreate(title, descr, peticion, price, date, lat, lon, categories)
        Log.d("createRequest", request.toString())
        try {
            viewModelScope.launch {
                httpUserClient.crearPeticionServicio(request)
                Log.e("KTOR", "Peticion creada con exito")
                showToastOnMainThread(context, "Servicio creado con exito")
                obtenerMisOfertas()
            }
        } catch (e: Exception) {
            showToastOnMainThread(context, "Ha ocurrido un error")
            Log.e("KTOR", e.toString())

        }

    }

    fun updateRequest(
        id: Int,
        title: String,
        descr: String,
        peticion: Boolean,
        price: Double,
        date: String,
        lat: Double,
        lon: Double,
        categories: String
    ) {
        val request =
            ServicioPeticionMod(id, title, descr, peticion, price, date, lat, lon, categories)
        try {
            viewModelScope.launch {
                httpUserClient.updateServiceRequest(request)
                Log.e("KTOR", "Peticion actualizada con exito")
                obtenerMisOfertas()
            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())

        }

    }

    fun deleteRequest(requestId: Int, context: Context) {
        try {
            viewModelScope.launch {
                httpUserClient.deleteServiceRequest(requestId)
                Log.d("KTOR", "Peticion borrada con exito")
                showToastOnMainThread(context, "Peticion borrada con exito")
                obtenerMisOfertas()

            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())

        }
    }

    //--------------------------------------------------------------//
    //---------------------------- DEALS ---------------------------//
    //--------------------------------------------------------------//

    fun createDeal(requestId: Int) {
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
                actualizarListaDeals()
            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())

        }
    }

    fun dealRate(deal: Deal) {
        var nota: Int
        if (deal.username_cliente == usuario) {
            nota = deal.nota_cliente
        } else {
            nota = deal.nota_host
        }

        if (nota == -1) {
            return
        }
        val rate = ValorarDeal(deal.id, nota)
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

                listaSolicitudesFavoritas.value = respuesta.filter { it.peticion }
                listaOfertasFavoritas.value = respuesta.filter { !it.peticion }

                ordenarServiciosFavoritos(ordenAscendenteFavoritas.value)

                Log.e("KTOR", "Peticion get favoritas completada ")

                if (!cargaInicialPeticionesFavoritas.value){
                    cargaInicialPeticionesFavoritas.value = true
                }
            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())

        }
        return respuesta
    }

    fun addFavorite(requestId: Int, servicioPeticion: ServicioPeticion) {
        val request = PeticionId(requestId)
        try {
            viewModelScope.launch {
                httpUserClient.addFavorite(request)
                Log.e("KTOR", "Peticion Favorita anadida")
                if ( servicioPeticion.peticion) {
                    listaSolicitudesFavoritas.value = listaSolicitudes.value + servicioPeticion
                } else {
                    listaOfertasFavoritas.value = listaOfertasFavoritas.value + servicioPeticion
                }
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
                listaSolicitudesFavoritas.value =
                    listaSolicitudesFavoritas.value.toMutableList().apply {
                        removeAll { it.id == requestId }
                    }

                listaOfertasFavoritas.value =
                    listaOfertasFavoritas.value.toMutableList().apply {
                        removeAll { it.id == requestId }
                    }
            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())

        }
    }

    fun filtrarFavoritas(titulo: String?,
                         categoria: String?,
                         distanciaMaxima: Double?,
                         precioMinimo: Double?,
                         precioMaximo: Double?
    ){

        var resultado: List<ServicioPeticion>
        try {
            viewModelScope.launch {
                isRefreshingFavorites.value = true
                resultado = httpUserClient.obtenerPetFavoritasUsuario()

                //Log.e("KTOR", "Inicio filtrado")
                //Log.e("KTOR", resultado.size.toString())
                if (titulo != null) {
                    if (titulo.isNotEmpty()) {
                        resultado = resultado.filter { it.titulo.contains(titulo) }
                        //Log.e("KTOR", titulo)
                        //Log.e("KTOR", resultado.size.toString())

                    }
                }
                if (categoria != null) {
                    if (!categoria.contains(",")) {
                        resultado = resultado.filter { it.categorias.contains(categoria) }
                        //Log.e("KTOR", categoria)
                        //Log.e("KTOR", resultado.size.toString())
                    }
                }
                //TODO filtrar por distancia maxima

                if (distanciaMaxima !=null){
                    resultado = resultado.filter { estanEnDistancia(it.latitud, it.longitud, infoUsuario.value.latitud,infoUsuario.value.longitud, distanciaMaxima) }
                }

                if (precioMinimo !=null){
                    resultado = resultado.filter { it.precio >= precioMinimo }
                }

                if (precioMaximo !=null){
                    resultado = resultado.filter { it.precio <= precioMaximo }
                }



                listaSolicitudesFavoritas.value = resultado.filter { it.peticion }
                listaOfertasFavoritas.value = resultado.filter { !it.peticion }

                Log.e("KTOR", "Peticion getMyOffers completada")

                if (!cargaInicialPeticionesFavoritas.value){
                    cargaInicialPeticionesFavoritas.value = true
                }
                isRefreshingFavorites.value = false


            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())
            isRefreshingFavorites.value = false
        }


    }

    fun estanEnDistancia(latitudPunto1: Double, longitudPunto1: Double, latitudPunto2: Double, longitudPunto2: Double, distanciaLimiteKm: Double): Boolean {
        val radioTierra = 6371 // Radio de la Tierra en kilómetros

        val latitud1 = Math.toRadians(latitudPunto1)
        val latitud2 = Math.toRadians(latitudPunto2)
        val longitud1 = Math.toRadians(longitudPunto1)
        val longitud2 = Math.toRadians(longitudPunto2)

        val diferenciaLatitud = latitud2 - latitud1
        val diferenciaLongitud = longitud2 - longitud1

        val a = sin(diferenciaLatitud / 2).pow(2) + cos(latitud1) * cos(latitud2) * sin(diferenciaLongitud / 2).pow(2)
        val distancia = 2 * atan2(sqrt(a), sqrt(1 - a))

        val distanciaEntrePuntos = radioTierra * distancia
        return distanciaEntrePuntos <= distanciaLimiteKm
    }

    fun esPeticionFavorita(id: Int): Boolean{
        return (listaOfertasFavoritas.value.any { it.id == id } || listaSolicitudesFavoritas.value.any { it.id == id })


    }

    // obtener info cualquier usuario
    fun obtenerInfoUsuario(username: String) {
        try {
            viewModelScope.launch {
                infoUsuarioDetalle.value = httpUserClient.getDatosCualquierUsuario(username)
                Log.e("KTOR", "Datos usuario conseguidos")
            }
        } catch (e: Exception) {
            Log.e("KTOR", e.toString())

        }
    }

    // obtener valoracion media
    suspend fun valoracionMedia(username: String): Pair<Int, Double> {
        val valoracion = httpUserClient.obtenerNotaUsuario(username)
        return Pair(valoracion.cant, valoracion.nota)
        Log.e("KTOR", "Valoracion media conseguida")
    }


}