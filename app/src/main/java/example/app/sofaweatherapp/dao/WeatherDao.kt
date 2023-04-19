package example.app.sofaweatherapp.dao

import androidx.room.*
import example.app.sofaweatherapp.model.LocationWeather

@Dao
interface WeatherDao {
    @Query(
        "SELECT * FROM location_weather WHERE locationName = :locationName"
    )
    suspend fun loadLocationWeatherData(locationName: String): LocationWeather?

    @Query(
        "SELECT * FROM location_weather WHERE favorite = true"
    )
    suspend fun loadFavoriteLocations(): LocationWeather?

    @Upsert
    suspend fun saveLocation(locationWeather: LocationWeather)

    @Update
    suspend fun updateLocation(locationWeather: LocationWeather)
}