package example.app.sofaweatherapp.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import example.app.sofaweatherapp.R
import example.app.sofaweatherapp.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.languages)
        )

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.metric -> saveUnitPreference("Metric")
                R.id.imperial -> saveUnitPreference("Imperial")
            }
        }

        binding.languagesDropdown.apply {
            setStringArrayAdapter(adapter)
            setOnItemClickListener { _, _, _, _ ->
                changeLocalization(getSelectedItemText())
            }
            setText(getCurrentLanguage())
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
            "en" -> resources.getStringArray(R.array.languages)[0]
            "hr" -> resources.getStringArray(R.array.languages)[1]
            else -> ""
        }
    }

    private fun changeLocalization(language: String) {
        val appLocale = when (language) {
            // English
            resources.getStringArray(R.array.languages)[0] -> {
                LocaleListCompat.forLanguageTags("en")
            }
            // Croatian
            resources.getStringArray(R.array.languages)[1] -> {
                LocaleListCompat.forLanguageTags("hr")
            }
            else -> null
        }

        appLocale?.let {
            AppCompatDelegate.setApplicationLocales(appLocale)
        }

        binding.languagesDropdown.setText(language)
    }

    private fun getUnitPreference(): String {
        val sharedPreferences =
            requireActivity().getSharedPreferences("MY_PREFERENCES", Context.MODE_PRIVATE)
        return sharedPreferences!!.getString("UNIT", "Metric") ?: "Metric"
    }

    private fun setUnitPreference() {
        val unit = getUnitPreference()
        if (unit == "Metric") {
            binding.metric.isChecked = true
        } else if (unit == "Imperial") {
            binding.imperial.isChecked = true
        }
    }

    private fun saveUnitPreference(unit: String) {
        val sharedPreferences =
            requireActivity().getSharedPreferences("MY_PREFERENCES", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("UNIT", unit)
        editor.apply()
    }
}
