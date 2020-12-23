package com.example.weatherapp.ui.pagephoto

import android.Manifest
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.example.weatherapp.R
import com.example.weatherapp.models.Weather
import com.example.weatherapp.models.WeatherItem
import com.example.weatherapp.utils.Constants
import com.example.weatherapp.utils.LOCATION_PERMISSION_REQUEST_CODE
import com.example.weatherapp.utils.PermissionUtils
import com.example.weatherapp.utils.UiUtils
import com.example.weatherapp.viewmodels.WeatherPhotoViewModel
import com.example.weatherapp.viewmodels.WeatherViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.fragment_photo.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.support.kodein
import org.kodein.di.generic.instance
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class PhotoFragment : Fragment(), KodeinAware {
    val TAG = "PhotoFragment"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var imageFile: File? = null
    var bitmap: Bitmap? = null

    /**
    Kodine to provide dependencies instances
     **/
    override val kodein: Kodein by kodein()
    private val weatherViewModelFactory: WeatherViewModelFactory by instance()
    private val weatherPhotoViewModel: WeatherPhotoViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            weatherViewModelFactory
        ).get(WeatherPhotoViewModel::class.java)
    }

    companion object {
        fun newInstance() = PhotoFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //trigger if GPS status is changed
        getContext()?.registerReceiver(
            gpsReceiver,
            IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        )

        //check permissions and get data
        checkLocationPermission()

        setObservablesLiveData()

        steShareClickListener()
    }


    private fun steShareClickListener() {
        imShare.setOnClickListener {
            if (weatherPhotoViewModel.liveDataCurrentFile.value != null && activity != null) {
                UiUtils.sharePhoto(activity!!, weatherPhotoViewModel.liveDataCurrentFile.value!!)
            } else {
                weatherPhotoViewModel.errorMessage.value = "Error occurred while sharing"
            }
        }
    }


    private fun setObservablesLiveData() {
        weatherPhotoViewModel.errorMessage.observe(viewLifecycleOwner, Observer { errMsg ->
            Toast.makeText(activity, errMsg, Toast.LENGTH_SHORT).show()
        })

        weatherPhotoViewModel.liveDataWeather.observe(viewLifecycleOwner, Observer { weatherData ->
            if (weatherData != null) {
                generateWeatherDataOverTheImage(weatherData)
            }
        })

        weatherPhotoViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            prgPhoto.visibility =
                if (isLoading) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        })
    }


    private fun checkLocationPermission() {
        if (!isLocationPermissionGranted())
            PermissionUtils.checkForPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION,
                LOCATION_PERMISSION_REQUEST_CODE
            )
        else {
            if (checkLocationEnabled())
                getCurrentLocation()
        }

    }


    private fun isLocationPermissionGranted(): Boolean {
        return PermissionUtils.isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)
    }


    fun checkLocationEnabled(): Boolean {
        val lm: LocationManager =
            context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder(context)
                .setMessage(R.string.gps_network_not_enabled)
                .setPositiveButton(R.string.open_location_settings) { _, _ ->
                    context!!.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .setNegativeButton(R.string.Cancel, null)
                .show()
            return false
        } else {
            return true
        }
    }


    private fun getCurrentLocation() {
        if (activity != null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    weatherPhotoViewModel.liveDataLatitude.value = location?.latitude
                    weatherPhotoViewModel.liveDataLongitude.value = location?.longitude

                    //get weather data
                    getWeatherData()
                }
        }
    }


    private fun getWeatherData() {
        Log.i(TAG, "getWeatherData")
        weatherPhotoViewModel.isLoading.value = true
        weatherPhotoViewModel.getWeatherInfo()
    }


    /**
     * this method sets all required views over the image and then convert the ViewGroup to bitmap.
     *
     * @param weatherModel data that will be displayed in the views
     */
    private fun generateWeatherDataOverTheImage(weatherModel: Weather) {
        setViews(weatherModel)
    }

    fun convertViewToBitmap(view: View): Bitmap? {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas)
        else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        bitmap = returnedBitmap
        return returnedBitmap
    }


    private fun setViews(weatherModel: Weather) {
        locationNameTv.text =
            java.lang.String.format(
                "%s, %s",
                weatherModel.name,
                weatherModel.sys?.country ?: ""
            )

        val weatherItems: List<WeatherItem?>? = weatherModel.weather
        if (weatherItems != null && weatherItems.size > 0) {
            tempStatusTv.text = weatherItems[0]?.main
            val urlIcon: String = Constants.ICON_URL + weatherItems[0]?.icon + Constants.ICON_TYPE
            Glide.with(activity!!).load(urlIcon).into(imTempStatus)
        }

        tempTv.setText(
            java.lang.String.format(
                "%s ° ",
                weatherModel.main?.temp?.let { Math.round(it) }
            )
        )
        minMaxTv.setText(
            java.lang.String.format(
                "%s ° / %s °",
                weatherModel.main?.temp_max?.let { Math.round(it) },
                weatherModel.main?.temp_min?.let { Math.round(it) }
            )
        )

        Glide.with(activity!!).asBitmap().load(weatherPhotoViewModel.liveDataCapturedImage.value)
            .into(object : CustomViewTarget<View, Bitmap>(overlay) {
                override fun onLoadFailed(errorDrawable: Drawable?) {}
                override fun onResourceCleared(placeholder: Drawable?) {}
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    val d = BitmapDrawable(resource)
                    overlay.background = d
                    convertViewToBitmap(clPhoto)
                    add(bitmap!!)
                }
            })
    }


    fun add(bitmap: Bitmap) {
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(Date())
        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(Date())
        val imageFileName = "image_" + timeStamp + "_"

        val storageDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        val file = File(storageDirectory, imageFileName + ".png")
        val fOut = FileOutputStream(file)

        bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut)
        fOut.flush()
        fOut.close()
        afterAddFile(file)
    }


    fun afterAddFile(file: File) {
        weatherPhotoViewModel.liveDataCapturedImagePath.value = file.path
        weatherPhotoViewModel.liveDataCurrentFile.value = file
        Log.i(TAG, "filePath - " + weatherPhotoViewModel.liveDataCapturedImagePath.value)
        imShare.visibility = View.VISIBLE
        weatherPhotoViewModel.savePhoto()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                // permission was granted.
                if (checkLocationEnabled())
                    getCurrentLocation()
            }
//            else {
            // permission denied
//                PermissionUtils.showApplicationSettingsDialog(activity)
//            }
        }

    }

    /*
    trigger after user enable or disable the location setting
     */
    private val gpsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action!!.equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                // Make an action or refresh an already managed state.
                val locationManager =
                    context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                val isNetworkEnabled =
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                // START DIALOG ACTIVITY

                // START DIALOG ACTIVITY
                if (isGpsEnabled || isNetworkEnabled) {
                    //Do your stuff on GPS status change
                    getCurrentLocation()
                }

            }
        }
    }


}
