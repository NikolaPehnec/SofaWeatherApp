package example.app.sofaweatherapp.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import example.app.sofaweatherapp.model.ForecastDay

class ForecastDayListConverter {

    @TypeConverter
    fun fromForecastDayList(value: List<ForecastDay>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toForecastDayList(value: String): List<ForecastDay> {
        return try {
            Gson().fromJson<List<ForecastDay>>(value) // using extension function
        } catch (e: Exception) {
            arrayListOf()
        }
    }

    inline fun <reified T> Gson.fromJson(json: String) =
        fromJson<T>(json, object : TypeToken<T>() {}.type)
}
