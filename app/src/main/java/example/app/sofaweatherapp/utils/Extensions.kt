package example.app.sofaweatherapp.utils

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.annotation.ColorInt
import com.google.android.material.R
import com.google.android.material.snackbar.Snackbar

fun Snackbar.setIcon(drawable: Drawable, @ColorInt colorTint: Int): Snackbar {
    return this.apply {
        setAction(" ") {}
        val textView = view.findViewById<TextView>(R.id.snackbar_action)
        textView.text = ""

        drawable.setTint(colorTint)
        drawable.setTintMode(PorterDuff.Mode.SRC_ATOP)
        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
    }
}
