package com.offerus.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.offerus.data.Deal
import com.offerus.data.ServicioPeticion
import com.offerus.data.Usuario
import com.offerus.model.database.daos.PetiServDao
import com.offerus.model.database.daos.UserDao

@Database(entities = [Usuario::class, ServicioPeticion::class, Deal::class], version = 1)
abstract  class OfferusDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun petiServDao(): PetiServDao
}