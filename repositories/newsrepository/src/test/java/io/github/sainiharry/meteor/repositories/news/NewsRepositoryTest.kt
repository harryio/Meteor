package io.github.sainiharry.meteor.repositories.news

import io.github.sainiharry.meteor.common.model.News
import io.github.sainiharry.meteor.repositories.news.database.NewsDao
import io.github.sainiharry.meteor.repositories.news.database.NewsDatabase
import io.github.sainiharry.meteor.repositories.news.network.NewsResponse
import io.github.sainiharry.meteor.repositories.news.network.NewsResponseWrapper
import io.github.sainiharry.meteor.repositories.news.network.NewsService
import io.github.sainiharry.meteor.repositories.news.network.toNewsModelList
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class NewsRepositoryTest {

    @Mock
    private lateinit var newsDatabase: NewsDatabase

    @Mock
    private lateinit var newsService: NewsService

    @Mock
    private lateinit var newsDao: NewsDao

    private lateinit var newsRepository: NewsRepository

    private val countryCode = "GB"

    @Before
    fun setup() {
        `when`(newsDatabase.newsDao()).thenReturn(newsDao)
        newsRepository = NewsRepositoryImpl(newsService, newsDatabase)
    }

    @Test
    fun testFetchNews() {
        val mockNewsResponse = mockNewsResponse()
        val mockNews = listOf(mockNews())
        `when`(newsService.fetchNews(countryCode)).thenReturn(Single.just(mockNewsResponse))
        `when`(newsDao.getAllNews()).thenReturn(Single.just(mockNews))

        val subscription = newsRepository.fetchNews(countryCode).test()
        subscription.assertNoErrors()
        subscription.assertValue(mockNews)

        verify(newsService).fetchNews(countryCode)
        verify(newsDao).clearAndInsertNews(mockNewsResponse.articles.toNewsModelList())
        verify(newsDao).getAllNews()
    }

    private fun mockNewsResponse(
        id: Long = 12,
        author: String = "Harsimran",
        title: String = "Android Developer make history",
        description: String = "Definitely because he so awesome",
        url: String = "https://www.abc.com",
        urlToImage: String = "https://www.image.com",
        publishedAt: String = "2020-06-14T22:20:57Z",
        content: String = "LOS ANGLES"
    ) = NewsResponseWrapper(
        listOf(
            NewsResponse(
                author,
                title,
                description,
                url,
                urlToImage,
                publishedAt,
                content
            )
        )
    )

    private fun mockNews(
        id: Long = 12,
        author: String = "Harsimran",
        title: String = "Android Developer make history",
        description: String = "Definitely because he so awesome",
        url: String = "https://www.abc.com",
        urlToImage: String = "https://www.image.com",
        publishedAt: String = "2020-06-14T22:20:57Z",
        content: String = "LOS ANGLES"
    ) = News(
        id,
        author,
        title,
        description,
        url,
        urlToImage,
        publishedAt,
        content
    )
}