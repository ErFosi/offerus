package com.offerus.data

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.offerus.R
import kotlinx.serialization.Serializable

/************************************************************************
 * Clases para la gestion de los servicios
 *
 * Nota importante, las categorias deben ser una definida en el servidor y separadas por comas
 *************************************************************************/
@Immutable
@Serializable
@Entity(tableName = "ServicioPeticion")
data class ServicioPeticion(
    @PrimaryKey
    val id: Int,
    val username: String,
    val titulo: String,
    val descripcion: String,
    val peticion: Boolean,
    val precio: Double,
    val fecha: String,
    val latitud: Double,
    val longitud: Double,
    val categorias: String
)

@Immutable
@Serializable
@Entity(tableName = "Deal")
data class Deal(
    @PrimaryKey
    val id: Int,
    val username_cliente: String,
    val username_host: String,
    val id_peticion: Int,
    val estado: String,  // Modified to match the original schema using estado instead of aceptado
    var nota_cliente: Int = -1,  // Defaults to -1 as per your SQLAlchemy model
    var nota_host: Int = -1  // Defaults to -1 as per your SQLAlchemy model
)
/************************************************************************
 * Clases para la gestion de los usuarios
 *
 * Nota importante, el sexo es un char, M, F ó O.
 *************************************************************************/
@Immutable
@Serializable
@Entity(tableName = "Usuario")
data class Usuario(
    @PrimaryKey
    val username: String,
    val contraseña: String,
    val nombre_apellido: String,
    val edad: Int,
    val latitud: Double,
    val longitud: Double,
    val mail: String,
    val telefono: String,
    val sexo: String,  // Sex as a string to match 'M', 'F', or 'O'
    val descripcion: String,
    val suscripciones: String
)


@Serializable
data class UsuarioUpdate(
    val nombre_apellido: String,
    val edad: Int,
    val latitud: Double,
    val longitud: Double,
    val mail: String,
    val telefono: String,
    val sexo: String,
    val descripcion: String,
    val suscripciones: String
)
@Serializable
data class UsuarioData(
    val username: String,
    val nombre_apellido: String,
    val edad: Int,
    val latitud: Double,
    val longitud: Double,
    val mail: String,
    val telefono: String,
    val sexo: String,
    val descripcion: String,
    val suscripciones: String
)

@Serializable
data class ContraseñaChange(
    val contraseña: String,
    val nueva_contraseña: String
)

/************************************************************************
 * Clases para gestionar las peticiones http
 *************************************************************************/
@Serializable
data class UsuarioCred(
    val usuario:String,
    val contraseña:String
)


@Serializable
data class ErrorDetail(
    val msg: String
)
@Serializable
data class ErrorResponse(
    val detail: List<ErrorDetail>
)
/************************************************************************
 * Clases para gestionar las peticiones de creacion de servicios
 *
 * NOTA IMPORTANTE, la cateogoria debe ser una definida en el servidor y separadas por comas
 * */
@Serializable
data class ServicioPeticionCreate(
    val titulo: String,
    val descripcion: String,
    val peticion: Boolean,
    val precio: Double,
    val fecha: String,
    val latitud: Double,
    val longitud: Double,
    val categorias: String
)
@Serializable
data class ServicioPeticionMod(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val peticion: Boolean,
    val precio: Double,
    val fecha: String,
    val latitud: Double,
    val longitud: Double,
    val categorias: String
)
@Serializable
data class PeticionId(
    val id_peticion: Int
)
@Serializable
data class NotaUsuario(
    val nota: Double,
    val cant: Int
)

@Serializable
data class PeticionesRequestBody(
    val ids_pet: List<Int>
)
@Serializable
data class DealPeticionAceptar(
    val deal_id: Int,
    val accept: Boolean
)
@Serializable
data class ValorarDeal(
    val deal_id: Int,
    val nota: Int
)

/************************************************************************
 * Clases para gestionar las peticiones de busqueda
 *
 * NOTA IMPORTANTE, la cateogoria debe ser una definida en el servidor
 * la manera de ordenar son las siguientes opciones: precio_desc, precio_asc ó distancia
 *************************************************************************/
@Serializable
data class BusquedaPeticionServicio(
    val texto_busqueda: String?,
    val categorias: String?,
    val distancia_maxima: Double?,
    val precio_minimo: Double?,
    val precio_maximo: Double?,
    val latitud: Double?,
    val longitud: Double?,
    val ordenar_por: String?
)

/************************************************************************
 * Clases para gestionar las peticiones de favoritos
 *************************************************************************/
@Serializable
data class Favoritos(
    val username: String,
    val id_peticion: Int
)


data class Categoria(val nombre: String, val icono: Int, val color: Color, val nombreMostrar: Int)

val CATEGORIAS = listOf(
    Categoria("gratis", R.drawable.gratis, Color.Green, R.string.gratis),
    Categoria("deporte", R.drawable.deporte, Color.Yellow, R.string.deporte),
    Categoria("hogar", R.drawable.hogar, Color.Magenta, R.string.hogar),
    Categoria("academico", R.drawable.academico, Color.Blue, R.string.academico),
    Categoria("online", R.drawable.online, Color.Cyan, R.string.online),
    Categoria("otros", R.drawable.otros, Color.Gray, R.string.otros),
    Categoria("entretenimiento", R.drawable.ocio, Color.Red, R.string.entretenimiento),
)