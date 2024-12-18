package org.iesharia.guiatransportepublico.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Road::class, Stop::class], version = 2)
abstract class AppDatabase2 : RoomDatabase() {
    abstract fun guideDao(): GuideDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase2? = null
        fun getDatabase(context: Context): AppDatabase2 {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase2::class.java,
                    "guia_transporte_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
    private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
        @SuppressLint("SuspiciousIndentation")
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Insertar datos iniciales cuando la base de datos se crea por primera vez
            CoroutineScope(Dispatchers.IO).launch {
                val database = getDatabase(context)
                val guideDao = database.guideDao()

                // Crear datos iniciales
                if (guideDao.getCountRoads() == 0) { // Verificar si está vacía
                    val roads = listOf(
                        Road(id = 1, name = "Arrecife-Orzola", "De Arrecife a Orzola"),
                        Road(id = 2, name = "Arrecife-Maguez", "De Arrecife a Maguez"),
                        Road(id = 3, name = "Arrecife-Yé", "De Arrecife a Yé"),
                    )
                        // Insertar datos
                        guideDao.insertAllRoads(roads)
                }
                if (guideDao.getCountStops() == 0) { // Verificar si está vacía
                    val stops = listOf(
                        Stop(
                            name = "Parada centro de salud de Mala",
                            latitude = 28.957473,
                            longitude = -13.554514,
                            road_id = 1
                        ),
                        Stop(
                            name = "Parada Jardín de Cactús",
                            latitude = 28.954321,
                            longitude = -13.551234,
                            road_id = 2
                        ),
                        Stop(
                            name = "Estación de guaguas",
                            latitude = 28.967133068307646,
                            longitude = -13.551609596110522,
                            road_id = 2
                        )
                    )
                    // Insertar datos
                    guideDao.insertAllStops(stops)
                }

            }
        }
    }
}
