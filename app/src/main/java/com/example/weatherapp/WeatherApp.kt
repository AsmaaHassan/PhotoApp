package com.example.weatherapp

import android.app.Application
import com.example.movielist.data.remote.RemoteDataSource
import com.example.movielist.data.remote.RetrofitProvider
import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.data.internal.WeatherDataBase
import com.example.weatherapp.data.internal.WeatherDataBase.Companion.getDatabase
import com.example.weatherapp.viewmodels.WeatherViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import retrofit2.Retrofit

class WeatherApp : Application(), KodeinAware {
    override fun onCreate() {
        super.onCreate()
//        RxJavaPlugins.setErrorHandler(Timber::e)

    }

    override val kodein by Kodein.lazy {
        bind<Retrofit>() with singleton { RetrofitProvider.getInstance() }
        bind() from singleton { RemoteDataSource(instance()) }
        bind() from singleton { WeatherDataBase.getDatabase(this@WeatherApp) }

        bind() from singleton { WeatherRepository(instance(), instance()) }

        bind() from provider {
            WeatherViewModelFactory(instance(), this@WeatherApp)
        }
    }
}