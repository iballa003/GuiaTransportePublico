package org.iesharia.guiatransportepublico.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
@Dao
interface MarkerDao {
    @Query("SELECT * FROM Road")
    fun getAllRoads(): LiveData<List<Road>>

    @Query("SELECT * FROM Stops")
    fun getAllStops(): List<Stop>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoad(road: Road) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStop(stop: Stop) : Long
}