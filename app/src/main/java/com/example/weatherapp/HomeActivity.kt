package com.example.weatherapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.ui.pagehome.HomeFragment

class HomeActivity : AppCompatActivity() {
    /*
    init kodin( for depency injection)
     */

//    override val kodein: Kodein by kodein()
//    private val weatherViewModelFactory: WeatherViewModelFactory by instance()
//    private val weatherPhotoViewModel: WeatherPhotoViewModel by lazy {
//        ViewModelProvider(
//            this,
//            weatherViewModelFactory
//        ).get(weatherPhotoViewModel::class.java)
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, HomeFragment.newInstance())
                .commitNow()
        }
    }
}
