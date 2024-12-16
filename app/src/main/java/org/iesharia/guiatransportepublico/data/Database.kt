package org.iesharia.guiatransportepublico.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Road::class, Stop::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun guideDao(): GuideDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "guia_transporte_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
