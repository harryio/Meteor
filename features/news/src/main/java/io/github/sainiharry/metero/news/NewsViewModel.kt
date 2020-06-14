package io.github.sainiharry.metero.news

import io.github.sainiharry.meteor.commonfeature.BaseViewModel
import io.github.sainiharry.meteor.repositories.news.NewsRepository
import io.reactivex.Scheduler

internal class NewsViewModel(
    private val newsRepository: NewsRepository,
    private val scheduler: Scheduler
) : BaseViewModel() {
}