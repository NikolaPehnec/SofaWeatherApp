package example.app.sofaweatherapp.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import example.app.sofaweatherapp.databinding.FragmentFavoritesBinding
import example.app.sofaweatherapp.viewmodel.ForecastViewModel

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val forecastViewModel: ForecastViewModel by viewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        setListeners()
        forecastViewModel.getAllFavoriteLocations()

        return binding.root
    }

    private fun setListeners() {
        forecastViewModel.favoriteData.observe(viewLifecycleOwner) { favoriteData ->

            println("SQL FAVORITE PODACI DOŠLI")
            println("SQL " + favoriteData.size)
            for (data in favoriteData)
                println("SQL " + data.locationName)
/*            fillBasicInformation(forecastData.location, forecastData.current)
            fillWeatherFeatures(forecastData.current, forecastData.forecastDays)

            launchMapFragment(
                forecastData.location.lat,
                forecastData.location.lon,
                forecastData.location.name
            )

            if (forecastData.forecastDays.isNotEmpty()) {
                todayWeatherRecyclerAdapter.addItems(forecastData.forecastDays[0].hour)
                nextDaysWeatherRecyclerAdapter.addItems(forecastData.forecastDays)
            }*/
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
