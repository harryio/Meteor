package io.github.sainiharry.meteor.repositories.news.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.sainiharry.meteor.repositories.news.News

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNews(news: List<News>)

    @Query("SELECT * FROM NewsModel")
    fun getNewsListener(): LiveData<List<News>>

    @Query("DELETE FROM NewsModel")
    fun clear()
}