package example.app.sofaweatherapp.model

data class WeatherApiData(
    override val location: LocationDetail,
    val forecast: Forecast,
    override val current: WeatherCurrent,
    override var favorite: Boolean?
) : WeatherGeneralData {
    override val forecastDays: List<ForecastDay>
        get() = forecast.forecastday
}

