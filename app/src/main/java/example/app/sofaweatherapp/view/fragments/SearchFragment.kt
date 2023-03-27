package example.app.sofaweatherapp.view.fragments

import android.R.layout
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import example.app.sofaweatherapp.databinding.FragmentSearchBinding
import example.app.sofaweatherapp.viewmodel.CitiesViewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val citiesViewModel: CitiesViewModel by activityViewModels()
    private lateinit var searchArrayAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        //Promjenit array adapter u onaj koji biljezi povijest i provjerava
        searchArrayAdapter = ArrayAdapter(
            requireContext(),
            layout.simple_list_item_1
        )
        binding.autoCompleteTv.setAdapter(searchArrayAdapter)

        setListeners()

        return binding.root
    }

    fun setListeners() {
        citiesViewModel.citiesList.observe(viewLifecycleOwner) { citiesList ->
            searchArrayAdapter.addAll(citiesList.map { c -> c.name })
            searchArrayAdapter.notifyDataSetChanged()
        }

        binding.autoCompleteTv.addTextChangedListener {
            if (it.toString().length > 2) {
                citiesViewModel.searchCities(it.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}