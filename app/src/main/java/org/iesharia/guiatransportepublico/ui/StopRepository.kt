package org.iesharia.guiatransportepublico.ui

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.iesharia.guiatransportepublico.data.GuideDao
import org.iesharia.guiatransportepublico.data.Stop

class StopRepository(private val guideDao: GuideDao) {
    val allstops: Flow<List<Stop>> = guideDao.getAllStops()

    suspend fun insertData(Stop: Stop) {
        guideDao.insertStop(Stop)
    }
    suspend fun deleteStop(id: Int) {
        guideDao.deleteStop(id)
    }
}