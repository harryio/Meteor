package io.github.sainiharry.meteor.repositories.news.database

import androidx.room.*
import io.github.sainiharry.meteor.common.model.News

@Dao
internal interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: List<NewsModel>)

    @Query("SELECT * FROM NewsModel")
    suspend fun getAllNews(): List<News>

    @Query("DELETE FROM NewsModel")
    suspend fun clear()

    @Transaction
    suspend fun clearAndInsertNews(news: List<NewsModel>) {
        clear()
        insertNews(news)
    }
}