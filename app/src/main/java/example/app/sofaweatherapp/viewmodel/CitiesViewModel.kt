package example.app.sofaweatherapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import example.app.sofaweatherapp.model.Error
import example.app.sofaweatherapp.model.Location
import example.app.sofaweatherapp.networking.Network
import example.app.sofaweatherapp.utils.Constants
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CitiesViewModel : ViewModel() {
    private val _citiesList = MutableLiveData<List<Location>>()
    val citiesList: LiveData<List<Location>> = _citiesList

    private val _citiesResponseError = MutableLiveData<Error>()
    val citiesResponseError: LiveData<Error> = _citiesResponseError

    fun searchCities(query: String) {
        viewModelScope.launch {
            try {
                val searchResponse = Network().getService().searchCity(Constants.API_KEY, query)
                searchResponse.let {
                    it?.let {
                        _citiesList.value = it
                    }
                }
            } catch (e: HttpException) {
                e.printStackTrace()
                _citiesResponseError.value = Error(e.code(), e.message())
            } catch (e: Exception) {
                _citiesResponseError.value = Error(400, e.toString())
            }
        }
    }

}