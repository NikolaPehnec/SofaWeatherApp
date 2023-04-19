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

    private val _forecastResponseError = MutableLiveData<String>()
    val forecastResponseError: LiveData<String> = _forecastResponseError

    fun searchForecast(locationName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (
                val result = weatherRepository.getForecast(locationName)
            ) {
                //Updatea uvijek na false
                is Result.Success -> {
                    result.data.apply {
                        weatherDao.saveLocation(
                            LocationWeather(
                                location.name,
                                location,
                                current,
                                forecastDays,
                                false
                            )
                        )
                        _forecastData.postValue(result.data)
                    }

                }
                is Result.Error -> _forecastResponseError.postValue(result.exception.message)
            }
        }
    }

    fun updateLocationData(locationWeather: LocationWeather) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherDao.updateLocation(locationWeather)
        }
    }
}
