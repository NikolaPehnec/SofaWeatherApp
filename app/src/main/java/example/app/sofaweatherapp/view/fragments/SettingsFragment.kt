package example.app.sofaweatherapp.view.fragments

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import dagger.hilt.android.AndroidEntryPoint
import example.app.sofaweatherapp.R
import example.app.sofaweatherapp.databinding.FragmentSettingsBinding
import example.app.sofaweatherapp.utils.Constants
import example.app.sofaweatherapp.utils.CustomDialog
import example.app.sofaweatherapp.utils.UtilityFunctions
import example.app.sofaweatherapp.utils.UtilityFunctions.getUnitFromSharedPreferences
import example.app.sofaweatherapp.utils.UtilityFunctions.saveUnitPreference
import example.app.sofaweatherapp.viewmodel.ForecastViewModel

@AndroidEntryPoint
class SettingsFragment : Fragment(),
    MenuProvider {

    private var _binding: FragmentSettingsBinding? = null
    private val forecastViewModel: ForecastViewModel by activityViewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.languages)
        )

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.metric -> saveUnitPreference(Constants.UNIT_METRIC, requireContext())
                R.id.imperial -> saveUnitPreference(Constants.UNIT_IMPERIAL, requireContext())
            }
        }

        binding.languagesDropdown.apply {
            setStringArrayAdapter(adapter)
            setOnItemClickListener { _, _, _, _ ->
                changeLocalization(getSelectedItemText())
            }
            setText(getCurrentLanguage())
        }

        binding.clearCitiesBtn.setOnClickListener {
            showClearCitiesDialog()
        }

        setUnitPreference()
        return binding.root
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        binding.languagesDropdown.setText(getCurrentLanguage())
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getCurrentLanguage(): String {
        return when (resources.configuration.locales.get(0).toString()) {
            Constants.LANG_EN -> resources.getStringArray(R.array.languages)[0]
            Constants.LANG_HR -> resources.getStringArray(R.array.languages)[1]
            else -> ""
        }
    }

    private fun changeLocalization(language: String) {
        val appLocale = when (language) {
            // English
            resources.getStringArray(R.array.languages)[0] -> {
                LocaleListCompat.forLanguageTags(Constants.LANG_EN)
            }
            // Croatian
            resources.getStringArray(R.array.languages)[1] -> {
                LocaleListCompat.forLanguageTags(Constants.LANG_HR)
            }
            else -> null
        }

        appLocale?.let {
            AppCompatDelegate.setApplicationLocales(appLocale)
        }

        binding.languagesDropdown.setText(language)
    }

    private fun setUnitPreference() {
        val unit = getUnitFromSharedPreferences(requireContext())
        if (unit == Constants.UNIT_METRIC) {
            binding.metric.isChecked = true
        } else if (unit == Constants.UNIT_IMPERIAL) {
            binding.imperial.isChecked = true
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.settings_fragment_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return true
    }

    private fun showClearCitiesDialog() {
        CustomDialog(
            requireContext(),
            R.layout.custom_dialog,
            getString(R.string.clear_my_cities_dialog_title),
            getString(R.string.clear_my_cities_dialog_mess),
            onConfirm = {
                forecastViewModel.clearAllFavoriteLocations()
                UtilityFunctions.makeNotifiationSnackBar(
                    binding.root,
                    null,
                    getString(R.string.clear_my_cities_after_message),
                    requireContext()
                ).show()
            }
        ).show()
    }
}
