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

    private val _favoriteData = MutableLiveData<List<LocationWeather>>()
    val favoriteData: LiveData<List<LocationWeather>> = _favoriteData

    private val _forecastResponseError = MutableLiveData<String>()
    val forecastResponseError: LiveData<String> = _forecastResponseError

    fun searchForecast(locationName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (
                val result = weatherRepository.getForecast(locationName)
            ) {
                //Updatea uvijek na false, promijenit
                is Result.Success -> {
                    result.data.apply {
                        val isFavorite =
                            weatherDao.getFavoriteFromLocation(result.data.location.name.lowercase())
                        result.data.favorite = isFavorite ?: false


                        println("SQL IS FAVORITE:" + isFavorite)
                        weatherDao.saveLocation(
                            LocationWeather(
                                location.name.lowercase(),
                                location,
                                current,
                                forecastDays,
                                isFavorite ?: false
                            )
                        )
                        println("SQL SAVED LOCATION:" + isFavorite)

                        _forecastData.postValue(result.data)
                    }

                }
                is Result.Error -> _forecastResponseError.postValue(result.exception.message)
            }
        }
    }

    fun updateFavoriteLocation(favorite: Boolean, locationName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherDao.updateFavoriteLocation(favorite, locationName)
        }
    }

    fun getAllFavoriteLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = weatherDao.getAllFavoriteLocations()
            _favoriteData.postValue(result)
        }
    }
}
