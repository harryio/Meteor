package io.github.sainiharry.meteor.weatherrepository.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WeatherModel::class], version = 1)
internal abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}