package org.iesharia.guiatransportepublico.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.iesharia.guiatransportepublico.data.Stop

class StopViewModel(private val repository: StopRepository) : ViewModel() {
    val allStops: LiveData<List<Stop>> = repository.allstops
    fun insertData(stop: Stop) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertData(stop)
        }
    }

}

class StopViewModelFactory(private val repository: StopRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StopViewModel::class.java)) {
            return StopViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}