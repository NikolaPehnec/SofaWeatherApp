package example.app.sofaweatherapp.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import example.app.sofaweatherapp.R
import example.app.sofaweatherapp.utils.Constants.MAPS_LAT
import example.app.sofaweatherapp.utils.Constants.MAPS_LONG
import example.app.sofaweatherapp.utils.Constants.MAPS_TITLE

class MapsFragment : Fragment() {
    private var location: LatLng? = null
    private var locationTitle = ""

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        googleMap.addMarker(MarkerOptions().position(location!!).title(locationTitle))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location!!, 10f))
        googleMap.uiSettings.isZoomControlsEnabled = true
    }

    fun newInstance(lat: Double, long: Double, title: String): MapsFragment {
        val myFragment = MapsFragment()
        val args = Bundle(3)
        args.putDouble(MAPS_LAT, lat)
        args.putDouble(MAPS_LONG, long)
        args.putString(MAPS_TITLE, title)
        myFragment.arguments = args
        return myFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!requireArguments().containsKey(MAPS_LAT) || !requireArguments().containsKey(MAPS_LONG)) {
            throw java.lang.IllegalStateException("Location must be set")
        } else {
            location = LatLng(
                requireArguments().getDouble(MAPS_LAT),
                requireArguments().getDouble(MAPS_LONG)
            )
            locationTitle = requireArguments().getString(MAPS_TITLE)!!
        }

        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}
