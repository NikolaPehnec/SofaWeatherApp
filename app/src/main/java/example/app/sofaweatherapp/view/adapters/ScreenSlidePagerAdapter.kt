package example.app.sofaweatherapp.view.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import example.app.sofaweatherapp.view.fragments.FavoritesFragment
import example.app.sofaweatherapp.view.fragments.SearchFragment
import example.app.sofaweatherapp.view.fragments.SettingsFragment

class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SearchFragment()
            1 -> FavoritesFragment()
            2 -> SettingsFragment()
            else -> SearchFragment()
        }
    }
}
