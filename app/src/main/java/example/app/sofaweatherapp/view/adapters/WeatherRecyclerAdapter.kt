package example.app.sofaweatherapp.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import coil.load
import example.app.sofaweatherapp.R
import example.app.sofaweatherapp.databinding.WeatherItemDetailBinding
import example.app.sofaweatherapp.model.ForecastDay
import example.app.sofaweatherapp.model.WeatherHour
import example.app.sofaweatherapp.utils.Constants
import example.app.sofaweatherapp.utils.UtilityFunctions

class WeatherRecyclerAdapter(
    private val context: Context,
    private var items: MutableList<Any>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<WeatherRecyclerAdapter.ViewHolderWeather>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderWeather {
        return ViewHolderWeather(
            WeatherItemDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolderWeather, position: Int) {
        holder.itemView.animation =
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.animation_recycler)
        holder.bind(items[position])
    }

    fun addItems(newItems: List<Any>) {
        for (item in newItems) {
            if (!items.contains(item)) {
                items.add(item)
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    interface OnItemClickListener {
        fun onItemClick(item: Any)
    }

    inner class ViewHolderWeather(private val binding: WeatherItemDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Any) {
            when (item) {
                is WeatherHour -> {
                    binding.title.text = item.time.split(" ")[1]
                    binding.image.load(Constants.HTTPS_PREFIX + item.condition.icon)
                    binding.value.text = context.getString(
                        R.string.temp_value,
                        item.temp_c.toInt().toString(),
                        context.getString(R.string.temp_unit)
                    )
                }

                is ForecastDay -> {
                    binding.title.text = UtilityFunctions.getNameOfDayFromDate(item.date_epoch)
                    binding.image.load(Constants.HTTPS_PREFIX + item.day.condition.icon)
                    binding.value.text = context.getString(
                        R.string.temp_value,
                        item.day.avgtemp_c.toInt().toString(),
                        context.getString(R.string.temp_unit)
                    )
                }
            }
        }
    }
}
