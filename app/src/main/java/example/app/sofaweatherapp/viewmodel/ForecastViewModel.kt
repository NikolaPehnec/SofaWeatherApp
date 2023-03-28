package example.app.sofaweatherapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import example.app.sofaweatherapp.model.Error
import example.app.sofaweatherapp.model.ForecastData
import example.app.sofaweatherapp.networking.Network
import example.app.sofaweatherapp.utils.Constants
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ForecastViewModel : ViewModel() {
    private val _forecastResponseData = MutableLiveData<ForecastData>()
    val forecastResponseData: LiveData<ForecastData> = _forecastResponseData

    private val _forecastResponseError = MutableLiveData<Error>()
    val forecastResponseError: LiveData<Error> = _forecastResponseError


    fun searchForecast(locationName: String, numOfDays: Int) {
        viewModelScope.launch {
            try {
                val searchResponse =
                    Network().getService()
                        .searchForecast(Constants.API_KEY, locationName, numOfDays)
                searchResponse.let {

                    it.error?.let { err ->
                        _forecastResponseError.value = err
                    }

                    if (it.forecast != null && it.location != null && it.current != null) {
                        _forecastResponseData.value =
                            ForecastData(it.location, it.forecast, it.current)
                    }
                }
            } catch (e: HttpException) {
                e.printStackTrace()
                _forecastResponseError.value = Error(e.code(), e.message())
            } catch (e: Exception) {
                _forecastResponseError.value = Error(400, e.toString())
            }
        }
    }

}