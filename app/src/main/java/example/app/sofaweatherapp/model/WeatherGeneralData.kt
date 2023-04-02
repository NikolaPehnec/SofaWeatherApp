package example.app.sofaweatherapp.model

interface WeatherGeneralData {
    val location: LocationDetail
    val forecastDays: List<ForecastDay>
    val current: WeatherCurrent
}
