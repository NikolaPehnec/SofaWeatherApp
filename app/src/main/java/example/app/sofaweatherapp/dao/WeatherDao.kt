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
        "SELECT * FROM location_weather WHERE favorite = 1"
    )
    suspend fun getAllFavoriteLocations(): List<LocationWeather>

    @Query(
        "UPDATE location_weather SET favorite = 0"
    )
    suspend fun clearAllFavoriteLocations()

    @Query(
        "SELECT favorite FROM location_weather WHERE locationName = :locationName"
    )
    suspend fun getFavoriteFromLocation(locationName: String): Boolean?

    @Upsert
    suspend fun saveLocation(locationWeather: LocationWeather)

    @Update
    suspend fun updateLocation(locationWeather: LocationWeather)

    @Query("UPDATE location_weather SET favorite=:favorite WHERE locationName = :locationName")
    suspend fun updateFavoriteLocation(favorite: Boolean, locationName: String)
}