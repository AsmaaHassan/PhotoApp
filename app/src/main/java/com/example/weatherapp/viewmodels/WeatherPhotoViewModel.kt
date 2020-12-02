package com.example.weatherapp.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.data.internal.PostEntity
import com.example.weatherapp.models.Weather
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File

class WeatherPhotoViewModel(
    private val weatherRepository: WeatherRepository,
    application: Application
) : AndroidViewModel(application) {

    val TAG = "WeatherPhotoViewModel"

    /**
    LiveData
     **/
    val liveDataSavedPhotos: MutableLiveData<ArrayList<String>> =
        MutableLiveData<ArrayList<String>>()

    val liveDataCurrentFile: MutableLiveData<File> =
        MutableLiveData<File>()

    val liveDataWeather: MutableLiveData<Weather> =
        MutableLiveData<Weather>()

    val liveDataCapturedImage: MutableLiveData<Bitmap> =
        MutableLiveData<Bitmap>()

    val liveDataCapturedImagePath: MutableLiveData<String> =
        MutableLiveData<String>()

    val liveDataLatitude: MutableLiveData<Double> =
        MutableLiveData<Double>()

    val liveDataLongitude: MutableLiveData<Double> =
        MutableLiveData<Double>()

    val liveDataImages: MutableLiveData<ArrayList<Bitmap>> =
        MutableLiveData<ArrayList<Bitmap>>()

    val isLoading = MutableLiveData<Boolean>()

    val errorMessage = MutableLiveData<String>()
    val liveDataIsPostsEmpty: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()


    fun getWeatherInfo() {
        Log.i(TAG, "lat: " + liveDataLatitude.value)
        Log.i(TAG, "lang:" + liveDataLongitude.value)
        if (liveDataLatitude.value != null && liveDataLongitude.value != null) {
            weatherRepository.getWeatherData(liveDataLatitude.value!!, liveDataLongitude.value!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { data ->
                        liveDataWeather.value = data
                        Log.i(TAG, "getWeatherInfo() - data: " + data)
                    },
                    { e ->
//                        errorMessage.value = e.message
                        Log.i(TAG, "getWeatherInfo() - error: " + e.message)
                    },
                    {
                        println("onComplete")
                    }
                )
        }
//            .subscribe(this@WeatherPhotoViewModel, Observer { weatherData ->
//            })
    }

    fun savePhoto() {
        if (liveDataCapturedImagePath.value != null)
            weatherRepository.insertPhoto(liveDataCapturedImagePath.value!!)
    }


    fun getSavedPhotos() {
        weatherRepository.getSavedPhotos()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { data ->
                    setSavedPostsValue(data)
                    Log.i(TAG, "getSavesPhotos() - data: " + data)
                },
                { e ->
                    errorMessage.value = e.message
                    Log.i(TAG, "getSavesPhotos() -error: " + e.message)
                },
                {
                    println("getSavesPhotos() - onComplete")
                }
            )
    }


    private fun setSavedPostsValue(data: List<PostEntity>) {
        Log.i(TAG, "setSavedPosts()")
        liveDataSavedPhotos.value = ArrayList()
//        if (data.size>0) {
            val stringList: ArrayList<String> = ArrayList()
            for (element in data) {
                Log.i(TAG, "setSavedPosts() - data")
                stringList.add(element.image_path)
            }
            liveDataSavedPhotos.value = stringList
//        }
    }


}