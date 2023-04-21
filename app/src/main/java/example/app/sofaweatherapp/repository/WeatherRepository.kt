package example.app.sofaweatherapp.repository

import android.content.Context
import example.app.sofaweatherapp.api.WeatherServiceApi
import example.app.sofaweatherapp.dao.WeatherDao
import example.app.sofaweatherapp.model.Location
import example.app.sofaweatherapp.model.LocationWeather
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
        if (NetworkChecker(context).isOnline()) {
            val resultApi = apiCall(call = {
                weatherServiceApi.searchForecast(
                    Constants.API_KEY,
                    location,
                    Constants.NUM_OF_FORECAST_DAYS
                )
            })

            if (resultApi is Result.Success) {
                val isFavorite =
                    weatherDao.getFavoriteFromLocation(resultApi.data.location.name.lowercase())
                resultApi.data.favorite = isFavorite ?: false

                //Refreshing DB value
                weatherDao.saveLocation(
                    LocationWeather(
                        resultApi.data.location.name.lowercase(),
                        resultApi.data.location,
                        resultApi.data.current,
                        resultApi.data.forecastDays,
                        isFavorite ?: false
                    )
                )
            }
            return resultApi
        } else {
            //Loading CityItemActivity offline
            val dbData = weatherDao.loadLocationWeatherData(location)
            return if (dbData != null)
                Result.Success(dbData)
            else Result.Error(Exception("No data in DB"))
        }
    }
}
