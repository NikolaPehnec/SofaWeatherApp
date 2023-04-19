package example.app.sofaweatherapp.view.fragments

import android.R.layout
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import example.app.sofaweatherapp.R
import example.app.sofaweatherapp.databinding.FragmentSearchBinding
import example.app.sofaweatherapp.model.Location
import example.app.sofaweatherapp.utils.Constants
import example.app.sofaweatherapp.utils.UtilityFunctions
import example.app.sofaweatherapp.view.activities.CityItemActivity
import example.app.sofaweatherapp.viewmodel.CitiesViewModel

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val citiesViewModel: CitiesViewModel by viewModels()
    private lateinit var searchArrayAdapter: ArrayAdapter<String>
    private val searchedLocations = mutableSetOf<Location>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        searchArrayAdapter = ArrayAdapter(
            requireContext(),
            layout.simple_list_item_1
        )
        binding.autoCompleteTv.setAdapter(searchArrayAdapter)

        setListeners()

        return binding.root
    }

    private fun setListeners() {
        citiesViewModel.citiesList.observe(viewLifecycleOwner) { locationList ->
            if (locationList.isEmpty()) {
                UtilityFunctions.makeErrorSnackBar(
                    binding.root,
                    binding.anchorView,
                    getString(R.string.no_location_mess),
                    requireContext()
                ).show()
            } else {
                binding.autoCompleteTv.error = null

                var added = false
                for (location in locationList) {
                    if (!searchedLocations.contains(location)) {
                        searchArrayAdapter.add(location.name)
                        added = true
                    }
                }
                // Forcing the textView to show new suggestions
                searchedLocations.addAll(locationList)
                if (added) {
                    binding.autoCompleteTv.apply {
                        text = text
                        setSelection(text.length)
                    }
                }
            }
        }

        citiesViewModel.citiesResponseError.observe(viewLifecycleOwner) { err ->
            UtilityFunctions.makeErrorSnackBar(
                binding.root,
                binding.anchorView,
                err,
                requireContext()
            ).show()
        }

        binding.autoCompleteTv.apply {
            addTextChangedListener {
                //On treshold call api, after api perform local filtering
                if (it.toString().length == Constants.SEARCH_TRESHOLD) {
                    citiesViewModel.searchCities(it.toString())
                } else if (it.toString().length > Constants.SEARCH_TRESHOLD) {
                    checkForSearchStringInLocations(it.toString())
                }
            }

            setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    binding.autoCompleteTv.text.toString().apply {
                        if (this.length > 2) {
                            startCityItemActivity(this)
                        } else {
                            UtilityFunctions.makeErrorSnackBar(
                                binding.root,
                                binding.anchorView,
                                getString(R.string.err_search_small),
                                requireContext()
                            ).show()
                        }
                    }
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }

            setOnItemClickListener { _, _, position, _ ->
                startCityItemActivity(adapter.getItem(position).toString())
            }
        }
    }

    private fun checkForSearchStringInLocations(search: String) {
        val filteredLocations = searchedLocations.filter { l -> l.name.lowercase().startsWith(search.lowercase()) }.toList()
        searchArrayAdapter.clear()
        searchArrayAdapter.addAll(filteredLocations.map { l -> l.name })
    }

    private fun startCityItemActivity(locationName: String) {
        val intent = Intent(requireContext(), CityItemActivity::class.java).apply {
            putExtra(getString(R.string.location_key), locationName)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
