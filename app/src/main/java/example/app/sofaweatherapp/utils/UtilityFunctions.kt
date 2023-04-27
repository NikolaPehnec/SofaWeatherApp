package example.app.sofaweatherapp.utils

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import example.app.sofaweatherapp.R
import example.app.sofaweatherapp.model.ForecastDay
import example.app.sofaweatherapp.model.WeatherCurrent
import example.app.sofaweatherapp.model.WeatherHour
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

object UtilityFunctions {
    // private val sdf = SimpleDateFormat("E, MMMM dd|HH:mm aa (zz)", Locale.US)
    private val sdfday = SimpleDateFormat("EE", Locale.US)
    private val dtf = DateTimeFormatter.ofPattern("EE, MMM dd|HH:mm a").withLocale(Locale.US)

    fun makeErrorSnackBar(
        view: View,
        anchorView: View?,
        message: String,
        context: Context
    ): Snackbar {
        val ctw = ContextThemeWrapper(context, R.style.CustomSnackbarTheme)
        return Snackbar.make(
            ctw,
            view,
            message,
            Snackbar.LENGTH_SHORT
        ).setBackgroundTint(
            ContextCompat.getColor(context, R.color.status_error_snackbar)
        ).setIcon(
            AppCompatResources.getDrawable(context, R.drawable.ic_baseline_close_24)!!,
            ContextCompat.getColor(context, R.color.on_color)
        ).setTextColor(ContextCompat.getColor(context, R.color.on_color)).setAnchorView(anchorView)
    }

    fun makeNotifiationSnackBar(
        view: View,
        anchorView: View?,
        message: String,
        context: Context
    ): Snackbar {
        val ctw = ContextThemeWrapper(context, R.style.CustomSnackbarTheme)
        return Snackbar.make(
            ctw,
            view,
            message,
            Snackbar.LENGTH_SHORT
        ).setBackgroundTint(
            ContextCompat.getColor(context, R.color.neutrals_n_lv_1)
        ).setIcon(
            AppCompatResources.getDrawable(context, R.drawable.ic_baseline_close_white_24)!!,
            ContextCompat.getColor(context, R.color.on_color)
        ).setTextColor(ContextCompat.getColor(context, R.color.on_color)).setAnchorView(anchorView)
    }

    fun epochToDateTimeAtTimeZone(epochSeconds: Long, timeZone: String): String {
        val dateTime = Instant.ofEpochMilli(epochSeconds * 1000).atZone(ZoneId.of(timeZone))

        return dtf.format(dateTime)
    }

    fun getNameOfDayFromDate(epochSeconds: Long): String = sdfday.format(Date(epochSeconds * 1000))

    fun getUnitFromSharedPreferences(context: Context) =
        context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE).getString(
            Constants.UNIT_PREF_KEY,
            Constants.UNIT_METRIC
        )!!

    fun saveUnitPreference(unit: String, context: Context) {
        val sharedPreferences =
            context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(Constants.UNIT_PREF_KEY, unit)
        editor.apply()
    }

    fun getUnitTempValueFromItem(unit: String, context: Context, item: Any): String {
        return when (item) {
            is WeatherHour -> if (unit == Constants.UNIT_METRIC) {
                context.getString(
                    R.string.temp_value,
                    item.temp_c.toInt().toString(),
                    context.getString(R.string.temp_unit)
                )
            } else {
                context.getString(
                    R.string.temp_value,
                    item.temp_f.toInt().toString(),
                    context.getString(R.string.temp_unit_F)
                )
            }
            is ForecastDay -> if (unit == Constants.UNIT_METRIC) {
                context.getString(
                    R.string.temp_value,
                    item.day.avgtemp_c.toInt().toString(),
                    context.getString(R.string.temp_unit)
                )
            } else {
                context.getString(
                    R.string.temp_value,
                    item.day.avgtemp_f.toInt().toString(),
                    context.getString(R.string.temp_unit_F)
                )
            }
            is WeatherCurrent -> if (unit == Constants.UNIT_METRIC) {
                context.getString(
                    R.string.temp_value,
                    item.temp_c.roundToInt().toString(),
                    context.getString(R.string.temp_unit)
                )
            } else {
                context.getString(
                    R.string.temp_value,
                    item.temp_f.roundToInt().toString(),
                    context.getString(R.string.temp_unit_F)
                )
            }
            else -> ""
        }
    }

    fun getUnitValueOfAttribute(
        unit: String,
        context: Context,
        item: WeatherCurrent,
        attribute: String
    ): String = when (attribute) {
        Constants.WIND -> context.getString(
            R.string.wind_value,
            if (unit == Constants.UNIT_METRIC) item.wind_kph.toString() else item.wind_mph,
            context.getString(if (unit == Constants.UNIT_METRIC) R.string.wind_unit else R.string.wind_unit_mph),
            item.wind_dir
        )
        Constants.PRESSURE -> context.getString(
            R.string.pressure_value,
            if (unit == Constants.UNIT_METRIC) item.pressure_mb.toString() else item.pressure_in.toString(),
            context.getString(if (unit == Constants.UNIT_METRIC) R.string.pressure_unit else R.string.pressure_unit_imp)
        )
        Constants.VISIBILITY -> context.getString(
            R.string.visibility_value,
            if (unit == Constants.UNIT_METRIC) item.vis_km.toString() else item.vis_miles.toString(),
            context.getString(if (unit == Constants.UNIT_METRIC) R.string.visibility_unit else R.string.visibility_unit_miles)
        )
        else -> ""
    }

    fun getUnitValueOfMinMaxTemp(
        unit: String,
        context: Context,
        item: ForecastDay
    ): String {
        return if (unit == Constants.UNIT_METRIC) {
            val minTemp = item.day.mintemp_c.toInt().toString()
            val maxTemp = item.day.maxtemp_c.toInt().toString()
            val unitVal = context.getString(R.string.temp_unit)
            context.getString(
                R.string.temp_min_max_value,
                minTemp,
                unitVal,
                maxTemp,
                unitVal
            )
        } else {
            val minTemp = item.day.mintemp_f.toInt().toString()
            val maxTemp = item.day.maxtemp_f.toInt().toString()
            val unitVal = context.getString(R.string.temp_unit_F)
            context.getString(
                R.string.temp_min_max_value,
                minTemp,
                unitVal,
                maxTemp,
                unitVal
            )
        }
    }
}
