package example.app.sofaweatherapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import example.app.sofaweatherapp.model.ForecastData
import example.app.sofaweatherapp.model.Result
import example.app.sofaweatherapp.networking.WeatherService
import kotlinx.coroutines.launch

class ForecastViewModel : ViewModel() {
    private val _forecastData = MutableLiveData<ForecastData>()
    val forecastResponseData: LiveData<ForecastData> = _forecastData

    private val _forecastResponseError = MutableLiveData<String>()
    val forecastResponseError: LiveData<String> = _forecastResponseError

    fun searchForecast(locationName: String) {
        viewModelScope.launch {

            when (val result = WeatherService().getForecast(locationName)) {
                is Result.Success -> _forecastData.value = result.data
                is Result.Error -> _forecastResponseError.value = result.exception.message
            }
        }
    }
}
