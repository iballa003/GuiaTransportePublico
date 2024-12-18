package org.iesharia.guiatransportepublico.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.room.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.iesharia.guiatransportepublico.data.AppDatabase2
import org.iesharia.guiatransportepublico.data.GuideDao
import org.iesharia.guiatransportepublico.data.Road
import org.iesharia.guiatransportepublico.data.Stop
import org.iesharia.guiatransportepublico.data.StopWithRoute

class StopViewModel(application: Context, private val repository: StopRepository) : ViewModel() {

    private val guideDao = AppDatabase2.getDatabase(application).guideDao()
    fun insertData(stop: Stop) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertData(stop)
        }
    }
    fun deleteStop(paradaId: Int) {
        viewModelScope.launch {
            repository.deleteStop(paradaId)
        }
    }

    fun updateStop(paradaId: Int, name: String, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            guideDao.updateStop(paradaId, name, latitude, longitude)
        }
    }

    // Exponer las paradas y rutas directamente como StateFlow
    val allStops: StateFlow<List<Stop>> = repository.allstops
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allRoads: StateFlow<List<Road>> = guideDao.getAllRoads()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    // Exponer paradas con sus rutas
    val stopsWithRoutes: StateFlow<List<StopWithRoute>> = guideDao.getStopsWithRoutes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

}
class StopViewModelFactory(private val context: Context, private val repository: StopRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StopViewModel::class.java)) {
            return StopViewModel(context, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}