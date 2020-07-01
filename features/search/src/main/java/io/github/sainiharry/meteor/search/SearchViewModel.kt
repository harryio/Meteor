package io.github.sainiharry.meteor.search

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.*
import io.github.sainiharry.meteor.common.ItemClickListener
import io.github.sainiharry.meteor.common.model.Search
import io.github.sainiharry.meteor.commonfeature.BaseViewModel
import io.github.sainiharry.meteor.commonfeature.Event
import io.github.sainiharry.searchrepository.SearchRepository
import io.github.sainiharry.searchrepository.getSearchRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

fun Fragment.searchViewModel(): Lazy<SearchViewModel> = activityViewModels(factoryProducer = {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(
                getSearchRepository(requireContext().applicationContext),
                Dispatchers.IO
            ) as T
        }
    }
})

class SearchViewModel(
    private val searchRepository: SearchRepository,
    defaultDispatcher: CoroutineDispatcher
) : BaseViewModel(), ItemClickListener<Search> {

    internal val recentSearchQueries = liveData(defaultDispatcher) {
        emit(searchRepository.getSearchQueries())
    }

    val searchTextInput = MutableLiveData<String>()

    private val _searchText = MutableLiveData<String>()
    val searchText: LiveData<String>
        get() = _searchText

    private val _navigateBackEvent = MutableLiveData<Event<Any?>>()
    internal val navigateBackEvent: LiveData<Event<Any?>>
        get() = _navigateBackEvent

    val recentSearchLabelVisible: LiveData<Boolean> = Transformations.map(recentSearchQueries) {
        it.isNotEmpty()
    }

    internal fun handleSearchDone() {
        onSearchQueryAvailable(searchTextInput.value)
    }

    override fun onItemClicked(item: Search, position: Int) {
        onSearchQueryAvailable(item.searchQuery)
    }

    private fun onSearchQueryAvailable(searchQuery: String?) {
        _searchText.value = searchQuery
        _navigateBackEvent.value = Event(Any())
        searchTextInput.value = ""
    }
}