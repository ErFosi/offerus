package com.offerus.model.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.offerus.model.database.entities.PeticionServicio

@Dao
interface PetiServDao {
    @Insert
    suspend fun addPetiServ(peti: PeticionServicio)

    @Update
    suspend fun updatePetiServ(peti: PeticionServicio)

    @Query("SELECT * FROM PeticionServicio where id = :id LIMIT 1")
    fun getPetiServData(id: Int): PeticionServicio

}