package example.app.sofaweatherapp.utils

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import example.app.sofaweatherapp.R
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

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
        ).setTextColor(ContextCompat.getColor(context, R.color.on_color))
            .setAnchorView(anchorView)
    }

    fun epochToDateTimeAtTimeZone(epochSeconds: Long, timeZone: String): String {
        val dateTime = Instant.ofEpochMilli(epochSeconds * 1000)
            .atZone(ZoneId.of(timeZone))

        return dtf.format(dateTime)
    }

    fun getNameOfDayFromDate(epochSeconds: Long): String = sdfday.format(Date(epochSeconds * 1000))
}
