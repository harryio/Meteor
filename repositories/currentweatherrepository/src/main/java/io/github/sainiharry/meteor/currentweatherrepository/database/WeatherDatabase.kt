package io.github.sainiharry.meteor.currentweatherrepository.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CurrentWeatherModel::class], version = 1)
internal abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}