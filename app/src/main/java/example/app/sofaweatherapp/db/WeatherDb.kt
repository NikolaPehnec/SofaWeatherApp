package example.app.sofaweatherapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import example.app.sofaweatherapp.dao.WeatherDao
import example.app.sofaweatherapp.model.LocationWeather
import example.app.sofaweatherapp.utils.ForecastDayListConverter

@Database(entities = [LocationWeather::class], version = 3)
@TypeConverters(ForecastDayListConverter::class)
abstract class WeatherDb : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao
}
