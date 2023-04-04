package example.app.sofaweatherapp.model

data class WeatherApiData(
    override val location: LocationDetail,
    val forecast: Forecast,
    override val current: WeatherCurrent
) : WeatherGeneralData {
    override val forecastDays: List<ForecastDay>
        get() = forecast.forecastday
}

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

data class WeatherCurrent(
    val condition: Condition,
    val temp_c: Float,
    val temp_f: Float,
    val pressure_mb: Int,
    val pressure_in: Float,
    val vis_km: Float,
    val vis_miles: Float,
    val wind_mph: Float,
    val wind_kph: Float,
    val wind_dir: String,
    val humidity: Int
)

data class WeatherDay(
    val condition: Condition,
    val avgtemp_c: Float,
    val avgtemp_f: Float,
    val maxtemp_c: Float,
    val maxtemp_f: Float,
    val mintemp_c: Float,
    val mintemp_f: Float
)

data class WeatherHour(
    val time: String,
    val time_epoch: Long,
    val temp_c: Float,
    val temp_f: Float,
    val condition: Condition
)

data class Condition(
    val text: String,
    val icon: String,
    val code: Int
)
