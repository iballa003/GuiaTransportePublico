package org.iesharia.guiatransportepublico.ui

import androidx.lifecycle.LiveData
import org.iesharia.guiatransportepublico.data.GuideDao
import org.iesharia.guiatransportepublico.data.Stop

class StopRepository(private val guideDao: GuideDao) {
    val allstops: LiveData<List<Stop>> = guideDao.getAllStops()

    suspend fun insertData(Stop: Stop) {
        guideDao.insertStop(Stop)
    }
}