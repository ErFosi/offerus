package com.offerus.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.offerus.model.database.daos.PetiServDao
import com.offerus.model.database.daos.UserDao
import com.offerus.model.database.entities.Deal
import com.offerus.model.database.entities.PeticionServicio
import com.offerus.model.database.entities.User

@Database(entities = [User::class, PeticionServicio::class, Deal::class], version = 1)
abstract  class OfferusDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun petiServDao(): PetiServDao
}