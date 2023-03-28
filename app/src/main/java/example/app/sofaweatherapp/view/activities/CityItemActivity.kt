package example.app.sofaweatherapp.view.activities

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.google.android.material.appbar.AppBarLayout
import example.app.sofaweatherapp.R
import example.app.sofaweatherapp.databinding.ActivityCityItemBinding
import example.app.sofaweatherapp.model.ForecastDay
import example.app.sofaweatherapp.model.LocationDetail
import example.app.sofaweatherapp.model.WeatherCurrent
import example.app.sofaweatherapp.utils.Constants
import example.app.sofaweatherapp.utils.UtilityFunctions
import example.app.sofaweatherapp.view.adapters.WeatherRecyclerAdapter
import example.app.sofaweatherapp.viewmodel.CitiesViewModel
import kotlin.math.roundToInt

class CityItemActivity : AppCompatActivity(), WeatherRecyclerAdapter.OnItemClickListener {

    private lateinit var binding: ActivityCityItemBinding
    private var locationName: String = ""
    private val citiesViewModel: CitiesViewModel by viewModels()
    private lateinit var todayWeatherRecyclerAdapter: WeatherRecyclerAdapter
    private lateinit var nextDaysWeatherRecyclerAdapter: WeatherRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCityItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        todayWeatherRecyclerAdapter =
            WeatherRecyclerAdapter(this, mutableListOf(), this)
        nextDaysWeatherRecyclerAdapter =
            WeatherRecyclerAdapter(this, mutableListOf(), this)
        binding.todayWeatherRv.adapter = todayWeatherRecyclerAdapter
        binding.nextDaysWeatherRv.adapter = nextDaysWeatherRecyclerAdapter

        locationName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(
                getString(R.string.location_key),
                String::class.java
            ) as String
        } else {
            intent.getSerializableExtra(getString(R.string.location_key)) as String
        }

        if (locationName != "")
            citiesViewModel.searchForecast(locationName, 7)
        else
            finish()


        setListeners()
    }

    private fun setListeners() {
        citiesViewModel.forecastResponseData.observe(this) { forecastData ->
            fillBasicInformation(forecastData.location, forecastData.current)
            fillWeatherFeatures(forecastData.current, forecastData.forecast.forecastday)

            if (forecastData.forecast.forecastday.isNotEmpty()) {
                todayWeatherRecyclerAdapter.addItems(forecastData.forecast.forecastday[0].hour)
                nextDaysWeatherRecyclerAdapter.addItems(forecastData.forecast.forecastday)
            }
        }

        citiesViewModel.forecastResponseError.observe(this) { err ->
            println("a")
        }

        binding.appbarlayout.addOnOffsetChangedListener(object :
            AppBarLayout.OnOffsetChangedListener {
            var isShow: Boolean? = null
            var scrollRange: Int = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout!!.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    binding.toolbar.title = locationName
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
            cityName.text = locationData.name

            weatherInfo.apply {
                val dateTime = UtilityFunctions.epochToDateTimeAtTimeZone(
                    locationData.localtime_epoch,
                    locationData.tz_id
                )
                date.text = dateTime.split("|")[0]
                time.text = dateTime.split("|")[1]

                conditionText.text = weatherCurrent.condition.text
                temperature.text =
                    getString(
                        R.string.temp_value,
                        weatherCurrent.temp_c.roundToInt().toString(),
                        getString(R.string.temp_unit)
                    )
                weatherImage.load(Constants.HTTPS_PREFIX + weatherCurrent.condition.icon)
            }
        }
    }

    private fun fillWeatherFeatures(
        weatherCurrent: WeatherCurrent,
        forecastDays: List<ForecastDay>
    ) {
        binding.weatherFeatures.apply {
            featureWind.setValue(
                getString(
                    R.string.wind_value,
                    weatherCurrent.wind_kph.toString(),
                    getString(R.string.wind_unit),
                    weatherCurrent.wind_dir
                )
            )
            featureHumidty.setValue(
                getString(
                    R.string.humidity_value,
                    weatherCurrent.humidity.toString(),
                    getString(R.string.humidity_unit)
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
            //Didnt find accuracy attribute
            featureAccuracy.setValue(
                getString(
                    R.string.accuracy_value,
                    "98",
                    getString(R.string.accuracy_unit)
                )
            )
            //Min, max value only in day forecasts
            if (forecastDays.isNotEmpty()) {
                val minTemp = forecastDays[0].day.mintemp_c.toInt().toString()
                val maxTemp = forecastDays[0].day.maxtemp_c.toInt().toString()
                val unit = getString(R.string.temp_unit)
                featureTemp.setValue(
                    getString(
                        R.string.temp_min_max_value,
                        minTemp, unit, maxTemp, unit
                    )
                )
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