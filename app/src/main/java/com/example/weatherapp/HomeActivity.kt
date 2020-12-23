package com.example.weatherapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.ui.pagehome.HomeFragment
import com.example.weatherapp.ui.pagephoto.PhotoFragment
import com.example.weatherapp.viewmodels.WeatherPhotoViewModel
import com.example.weatherapp.viewmodels.WeatherViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.android.support.kodein
import org.kodein.di.generic.instance

class HomeActivity : AppCompatActivity() , KodeinAware  {

    /**
    Kodine to provide dependencies instances
     **/
    override val kodein: Kodein by kodein()
    private val weatherViewModelFactory: WeatherViewModelFactory by instance()
    private val weatherPhotoViewModel: WeatherPhotoViewModel by lazy {
        ViewModelProvider(
            this,
            weatherViewModelFactory
        ).get(WeatherPhotoViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, HomeFragment.newInstance())
                .commitNow()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        weatherPhotoViewModel.dispose()
    }
}
