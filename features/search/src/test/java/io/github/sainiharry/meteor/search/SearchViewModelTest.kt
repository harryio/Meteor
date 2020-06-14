package io.github.sainiharry.meteor.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.github.sainiharry.meteor.commonfeature.Event
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SearchViewModelTest {

    @Mock
    private lateinit var searchTextObserver: Observer<String>

    @Mock
    private lateinit var navigateBackEventObserver: Observer<Event<Any?>>


    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val model = SearchViewModel()

    @Before
    fun setup() {
        model.searchText.observeForever(searchTextObserver)
        model.navigateBackEvent.observeForever(navigateBackEventObserver)
    }

    @After
    fun tearDown() {
        model.searchText.removeObserver(searchTextObserver)
        model.navigateBackEvent.observeForever(navigateBackEventObserver)
    }

    @Test
    fun testHandleSearchDoneWithInitialState() {
        model.handleSearchDone()
        verify(searchTextObserver).onChanged(null)
        verify(navigateBackEventObserver).onChanged(any())
        verifyNoMoreInteractions(searchTextObserver)
        verifyNoMoreInteractions(navigateBackEventObserver)

        Assert.assertEquals("", model.searchTextInput.value)
    }

    @Test
    fun testHandleSearchDoneWithText() {
        val cityName = "London"
        model.searchTextInput.value = cityName

        model.handleSearchDone()

        verify(searchTextObserver).onChanged(cityName)
        verify(navigateBackEventObserver).onChanged(any())

        verifyNoMoreInteractions(searchTextObserver)
        verifyNoMoreInteractions(navigateBackEventObserver)

        Assert.assertEquals("", model.searchTextInput.value)
    }
}