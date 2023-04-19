package example.app.sofaweatherapp.api

import example.app.sofaweatherapp.model.Location
import example.app.sofaweatherapp.model.WeatherApiData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherServiceApi {

    @GET("v1/search.json")
    suspend fun searchCity(
        @Query("key") key: String,
        @Query("q") city: String
    ): Response<List<Location>>

    @GET("v1/forecast.json")
    suspend fun searchForecast(
        @Query("key") key: String,
        @Query("q") locationName: String,
        @Query("days") forecastDays: Int
    ): Response<WeatherApiData>
}