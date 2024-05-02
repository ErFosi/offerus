package com.offerus.di

import com.offerus.utils.AuthClient
import android.content.Context
import androidx.room.Room
import com.offerus.model.database.OfferusDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * This is the hilt module. this module is installed in singletoncomponent, meaning that all the instance here are stored in
 * the application level, so they arent destroyed until the app is closed and can be shared between activitites
 */

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun providesOfferusDatabase(@ApplicationContext app: Context) =
        Room.databaseBuilder(app, OfferusDatabase::class.java, "OfferusDB")
            .fallbackToDestructiveMigration()
            .build()

    //DAOS
    @Singleton
    @Provides
    fun provideUserDao(db: OfferusDatabase) = db.userDao()

    @Singleton
    @Provides
    fun providePetiServDao(db: OfferusDatabase) = db.petiServDao()

    @Provides
    @Singleton
    fun provideWebClient(): AuthClient {
        return AuthClient()
    }

}