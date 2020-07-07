package io.github.sainiharry.meteor.network

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Provides basic implementation of network stack. Provides a Retrofit instance with shared resources
 * which can be used by clients to create network service classes. Uses Moshi as library for
 * serialization/deserialization of network request and responses.
 */
object NetworkInteractor {

    private val retrofitMap = mutableMapOf<Api, Retrofit>()

    fun getRetrofit(api: Api): Retrofit {
        val retrofit = retrofitMap[api]
        return if (retrofit == null) {
            val apiOkHttpClient = api.buildOkHttpClient(baseOkHttpClient.newBuilder())
                .addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
                .build()
            val apiMoshi = api.buildMoshi(baseMoshi.newBuilder()).build()
            val apiRetrofit = api.buildRetrofit()
                .client(apiOkHttpClient)
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

/**
 * Network stack for a network Api
 */
interface Api {

    /**
     * Provides basic implementation of [Retrofit.Builder]. This Builder is used for creating
     * cached instances of [Retrofit] for this particular [Api]
     * @return instance of [Retrofit.Builder]
     */
    fun buildRetrofit(): Retrofit.Builder

    /**
     * Provides basic implementation of [OkHttpClient.Builder]. The returned builder is used for
     * creating networking layer of cached instances of [Retrofit] for this particular [Api]
     * @param okHttpBuilder a shared instance of [OkHttpClient.Builder] among different Api's
     * @return instance of [OkHttpClient.Builder]
     */
    fun buildOkHttpClient(okHttpBuilder: OkHttpClient.Builder): OkHttpClient.Builder = okHttpBuilder

    /**
     * Provides basic implementation of [Moshi.Builder]. The returned builder is used for adding
     * serialization/deserialization capabilities to the cached instance of [Retrofit] for this
     * particular [Api]
     * @param moshiBuilder a shared instance of [Moshi.Builder] among different Api's
     * @return instance of [Moshi.Builder]
     */
    fun buildMoshi(moshiBuilder: Moshi.Builder): Moshi.Builder = moshiBuilder
}