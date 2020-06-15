package io.github.sainiharry.metero.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.sainiharry.meteor.common.ItemClickListener
import io.github.sainiharry.meteor.common.News
import io.github.sainiharry.meteor.commonfeature.BaseViewModel
import io.github.sainiharry.meteor.commonfeature.Event
import io.github.sainiharry.meteor.repositories.news.NewsRepository
import io.reactivex.Scheduler

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

        countryCodeLiveData.value = countryCode
        _loading.value = Event(true)
        _news.value = emptyList()
        disposables.add(
            newsRepository.fetchNews(countryCode)
                .observeOn(observableScheduler)
                .doOnEvent { _, _ -> _loading.value = Event(false) }
                .subscribe({
                    _news.value = it
                }, getErrorHandler(R.string.error_fetch_news))
        )
    }
}