package example.app.sofaweatherapp.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

object UtilityFunctions {
    //private val sdf = SimpleDateFormat("E, MMMM dd|HH:mm aa (zz)", Locale.US)
    private val sdfday = SimpleDateFormat("EE", Locale.US)
    private val dtf = DateTimeFormatter.ofPattern("EE, MMM dd|HH:mm a").withLocale(Locale.US)

    fun showErrorSnackBar(message: String, view: View) {
        Snackbar.make(
            view,
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    fun epochToDateTimeAtTimeZone(epochSeconds: Long, timeZone: String): String {
        val dateTime = Instant.ofEpochMilli(epochSeconds * 1000)
            .atZone(ZoneId.of(timeZone))

        return dtf.format(dateTime)
    }

    fun getNameOfDayFromDate(epochSeconds: Long): String = sdfday.format(Date(epochSeconds * 1000))


}