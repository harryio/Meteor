package io.github.sainiharry.meteor.repositories.news.database

import androidx.room.*
import io.github.sainiharry.meteor.common.News
import io.reactivex.Single

@Dao
internal interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNews(news: List<NewsModel>)

    @Query("SELECT * FROM NewsModel")
    fun getAllNews(): Single<List<News>>

    @Query("DELETE FROM NewsModel")
    fun clear()

    @Transaction
    fun clearAndInsertNews(news: List<NewsModel>) {
        clear()
        insertNews(news)
    }
}