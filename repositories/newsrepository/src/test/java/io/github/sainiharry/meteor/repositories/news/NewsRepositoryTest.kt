package io.github.sainiharry.meteor.repositories.news

import io.github.sainiharry.meteor.common.model.News
import io.github.sainiharry.meteor.repositories.news.database.NewsDao
import io.github.sainiharry.meteor.repositories.news.database.NewsDatabase
import io.github.sainiharry.meteor.repositories.news.network.NewsResponse
import io.github.sainiharry.meteor.repositories.news.network.NewsResponseWrapper
import io.github.sainiharry.meteor.repositories.news.network.NewsService
import io.github.sainiharry.meteor.repositories.news.network.toNewsModelList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
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
    fun testFetchNews() = runBlockingTest {
        val mockNewsResponse = mockNewsResponse()
        val mockNews = listOf(mockNews())
        `when`(newsService.fetchNews(countryCode)).thenReturn(mockNewsResponse)
        `when`(newsDao.getAllNews()).thenReturn(mockNews)

        val news = newsRepository.fetchNews(countryCode)
        assertTrue(news == mockNews)
        verify(newsService).fetchNews(countryCode)
        verify(newsDao).clearAndInsertNews(mockNewsResponse.articles.toNewsModelList())
        verify(newsDao).getAllNews()
    }

    private fun mockNewsResponse(
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
        author: String = "Harsimran",
        title: String = "Android Developer make history",
        description: String = "Definitely because he so awesome",
        url: String = "https://www.abc.com",
        urlToImage: String = "https://www.image.com",
        publishedAt: String = "2020-06-14T22:20:57Z",
        content: String = "LOS ANGLES"
    ) = News(
        author,
        title,
        description,
        url,
        urlToImage,
        publishedAt,
        content
    )
}