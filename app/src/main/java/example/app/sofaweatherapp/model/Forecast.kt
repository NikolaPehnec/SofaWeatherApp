package example.app.sofaweatherapp.model


data class ForecastData(
    val location: LocationDetail,
    val forecast: Forecast,
    val current: WeatherCurrent
)

data class LocationDetail(
    val name: String,
    val region: String,
    val country: String,
    val tz_id: String,
    val localtime_epoch: Long,
    val lat: Double,
    val lon: Double
)

data class Forecast(
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val date_epoch: Long,
    val day: WeatherDay,
    val hour: List<WeatherHour>
)

