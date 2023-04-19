package example.app.sofaweatherapp.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import example.app.sofaweatherapp.R
import example.app.sofaweatherapp.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
       // setSupportActionBar(binding.activityToolbar)

        val bottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        bottomNavigationView.setupWithNavController(navController)
    }

}
