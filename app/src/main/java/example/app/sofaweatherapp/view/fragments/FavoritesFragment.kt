package example.app.sofaweatherapp.view.fragments

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import example.app.sofaweatherapp.R
import example.app.sofaweatherapp.databinding.FragmentFavoritesBinding
import example.app.sofaweatherapp.utils.OnStartDragListener
import example.app.sofaweatherapp.utils.ReorderHelperCallback
import example.app.sofaweatherapp.view.adapters.FavoriteLocationRecyclerAdapter
import example.app.sofaweatherapp.viewmodel.ForecastViewModel

@AndroidEntryPoint
class FavoritesFragment :
    Fragment(),
    FavoriteLocationRecyclerAdapter.OnFavoriteItemClick,
    MenuProvider,
    OnStartDragListener {

    private var _binding: FragmentFavoritesBinding? = null
    private val forecastViewModel: ForecastViewModel by activityViewModels()
    private val binding get() = _binding!!
    private lateinit var favoriteLocationsRecyclerAdapter: FavoriteLocationRecyclerAdapter
    private var mItemTouchHelper: ItemTouchHelper? = null
    private var mMenu: Menu? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        favoriteLocationsRecyclerAdapter =
            FavoriteLocationRecyclerAdapter(requireContext(), mutableListOf(), this, this)
        binding.favoriteLocationsRv.adapter = favoriteLocationsRecyclerAdapter
        val callback: ItemTouchHelper.Callback =
            ReorderHelperCallback(favoriteLocationsRecyclerAdapter)
        mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper?.attachToRecyclerView(binding.favoriteLocationsRv)

        setListeners()
        forecastViewModel.getAllFavoriteLocations()

        return binding.root
    }

    private fun setListeners() {
        forecastViewModel.favoriteForecastListData.observe(viewLifecycleOwner) { favoriteData ->
            favoriteLocationsRecyclerAdapter.addItems(favoriteData)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onFavoriteItemClick(favorite: Boolean, locationName: String) {
        forecastViewModel.updateFavoriteLocation(favorite, locationName)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        mMenu = menu
        menuInflater.inflate(R.menu.favorite_fragment_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.edit -> {
                favoriteLocationsRecyclerAdapter.setRecyclerEditState(true)
                menuItem.isVisible = false
                mMenu?.findItem(R.id.done)?.isVisible = true
                return true
            }
            R.id.done -> {
                favoriteLocationsRecyclerAdapter.setRecyclerEditState(false)
                menuItem.isVisible = false
                mMenu?.findItem(R.id.edit)?.isVisible = true
                return true
            }
            else -> false
        }
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder?) {
        viewHolder?.let {
            mItemTouchHelper?.startDrag(it)
        }
    }
}
