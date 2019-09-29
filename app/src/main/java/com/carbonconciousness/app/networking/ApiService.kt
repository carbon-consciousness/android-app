package com.carbonconciousness.app.networking

import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {

    @GET("/carbon_footprint")
    fun status(): Observable<Model.Result>

    @GET("/demo_carbon_footprint")
    fun demo_data(): Observable<Model.DemoResult>

    companion object {
        fun create() : ApiService {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://carboccc.eu-gb.mybluemix.net/")
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}

object Model {
    data class Result(val step_counter: Float, val carbon_footprint: Float)
    data class DemoResult(val step_counter: Float, val carbon_footprint: Float)
}