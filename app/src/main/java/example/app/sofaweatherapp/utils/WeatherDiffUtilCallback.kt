package example.app.sofaweatherapp.utils

import androidx.recyclerview.widget.DiffUtil
import example.app.sofaweatherapp.model.WeatherGeneralData

class WeatherDiffUtilCallback(
    private val oldList: List<WeatherGeneralData>, private val newList: List<WeatherGeneralData>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].location.name == newList[newItemPosition].location.name

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].location.name == newList[newItemPosition].location.name
                && oldList[oldItemPosition].location.name == newList[newItemPosition].location.name
                && oldList[oldItemPosition].current.condition.icon == newList[newItemPosition].current.condition.icon
                && oldList[oldItemPosition].location.localtime_epoch == newList[newItemPosition].location.localtime_epoch
                && oldList[oldItemPosition].favorite == newList[newItemPosition].favorite
                && oldList[oldItemPosition].location.name == newList[newItemPosition].location.name
                && oldList[oldItemPosition].current.temp_c == newList[newItemPosition].current.temp_c

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}