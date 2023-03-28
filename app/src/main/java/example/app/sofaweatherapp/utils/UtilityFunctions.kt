package example.app.sofaweatherapp.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

object UtilityFunctions {

    fun showErrorSnackBar(message: String, view: View) {
        Snackbar.make(
            view,
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }
}