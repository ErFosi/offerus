package com.offerus.model.repositories

import android.graphics.Bitmap
import com.offerus.data.Usuario
import com.offerus.model.database.daos.UserDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataRepository @Inject constructor(
    private val userDao: UserDao,
    //private val datastore: PreferencesDataStore,
    //private val authenticationClient: AuthenticationClient,
    //private val apiClient: APIClient,
) {

    private lateinit var profileImage: Bitmap

    suspend fun addUserData(user: Usuario) = userDao.addUser(user)
    suspend fun updateUser(user: Usuario) {
        userDao.updateUser(user)
    }

    fun getUserData(username: String) = userDao.getUserData(username)

    fun checkUsernameExists(username: String) = userDao.checkUsernameExists(username)


    //DATASTORE
    //change language preferences
    //suspend fun changeUserLanguage(username: String, lang: String) = datastore.setUserLanguage(username, lang)


    //API
    //@Throws(AuthenticationException::class)
    //suspend fun authenticateUser(user: AuthUser): Boolean {
    //    return try {
    //        authenticationClient.authenticate(user)
    //        true
    //    } catch (e: AuthenticationException) {
    //        false
    //    }
    //}
}