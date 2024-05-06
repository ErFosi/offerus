package com.offerus.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.offerus.data.BusquedaPeticionServicio
import com.offerus.data.ContraseñaChange
import com.offerus.data.Deal
import com.offerus.data.DealPeticionAceptar
import com.offerus.data.ErrorResponse
import com.offerus.data.PeticionId
import com.offerus.data.PeticionesRequestBody
import com.offerus.data.ServicioPeticion
import com.offerus.data.ServicioPeticionCreate
import com.offerus.data.ServicioPeticionMod
import com.offerus.data.UsuarioCred
import com.offerus.data.UsuarioData
import com.offerus.data.UsuarioUpdate
import com.offerus.data.ValorarDeal
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.accept
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readBytes
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/************************************************************************
 * Clase singleton que se encarga de mandar todas las peticiones a la API
 *************************************************************************/

@Singleton
class UserClient @Inject constructor() {
    private val clienteHttp = HttpClient(OkHttp) {
        expectSuccess = true

        install(ContentNegotiation) {
            json(Json {
                // Configura JSON para manejar adecuadamente las propiedades que podrían ser nulas
                isLenient = true
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
        engine {
            config {
                // Configura específicamente el cliente OkHttp si es necesario
                connectTimeout(30, TimeUnit.SECONDS)
                readTimeout(30, TimeUnit.SECONDS)
                writeTimeout(30, TimeUnit.SECONDS)
            }
        }

        HttpResponseValidator {
            handleResponseExceptionWithRequest { exception, _ ->
                when {
                    exception is ClientRequestException && exception.response.status == HttpStatusCode.Unauthorized -> throw AuthenticationException()
                    exception is ClientRequestException && exception.response.status == HttpStatusCode.BadRequest -> throw UserExistsException()
                    exception is ClientRequestException && exception.response.status == HttpStatusCode.UnprocessableEntity -> throw UnprocessableEntityException()
                    else -> {
                        exception.printStackTrace()
                        throw exception
                    }
                }
            }
        }
    }


    /************************************************************************
     * Función que manda usuario y contraseña al servidor, emite dos errores
     * AuthenticationException y Exception, cuando se da el primero los credenciales
     * son incorrectos, si no se asume que no se ha podido entablecer conexión
     * con el servidor.
     *
     * Además, si se consigue hacer login se almacena el token Oauth (Última linea)
     *************************************************************************/

    /**
    * @Description: Función que manda usuario y contraseña al servidor
    * @Param user: UsuarioCred
    * @Throws AuthenticationException
    * @Throws Exception
    * @Return Unit
     */
    @Throws(AuthenticationException::class, Exception::class)
    suspend fun authenticate(user: UsuarioCred) {
        val tokenInfo: TokenInfo = clienteHttp.submitForm(
            url = "https://offerus.zapto.org/token",
            formParameters = Parameters.build {
                append("grant_type", "password")
                append("username", user.usuario)
                append("password", user.contraseña)
            }).body()

        bearerTokenStorage.add(BearerTokens(tokenInfo.accessToken, tokenInfo.refreshToken))
    }



    /************************************************************************
     * Peticiones relacionadas a las fotos de perfil
     *************************************************************************/


    /**
     *@Description: Función que sube la foto de perfil del usuario
    * @Param image: Bitmap
    * @Return Unit
    * */
    @Throws(AuthenticationException::class, Exception::class)
    suspend fun uploadUserProfile(image: Bitmap) {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 75, stream)
        val byteArray = stream.toByteArray()
        val token = bearerTokenStorage.last().accessToken

        clienteHttp.submitFormWithBinaryData(
            url = "https://offerus.zapto.org/profile/image",
            formData = formData {
                append("file", byteArray, Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=profile_image.jpg")
                })
            }
        ) {
            method = HttpMethod.Post
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }

    /************************************************************************
     * Función que, dado el username descarga la imagen de perfil de un usuario
     * @Param usuario: String
     * @Throws Exception
     * @Return Bitmap
     * */
    @Throws(AuthenticationException::class, Exception::class)
    suspend fun descargarImagenDeUsuario(usuario: String): Bitmap {
        val httpResponse: HttpResponse = clienteHttp.get("https://offerus.zapto.org/profile/image") {
            parameter("usuario_actual", usuario)  // Agregar el usuario como parámetro de consulta
            accept(ContentType.Any)
        }

        if (httpResponse.status == HttpStatusCode.OK) {
            val bytes = httpResponse.readBytes()
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                ?: throw Exception("No se pudo decodificar la imagen")
        } else {
            throw Exception("Error al descargar la imagen: ${httpResponse.status.description}")
        }
    }


    /************************************************************************
     * Peticiones relacionadas a las peticion/servicio
     *************************************************************************/

   /**
    *@Description: Función que manda la petición de crear una peticion/servicio, NOTA IMPORTANTE: las categorias
    * deben ser una definida en el servidor y separadas por comas
    * @Param servicio: ServicioPeticionCreate
    * @Throws AuthenticationException
    * @Throws UnprocessableEntityException
    * @Throws Exception
    * @Return Unit
    */
    @Throws(AuthenticationException::class,UnprocessableEntityException::class , Exception::class)
    suspend fun crearPeticionServicio(servicio: ServicioPeticionCreate) {
        val token = bearerTokenStorage.last().accessToken
        val jsonContent = Json.encodeToString(servicio)
        val response: HttpResponse = clienteHttp.post("https://offerus.zapto.org/peticiones_servicio") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(jsonContent)
        }

    }

    /**
    *@Description: Función que manda la petición de ver una petición/servicio
    * @Param idPet: Int
    * @Throws Exception
    * @Return ServicioPeticion
     */
    @Throws(Exception::class)
    suspend fun verPeticion(idPet: Int): ServicioPeticion {
        val response: HttpResponse = clienteHttp.get("https://offerus.zapto.org/peticion") {
            parameter("id_pet", idPet)
            accept(ContentType.Application.Json)
        }
        if (response.status == HttpStatusCode.OK) {
            return Json.decodeFromString(response.bodyAsText())
        } else {
            throw Exception("Failed to fetch request: HTTP ${response.status}")
        }
    }

    /**
     * @Description: Función que manda la petición para obtener una lista de peticiones/servicios
     * @Param idsPet: Lista de enteros que representan los IDs de las peticiones
     * @Throws Exception
     * @Return Lista de ServicioPeticion
     */
    @Throws(Exception::class)
    suspend fun obtenerPeticiones(idsPet: List<Int>): List<ServicioPeticion> {
        val jsonBody = Json.encodeToString(PeticionesRequestBody(idsPet))
        val response: HttpResponse = clienteHttp.post("https://offerus.zapto.org/peticiones") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(jsonBody)
        }
        when (response.status) {
            HttpStatusCode.OK -> {
                return Json.decodeFromString(response.bodyAsText())
            }
            HttpStatusCode.NotFound -> {
                throw Exception("Peticiones no encontradas: HTTP ${response.status}")
            }
            else -> {
                throw Exception("Error al realizar la solicitud: HTTP ${response.status}")
            }
        }
    }

    /**
    *@Description: Función que manda la petición de visualizar las peticiones/servicio de un usuario
    * @Throws AuthenticationException
    * @Throws UnprocessableEntityException
    * @Throws Exception
    * @Return List<ServicioPeticion>
     */
    @Throws(AuthenticationException::class, UnprocessableEntityException::class, Exception::class)
    suspend fun obtenerPeticionesServicioUsuario(): List<ServicioPeticion> {
        val token = bearerTokenStorage.last().accessToken

        val response: HttpResponse = clienteHttp.get("https://offerus.zapto.org/usuarios/peticiones_servicio") {
            header("Authorization", "Bearer $token")
            accept(ContentType.Application.Json)
        }

        if (response.status == HttpStatusCode.OK) {
            return Json.decodeFromString(response.bodyAsText())
        } else if (response.status == HttpStatusCode.UnprocessableEntity) {
            throw UnprocessableEntityException()
        } else if (response.status == HttpStatusCode.Unauthorized) {
            throw AuthenticationException()
        } else {
            throw Exception("Failed to load service requests: ${response.status.description}")
        }
    }


    /**
    *@Description: Función que manda la petición de modificar una petición/servicio
    * @Param request: ServicioPeticion
    * @Throws AuthenticationException
    * @Throws Exception
    * @Return Unit
     */
    @Throws(AuthenticationException::class, Exception::class)
    suspend fun updateServiceRequest(request: ServicioPeticionMod) {
        val token = bearerTokenStorage.last().accessToken
        val jsonContent = Json.encodeToString(request)
        val response: HttpResponse = clienteHttp.put("https://offerus.zapto.org/peticiones_servicio") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(jsonContent)
        }

        when (response.status) {
            HttpStatusCode.OK -> Log.d("KTOR", "Servicio modificado correctamente")
            HttpStatusCode.Unauthorized -> throw AuthenticationException()
            HttpStatusCode.BadRequest -> throw IllegalArgumentException()
            HttpStatusCode.UnprocessableEntity -> throw IllegalArgumentException("Unprocessable entity, check input data")
            else -> throw Exception("Failed to update service request: HTTP ${response.status}")
        }
    }

    /**
    *@Description: Función que manda la petición de eliminar una petición/servicio
    * @Param idPet: Int
    * @Throws AuthenticationException
    * @Throws Exception
    * @Return Unit
     */
    @Throws(AuthenticationException::class, Exception::class)
    suspend fun deleteServiceRequest(idPet: Int) {
        val token = bearerTokenStorage.last().accessToken
        val response: HttpResponse = clienteHttp.delete("https://offerus.zapto.org/peticiones_servicio") {
            parameter("id_pet", idPet)
            header(HttpHeaders.Authorization, "Bearer $token")
            accept(ContentType.Application.Json)
        }

        when (response.status) {
            HttpStatusCode.OK -> Log.d("KTOR", "Servicio eliminado correctamente")
            HttpStatusCode.Unauthorized -> throw AuthenticationException()
            HttpStatusCode.NotFound -> throw Exception("Service request not found")
            else -> throw Exception("Failed to delete service request: HTTP ${response.status}")
        }
    }


    /**
    *@Description: Función que manda la petición de crear un deal
    * @Param deal: DealPeticion
    * @Throws AuthenticationException
    * @Throws UnprocessableEntityException
    * @Throws Exception
    * @Return Unit
     */
    @Throws(AuthenticationException::class,UnprocessableEntityException::class , Exception::class)
    suspend fun crearDeal(deal: PeticionId) {
        val token = bearerTokenStorage.last().accessToken
        val jsonContent = Json.encodeToString(deal)
        val response: HttpResponse = clienteHttp.post("https://offerus.zapto.org/deals") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(jsonContent)
        }
        when (response.status) {
            HttpStatusCode.OK -> Log.d("Deal", "Deal successfully created")
            HttpStatusCode.Unauthorized -> throw AuthenticationException()
            HttpStatusCode.BadRequest -> {
                val errorResponse = Json.decodeFromString<ErrorResponse>(response.bodyAsText())
                val errorMessages = errorResponse.detail.joinToString("; ") { detail ->
                    detail.msg
                }
                throw IllegalArgumentException("Usuario host no puede aceptar su propia petición")

            }
            HttpStatusCode.UnprocessableEntity -> throw UnprocessableEntityException()
            else -> throw Exception("Failed to create deal: HTTP ${response.status}")
        }

    }

    /**
    *@Description: Función que manda la petición de aceptar un deal
    * @Param dealId: Int
    * @Throws AuthenticationException
    * @Throws Exception
    * @Return Unit
     */
    @Throws(AuthenticationException::class, Exception::class)
    suspend fun aceptarDeal(dealId: Int, accept : Boolean) {
        val token = bearerTokenStorage.last().accessToken
        val jsonContent = Json.encodeToString(DealPeticionAceptar(dealId,accept))
        val response: HttpResponse = clienteHttp.post("https://offerus.zapto.org/deals/accept/accept_deny") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(jsonContent)
        }
        when (response.status) {
            HttpStatusCode.OK -> println("Deal processed successfully")
            HttpStatusCode.Unauthorized -> throw AuthenticationException()
            else -> throw Exception("Failed to process deal: HTTP ${response.status}")
        }
    }
    /**
    *@Description: Función que manda la petición de visualizar los deals de un usuario
    * @Throws AuthenticationException
    * @Throws UnprocessableEntityException
    * @Throws Exception
    * @Return List<Deal>
     */
    @Throws(AuthenticationException::class,UnprocessableEntityException::class , Exception::class)
    suspend fun obtenerDealsUsuario(): List<Deal> {
        val token = bearerTokenStorage.last().accessToken

        val response: HttpResponse = clienteHttp.get("https://offerus.zapto.org/usuarios/mis_deals") {
            header("Authorization", "Bearer $token")
            accept(ContentType.Application.Json)
        }

        if (response.status == HttpStatusCode.OK) {
            return Json.decodeFromString(response.bodyAsText())
        } else if (response.status == HttpStatusCode.UnprocessableEntity) {
            throw UnprocessableEntityException()
        }else if (response.status == HttpStatusCode.Unauthorized) {
            throw AuthenticationException()
        }else {
            throw Exception("Failed to load deals: ${response.status.description}")
        }
    }

    /**
    *@Description: Función que manda la petición de buscar peticiones/servicio dados parámetros de búsqueda, NOTA IMPORTANTE: la busqueda
    * debe ser precio_desc, precio_asc ó distancia.
    * @Param busqueda: BusquedaPeticionServicio
    * @Throws UnprocessableEntityException
    * @Throws Exception
    * @Return List<ServicioPeticion>
     */
    @Throws(UnprocessableEntityException::class , Exception::class)
    suspend fun buscarPeticionesServicio(busqueda: BusquedaPeticionServicio): List<ServicioPeticion>  {
        val token = bearerTokenStorage.last().accessToken
        val jsonContent = Json.encodeToString(busqueda)

        val response: HttpResponse = clienteHttp.post("https://offerus.zapto.org/buscar_peticiones_servicio") {
            header("Authorization", "Bearer $token")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(jsonContent)
        }

        if (response.status == HttpStatusCode.OK) {
            return Json.decodeFromString(response.bodyAsText())
        } else if (response.status == HttpStatusCode.UnprocessableEntity) {
            throw UnprocessableEntityException()
        }else {
            throw Exception("Failed to search requests: HTTP ${response.status}")
        }
    }

    /**
     * @Description: Función que manda la petición de valorar un deal
     * @Param request: ValorarDeal
     * @Throws AuthenticationException
     * @Throws Exception
     * @Return Unit
     */
    @Throws(AuthenticationException::class, Exception::class)
    suspend fun rateDeal(request: ValorarDeal) {
        val token = bearerTokenStorage.last().accessToken
        val jsonContent = Json.encodeToString(request)
        val response: HttpResponse = clienteHttp.post("https://offerus.zapto.org/deals/valorar") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(jsonContent)
        }

        when (response.status) {
            HttpStatusCode.OK -> Log.d("KTOR", "Deal valorado correctamente")
            HttpStatusCode.Unauthorized -> throw AuthenticationException()
            HttpStatusCode.BadRequest -> throw IllegalArgumentException("Bad request, check request parameters")
            HttpStatusCode.NotFound -> throw Exception("Deal not found")
            HttpStatusCode.UnprocessableEntity -> throw IllegalArgumentException("Validation error, check input data")
            else -> throw Exception("Failed to rate deal: HTTP ${response.status}")
        }
    }



    /**
     *@Description: Función para modificar los datos del usuario haciendo put a /usuarios
     * @Param user: Usuario
     * @Throws UserExistsException
     * @Throws Exception
     * @Return Unit
     */

    @Throws(AuthenticationException::class,UnprocessableEntityException::class ,Exception::class)
    suspend fun modifyUser(user: UsuarioUpdate) {
        val token = bearerTokenStorage.last().accessToken
        val jsonContent = Json.encodeToString(user)
        val response: HttpResponse = clienteHttp.put("https://offerus.zapto.org/usuario") {
            header("Authorization", "Bearer $token")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(jsonContent)
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                Log.d("KTOR", "Usuario modificado")
            }
            HttpStatusCode.Unauthorized -> throw AuthenticationException()
            HttpStatusCode.BadRequest -> throw UserExistsException()
            HttpStatusCode.UnprocessableEntity -> {
                val errorResponse = Json.decodeFromString<ErrorResponse>(response.bodyAsText())
                val errorMessages = errorResponse.detail.joinToString("; ") { detail ->
                    detail.msg
                }
                throw IllegalArgumentException("Validation error: $errorMessages")
            }
            else -> throw Exception("No se ha conseguido modificar el usuario: HTTP ${response.status}")
        }
    }

    /************************************************************************
     * Funcion para cambiar la contraseña del usuario
     *************************************************************************/

    /**
     * @Description: Funcion para cambiar la contraseña del usuario
     * @Param request: ContraseñaChange
     * @Throws AuthenticationException
     * @Throws UnprocessableEntityException
     * @Throws Exception
     * @Return Unit
     */
    @Throws(AuthenticationException::class,UnprocessableEntityException::class ,Exception::class)
    suspend fun changePassword(request: ContraseñaChange) {
        val token = bearerTokenStorage.last().accessToken
        val jsonContent = Json.encodeToString(request)
        val response: HttpResponse = clienteHttp.put("https://offerus.zapto.org/usuario/cambiar_contraseña") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(jsonContent)
        }

        when (response.status) {
            HttpStatusCode.OK -> Log.d("KTOR", "Contraseña modificada")
            HttpStatusCode.Unauthorized -> throw AuthenticationException()
            HttpStatusCode.UnprocessableEntity -> {
                val errorResponse = Json.decodeFromString<ErrorResponse>(response.bodyAsText())
                val errorMessages = errorResponse.detail.joinToString("; ") { detail ->
                    detail.msg
                }
                throw IllegalArgumentException("Validation error: $errorMessages")
            }
            else -> throw Exception("Failed to change password: HTTP ${response.status}")
        }
    }

    /**
     * @Description: Función que manda la petición de obtener las peticiones/servicio favoritas de un usuario
     * @Throws AuthenticationException
     * @Throws UnprocessableEntityException
     * @Throws Exception
     * @Return List<ServicioPeticion>
     */
    suspend fun getDatosUsuario(): UsuarioData {
        val response: HttpResponse = clienteHttp.get("https://offerus.zapto.org/usuario") {
            val token = bearerTokenStorage.last().accessToken
            header(HttpHeaders.Authorization, "Bearer $token")
            accept(ContentType.Application.Json)
        }

        if (response.status == HttpStatusCode.OK) {
            return Json.decodeFromString<UsuarioData>(response.bodyAsText())
        }else if (response.status == HttpStatusCode.Unauthorized) {
            throw AuthenticationException()
        } else {
            throw Exception("Failed to fetch user data: HTTP ${response.status}")
        }
    }

    @Throws(AuthenticationException::class, UnprocessableEntityException::class, Exception::class)
    suspend fun obtenerPetFavoritasUsuario(): List<ServicioPeticion> {
        val token = bearerTokenStorage.last().accessToken

        val response: HttpResponse = clienteHttp.get("https://offerus.zapto.org/favoritos") {
            header("Authorization", "Bearer $token")
            accept(ContentType.Application.Json)
        }

        if (response.status == HttpStatusCode.OK) {
            return Json.decodeFromString(response.bodyAsText())
        }
        else if (response.status == HttpStatusCode.Unauthorized) {
            throw AuthenticationException()
        } else {
            throw Exception("Error con el servidor: ${response.status.description}")
        }
    }

    /**
     *@Description: Función que manda la petición de añadir una petición/servicio a favoritos
     * @Param favoriteRequest: PeticionId , clase que contiene un id_peticion
     * @Throws AuthenticationException
     * @Throws Exception
     * @Return Unit
     */

    @Throws(AuthenticationException::class, Exception::class)
    suspend fun addFavorite( favoriteRequest: PeticionId) {
        val token = bearerTokenStorage.last().accessToken
        val jsonContent = Json.encodeToString(favoriteRequest)
        val response: HttpResponse =  clienteHttp.post("https://offerus.zapto.org/favoritos") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(jsonContent)
        }

        when (response.status) {
            HttpStatusCode.OK -> println("Favorite añadida")
            HttpStatusCode.Unauthorized -> throw AuthenticationException()
            HttpStatusCode.BadRequest -> throw IllegalArgumentException("Bad request, check request parameters")
            HttpStatusCode.NotFound -> throw Exception("Resource not found")
            HttpStatusCode.UnprocessableEntity -> throw Exception("Unprocessable entity, check input data")
            else -> throw Exception("Failed to add favorite: HTTP ${response.status}")
        }
    }

    /**
     *@Description: Función que manda la petición de eliminar una petición/servicio de favoritos
     * @Param favoriteRequest: PeticionId , clase que contiene un id_peticion
     * @Throws AuthenticationException
     * @Throws Exception
     * @Return Unit
     */
    @Throws(AuthenticationException::class, Exception::class)
    suspend fun deleteFavorite( favoriteRequest: PeticionId) {
        val token = bearerTokenStorage.last().accessToken
        val jsonContent = Json.encodeToString(favoriteRequest)
        val response: HttpResponse =  clienteHttp.delete("https://offerus.zapto.org/favoritos") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(jsonContent)
        }

        when (response.status) {
            HttpStatusCode.OK -> println("Favorite eliminada ")
            HttpStatusCode.Unauthorized -> throw AuthenticationException()
            HttpStatusCode.BadRequest -> throw IllegalArgumentException("Bad request, check request parameters")
            HttpStatusCode.NotFound -> throw Exception("Resource not found")
            HttpStatusCode.UnprocessableEntity -> throw Exception("Unprocessable entity, check input data")
            else -> throw Exception("Failed to delete favorite: HTTP ${response.status}")
        }
    }


    /**
     * @Description: Funcion que manda la peticion de suscripcion a FCM dado el token
     * @Param token: String
     * @Throws AuthenticationException
     * @Throws Exception
     * @Return Unit
     */
    @Throws(AuthenticationException::class, Exception::class)
    suspend fun subscribeToFCM(token: String) {
        val token = bearerTokenStorage.last().accessToken
        val response: HttpResponse = clienteHttp.post("https://offerus.zapto.org/suscribir_fcm") {
            header(HttpHeaders.Authorization, "Bearer $token")
            parameter("fcm_client_token", token)
            accept(ContentType.Application.Json)
        }

        when (response.status) {
            HttpStatusCode.OK -> println("FCM token registrado")
            HttpStatusCode.Unauthorized -> throw AuthenticationException()
            else -> throw Exception("Error al registrar el token FCM: HTTP ${response.status}")
        }
    }

}