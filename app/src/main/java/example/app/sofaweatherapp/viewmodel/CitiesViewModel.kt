package example.app.sofaweatherapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import example.app.sofaweatherapp.model.ForecastData
import example.app.sofaweatherapp.model.ForecastError
import example.app.sofaweatherapp.model.Location
import example.app.sofaweatherapp.networking.Network
import example.app.sofaweatherapp.utils.Constants
import kotlinx.coroutines.launch

class CitiesViewModel : ViewModel() {
    private val _citiesList = MutableLiveData<List<Location>>()
    val citiesList: LiveData<List<Location>> = _citiesList

    private val _forecastResponseData = MutableLiveData<ForecastData>()
    val forecastResponseData: LiveData<ForecastData> = _forecastResponseData

    private val _forecastResponseError = MutableLiveData<ForecastError>()
    val forecastResponseError: LiveData<ForecastError> = _forecastResponseError

    fun searchCities(query: String) {
        viewModelScope.launch {
            val searchResponse = Network().getService().searchCity(Constants.API_KEY, query)
            searchResponse.let {
                _citiesList.value = it
            }
        }
    }

    fun searchForecast(locationName: String, numOfDays: Int) {
        viewModelScope.launch {
            val searchResponse =
                Network().getService().searchForecast(Constants.API_KEY, locationName, numOfDays)
            searchResponse.let { it ->

                it.error?.let { err ->
                    _forecastResponseError.value = err
                }

                if (it.forecast != null && it.location != null && it.current != null) {
                    _forecastResponseData.value =
                        ForecastData(it.location, it.forecast, it.current)
                }

            }
        }
    }


}