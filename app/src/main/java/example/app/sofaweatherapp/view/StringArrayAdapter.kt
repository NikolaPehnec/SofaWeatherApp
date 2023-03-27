package example.app.sofaweatherapp.view

import android.content.Context
import android.widget.ArrayAdapter

class StringArrayAdapter(context: Context, res: Int) : ArrayAdapter<String>(context, res) {
    val _items = mutableListOf<String>()

    fun addItems(items: ArrayList<String>) {
        _items.addAll(items)
    }


}