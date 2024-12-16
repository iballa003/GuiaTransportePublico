package org.iesharia.guiatransportepublico.data
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class Alert(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String,
    val message: String,
    val road_id: String,
)