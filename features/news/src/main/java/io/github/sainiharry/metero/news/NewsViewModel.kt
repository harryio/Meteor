package io.github.sainiharry.metero.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import io.github.sainiharry.meteor.common.News
import io.github.sainiharry.meteor.commonfeature.BaseViewModel
import io.github.sainiharry.meteor.repositories.news.NewsRepository
import io.reactivex.Scheduler

internal class NewsViewModel(
    private val newsRepository: NewsRepository,
    private val observableScheduler: Scheduler
) : BaseViewModel() {

    private val _news = MutableLiveData<List<News>>()
    val news: LiveData<List<News>>
        get() = _news

    internal fun handleCountry(countryCode: String?) {
        if (countryCode.isNullOrEmpty() || countryCode.isBlank()) {
            return
        }

        _news.value = emptyList()
        disposables.add(
            newsRepository.fetchNews(countryCode)
                .observeOn(observableScheduler)
                .subscribe({
                    _news.value = it
                }, getErrorHandler(R.string.error_fetch_news))
        )
    }
}