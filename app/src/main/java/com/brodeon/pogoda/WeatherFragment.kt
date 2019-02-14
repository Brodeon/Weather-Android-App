package com.brodeon.pogoda


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.brodeon.pogoda.ViewModels.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_weather.*

class WeatherFragment : Fragment() {

    private val TAG: String = "WeatherFrag"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var temperatureTextView: TextView
    private lateinit var cityTextView: TextView
    private lateinit var weatherImageView: ImageView
    private lateinit var swipeLayout: SwipeRefreshLayout

    private val MY_PERMISSIONS_REQUEST_LOCATION: Int = 101

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity as AppCompatActivity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        temperatureTextView = temp_tv
        cityTextView = city_tv
        weatherImageView = weather_image

        swipeLayout = swipe_layout
        swipeLayout.setOnRefreshListener {
            requestLocationAndWeather()
        }


        weatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel::class.java)
        weatherViewModel.getWeather().observe(this, Observer {
            temperatureTextView.text = "${convertTemperatureToCelcius(it.main["temp"].toString().toDouble())}"
            cityTextView.text = it.name

            if (!it.weatherImage.equals("unknown")) {
                weatherImageView.setImageResource(resources.getIdentifier(it.weatherImage, "drawable", context?.packageName))
            }
        })

        checkLocationPermission()
    }

    private fun requestLocationAndWeather() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    location?.let {
                        Log.d(TAG, "latitude = ${it.latitude}, longitude = ${it.longitude}")
                        weatherViewModel.loadWeather(it.latitude, it.longitude)
                    }
                }
        } catch(e: SecurityException) {
            e.printStackTrace()
        } finally {
            swipeLayout.isRefreshing = false
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            requestLocationPermission()
        } else {
            requestLocationAndWeather()
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity as AppCompatActivity, Manifest.permission.ACCESS_COARSE_LOCATION)) {

            val snackbar = Snackbar.make(activity!!.findViewById(android.R.id.content),
                "Please grant a location permission for checking weather in Your location",
                Snackbar.LENGTH_INDEFINITE)

            snackbar.setAction("Enable") {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), MY_PERMISSIONS_REQUEST_LOCATION)
            }

        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), MY_PERMISSIONS_REQUEST_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    requestLocationAndWeather()
                } else {
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), MY_PERMISSIONS_REQUEST_LOCATION)
                }
            }
        }
    }

    fun convertTemperatureToCelcius(temperature: Double): String {
        return (temperature - 273).toInt().toString() + "°"
    }




}
