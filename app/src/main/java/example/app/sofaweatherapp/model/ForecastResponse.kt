package example.app.sofaweatherapp.model


data class ForecastResponse(
    val error: ForecastError?,
    val location: LocationDetail?,
    val forecast: Forecast?,
    val current: WeatherCurrent?
) : java.io.Serializable


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
    val lat: Float,
    val long: Float
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
    val pressure_mb: Float,
    val pressure_in: Float,
    val vis_km: Int,
    val vis_miles: Int,
    val humidty: Int,
)

data class WeatherHour(
    val time: String,
    val time_epoch: Long,
    val temp_c: Float,
    val temp_f: Float,
    val condition: Condition,
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

data class Condition(
    val text: String,
    val icon: String,
    val code: Int
)

data class ForecastError(
    val code: Int,
    val message: String
)