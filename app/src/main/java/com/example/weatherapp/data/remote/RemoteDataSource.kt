package com.example.movielist.data.remote

import androidx.lifecycle.LiveData
import com.example.weatherapp.models.Weather
import retrofit2.Retrofit
import java.util.*
import io.reactivex.Observable

class RemoteDataSource(retrofit: Retrofit) {
    private val serviceApi: ServiceApi = retrofit.create(ServiceApi::class.java)

    fun getWeather(lat: Double, lon: Double): Observable<Weather> {
        return serviceApi.getCurrentWeather(lat, lon)
    }
}