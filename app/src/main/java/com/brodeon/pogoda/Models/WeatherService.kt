package com.brodeon.pogoda.Models

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("data/2.5/weather")
    fun getWeatherByCoordinates(@Query("lat") latitude: Double,@Query("lon") longitude: Double,@Query("appid") appID: String): Call<Weather>



}