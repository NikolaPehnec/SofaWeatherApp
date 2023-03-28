package example.app.sofaweatherapp.view.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import example.app.sofaweatherapp.R
import example.app.sofaweatherapp.databinding.ActivityCityItemBinding
import example.app.sofaweatherapp.viewmodel.CitiesViewModel

class CityItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCityItemBinding
    private var locationName: String = ""
    private val citiesViewModel: CitiesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCityItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(
                getString(R.string.location_key),
                String::class.java
            ) as String
        } else {
            intent.getSerializableExtra(getString(R.string.location_key)) as String
        }

        if (locationName != "") {
            citiesViewModel.searchForecast(locationName, 7)
        } else {
            finish()
        }

        setListeners()
    }

    private fun setListeners() {
        citiesViewModel.forecastResponseData.observe(this) { forecastData ->
            println("a")
        }

        citiesViewModel.forecastResponseError.observe(this) { err ->
            println("a")
        }

    }
}