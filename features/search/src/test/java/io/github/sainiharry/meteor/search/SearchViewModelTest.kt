package io.github.sainiharry.meteor.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.github.sainiharry.meteor.common.model.Search
import io.github.sainiharry.meteor.commonfeature.Event
import io.github.sainiharry.searchrepository.SearchRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SearchViewModelTest {

    @Mock
    private lateinit var searchRepository: SearchRepository

    @Mock
    private lateinit var searchTextObserver: Observer<String>

    @Mock
    private lateinit var recentSearchQueriesObserver: Observer<List<Search>>

    @Mock
    private lateinit var recentSearchLabelVisibleObserver: Observer<Boolean>

    @Mock
    private lateinit var navigateBackEventObserver: Observer<Event<Any?>>

    private lateinit var model: SearchViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        model = SearchViewModel(searchRepository, Schedulers.trampoline())
        model.searchText.observeForever(searchTextObserver)
        model.navigateBackEvent.observeForever(navigateBackEventObserver)
        model.recentSearchQueries.observeForever(recentSearchQueriesObserver)
        model.recentSearchLabelVisible.observeForever(recentSearchLabelVisibleObserver)
    }

    @After
    fun tearDown() {
        model.searchText.removeObserver(searchTextObserver)
        model.navigateBackEvent.observeForever(navigateBackEventObserver)
        model.recentSearchQueries.removeObserver(recentSearchQueriesObserver)
        model.recentSearchLabelVisible.removeObserver(recentSearchLabelVisibleObserver)
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

    @Test
    fun testLoadSearchData() {
        val searchQueries = listOf(
            Search(1, "New York"),
            Search(2, "Toronto"),
            Search(3, "Chicago")
        )
        `when`(searchRepository.getSearchQueries()).thenReturn(Single.just(searchQueries))

        model.loadSearchData()
        verify(recentSearchQueriesObserver).onChanged(searchQueries)
        verify(recentSearchLabelVisibleObserver).onChanged(true)
        verifyNoMoreInteractions(recentSearchQueriesObserver)
        verifyNoMoreInteractions(recentSearchLabelVisibleObserver)
    }

    @Test
    fun testLoadSearchDataWithEmptyEmptyData() {
        `when`(searchRepository.getSearchQueries()).thenReturn(Single.just(emptyList()))

        model.loadSearchData()
        verify(recentSearchQueriesObserver).onChanged(emptyList())
        verify(recentSearchLabelVisibleObserver).onChanged(false)
        verifyNoMoreInteractions(recentSearchQueriesObserver)
        verifyNoMoreInteractions(recentSearchLabelVisibleObserver)
    }
}