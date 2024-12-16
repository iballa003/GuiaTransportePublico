package org.iesharia.guiatransportepublico.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.iesharia.guiatransportepublico.data.Stop

class StopViewModel(private val repository: MarkerRepository) : ViewModel() {
    val allMarkers: LiveData<List<Stop>> = repository.allMarkers
    fun insertData(markerTypes: List<Stop>, markers: List<Stop>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertData(markerTypes, markers)
        }
    }

}