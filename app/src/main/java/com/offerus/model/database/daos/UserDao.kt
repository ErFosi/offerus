package com.offerus.model.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.offerus.data.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert
    suspend fun addUser(user: Usuario)

    @Update
    suspend fun updateUser(user: Usuario)

    @Query("SELECT * FROM Usuario where username = :username LIMIT 1")
    fun getUserData(username: String): Flow<Usuario>


    @Query("SELECT username FROM Usuario where username = :username")
    fun checkUsernameExists(username: String): String

    @Query("SELECT contraseña FROM Usuario WHERE username = :username")
    suspend fun getUserPassword(username: String): String

    //API
    @Transaction
    @Query("DELETE FROM Usuario")
    suspend fun deleteUsers()

    @Transaction
    suspend fun addUsers(users: Iterable<Usuario>) {
        users.forEach { addUser(it) }
    }
    @Transaction
    suspend fun overwriteUsers(users: Iterable<Usuario>) {
        deleteUsers()
        addUsers(users)
    }
}