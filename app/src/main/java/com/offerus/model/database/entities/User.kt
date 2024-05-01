package com.offerus.model.database.entities

import android.net.MailTo
import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
@Entity(tableName = "User")
data class User(
    @PrimaryKey
    val username: String,
    val password: String = "",
    val nombre: String,
    val apellido: String,
    val edad: Int,
    val latitud: Double,
    val longitud: Double,
    val mail: String,
    val telefono: String,
    val sexo: String,
    val descripcion: String,
    val suscripciones: String,

)