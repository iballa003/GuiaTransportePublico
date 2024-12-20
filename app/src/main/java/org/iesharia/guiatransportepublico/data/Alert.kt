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
data class Alert(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String,
    val message: String,
    val road_id: Int,
)