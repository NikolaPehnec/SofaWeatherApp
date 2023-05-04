package example.app.sofaweatherapp.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import example.app.sofaweatherapp.R
import example.app.sofaweatherapp.databinding.LocationFavoriteItemBinding
import example.app.sofaweatherapp.model.WeatherGeneralData
import example.app.sofaweatherapp.utils.Constants
import example.app.sofaweatherapp.utils.ItemTouchHelperAdapter
import example.app.sofaweatherapp.utils.OnStartDragListener
import example.app.sofaweatherapp.utils.UtilityFunctions
import example.app.sofaweatherapp.utils.UtilityFunctions.getUnitFromSharedPreferences
import example.app.sofaweatherapp.utils.UtilityFunctions.getUnitTempValueFromItem
import java.util.*

class FavoriteLocationRecyclerAdapter(
    private val context: Context,
    private var items: MutableList<WeatherGeneralData>,
    private val onItemClick: OnItemClick,
    private val dragStartListener: OnStartDragListener,
    var editState: Boolean = false
) :
    RecyclerView.Adapter<FavoriteLocationRecyclerAdapter.ViewHolderWeather>(),
    ItemTouchHelperAdapter {

    val unit: String = getUnitFromSharedPreferences(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderWeather {
        return ViewHolderWeather(
            LocationFavoriteItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            dragStartListener
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

        // Checking if new list of items has any of the old items removed
        val itemsToDelete = mutableListOf<WeatherGeneralData>()
        for (oldItem in items) {
            var deletedItem = true
            if (newItems.any { ni -> ni.location.name == oldItem.location.name }) {
                deletedItem = false
            }
            if (deletedItem) {
                itemsToDelete.add(oldItem)
            }
        }
        for (item in itemsToDelete) {
            val indexDeleted = items.indexOf(item)
            items.remove(item)
            notifyItemRemoved(indexDeleted)
        }
    }

    fun removeItem(item: WeatherGeneralData) {
        val index = items.indexOf(item)
        items.removeAt(index)
        notifyItemRemoved(index)
    }

    fun setRecyclerEditState(newEditState: Boolean) {
        editState = newEditState
        for (i in items.indices)
            notifyItemChanged(i, Constants.UPDATE_PAYLOAD)
    }

    override fun getItemCount(): Int = items.size

    interface OnItemClick {
        fun onFavoriteItemClick(favorite: Boolean, locationName: String)
        fun onItemClick(locationName: String)
    }

    inner class ViewHolderWeather(
        private val binding: LocationFavoriteItemBinding,
        private val dragStartListener: OnStartDragListener? = null
    ) :
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
            binding.reorderim.visibility = if (editState) View.VISIBLE else View.GONE

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
                onItemClick.onFavoriteItemClick(false, item.location.name.lowercase())
                removeItem(item)
            }
            binding.cv.setOnClickListener {
                onItemClick.onItemClick(item.location.name.lowercase())
            }

            binding.reorderim?.setOnTouchListener(
                View.OnTouchListener { v, event ->
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        dragStartListener?.onStartDrag(this@ViewHolderWeather)
                    }
                    false
                }
            )
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(items, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemDismiss(position: Int) {
    }
}
