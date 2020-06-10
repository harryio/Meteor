package io.github.sainiharry.meteor.network

import com.squareup.moshi.Moshi
import io.reactivex.Scheduler
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

private const val BASE_API_URL = "http://api.openweathermap.org/data/"

class NetworkInteractor(val apiKey: String, val defaultScheduler: Scheduler) {

    companion object {

        private var networkInteractor: NetworkInteractor? = null

        fun getOpenWeatherService(apiKey: String, defaultScheduler: Scheduler): OpenWeatherService {
            if (networkInteractor == null) {
                networkInteractor = NetworkInteractor(apiKey, defaultScheduler)
            }

            return networkInteractor!!.openWeatherService
        }
    }

    private val openWeatherService by lazy {
        getRetrofit().create(OpenWeatherService::class.java)
    }

    private fun getRetrofit() = Retrofit.Builder().baseUrl(BASE_API_URL)
        .client(getOkHttpClient())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(defaultScheduler))
        .addConverterFactory(MoshiConverterFactory.create(getMoshi()))
        .build()

    private fun getMoshi() = Moshi.Builder().build()

    private fun getOkHttpClient() = OkHttpClient.Builder()
        .addInterceptor(ApiKeyInterceptor(apiKey))
        .build()
}