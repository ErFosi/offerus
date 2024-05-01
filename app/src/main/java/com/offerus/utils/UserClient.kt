package com.offerus.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.offerus.data.BusquedaPeticionServicio
import com.offerus.data.Deal
import com.offerus.data.DealPeticion
import com.offerus.data.ServicioPeticion
import com.offerus.data.ServicioPeticionCreate
import com.offerus.data.UsuarioCred
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.accept
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
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

    /*
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


    /*
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

   /*
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

    /*
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

    /*
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




    /*
    *@Description: Función que manda la petición de crear un deal
    * @Param deal: DealPeticion
    * @Throws AuthenticationException
    * @Throws UnprocessableEntityException
    * @Throws Exception
    * @Return Unit
     */
    @Throws(AuthenticationException::class,UnprocessableEntityException::class , Exception::class)
    suspend fun crearDeal(deal: DealPeticion) {
        val token = bearerTokenStorage.last().accessToken
        val jsonContent = Json.encodeToString(deal)
        val response: HttpResponse = clienteHttp.post("https://offerus.zapto.org/deals") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(jsonContent)
        }

    }

    /*
    *@Description: Función que manda la petición de aceptar un deal
    * @Param dealId: Int
    * @Throws AuthenticationException
    * @Throws Exception
    * @Return Unit
     */
    @Throws(AuthenticationException::class, Exception::class)
    suspend fun aceptarDeal(dealId: Int) {
        val token = bearerTokenStorage.last().accessToken
        val jsonContent = Json.encodeToString(DealPeticion(dealId))
        val response: HttpResponse = clienteHttp.post("https://offerus.zapto.org/deals/accept?deal_id=$dealId") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(jsonContent)
        }

    }
    /*
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

    /*
    *@Description: Función que manda la petición de buscar peticiones/servicio dados parámetros de búsqueda
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



}