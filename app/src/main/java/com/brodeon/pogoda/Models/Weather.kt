package com.brodeon.pogoda.Models

data class Weather(
    var main: Map<String, Any>,
    var weather: Array<Map<String, Any>>,
    var name: String
) {
    var weatherImage: String = "unknown"
}
