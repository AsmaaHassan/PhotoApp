package com.example.weatherapp.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.data.WeatherRepository


class WeatherViewModelFactory(private val weatherRepository: WeatherRepository, private val application: Application) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WeatherPhotoViewModel(weatherRepository, application) as T
    }
}