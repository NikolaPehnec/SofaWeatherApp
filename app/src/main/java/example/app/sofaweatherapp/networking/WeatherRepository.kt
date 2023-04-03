package example.app.sofaweatherapp.networking

import android.content.Context
import example.app.sofaweatherapp.model.Location
import example.app.sofaweatherapp.model.Result
import example.app.sofaweatherapp.model.WeatherApiData
import example.app.sofaweatherapp.model.WeatherGeneralData
import example.app.sofaweatherapp.utils.Constants
import example.app.sofaweatherapp.utils.NetworkChecker
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

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

open class WeatherRepository @Inject constructor(
    val context: Context,
    private val weatherServiceApi: WeatherServiceApi
) : BasicRepository() {

    suspend fun searchCities(cityName: String): Result<List<Location>> =
        apiCall(call = { weatherServiceApi.searchCity(Constants.API_KEY, cityName) })

    suspend fun getForecast(location: String): Result<WeatherGeneralData> {
        return if (NetworkChecker(context).isOnline()) {
            apiCall(call = {
                weatherServiceApi.searchForecast(
                    Constants.API_KEY,
                    location,
                    Constants.NUM_OF_FORECAST_DAYS
                )
            })
        } else {
            // Future DB Call
            apiCall(call = {
                weatherServiceApi.searchForecast(
                    Constants.API_KEY,
                    location,
                    Constants.NUM_OF_FORECAST_DAYS
                )
            })
        }
    }
}
