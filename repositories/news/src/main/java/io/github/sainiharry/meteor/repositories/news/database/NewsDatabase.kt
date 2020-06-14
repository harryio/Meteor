package io.github.sainiharry.meteor.repositories.news.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NewsModel::class], version = 1)
internal abstract class NewsDatabase: RoomDatabase() {

    abstract fun newsDao(): NewsDao
}