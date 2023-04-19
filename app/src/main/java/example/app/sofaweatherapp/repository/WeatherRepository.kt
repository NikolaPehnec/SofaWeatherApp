package example.app.sofaweatherapp.repository

import android.content.Context
import example.app.sofaweatherapp.api.WeatherServiceApi
import example.app.sofaweatherapp.dao.WeatherDao
import example.app.sofaweatherapp.model.Location
import example.app.sofaweatherapp.model.Result
import example.app.sofaweatherapp.model.WeatherGeneralData
import example.app.sofaweatherapp.utils.Constants
import example.app.sofaweatherapp.utils.NetworkChecker
import javax.inject.Inject

open class WeatherRepository @Inject constructor(
    val context: Context,
    private val weatherServiceApi: WeatherServiceApi,
    private val weatherDao: WeatherDao
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
            val dbData = weatherDao.loadLocationWeatherData(location)
            if (dbData != null)
                return Result.Success(dbData)
            else return Result.Error(Exception("No data in DB"))
        }
    }
}
