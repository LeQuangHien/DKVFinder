package com.hien.le.dkvfinder.feature.evcharging.webview

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.hien.le.dkvfinder.feature.evcharging.databinding.FragmentPoiDetailsWebviewBinding // Import generated binding

class PoiDetailsWebviewFragment : Fragment() {
    private var _binding: FragmentPoiDetailsWebviewBinding? = null
    private val binding get() = _binding!!

    // Use Safe Args to retrieve the poiId
    private val args: PoiDetailsWebviewFragmentArgs by navArgs()

    companion object {
        const val BASE_URL = "https://openchargemap.org/site/poi/details/"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPoiDetailsWebviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val poiId = args.poiId
        val url = "$BASE_URL$poiId"

        setupWebView()
        binding.webViewPoiDetails.loadUrl(url)
    }

    private fun setupWebView() {
        binding.webViewPoiDetails.settings.javaScriptEnabled = true // Enable JavaScript if needed
        binding.webViewPoiDetails.webViewClient =
            object : WebViewClient() {
                override fun onPageStarted(
                    view: WebView?,
                    url: String?,
                    favicon: Bitmap?,
                ) {
                    super.onPageStarted(view, url, favicon)
                    binding.progressBarWebview.visibility = View.VISIBLE
                }

                override fun onPageFinished(
                    view: WebView?,
                    url: String?,
                ) {
                    super.onPageFinished(view, url)
                    binding.progressBarWebview.visibility = View.GONE
                }

                // Optional: Handle URL loading errors
                // override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                //     super.onReceivedError(view, request, error)
                //     binding.progressBarWebview.visibility = View.GONE
                //     // Show an error message or a custom error page
                //     // view.loadUrl("file:///android_asset/error_page.html")
                // }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Important to avoid memory leaks with WebView
        binding.webViewPoiDetails.destroy()
        _binding = null
    }
}
