package com.example.myapplication.util.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstanse {
    private const val BASE_URL = "http://79.174.81.166:8000"

    private val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .readTimeout(60, TimeUnit.SECONDS) // Установка тайм-аута на чтение (60 секунд)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(interceptor)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL).client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    val api: NetworkApi by lazy {
        retrofit.create(NetworkApi::class.java)
    }
}
