package example.app.sofaweatherapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import example.app.sofaweatherapp.model.Location
import example.app.sofaweatherapp.model.Result
import example.app.sofaweatherapp.networking.WeatherService
import kotlinx.coroutines.launch

class CitiesViewModel : ViewModel() {
    private val _citiesList = MutableLiveData<List<Location>>()
    val citiesList: LiveData<List<Location>> = _citiesList

    private val _citiesResponseError = MutableLiveData<String>()
    val citiesResponseError: LiveData<String> = _citiesResponseError

    fun searchCities(location: String) {
        viewModelScope.launch {

            when (val result = WeatherService().searchCities(location)) {
                is Result.Success -> _citiesList.value =
                    result.data
                is Result.Error -> _citiesResponseError.value =
                    result.exception.message
            }
        }
    }
}
