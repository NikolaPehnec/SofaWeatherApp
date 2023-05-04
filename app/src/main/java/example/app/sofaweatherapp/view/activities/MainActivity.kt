package example.app.sofaweatherapp.view.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import example.app.sofaweatherapp.R
import example.app.sofaweatherapp.databinding.ActivityMainBinding
import example.app.sofaweatherapp.view.adapters.ScreenSlidePagerAdapter
import example.app.sofaweatherapp.viewmodel.ForecastViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val forecastViewModel: ForecastViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.activityToolbar)

        val pagerAdapter = ScreenSlidePagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
        binding.navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    binding.viewPager.currentItem = 0
                    binding.activityToolbar.visibility = View.INVISIBLE
                    true
                }

                R.id.navigation_favorites -> {
                    forecastViewModel.getAllFavoriteLocations()
                    binding.viewPager.currentItem = 1
                    binding.activityToolbar.visibility = View.VISIBLE
                    binding.activityToolbar.title = getString(R.string.my_cities)
                    true
                }

                R.id.navigation_settings -> {
                    binding.viewPager.currentItem = 2
                    binding.activityToolbar.visibility = View.VISIBLE
                    binding.activityToolbar.title = getString(R.string.settings)
                    true
                }

                else -> false
            }
        }

        binding.activityToolbar.visibility = View.INVISIBLE
        binding.viewPager.isUserInputEnabled = false
    }

    //Refresh favorite locations when coming back from CityItemActivity
    override fun onResume() {
        super.onResume()
        forecastViewModel.getAllFavoriteLocations()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return false
    }
}
