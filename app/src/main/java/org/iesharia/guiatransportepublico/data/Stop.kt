package org.iesharia.guiatransportepublico.data
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
@Entity(
    foreignKeys = [ForeignKey(
        entity = Road::class,
        parentColumns = ["id"],
        childColumns = ["road_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Stop(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val road_id: Int
)