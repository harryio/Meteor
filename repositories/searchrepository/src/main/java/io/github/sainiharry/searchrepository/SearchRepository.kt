package io.github.sainiharry.searchrepository

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.runtime.rx.asObservable
import com.squareup.sqldelight.runtime.rx.mapToList
import io.github.sainiharry.meteor.common.DATABASE_NAME
import io.reactivex.Observable
import io.reactivex.Single

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

    fun getSearchQueries(): Single<List<String>>
}

internal class SearchRepositoryImpl(private val database: Database) : SearchRepository {

    override fun getSearchQueries(): Single<List<String>> {
        return database.searchQueries.selectQueries().asObservable().mapToList()
            .flatMapSingle { searchQueries ->
                Observable.fromIterable(searchQueries)
                    .map(Search::searchQuery)
                    .toList()
            }
            .single(emptyList<String>())
    }
}