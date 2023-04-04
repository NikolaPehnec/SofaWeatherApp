package example.app.sofaweatherapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import example.app.sofaweatherapp.model.Result
import example.app.sofaweatherapp.model.WeatherGeneralData
import example.app.sofaweatherapp.networking.WeatherRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForecastViewModel @Inject constructor(private val weatherRepository: WeatherRepository) :
    ViewModel() {
    private val _forecastData = MutableLiveData<WeatherGeneralData>()
    val forecastResponseData: LiveData<WeatherGeneralData> = _forecastData

    private val _forecastResponseError = MutableLiveData<String>()
    val forecastResponseError: LiveData<String> = _forecastResponseError

    fun searchForecast(locationName: String) {
        viewModelScope.launch {
            when (
                val result = weatherRepository.getForecast(locationName)
            ) {
                is Result.Success -> _forecastData.value = result.data
                is Result.Error -> _forecastResponseError.value = result.exception.message
            }
        }
    }
}
