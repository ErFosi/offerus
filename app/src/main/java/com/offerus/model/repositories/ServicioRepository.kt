package com.offerus.model.repositories

import com.offerus.data.ServicioPeticion
import com.offerus.model.database.daos.PetiServDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServicioRepository @Inject constructor(
    private val petiServDao: PetiServDao,
) {

    suspend fun addServicio(peticion: ServicioPeticion) = petiServDao.addPetiServ(peticion)
    suspend fun updateServicio(peticion: ServicioPeticion) {
        petiServDao.updatePetiServ(peticion)
    }

    fun getServicio(id: Int) = petiServDao.getPetiServData(id)

    // delete table
    suspend fun deleteServicio() {
        petiServDao.deletePetiServ()
    }
}