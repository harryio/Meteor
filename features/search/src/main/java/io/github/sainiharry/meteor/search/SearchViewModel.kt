package io.github.sainiharry.meteor.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.sainiharry.meteor.commonfeature.BaseViewModel

class SearchViewModel : BaseViewModel() {

    val searchTextInput = MutableLiveData<String>()

    private val _searchText = MutableLiveData<String>()

    val searchText: LiveData<String>
        get() = _searchText

    fun handleSearchDone() {
        _searchText.value = searchTextInput.value
    }
}