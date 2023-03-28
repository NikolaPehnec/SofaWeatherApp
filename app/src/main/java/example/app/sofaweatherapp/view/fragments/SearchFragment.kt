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
import androidx.fragment.app.activityViewModels
import example.app.sofaweatherapp.R
import example.app.sofaweatherapp.databinding.FragmentSearchBinding
import example.app.sofaweatherapp.model.Location
import example.app.sofaweatherapp.utils.UtilityFunctions
import example.app.sofaweatherapp.view.activities.CityItemActivity
import example.app.sofaweatherapp.viewmodel.CitiesViewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val citiesViewModel: CitiesViewModel by activityViewModels()
    private lateinit var searchArrayAdapter: ArrayAdapter<String>
    private val searchedLocations = mutableSetOf<Location>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        searchArrayAdapter = ArrayAdapter(
            requireContext(), layout.simple_list_item_1
        )
        binding.autoCompleteTv.setAdapter(searchArrayAdapter)

        setListeners()

        return binding.root
    }


    private fun setListeners() {
        citiesViewModel.citiesList.observe(viewLifecycleOwner) { locationList ->
            if (locationList.isEmpty()) {
                UtilityFunctions.showErrorSnackBar(
                    binding.root,
                    binding.anchorView,
                    getString(R.string.no_location_mess),
                    requireContext()
                )
            } else {
                binding.autoCompleteTv.error = null

                var added = false
                for (location in locationList) {
                    if (!searchedLocations.contains(location)) {
                        searchArrayAdapter.add(location.name)
                        added = true
                    }
                }
                //Forcing the textView to show new suggestions
                if (added) {
                    binding.autoCompleteTv.apply {
                        text = text
                        setSelection(text.length)
                    }
                }
                searchedLocations.addAll(locationList)
            }
        }

        citiesViewModel.citiesResponseError.observe(viewLifecycleOwner) { err ->
            UtilityFunctions.showErrorSnackBar(
                binding.root,
                binding.anchorView,
                err.message,
                requireContext()
            )
        }

        binding.autoCompleteTv.apply {
            addTextChangedListener {
                if (it.toString().length > 2) {
                    citiesViewModel.searchCities(it.toString())
                }
            }

            //When returning from activity, can press ok for dropdown menu
            setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    showDropDown()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }

            setOnItemClickListener { _, _, position, _ ->
                startCityItemActivity(adapter.getItem(position).toString())
            }
        }
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