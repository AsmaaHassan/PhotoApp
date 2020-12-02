package com.example.movielist.data.remote

import io.reactivex.Observable
import com.example.weatherapp.models.Weather
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*


interface ServiceApi {
    @GET("/data/2.5/weather")
    fun getCurrentWeather(@Query("lat") lat: Double, @Query("lon")long: Double): Observable<Weather>
}