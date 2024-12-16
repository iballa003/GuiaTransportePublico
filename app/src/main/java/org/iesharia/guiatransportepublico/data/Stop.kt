package org.iesharia.guiatransportepublico.data
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
@Entity(
    foreignKeys = [ForeignKey(
        entity = Road::class,
        parentColumns = ["id"],
        childColumns = ["ruta_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Stop(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val latitude: Double,
    val longitude: Double,
    val ruta_id: Int
)