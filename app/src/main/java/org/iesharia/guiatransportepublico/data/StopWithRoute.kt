package org.iesharia.guiatransportepublico.data

import androidx.room.Entity

data class StopWithRoute(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val routeName: String // Nombre de la ruta asociada
)