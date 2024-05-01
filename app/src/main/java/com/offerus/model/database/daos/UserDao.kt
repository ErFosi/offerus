package com.offerus.model.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.offerus.model.database.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert
    suspend fun addUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM User where username = :username LIMIT 1")
    fun getUserData(username: String): Flow<User>


    @Query("SELECT username FROM User where username = :username")
    fun checkUsernameExists(username: String): String

    @Query("SELECT password FROM User WHERE username = :username")
    suspend fun getUserPassword(username: String): String

    //API
    @Transaction
    @Query("DELETE FROM User")
    suspend fun deleteUsers()

    @Transaction
    suspend fun addUsers(users: Iterable<User>) {
        users.forEach { addUser(it) }
    }
    @Transaction
    suspend fun overwriteUsers(users: Iterable<User>) {
        deleteUsers()
        addUsers(users)
    }
}