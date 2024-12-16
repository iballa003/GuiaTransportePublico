package org.iesharia.guiatransportepublico.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class Road(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: Double,
)