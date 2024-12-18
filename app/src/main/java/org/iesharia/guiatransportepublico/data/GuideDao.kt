package org.iesharia.guiatransportepublico.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
@Dao
interface GuideDao {
    @Query("SELECT * FROM Road")
    fun getAllRoads(): Flow<List<Road>>

    @Query("SELECT * FROM Stop")
    fun getAllStops(): Flow<List<Stop>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoad(road: Road) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStop(stop: Stop) : Long

    @Query("DELETE FROM Stop WHERE id = :paradaId")
    suspend fun deleteStop(paradaId: Int)
}