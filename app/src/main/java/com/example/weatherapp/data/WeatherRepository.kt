package com.example.weatherapp.data

import com.example.movielist.data.remote.RemoteDataSource
import com.example.weatherapp.data.internal.PostEntity
import com.example.weatherapp.data.internal.WeatherDataBase
import com.example.weatherapp.models.Weather
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Callable


class WeatherRepository(
    private val remoteDataSource: RemoteDataSource,
    private val weatherDataBase: WeatherDataBase
) {
    //********Remote APIs********************
    fun getWeatherData(lat: Double, lon: Double): Observable<Weather> {
        return remoteDataSource.getWeather(lat, lon)
    }


    fun insertPhoto(photoPath: String) {
        val timeStamp: String =
            SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().time)

        var postEntity = PostEntity(photoPath, timeStamp)
        Observable.fromCallable(Callable {
            weatherDataBase.weatherDao().insert(postEntity)
        })
            .subscribeOn(Schedulers.io())
            .subscribe()
    }


    fun getSavedPhotos(): Observable<List<PostEntity>> {
        return weatherDataBase.weatherDao().getPosts()
    }

}
