package io.github.sainiharry.meteor.currentweatherrepository.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

private const val DATABASE_NAME = "WeatherDb"

@Database(entities = [CurrentWeatherModel::class], version = 1)
internal abstract class WeatherDatabase : RoomDatabase() {

    companion object {

        private var weatherDatabase: WeatherDatabase? = null

        fun getInstance(applicationContext: Context): WeatherDatabase {
            if (weatherDatabase == null) {
                weatherDatabase = Room.databaseBuilder(
                    applicationContext,
                    WeatherDatabase::class.java,
                    DATABASE_NAME
                ).build()
            }

            return weatherDatabase!!
        }
    }

    abstract fun weatherDao(): WeatherDao
}