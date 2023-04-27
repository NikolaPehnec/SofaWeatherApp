package example.app.sofaweatherapp.utils

import android.content.Intent
import android.os.Build
import example.app.sofaweatherapp.R
import example.app.sofaweatherapp.view.activities.CityItemActivity

/**
 * Helper methods only for methods used in CityItemActivity
 */

fun CityItemActivity.getLocationNameFromIntent(intent: Intent): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        intent.getSerializableExtra(
            getString(R.string.location_key),
            String::class.java
        ) as String
    } else {
        intent.getSerializableExtra(getString(R.string.location_key)) as String
    }
}
