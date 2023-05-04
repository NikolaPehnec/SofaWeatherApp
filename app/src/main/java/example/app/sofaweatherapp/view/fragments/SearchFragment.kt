package example.app.sofaweatherapp.view.fragments

import android.R.layout
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import example.app.sofaweatherapp.R
import example.app.sofaweatherapp.databinding.FragmentSearchBinding
import example.app.sofaweatherapp.model.Location
import example.app.sofaweatherapp.utils.Constants
import example.app.sofaweatherapp.utils.UtilityFunctions
import example.app.sofaweatherapp.utils.UtilityFunctions.startCityItemActivity
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
            setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_baseline_search_24
                ),
                null,
                null,
                null
            )

            addTextChangedListener {
                setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_baseline_search_24
                    ),
                    null,
                    if (it.toString().isEmpty()) {
                        null
                    } else {
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_baseline_close_24
                        )
                    },
                    null
                )

                // On treshold call api, after api perform local filtering
                if (it.toString().length == Constants.SEARCH_TRESHOLD) {
                    citiesViewModel.searchCities(it.toString())
                } else if (it.toString().length > Constants.SEARCH_TRESHOLD) {
                    checkForSearchStringInLocations(it.toString())
                }
            }

            setOnTouchListener(object : OnTouchListener {
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    val drawableLeftIndex = 0
                    val drawableRightIndex = 2

                    if (event?.action == MotionEvent.ACTION_UP) {
                        binding.autoCompleteTv.compoundDrawables[drawableRightIndex]?.let {
                            if (event.rawX >= (binding.autoCompleteTv.right - it.bounds.width())
                            ) {
                                binding.autoCompleteTv.setText("")
                                return true
                            } else if (event.rawX <= (
                                binding.autoCompleteTv.left +
                                    binding.autoCompleteTv.compoundDrawables[drawableLeftIndex].bounds.width()
                                )
                            ) {
                                startSearch(binding.autoCompleteTv.text.toString().lowercase())
                                return true
                            }
                        }
                    }
                    return false
                }
            })

            setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    binding.autoCompleteTv.text.toString().apply {
                        startSearch(this)
                    }
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }

            setOnItemClickListener { _, _, position, _ ->
                startCityItemActivity(adapter.getItem(position).toString(), requireContext())
            }
        }
    }

    private fun startSearch(searchString: String) {
        if (searchString.length > 2) {
            startCityItemActivity(searchString.lowercase().trim(), requireContext())
        } else {
            UtilityFunctions.makeErrorSnackBar(
                binding.root,
                binding.anchorView,
                getString(R.string.err_search_small),
                requireContext()
            ).show()
        }
    }

    private fun checkForSearchStringInLocations(search: String) {
        val filteredLocations =
            searchedLocations.filter { l -> l.name.lowercase().startsWith(search.lowercase()) }
                .toList()
        searchArrayAdapter.clear()
        searchArrayAdapter.addAll(filteredLocations.map { l -> l.name })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
