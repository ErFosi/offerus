package com.offerus

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Offerus: Application(){
    override fun onCreate() {
        super.onCreate()

        /**
         * CREATE NOTIFICATION CHANNEL
         * create and initialize the default notification channel
         */

        val channel = NotificationChannel(
            "0",
            "Offerus",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        // TODO channel.description = stringResource

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)


    }
}


/*
Class used to centralize and have better control over application's notification IDs.
It uses an enum class what gives better readability over the code, and avoids ID mistakes
*/
enum class NotificationID(val id: Int) {
    USER_CREATED(0)
}