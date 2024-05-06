package com.offerus.model.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.offerus.data.ServicioPeticion

@Dao
interface PetiServDao {
    @Insert
    suspend fun addPetiServ(peti: ServicioPeticion)

    @Update
    suspend fun updatePetiServ(peti: ServicioPeticion)

    @Query("SELECT * FROM ServicioPeticion where id = :id LIMIT 1")
    fun getPetiServData(id: Int): ServicioPeticion

}