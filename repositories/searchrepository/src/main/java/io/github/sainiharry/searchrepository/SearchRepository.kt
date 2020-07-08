package io.github.sainiharry.searchrepository

import com.squareup.sqldelight.android.AndroidSqliteDriver
import io.github.sainiharry.meteor.common.model.Search
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.dsl.module

/**
 * Koin module for providing single instance of SearchRepository.
 */
val searchRepositoryModule = module {
    single<SearchRepository> {
        SearchRepositoryImpl(Database(AndroidSqliteDriver(Database.Schema, get())))
    }
}

/**
 * Repository for accessing search queries
 */
interface SearchRepository {

    /**
     * Get list of 5 most recent search queries
     */
    suspend fun getSearchQueries(): List<Search>

    /**
     * Submit a new search query to the repository
     * @param searchQuery: search query to be submitted
     */
    suspend fun handleSearchQuery(searchQuery: String)
}

/**
 * Internal implementation of SearchRepository
 */
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
