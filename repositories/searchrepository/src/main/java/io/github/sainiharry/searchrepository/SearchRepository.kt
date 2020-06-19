package io.github.sainiharry.searchrepository

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import io.github.sainiharry.meteor.common.DATABASE_NAME
import io.github.sainiharry.meteor.common.model.Search
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

fun getSearchRepository(
    applicationContext: Context
) = SearchRepository.getInstance {
    Database(AndroidSqliteDriver(Database.Schema, applicationContext, DATABASE_NAME))
}

interface SearchRepository {

    companion object {
        private var searchRepository: SearchRepository? = null

        internal fun getInstance(databaseProvider: () -> Database) {
            searchRepository ?: SearchRepositoryImpl(databaseProvider()).also {
                searchRepository = it
            }
        }
    }

    fun getSearchQueries(): Single<List<Search>>
}

internal class SearchRepositoryImpl(private val database: Database) : SearchRepository {

    override fun getSearchQueries(): Single<List<Search>> =
        Single.fromCallable { database.searchQueries.selectSearchQueries().executeAsList() }
            .subscribeOn(Schedulers.io())
            .flatMap {
                Observable.fromIterable(it)
                    .map(SearchModel::toSearch)
                    .toList()
            }
}

internal fun SearchModel.toSearch() = Search(id, searchQuery)
