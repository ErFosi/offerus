package com.offerus.utils

import android.util.Log
import com.offerus.data.ErrorResponse
import com.offerus.data.Usuario
import com.offerus.data.UsuarioCred
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.accept
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/************************************************************************
 * Clase singleton que se encarga de mandar todas las peticiones a la API
 *************************************************************************/

@Singleton
class AuthClient @Inject constructor() {
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
     * Función que manda la peticion de registrar un usuario, emite dos errores
     * UserExistsException y Exception, cuando se da el primero significa que
     * ya existe el usuario en la BD,si no
     *se asume que no se ha podido entablecer conexión con el servidor.
     *
     * Además, si se consigue hacer login se almacena el token Oauth (Última linea)
     *************************************************************************/


   /*
    *@Description: Función que manda la petición de registrar un usuario, recordar que el sexo
    * es un char, M, F ó O.
   * @Param user: UsuarioRegistro
   * @Throws UserExistsException
   * @Throws Exception
   * @Return Unit
   * */
    @Throws(UserExistsException::class,UnprocessableEntityException::class ,Exception::class)
    suspend fun register(user: Usuario) {
        val jsonContent = Json.encodeToString(user)
        val response: HttpResponse = clienteHttp.post("https://offerus.zapto.org/usuarios") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(jsonContent)
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                Log.d("KTOR", "Usuario registrado")
            }
            HttpStatusCode.BadRequest -> throw UserExistsException()
            HttpStatusCode.UnprocessableEntity -> {
                val errorResponse = Json.decodeFromString<ErrorResponse>(response.bodyAsText())
                val errorMessages = errorResponse.detail.joinToString("; ") { detail ->
                    detail.msg
                }
                throw IllegalArgumentException("Validation error: $errorMessages")
            }
            else -> throw Exception("No se ha conseguido registrar el usuario: HTTP ${response.status}")
        }
    }


}