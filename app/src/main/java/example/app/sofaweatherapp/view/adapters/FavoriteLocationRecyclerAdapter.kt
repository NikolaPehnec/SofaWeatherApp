package example.app.sofaweatherapp.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import coil.load
import example.app.sofaweatherapp.R
import example.app.sofaweatherapp.databinding.LocationFavoriteItemBinding
import example.app.sofaweatherapp.model.WeatherGeneralData
import example.app.sofaweatherapp.utils.Constants
import example.app.sofaweatherapp.utils.UtilityFunctions
import example.app.sofaweatherapp.utils.UtilityFunctions.getUnitFromSharedPreferences
import example.app.sofaweatherapp.utils.UtilityFunctions.getUnitTempValueFromItem

class FavoriteLocationRecyclerAdapter(
    private val context: Context,
    private var items: MutableList<WeatherGeneralData>,
    private val onFavoriteItemClick: OnFavoriteItemClick
) :
    RecyclerView.Adapter<FavoriteLocationRecyclerAdapter.ViewHolderWeather>() {

    private var unit = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderWeather {
        unit = getUnitFromSharedPreferences(context)
        return ViewHolderWeather(
            LocationFavoriteItemBinding.inflate(
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

    override fun onBindViewHolder(
        holder: ViewHolderWeather,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty() && payloads[0] == Constants.UPDATE_PAYLOAD) {
            holder.bind(items[position])
        } else {
            holder.itemView.animation =
                AnimationUtils.loadAnimation(holder.itemView.context, R.anim.animation_recycler)
            holder.bind(items[position])
        }
    }

    fun addItems(newItems: List<WeatherGeneralData>) {
        for (item in newItems) {
            if (items.any { i -> i.location.name == item.location.name }) {
                val position = items.indexOf(
                    items.first { i -> i.location.name == item.location.name }
                )
                items[position] = item
                notifyItemChanged(position, Constants.UPDATE_PAYLOAD)
            } else {
                items.add(item)
                notifyItemInserted(items.size - 1)
            }
        }
    }

    fun removeItem(item: WeatherGeneralData) {
        val index = items.indexOf(item)
        items.removeAt(index)
        notifyItemRemoved(index)
    }

    override fun getItemCount(): Int = items.size

    interface OnFavoriteItemClick {
        fun onFavoriteItemClick(favorite: Boolean, locationName: String)
    }

    inner class ViewHolderWeather(private val binding: LocationFavoriteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WeatherGeneralData) {
            binding.conditionImage.load(Constants.HTTPS_PREFIX + item.current.condition.icon)
            binding.locationName.text = item.location.name
            binding.tempValue.text = getUnitTempValueFromItem(unit, context, item.current)
            val dateTime = UtilityFunctions.epochToDateTimeAtTimeZone(
                item.location.localtime_epoch,
                item.location.tz_id
            )
            binding.time.text = dateTime.split("|")[1]
            binding.timezone.text = item.location.tz_id

            binding.favoriteImage.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    if (item.favorite != false) R.drawable.ic_baseline_star_24 else R.drawable.ic_baseline_star_outline_24
                )
            )
            binding.favoriteImage.setOnClickListener {
                binding.favoriteImage.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_baseline_star_outline_24
                    )
                )
                onFavoriteItemClick.onFavoriteItemClick(false, item.location.name.lowercase())
                removeItem(item)
            }
        }
    }
}
