package org.iesharia.guiatransportepublico.data
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

data class Stop(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val typeId: Int
)