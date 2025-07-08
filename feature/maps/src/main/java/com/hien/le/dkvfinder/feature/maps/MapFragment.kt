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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve arguments from the bundle
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
        // Initialize HERE SDK only if it hasn't been initialized yet in the app's lifecycle
        // Typically, this is done in the Application class, but if not,
        // this fragment can be a secondary point of initialization.
        // However, it's best if the Application class handles this to avoid re-initialization issues.
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

        // The MapView needs to be created after the SDK is initialized.
        // If SDK initialization is asynchronous, ensure loadMapScene is called in its callback.
        // For simplicity here, we assume SDK is either already initialized or will be synchronously enough.
        binding.mapView.onCreate(savedInstanceState) // Important for MapView lifecycle
        loadMapScene()
    }

    private fun initializeHERESDK(context: Context) {
        // Set your credentials for the HERE SDK.
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
        // Free HERE SDK resources before the application shuts down.
        // Usually, this should be called only on application termination.
        // Afterwards, the HERE SDK is no longer usable unless it is initialized again.
        SDKNativeEngine.getSharedInstance()?.dispose()
        // For safety reasons, we explicitly set the shared instance to null to avoid situations,
        // where a disposed instance is accidentally reused.
        SDKNativeEngine.setSharedInstance(null)
    }

    private fun loadMapScene() {
        // Load a scene from the HERE SDK to render the map with a map scheme.
        binding.mapView.mapScene.loadScene(MapScheme.NORMAL_DAY) { mapError ->
            if (mapError == null) {
                val distanceInMeters = (1000 * 10).toDouble()
                val mapMeasureZoom =
                    MapMeasure(MapMeasure.Kind.DISTANCE_IN_METERS, distanceInMeters)
                binding.mapView.camera.lookAt(GeoCoordinates(receivedLatitude, receivedLongitude), mapMeasureZoom)
            } else {
                Log.d(TAG, "Loading map failed: mapError: " + mapError.name)
            }
        }
    }

    // --- MapView Lifecycle Methods (Essential) ---
    override fun onResume() {
        binding.mapView.onResume()
        super.onResume()
    }

    override fun onPause() {
        binding.mapView.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        binding.mapView.onDestroy() // Destroy MapView
        _binding = null
        disposeHERESDK()
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        binding.mapView.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }
}
