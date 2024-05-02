package com.offerus.data

import kotlinx.serialization.Serializable

/************************************************************************
 * Clases para la gestion de los servicios
 *
 * Nota importante, las categorias deben ser una definida en el servidor y separadas por comas
 *************************************************************************/
@Serializable
data class ServicioPeticion(
    val id: Int,
    val username: String,
    val titulo: String,
    val descripcion: String,
    val precio: Double,
    val fecha: String,
    val latitud: Double,
    val longitud: Double,
    val categorias: String
)
@Serializable
data class Deal(
    val id: Int,
    val nota: String,
    val username_cliente: String,
    val username_host: String,
    val id_peticion: Int,
    val aceptado: Boolean
)
/************************************************************************
 * Clases para la gestion de los usuarios
 *
 * Nota importante, el sexo es un char, M, F ó O.
 *************************************************************************/
@Serializable
data class Usuario(
    val username: String,
    val contraseña: String,
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
/************************************************************************
 * Clases para gestionar las peticiones http
 *************************************************************************/
@Serializable
data class UsuarioCred(
    val usuario:String,
    val contraseña:String
)
@Serializable
data class UsuarioResponse(val message: String)

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
    val precio: Double,
    val fecha: String,
    val latitud: Double,
    val longitud: Double,
    val categorias: String
)

@Serializable
data class DealPeticion(
    val id_peticion: Int
)

/************************************************************************
 * Clases para gestionar las peticiones de busqueda
 *
 * NOTA IMPORTANTE, la cateogoria debe ser una definida en el servidor
 * la manera de ordenar son las siguientes opciones: precio_desc, precio_asc ó distancia
 *************************************************************************/
@Serializable
data class BusquedaPeticionServicio(
    val texto_busqueda: String,
    val categorias: String,
    val distancia_maxima: Double,
    val precio_minimo: Double,
    val precio_maximo: Double,
    val latitud: Double,
    val longitud: Double,
    val ordenar_por: String
)