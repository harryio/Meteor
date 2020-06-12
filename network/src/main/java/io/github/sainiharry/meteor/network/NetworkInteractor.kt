package io.github.sainiharry.meteor.network

import com.squareup.moshi.Moshi
import io.reactivex.Scheduler
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class NetworkInteractor(val scheduler: Scheduler) {

    private val retrofitMap = mutableMapOf<Api, Retrofit>()

    companion object {

        private var networkInteractor: NetworkInteractor? = null

        fun getRetrofit(api: Api, scheduler: Scheduler): Retrofit {
            if (networkInteractor == null) {
                networkInteractor = NetworkInteractor(scheduler)
            }

            return networkInteractor!!.getRetrofit(api)
        }
    }

    private fun getRetrofit(api: Api): Retrofit {
        val retrofit = retrofitMap[api]
        return if (retrofit == null) {
            val apiOkHttpClient = api.buildOkHttpClient(baseOkHttpClient.newBuilder())
                .addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
                .build()
            val apiMoshi = api.buildMoshi(baseMoshi.newBuilder()).build()
            val apiRetrofit = api.buildRetrofit()
                .client(apiOkHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(scheduler))
                .addConverterFactory(MoshiConverterFactory.create(apiMoshi))
                .build()
            retrofitMap[api] = apiRetrofit
            apiRetrofit
        } else {
            retrofit
        }
    }

    private val baseOkHttpClient by lazy {
        OkHttpClient.Builder().build()
    }

    private val baseMoshi by lazy {
        Moshi.Builder().build()
    }
}

interface Api {

    fun buildRetrofit(): Retrofit.Builder

    fun buildOkHttpClient(okHttpBuilder: OkHttpClient.Builder): OkHttpClient.Builder = okHttpBuilder

    fun buildMoshi(moshiBuilder: Moshi.Builder): Moshi.Builder = moshiBuilder
}