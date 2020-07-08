package io.github.sainiharry.meteor

import android.app.Application
import io.github.sainiharry.meteor.repositories.news.newsRepositoryModule
import io.github.sainiharry.meteor.weatherrepository.weatherRepositoryModule
import io.github.sainiharry.searchrepository.searchRepositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MeteorApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MeteorApp)

            modules(weatherRepositoryModule, newsRepositoryModule, searchRepositoryModule)
        }
    }
}