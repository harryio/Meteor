package io.github.sainiharry.metero.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.github.sainiharry.meteor.common.ItemClickListener
import io.github.sainiharry.meteor.common.model.News
import io.github.sainiharry.meteor.commonfeature.BaseViewModel
import io.github.sainiharry.meteor.commonfeature.Event
import io.github.sainiharry.meteor.repositories.news.NewsRepository
import io.reactivex.Scheduler
import kotlinx.coroutines.launch

internal class NewsViewModel(
    private val newsRepository: NewsRepository,
    private val observableScheduler: Scheduler
) : BaseViewModel(), ItemClickListener<News> {

    private val countryCodeLiveData = MutableLiveData<String>()

    private val _news = MutableLiveData<List<News>>()
    val news: LiveData<List<News>>
        get() = _news

    private val _viewNewsEvent = MutableLiveData<Event<News>>()
    val viewNewsEvent: LiveData<Event<News>>
        get() = _viewNewsEvent

    override fun onItemClicked(item: News, position: Int) {
        _viewNewsEvent.value = Event(item)
    }

    internal fun refresh() {
        handleCountry(countryCodeLiveData.value)
    }

    internal fun handleCountry(countryCode: String?) {
        if (countryCode.isNullOrEmpty() || countryCode.isBlank()) {
            _loading.value = Event(false)
            return
        }

        // TODO: 28/06/20 How to handle errors here?
        countryCodeLiveData.value = countryCode
        viewModelScope.launch {
            _loading.value = Event(true)
            _news.value = newsRepository.fetchNews(countryCode)
            _loading.value = Event(false)
        }
    }
}