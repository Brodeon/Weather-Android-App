package com.brodeon.pogoda.Models

import com.brodeon.pogoda.Setting
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherModel(val listener: OnDataLoaded) {

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val weatherService: WeatherService = retrofit.create(WeatherService::class.java)

    interface OnDataLoaded {
        fun onWeatherLoaded(weather: Weather)
    }

    fun downloadWeatherFromAPI(latitude: Double, longitude: Double) {
        val weatherCall: Call<Weather> = weatherService.getWeatherByCoordinates(latitude, longitude, Setting.API_KEY)
        weatherCall.enqueue(object: Callback<Weather> {
            override fun onFailure(call: Call<Weather>, t: Throwable) {
                //nic nie rob
            }

            override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                if (response.isSuccessful) {
                    response.body()?.let { weather ->
                        weather.weatherImage = chooseWeatherImage((weather.weather[0]["id"] as Double).toInt())
                        listener.onWeatherLoaded(weather)
                    }
                }
            }
        })
    }

    private fun chooseWeatherImage(weatherType: Int): String {
        when (weatherType) {
            in 0..300 -> return "tstorm1"
            in 301..500 -> return "light_rain"
            in 501..600 -> return "shower3"
            in 601..700 -> return "snow4"
            in 701..771 -> return "fog"
            in 772..799 -> return "tstorm3"
            800 -> return "sunny"
            in 801..804 -> return "cloudy2"
            in 900..903, in 905..1000 -> return "tstorm3"
            903 -> return "snow5"
            904 -> return "sunny"

        }
        return "unknown"
   }
}