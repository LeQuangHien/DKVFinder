package com.hien.le.dkvfinder.feature.maps

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.core.engine.AuthenticationMode
import com.here.sdk.core.engine.SDKNativeEngine
import com.here.sdk.core.engine.SDKOptions
import com.here.sdk.core.errors.InstantiationErrorException
import com.here.sdk.mapview.MapImageFactory
import com.here.sdk.mapview.MapMarker
import com.here.sdk.mapview.MapMeasure
import com.here.sdk.mapview.MapScheme
import com.hien.le.dkvfinder.feature.maps.databinding.FragmentMapBinding

// Argument keys MUST match those defined in main_nav_graph.xml in :app
private const val ARG_LATITUDE_KEY = "latitude_map_arg"
private const val ARG_LONGITUDE_KEY = "longitude_map_arg"

class MapFragment : Fragment() {

    companion object {
        private const val TAG = "MapFragment"
    }

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private var receivedLatitude: Double = 0.0
    private var receivedLongitude: Double = 0.0

    private val mapMarkers =
        mutableListOf<MapMarker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (it.containsKey(ARG_LATITUDE_KEY) && it.containsKey(ARG_LONGITUDE_KEY)) {
                receivedLatitude = it.getString(ARG_LATITUDE_KEY)?.toDouble() ?: 0.0
                receivedLongitude = it.getString(ARG_LONGITUDE_KEY)?.toDouble() ?: 0.0
                Log.d(TAG, "Args received: Lat $receivedLatitude, Lon $receivedLongitude")
            } else {
                Log.w(TAG, "One or both map arguments not found in bundle.")
            }
        } ?: run {
            Log.w(TAG, "Arguments bundle is null.")
        }
        initializeHERESDK(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.onCreate(savedInstanceState)
        loadMapScene()
    }

    private fun initializeHERESDK(context: Context) {
        val accessKeyID = BuildConfig.HERE_ACCESS_KEY_ID
        val accessKeySecret = BuildConfig.HERE_ACCESS_KEY_SECRET
        val authenticationMode = AuthenticationMode.withKeySecret(accessKeyID, accessKeySecret)
        val options = SDKOptions(authenticationMode)
        try {
            SDKNativeEngine.makeSharedInstance(context, options)
        } catch (e: InstantiationErrorException) {
            throw RuntimeException("Initialization of HERE SDK failed: " + e.error.name)
        }
    }

    private fun disposeHERESDK() {
        SDKNativeEngine.getSharedInstance()?.dispose()
        SDKNativeEngine.setSharedInstance(null)
    }

    private fun loadMapScene() {
        binding.mapView.mapScene.loadScene(MapScheme.NORMAL_DAY) { mapError ->
            if (mapError == null) {
                val targetCoordinates = GeoCoordinates(receivedLatitude, receivedLongitude)
                val distanceInMeters = (1000 * 10).toDouble()
                val mapMeasureZoom =
                    MapMeasure(MapMeasure.Kind.DISTANCE_IN_METERS, distanceInMeters)
                binding.mapView.camera.lookAt(targetCoordinates, mapMeasureZoom)

                addMapMarker(targetCoordinates)
            } else {
                Log.d(TAG, "Loading map failed: mapError: " + mapError.name)
            }
        }
    }

    private fun addMapMarker(coordinates: GeoCoordinates) {
        val mapImage = MapImageFactory.fromResource(
            requireContext().resources,
            R.drawable.ic_map_pin
        )
        val mapMarker = MapMarker(coordinates, mapImage)
        binding.mapView.mapScene.addMapMarker(mapMarker)
        mapMarkers.add(mapMarker)

        Log.d(TAG, "MapMarker added at $coordinates")
    }

    private fun clearMapMarkers() {
        for (mapMarker in mapMarkers) {
            binding.mapView.mapScene.removeMapMarker(mapMarker)
        }
        mapMarkers.clear()
        Log.d(TAG, "All map markers cleared.")
    }


    override fun onResume() {
        binding.mapView.onResume()
        super.onResume()
    }

    override fun onPause() {
        binding.mapView.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        clearMapMarkers()
        binding.mapView.onDestroy()
        disposeHERESDK()
        _binding = null
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        binding.mapView.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }
}
