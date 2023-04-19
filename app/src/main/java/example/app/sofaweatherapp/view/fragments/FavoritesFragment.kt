package example.app.sofaweatherapp.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import example.app.sofaweatherapp.databinding.FragmentFavoritesBinding
import example.app.sofaweatherapp.view.adapters.FavoriteLocationRecyclerAdapter
import example.app.sofaweatherapp.viewmodel.ForecastViewModel

@AndroidEntryPoint
class FavoritesFragment : Fragment(), FavoriteLocationRecyclerAdapter.OnFavoriteItemClick {

    private var _binding: FragmentFavoritesBinding? = null
    private val forecastViewModel: ForecastViewModel by viewModels()
    private val binding get() = _binding!!
    private lateinit var favoriteLocationsRecyclerAdapter: FavoriteLocationRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        favoriteLocationsRecyclerAdapter =
            FavoriteLocationRecyclerAdapter(requireContext(), mutableListOf(), this)
        binding.favoriteLocationsRv.adapter = favoriteLocationsRecyclerAdapter

        setListeners()
        forecastViewModel.getAllFavoriteLocations()

        return binding.root
    }

    private fun setListeners() {
        forecastViewModel.forecastListData.observe(viewLifecycleOwner) { favoriteData ->
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
}
