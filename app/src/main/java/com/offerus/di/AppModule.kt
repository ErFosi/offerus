package com.offerus.di

import com.offerus.utils.AuthClient
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

    @Provides
    @Singleton
    fun provideWebClient(): AuthClient {
        return AuthClient()
    }

}