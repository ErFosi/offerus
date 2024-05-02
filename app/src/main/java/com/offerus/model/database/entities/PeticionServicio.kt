package com.offerus.model.database.entities

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.serialization.Serializable

@Immutable
@Serializable
@Entity(tableName = "PeticionServicio")
data class PeticionServicio(
    @PrimaryKey
    val id: Int,
    val username: String,
    val titulo: String,
    val esPeti: Boolean,
    val categoria: String,
    val descripcion: String,
    val precio: Double,
    val fecha: String,
    val latitud: Double,
    val longitud: Double,
)
