package example.app.sofaweatherapp.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_weather")
class LocationWeather(
    @PrimaryKey
    val locationName: String,
    @Embedded
    override val location: LocationDetail,
    @Embedded
    override val current: WeatherCurrent,
    override val forecastDays: List<ForecastDay>,
    val favorite: Boolean = false
) : WeatherGeneralData
