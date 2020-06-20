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
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers

fun Fragment.searchViewModel(): Lazy<SearchViewModel> = activityViewModels(factoryProducer = {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(
                getSearchRepository(requireContext().applicationContext),
                AndroidSchedulers.mainThread()
            ) as T
        }
    }
})

class SearchViewModel(
    private val searchRepository: SearchRepository,
    private val observableScheduler: Scheduler
) : BaseViewModel(), ItemClickListener<Search> {

    private val _recentSearchQueries = MutableLiveData<List<Search>>()
    internal val recentSearchQueries: LiveData<List<Search>>
        get() = _recentSearchQueries

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
        searchTextInput.value?.let {
            onSearchQueryAvailable(it)
        }
    }

    internal fun loadSearchData() {
        disposables.add(
            searchRepository.getSearchQueries().observeOn(observableScheduler)
                .subscribe({
                    _recentSearchQueries.value = it
                }, Throwable::printStackTrace)
        )
    }

    override fun onItemClicked(item: Search, position: Int) {
        onSearchQueryAvailable(item.searchQuery)
    }

    private fun onSearchQueryAvailable(searchQuery: String) {
        _searchText.value = searchQuery
        _navigateBackEvent.value = Event(Any())
        searchTextInput.value = ""
    }
}