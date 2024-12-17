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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.iesharia.guiatransportepublico.data.AppDatabase2
import org.iesharia.guiatransportepublico.data.GuideDao
import org.iesharia.guiatransportepublico.data.Road
import org.iesharia.guiatransportepublico.data.Stop

class StopViewModel(application: Context, private val repository: StopRepository) : ViewModel() {
    val allStops: LiveData<List<Stop>> = repository.allstops
    private val guideDao = AppDatabase2.getDatabase(application).guideDao()
    fun insertData(stop: Stop) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertData(stop)
        }
    }
    private val _rutas = MutableStateFlow<List<Road>>(emptyList())
    private val _stops = MutableStateFlow<List<Stop>>(emptyList())
    val rutas: StateFlow<List<Road>> get() = _rutas
    val paradas: StateFlow<List<Stop>> get() = _stops

    init {
        viewModelScope.launch {
            // Convertir LiveData a StateFlow usando asFlow()
            guideDao.getAllRoads().asFlow().collect { roadList ->
                _rutas.value = roadList
            }
            guideDao.getAllStops().asFlow().collect { stopList ->
                _stops.value = stopList
            }
        }
}
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