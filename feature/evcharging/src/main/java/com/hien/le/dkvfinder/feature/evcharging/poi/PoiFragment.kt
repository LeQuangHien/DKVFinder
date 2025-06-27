package com.hien.le.dkvfinder.feature.evcharging.poi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hien.le.dkvfinder.feature.evcharging.R
import com.hien.le.dkvfinder.feature.evcharging.databinding.FragmentPoiBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PoiFragment : Fragment(), PoiItemClickListener {

    private var _binding: FragmentPoiBinding? = null
    private val binding get() = _binding!!

    private lateinit var poiAdapter: PoiAdapter
    private val viewModel: PoiViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPoiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        viewModel.fetchPois()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupRecyclerView() {
        poiAdapter = PoiAdapter(this)
        binding.recyclerViewPois.apply {
            adapter = poiAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeViewModel() {
        viewModel.poiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PoiUiState.Loading -> {
                    // Show loading indicator
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerViewPois.visibility = View.GONE
                    binding.textViewError.visibility = View.GONE
                }
                is PoiUiState.Success -> {
                    // Hide loading indicator, show data
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerViewPois.visibility = View.VISIBLE
                    binding.textViewError.visibility = View.GONE
                    poiAdapter.submitList(state.pois)
                }
                is PoiUiState.Error -> {
                    // Hide loading indicator, show error message
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerViewPois.visibility = View.GONE
                    binding.textViewError.visibility = View.VISIBLE
                    binding.textViewError.text = getString(R.string.failed_to_load_pois)
                }
            }
        }
    }

    // --- PoiItemClickListener Implementation ---
    override fun onFavoriteClicked(poi: PoiItemUiState) {
        poi.id?.let { viewModel.toggleFavorite(it, poi.isFavorite) }
    }

    override fun onItemClicked(poi: PoiItemUiState) {
        poi.id?.let { poiId ->
            val action = PoiFragmentDirections.actionPoiFragmentToPoiDetailsWebviewFragment(poiId)
            findNavController().navigate(action)
        }
    }
}