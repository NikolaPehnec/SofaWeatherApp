package example.app.sofaweatherapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import example.app.sofaweatherapp.model.Location
import example.app.sofaweatherapp.networking.Network
import example.app.sofaweatherapp.utils.Constants
import kotlinx.coroutines.launch

class CitiesViewModel : ViewModel() {
    private val _citiesList = MutableLiveData<List<Location>>()
    val citiesList: LiveData<List<Location>> = _citiesList

    fun searchCities(query: String) {
        viewModelScope.launch {
            val searchResponse = Network().getService().searchCity(Constants.API_KEY, query)
            searchResponse.let {
                _citiesList.value = it
            }
        }
    }

}