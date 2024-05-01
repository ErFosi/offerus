package com.offerus.model.database.entities

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Immutable
@Serializable
@Entity(tableName = "Deal")
data class Deal(
    @PrimaryKey
    val id: Int,
    val idPeticion: Int,
    val usernameOfrece: String,
    val usernamePide: String,
    val nota: String,
    val aceptado: Boolean,
)