package io.github.sainiharry.searchrepository

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class SearchRepositoryTest {

    private lateinit var searchRepository: SearchRepository

    @Before
    fun setup() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        Database.Schema.create(driver)
        val database = Database(driver)

        searchRepository = SearchRepositoryImpl(database, TestCoroutineDispatcher())
    }


    @Test
    fun testGetSearchQueries() = runBlockingTest {
        val cities = listOf("Montreal", "New York", "London")
        cities.forEach {
            searchRepository.handleSearchQuery(it)
        }

        val databaseCities = searchRepository.getSearchQueries().map {
            it.searchQuery
        }.reversed()

        assertEquals(cities, databaseCities)
    }
}