package example.app.sofaweatherapp.view.activities

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import example.app.sofaweatherapp.R
import example.app.sofaweatherapp.databinding.ActivityCityItemBinding
import example.app.sofaweatherapp.model.ForecastDay
import example.app.sofaweatherapp.model.LocationDetail
import example.app.sofaweatherapp.model.WeatherCurrent
import example.app.sofaweatherapp.utils.Constants
import example.app.sofaweatherapp.utils.UtilityFunctions
import example.app.sofaweatherapp.view.adapters.WeatherRecyclerAdapter
import example.app.sofaweatherapp.view.fragments.MapsFragment
import example.app.sofaweatherapp.viewmodel.ForecastViewModel
import kotlin.math.roundToInt

@AndroidEntryPoint
class CityItemActivity : AppCompatActivity(), WeatherRecyclerAdapter.OnItemClickListener {

    private lateinit var binding: ActivityCityItemBinding
    private var locationName: String = ""
    private var locationNameApi: String = ""
    private val forecastViewModel: ForecastViewModel by viewModels()
    private lateinit var todayWeatherRecyclerAdapter: WeatherRecyclerAdapter
    private lateinit var nextDaysWeatherRecyclerAdapter: WeatherRecyclerAdapter
    private var unit = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCityItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        todayWeatherRecyclerAdapter = WeatherRecyclerAdapter(this, mutableListOf(), this)
        nextDaysWeatherRecyclerAdapter = WeatherRecyclerAdapter(this, mutableListOf(), this)
        binding.todayWeatherRv.adapter = todayWeatherRecyclerAdapter
        binding.nextDaysWeatherRv.adapter = nextDaysWeatherRecyclerAdapter

        unit = getSharedPreferences("MY_PREFERENCES", Context.MODE_PRIVATE).getString(
            "UNIT",
            "Metric"
        )!!

        locationName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(
                getString(R.string.location_key),
                String::class.java
            ) as String
        } else {
            intent.getSerializableExtra(getString(R.string.location_key)) as String
        }

        forecastViewModel.searchForecast(locationName)

        setListeners()
    }

    private fun launchMapFragment(lat: Double, long: Double, title: String) {
        supportFragmentManager.beginTransaction()
            .replace(binding.mapFrameLayout.id, MapsFragment().newInstance(lat, long, title))
            .commit()
    }

    private fun setListeners() {
        forecastViewModel.forecastResponseData.observe(this) { forecastData ->
            fillBasicInformation(forecastData.location, forecastData.current)
            fillWeatherFeatures(forecastData.current, forecastData.forecastDays)

            launchMapFragment(
                forecastData.location.lat,
                forecastData.location.lon,
                forecastData.location.name
            )

            if (forecastData.forecastDays.isNotEmpty()) {
                todayWeatherRecyclerAdapter.addItems(forecastData.forecastDays[0].hour)
                nextDaysWeatherRecyclerAdapter.addItems(forecastData.forecastDays)
            }
        }

        // Show error, finish actvitiy
        forecastViewModel.forecastResponseError.observe(this) { err ->
            UtilityFunctions.makeErrorSnackBar(binding.root, null, err, this)
                .addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(snackbar: Snackbar, event: Int) {
                        super.onDismissed(snackbar, event)
                        finish()
                    }
                }).show()
        }

        binding.appbarlayout.addOnOffsetChangedListener(object :
                AppBarLayout.OnOffsetChangedListener {
                var isShow: Boolean? = null
                var scrollRange: Int = -1

                // Vertical offset -10, -50,
                // Total scroll range -201
                override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout!!.totalScrollRange
                    }
                    if (scrollRange + verticalOffset <= 0) {
                        if (binding.toolbar.title != locationNameApi) {
                            binding.toolbar.title = locationNameApi
                        }
                        isShow = true
                    } else if (isShow == true) {
                        binding.toolbar.title = ""
                        isShow = false
                    }
                }
            })
    }

    private fun fillBasicInformation(
        locationData: LocationDetail,
        weatherCurrent: WeatherCurrent
    ) {
        binding.apply {
            locationNameApi = locationData.name
            cityName.text = locationNameApi

            weatherInfo.apply {
                val dateTime = UtilityFunctions.epochToDateTimeAtTimeZone(
                    locationData.localtime_epoch,
                    locationData.tz_id
                )
                date.text = dateTime.split("|")[0]
                time.text = dateTime.split("|")[1]

                conditionText.text = weatherCurrent.condition.text
                weatherImage.load(Constants.HTTPS_PREFIX + weatherCurrent.condition.icon)

                if (unit == "Metric") {
                    temperature.text = getString(
                        R.string.temp_value,
                        weatherCurrent.temp_c.roundToInt().toString(),
                        getString(R.string.temp_unit)
                    )
                } else {
                    temperature.text = getString(
                        R.string.temp_value,
                        weatherCurrent.temp_f.roundToInt().toString(),
                        getString(R.string.temp_unit_F)
                    )
                }
            }
        }
    }

    private fun fillWeatherFeatures(
        weatherCurrent: WeatherCurrent,
        forecastDays: List<ForecastDay>
    ) {
        binding.weatherFeatures.apply {
            featureHumidty.setValue(
                getString(
                    R.string.humidity_value,
                    weatherCurrent.humidity.toString(),
                    getString(R.string.humidity_unit)
                )
            )
            // Didnt find accuracy attribute
            featureAccuracy.setValue(
                getString(
                    R.string.accuracy_value,
                    "98",
                    getString(R.string.accuracy_unit)
                )
            )

            if (unit == "Metric") {
                featureWind.setValue(
                    getString(
                        R.string.wind_value,
                        weatherCurrent.wind_kph.toString(),
                        getString(R.string.wind_unit),
                        weatherCurrent.wind_dir
                    )
                )
                featurePressure.setValue(
                    getString(
                        R.string.pressure_value,
                        weatherCurrent.pressure_mb.toString(),
                        getString(R.string.pressure_unit)
                    )
                )
                featureVisibility.setValue(
                    getString(
                        R.string.visibility_value,
                        weatherCurrent.vis_km.toString(),
                        getString(R.string.visibility_unit)
                    )
                )
                // Min, max value only in day forecasts
                if (forecastDays.isNotEmpty()) {
                    val minTemp = forecastDays[0].day.mintemp_c.toInt().toString()
                    val maxTemp = forecastDays[0].day.maxtemp_c.toInt().toString()
                    val unit = getString(R.string.temp_unit)
                    featureTemp.setValue(
                        getString(
                            R.string.temp_min_max_value,
                            minTemp,
                            unit,
                            maxTemp,
                            unit
                        )
                    )
                }
            } else {
                featureWind.setValue(
                    getString(
                        R.string.wind_value,
                        weatherCurrent.wind_mph.toString(),
                        getString(R.string.wind_unit_mph),
                        weatherCurrent.wind_dir
                    )
                )
                featurePressure.setValue(
                    getString(
                        R.string.pressure_value,
                        weatherCurrent.pressure_in.toString(),
                        getString(R.string.pressure_unit_imp)
                    )
                )
                featureVisibility.setValue(
                    getString(
                        R.string.visibility_value,
                        weatherCurrent.vis_miles.toString(),
                        getString(R.string.visibility_unit_miles)
                    )
                )
                // Min, max value only in day forecasts
                if (forecastDays.isNotEmpty()) {
                    val minTemp = forecastDays[0].day.mintemp_f.toInt().toString()
                    val maxTemp = forecastDays[0].day.maxtemp_f.toInt().toString()
                    val unit = getString(R.string.temp_unit_F)
                    featureTemp.setValue(
                        getString(
                            R.string.temp_min_max_value,
                            minTemp,
                            unit,
                            maxTemp,
                            unit
                        )
                    )
                }
            }
        }
    }

    override fun onItemClick(item: Any) {
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.city_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.favorite -> changeFavorite(item)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun changeFavorite(item: MenuItem) {
        if (item.title!! == getString(R.string.favorite)) {
            item.setIcon(R.drawable.ic_baseline_star_24)
            item.title = getString(R.string.unfavorite)
        } else {
            item.setIcon(R.drawable.ic_baseline_star_outline_24)
            item.title = getString(R.string.favorite)
        }
    }
}
