package com.brodeon.pogoda.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.brodeon.pogoda.Models.Weather
import com.brodeon.pogoda.Models.WeatherModel

class WeatherViewModel: ViewModel(), WeatherModel.OnDataLoaded {

    private lateinit var weather: MutableLiveData<Weather>
    private val weatherModel: WeatherModel = WeatherModel(this)

    fun getWeather(): LiveData<Weather> {
        if (!::weather.isInitialized) {
            weather = MutableLiveData()
        }

        return weather
    }

    fun loadWeather(latitude: Double, longitude: Double) {
        weatherModel.downloadWeatherFromAPI(latitude, longitude)
    }

    override fun onWeatherLoaded(weather: Weather) {
        this.weather.value = weather
    }
}