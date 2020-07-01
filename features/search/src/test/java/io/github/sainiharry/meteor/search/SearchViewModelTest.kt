package io.github.sainiharry.meteor.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.github.sainiharry.meteor.common.model.Search
import io.github.sainiharry.meteor.commonfeature.Event
import io.github.sainiharry.searchrepository.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description
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

    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    private lateinit var model: SearchViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineScopeRule = MainCoroutineScopeRule(testCoroutineDispatcher)

    @Before
    fun setup() {
        model = SearchViewModel(searchRepository, testCoroutineDispatcher)
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

    @Test
    fun testLoadSearchData() = runBlockingTest {
        val searchQueries = listOf(
            Search(1, "New York"),
            Search(2, "Toronto"),
            Search(3, "Chicago")
        )
        `when`(searchRepository.getSearchQueries()).thenReturn(searchQueries)

        model.recentSearchQueries.observeForever(recentSearchQueriesObserver)
        model.recentSearchLabelVisible.observeForever(recentSearchLabelVisibleObserver)
        verify(recentSearchQueriesObserver).onChanged(searchQueries)
        verify(recentSearchLabelVisibleObserver).onChanged(true)
        verifyNoMoreInteractions(recentSearchQueriesObserver)
        verifyNoMoreInteractions(recentSearchLabelVisibleObserver)
        model.recentSearchQueries.removeObserver(recentSearchQueriesObserver)
        model.recentSearchLabelVisible.removeObserver(recentSearchLabelVisibleObserver)
    }

    @Test
    fun testLoadSearchDataWithEmptyEmptyData() = runBlockingTest {
        `when`(searchRepository.getSearchQueries()).thenReturn(emptyList())

        model.recentSearchQueries.observeForever(recentSearchQueriesObserver)
        model.recentSearchLabelVisible.observeForever(recentSearchLabelVisibleObserver)
        verify(recentSearchQueriesObserver).onChanged(emptyList())
        verify(recentSearchLabelVisibleObserver).onChanged(false)
        verifyNoMoreInteractions(recentSearchQueriesObserver)
        verifyNoMoreInteractions(recentSearchLabelVisibleObserver)
        model.recentSearchQueries.removeObserver(recentSearchQueriesObserver)
        model.recentSearchLabelVisible.removeObserver(recentSearchLabelVisibleObserver)
    }
}

@ExperimentalCoroutinesApi
class MainCoroutineScopeRule(private val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()) :
    TestWatcher(),
    TestCoroutineScope by TestCoroutineScope(dispatcher) {
    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        cleanupTestCoroutines()
        Dispatchers.resetMain()
    }
}