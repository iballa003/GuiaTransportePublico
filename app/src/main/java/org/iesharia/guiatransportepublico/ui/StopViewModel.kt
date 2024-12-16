package org.iesharia.guiatransportepublico.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
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