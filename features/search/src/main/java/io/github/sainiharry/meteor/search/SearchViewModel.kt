package io.github.sainiharry.meteor.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.sainiharry.meteor.commonfeature.BaseViewModel
import io.github.sainiharry.meteor.commonfeature.Event

class SearchViewModel : BaseViewModel() {

    val searchTextInput = MutableLiveData<String>()

    private val _searchText = MutableLiveData<String>()
    val searchText: LiveData<String>
        get() = _searchText

    private val _searchBarClickedEvent = MutableLiveData<Event<Any>>()
    val searchBarClickedEvent: LiveData<Event<Any>>
        get() = _searchBarClickedEvent

    internal fun handleSearchDone() {
        _searchText.value = searchTextInput.value
    }

    internal fun handleSearchBarClick() {
        _searchBarClickedEvent.value = Event(Any())
    }
}