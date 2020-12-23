package com.example.weatherapp.ui.pagehome

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.ui.pagephoto.PhotoFragment
import com.example.weatherapp.ui.pagehome.adapters.ImagesAdapter
import com.example.weatherapp.utils.MULTIPLE_PERMISSION_REQUEST_CODE
import com.example.weatherapp.utils.PermissionUtils
import com.example.weatherapp.viewmodels.WeatherPhotoViewModel
import com.example.weatherapp.viewmodels.WeatherViewModelFactory
import kotlinx.android.synthetic.main.fragment_home.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.support.kodein
import org.kodein.di.generic.instance


class HomeFragment : Fragment(), KodeinAware {
    val REQUEST_CODE = 200
    val TAG = "HomeActivity"


    /*
    init kodin( for depency injection)
     */
    companion object {
        fun newInstance() = HomeFragment()
    }

    override val kodein: Kodein by kodein()
    private val weatherViewModelFactory: WeatherViewModelFactory by instance()
    private val weatherPhotoViewModel: WeatherPhotoViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            weatherViewModelFactory
        ).get(WeatherPhotoViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setClickListeners()

        setObservablesLiveData()

        getPostsFromDB()

    }


    private fun getPostsFromDB() {
        weatherPhotoViewModel.getSavedPhotos()
    }


    private fun setObservablesLiveData() {
        weatherPhotoViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                prgHome.visibility = View.VISIBLE
            } else {
                prgHome.visibility = View.GONE
            }
        })

        weatherPhotoViewModel.errorMessage.observe(viewLifecycleOwner, Observer { errMsg ->
            Toast.makeText(activity, errMsg, Toast.LENGTH_SHORT).show()
        })


        weatherPhotoViewModel.liveDataSavedPhotos.observe(
            viewLifecycleOwner,
            Observer { imagesArray ->
                if (imagesArray.size != 0) {
                    setViewsImagesExist()
                } else {
                    setViewsImagesNotExist()
                }
            })

    }

    /* set view if images array holds data */
    private fun setViewsImagesNotExist() {
        fabAddPost.hide()
        llAddPost.visibility = View.VISIBLE
        rvPosts.visibility = View.GONE
        weatherPhotoViewModel.isLoading.value = false
    }

    /* set view if images array size is 0 */
    private fun setViewsImagesExist() {
        updateRecyclerView()
        fabAddPost.show()
        rvPosts.visibility = View.VISIBLE
        llAddPost.visibility = View.GONE
        weatherPhotoViewModel.isLoading.value = false
    }


    private fun updateRecyclerView() {
        rvPosts.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            if (weatherPhotoViewModel.liveDataSavedPhotos.value != null)
                adapter = ImagesAdapter(
                    weatherPhotoViewModel.liveDataSavedPhotos.value!!,
                    this@HomeFragment.context
                )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {

        if (requestCode == MULTIPLE_PERMISSION_REQUEST_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                // permission was granted.
                addNewPost()
            }
//            else {
                // permission denied
//                PermissionUtils.showApplicationSettingsDialog(activity)
//            }
        }

    }


    private fun setClickListeners() {
        llAddPost.setOnClickListener { addNewPost() }
        fabAddPost.setOnClickListener { addNewPost() }
    }


    fun openCamera() {
        Log.i("openCamera", "openCamera")
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && data != null) {
            imageSuccess(data.extras?.get("data") as Bitmap)
        }
    }


    private fun imageSuccess(bitmap: Bitmap) {
            weatherPhotoViewModel.liveDataCapturedImage.value = bitmap
            navigateToPhoto()
    }


    private fun navigateToPhoto() {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(
            R.id.container,
            PhotoFragment()
        )?.addToBackStack("a")
        transaction?.commit()
    }


    fun addNewPost() {
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
        if (PermissionUtils.isAllPermissionGranted(this, permissions)) {
            Log.i(TAG, "permissionAccepted")
            openCamera()
        } else {
            Log.i(TAG, "permissionDenied")
            PermissionUtils.checkForPermissions(this, permissions)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

}
