package io.github.sainiharry.searchrepository

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import io.github.sainiharry.meteor.common.DATABASE_NAME
import io.github.sainiharry.meteor.common.model.Search
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun getSearchRepository(
    applicationContext: Context
) = SearchRepository.getInstance {
    Database(AndroidSqliteDriver(Database.Schema, applicationContext, DATABASE_NAME))
}

interface SearchRepository {

    companion object {
        private var searchRepository: SearchRepository? = null

        internal fun getInstance(databaseProvider: () -> Database): SearchRepository =
            searchRepository ?: SearchRepositoryImpl(databaseProvider()).also {
                searchRepository = it
            }
    }

    suspend fun getSearchQueries(): List<Search>

    suspend fun handleSearchQuery(searchQuery: String)
}

internal class SearchRepositoryImpl(
    private val database: Database,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SearchRepository {

    override suspend fun getSearchQueries(): List<Search> = withContext(defaultDispatcher) {
        database.searchQueries.selectSearchQueries().executeAsList()
            .map(SearchModel::toSearch)
    }

    override suspend fun handleSearchQuery(searchQuery: String) = withContext(defaultDispatcher) {
        database.searchQueries.insert(searchQuery)
    }
}

internal fun SearchModel.toSearch() = Search(id, searchQuery)
