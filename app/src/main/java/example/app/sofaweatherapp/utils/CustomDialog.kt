package example.app.sofaweatherapp.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import example.app.sofaweatherapp.R

class CustomDialog(
    private val context: Context,
    private val dialog_res: Int,
    private val title: String,
    private val description: String,
    private val onConfirm: (() -> Unit)? = null
) {
    private lateinit var dialog: AlertDialog

    fun show() {
        val dialogView = LayoutInflater.from(context).inflate(dialog_res, null)
        val titleTextView = dialogView.findViewById<TextView>(R.id.dialog_title)
        val descriptionTextView = dialogView.findViewById<TextView>(R.id.dialog_description)
        titleTextView.text = title
        descriptionTextView.text = description

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)

        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<Button>(R.id.dialog_cancel_button).setOnClickListener {
            dialog.dismiss()
        }
        dialogView.findViewById<Button>(R.id.dialog_ok_button).setOnClickListener {
            onConfirm?.invoke()
            dialog.dismiss()
        }

        dialog.show()
    }
}