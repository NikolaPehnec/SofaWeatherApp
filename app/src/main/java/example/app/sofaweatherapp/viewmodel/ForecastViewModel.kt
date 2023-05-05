package example.app.sofaweatherapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import example.app.sofaweatherapp.dao.WeatherDao
import example.app.sofaweatherapp.model.Result
import example.app.sofaweatherapp.model.WeatherGeneralData
import example.app.sofaweatherapp.repository.WeatherRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val weatherDao: WeatherDao
) : ViewModel() {
    private val _forecastData = MutableLiveData<WeatherGeneralData>()
    val forecastResponseData: LiveData<WeatherGeneralData> = _forecastData

    private val _favoriteForecastListData = MutableLiveData<List<WeatherGeneralData>>()
    val favoriteForecastListData: LiveData<List<WeatherGeneralData>> = _favoriteForecastListData

    private val _forecastResponseError = MutableLiveData<String>()
    val forecastResponseError: LiveData<String> = _forecastResponseError

    fun searchForecast(locationName: String) {
        viewModelScope.launch {
            when (val result = weatherRepository.getForecast(locationName)) {
                is Result.Success -> _forecastData.postValue(result.data)
                is Result.Error -> _forecastResponseError.postValue(result.exception.message)
            }
        }
    }

    private fun getMultipleForecasts(locationNames: List<String>) {
        viewModelScope.launch {
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

            _favoriteForecastListData.postValue(weatherData)
        }
    }

    fun updateFavoriteLocation(favorite: Boolean, locationName: String) {
        viewModelScope.launch {
            weatherDao.updateFavoriteLocation(favorite, locationName)
            // If I dont update liveData onCreate of fragment observer is called with old data, then with new fetched data
            if (!favorite) {
                _favoriteForecastListData.postValue(
                    _favoriteForecastListData.value?.filter { it.location.name.lowercase() != locationName }
                        ?.toList()
                )
            }
        }
    }

    fun getAllFavoriteLocations() {
        viewModelScope.launch {
            val result = weatherDao.getAllFavoriteLocations()
            _favoriteForecastListData.postValue(result)
            getMultipleForecasts(result.map { l -> l.location.name }.toList())
        }
    }

    fun clearAllFavoriteLocations() {
        viewModelScope.launch {
            _favoriteForecastListData.postValue(listOf())
            weatherDao.clearAllFavoriteLocations()
        }
    }
}
