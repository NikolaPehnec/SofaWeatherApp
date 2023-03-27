package example.app.sofaweatherapp.networking

import example.app.sofaweatherapp.model.Location
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("v1/search.json")
    suspend fun searchCity(@Query("key") key: String, @Query("q") query: String): List<Location>

}