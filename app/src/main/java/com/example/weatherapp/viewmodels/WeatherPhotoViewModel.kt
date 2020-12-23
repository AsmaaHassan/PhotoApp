package com.example.weatherapp.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.movielist.ConnectivityUtil
import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.data.internal.PostEntity
import com.example.weatherapp.models.Weather
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File

class WeatherPhotoViewModel(
    private val weatherRepository: WeatherRepository,
    application: Application
) : AndroidViewModel(application) {

    val TAG = "WeatherPhotoViewModel"
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()


    /**
    LiveData
     **/
    val liveDataSavedPhotos: MutableLiveData<List<PostEntity>> =
        MutableLiveData<List<PostEntity>>()

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

    val isLoading = MutableLiveData<Boolean>()

    val errorMessage = MutableLiveData<String>()


    fun getWeatherInfo() {
        val isConnected = ConnectivityUtil.isInternetAvailable(getApplication())
        if (isConnected) {
            if (liveDataLatitude.value != null && liveDataLongitude.value != null) {
                val disposable: Disposable = weatherRepository.getWeatherData(
                    liveDataLatitude.value!!,
                    liveDataLongitude.value!!
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { data ->
                            liveDataWeather.value = data
                            isLoading.value = false
                            Log.i(TAG, "getWeatherInfo() - data: " + data)
                        },
                        { e ->
                        errorMessage.value = e.message
                            Log.i(TAG, "getWeatherInfo() - error: " + e.message)
                            isLoading.value = false
                        },
                        {
                            println("onComplete")
                        }
                    )
                compositeDisposable.add(disposable)
            }
        } else {
            isLoading.value = false
            errorMessage.value = "Check internet connection"
        }
    }

    fun savePhoto() {
        if (liveDataCapturedImagePath.value != null)
            weatherRepository.insertPhoto(liveDataCapturedImagePath.value!!)
    }


    fun getSavedPhotos() {
        val disposable: Disposable = weatherRepository.getSavedPhotos()
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
        compositeDisposable.add(disposable)
    }


    private fun setSavedPostsValue(data: List<PostEntity>) {
        liveDataSavedPhotos.value = data
    }


    fun dispose() {
        compositeDisposable.dispose()
    }
}