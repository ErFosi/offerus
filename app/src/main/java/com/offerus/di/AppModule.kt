package com.offerus.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * This is the hilt module. this module is installed in singletoncomponent, meaning that all the instance here are stored in
 * the application level, so they arent destroyed until the app is closed and can be shared between activitites
 */

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

}