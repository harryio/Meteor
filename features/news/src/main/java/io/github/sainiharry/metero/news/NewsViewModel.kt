package io.github.sainiharry.metero.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.github.sainiharry.meteor.commonfeature.BaseViewModel
import io.github.sainiharry.meteor.repositories.news.News
import io.github.sainiharry.meteor.repositories.news.NewsRepository
import io.reactivex.Scheduler
import java.util.*

internal class NewsViewModel(
    private val newsRepository: NewsRepository,
    private val observableScheduler: Scheduler
) : BaseViewModel() {

    private val _news = MutableLiveData<List<News>>()
    val news: LiveData<List<News>>
        get() = _news

    private val newsLiveData = MediatorLiveData<List<News>>()
    private val newsObserver = Observer<List<News>> {
        _news.value = it
    }

    internal fun handleCountry(countryCode: String?) {
        if (countryCode.isNullOrEmpty() || countryCode.isBlank()) {
            return
        }

        _news.value = emptyList()
        newsLiveData.removeSource(newsRepository.getNewsListener())
        disposables.add(
            newsRepository.fetchNews(countryCode)
                .ignoreElement()
                .observeOn(observableScheduler)
                .subscribe({
                    newsLiveData.addSource(newsRepository.getNewsListener(), newsObserver)
                }, getErrorHandler(R.string.error_fetch_news))
        )
    }
}