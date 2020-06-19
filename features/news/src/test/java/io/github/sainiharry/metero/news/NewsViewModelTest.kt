package io.github.sainiharry.metero.news

import android.accounts.NetworkErrorException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.github.sainiharry.meteor.common.model.News
import io.github.sainiharry.meteor.commonfeature.Event
import io.github.sainiharry.meteor.repositories.news.NewsRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class NewsViewModelTest {

    @Mock
    lateinit var newsRepository: NewsRepository

    @Mock
    lateinit var newsObserver: Observer<List<News>>

    @Mock
    lateinit var viewNewsEventObserver: Observer<Event<News>>

    @Mock
    lateinit var loadingObserver: Observer<Event<Boolean>>

    @Mock
    lateinit var errorObserver: Observer<Event<Int>>

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var model: NewsViewModel

    private val countryCode = "GB"

    @Before
    fun setup() {
        model = NewsViewModel(newsRepository, Schedulers.trampoline())
        model.news.observeForever(newsObserver)
        model.viewNewsEvent.observeForever(viewNewsEventObserver)
        model.loading.observeForever(loadingObserver)
        model.error.observeForever(errorObserver)
        `when`(newsRepository.fetchNews(countryCode))
            .thenReturn(Single.just(listOf(mockNews())))
    }

    @After
    fun tearDown() {
        model.news.removeObserver(newsObserver)
        model.viewNewsEvent.removeObserver(viewNewsEventObserver)
        model.loading.removeObserver(loadingObserver)
        model.error.removeObserver(errorObserver)
    }

    @Test
    fun testSuccessNewsFetch() {
        model.handleCountry(countryCode)
        verify(loadingObserver).onChanged(Event(true))
        verify(newsObserver).onChanged(listOf(mockNews()))
        verify(loadingObserver).onChanged(Event(false))
        verifyNoMoreInteractions(loadingObserver)
        verifyNoMoreInteractions(newsObserver)
        verifyZeroInteractions(errorObserver)
    }

    @Test
    fun testNewsFetchError() {
        `when`(newsRepository.fetchNews(countryCode)).thenReturn(Single.error(NetworkErrorException()))
        model.handleCountry(countryCode)
        verify(loadingObserver).onChanged(Event(true))
        verify(loadingObserver).onChanged(Event(false))
        verify(errorObserver).onChanged(Event(R.string.error_fetch_news))
        verifyNoMoreInteractions(loadingObserver)
        verifyNoMoreInteractions(errorObserver)
        verifyZeroInteractions(newsObserver)
    }

    @Test
    fun testOnItemClicked() {
        val news = mockNews()
        model.onItemClicked(news, 0)
        verify(viewNewsEventObserver).onChanged(Event(news))
        verifyNoMoreInteractions(viewNewsEventObserver)
        verifyZeroInteractions(loadingObserver)
        verifyZeroInteractions(errorObserver)
        verifyZeroInteractions(newsObserver)
    }

    @Test
    fun testRefresh() {
        model.refresh()
        verify(loadingObserver).onChanged(Event(false))

        model.handleCountry("GB")
        verify(loadingObserver).onChanged(Event(true))
        verify(newsObserver).onChanged(listOf(mockNews()))
        verify(loadingObserver, times(2)).onChanged(Event(false))

        model.refresh()
        verify(loadingObserver, times(2)).onChanged(Event(true))
        verify(newsObserver, times(2)).onChanged(listOf(mockNews()))
        verify(loadingObserver, times(3)).onChanged(Event(false))

        verifyNoMoreInteractions(loadingObserver)
        verifyNoMoreInteractions(newsObserver)
        verifyZeroInteractions(errorObserver)
    }

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