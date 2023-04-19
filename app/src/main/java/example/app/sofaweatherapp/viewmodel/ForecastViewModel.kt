package example.app.sofaweatherapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import example.app.sofaweatherapp.dao.WeatherDao
import example.app.sofaweatherapp.model.LocationWeather
import example.app.sofaweatherapp.model.Result
import example.app.sofaweatherapp.model.WeatherGeneralData
import example.app.sofaweatherapp.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val weatherDao: WeatherDao
) :
    ViewModel() {
    private val _forecastData = MutableLiveData<WeatherGeneralData>()
    val forecastResponseData: LiveData<WeatherGeneralData> = _forecastData

    private val _forecastListData = MutableLiveData<List<WeatherGeneralData>>()
    val forecastListData: LiveData<List<WeatherGeneralData>> = _forecastListData

    private val _forecastResponseError = MutableLiveData<String>()
    val forecastResponseError: LiveData<String> = _forecastResponseError

    fun searchForecast(locationName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (
                val result = weatherRepository.getForecast(locationName)
            ) {
                is Result.Success -> {
                    result.data.apply {
                        val isFavorite =
                            weatherDao.getFavoriteFromLocation(result.data.location.name.lowercase())
                        result.data.favorite = isFavorite ?: false

                        weatherDao.saveLocation(
                            LocationWeather(
                                location.name.lowercase(),
                                location,
                                current,
                                forecastDays,
                                isFavorite ?: false
                            )
                        )
                        _forecastData.postValue(result.data)
                    }
                }
                is Result.Error -> _forecastResponseError.postValue(result.exception.message)
            }
        }
    }

    fun searchMultipleForecasts(locationNames: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val weatherData = locationNames.map { name ->
                async {
                    val result = weatherRepository.getForecast(name)
                    if (result is Result.Success) {
                        result.data
                    } else {
                        null
                    }
                }
            }.awaitAll().filterNotNull()

            _forecastListData.postValue(weatherData)
        }
    }

    fun updateFavoriteLocation(favorite: Boolean, locationName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherDao.updateFavoriteLocation(favorite, locationName)
        }
    }

    /**
     * Load favorite locations from DB, refresh from API
     */
    fun getAllFavoriteLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = weatherDao.getAllFavoriteLocations()
            _forecastListData.postValue(result)

            searchMultipleForecasts(result.map { l -> l.location.name }.toList())
        }
    }
}
