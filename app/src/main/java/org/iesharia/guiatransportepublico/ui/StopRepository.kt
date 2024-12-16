package org.iesharia.guiatransportepublico.ui

class MarkerRepository(private val markerDao: MarkerDao) {
    val allMarkers: LiveData<List<Marker>> = markerDao.getAllMarkers()

    suspend fun insertData(markerTypes: List<MarkerType>, markers: List<Marker>) {
        markerDao.insertMarkerTypes(markerTypes)
        markerDao.insertMarkers(markers)
    }
}