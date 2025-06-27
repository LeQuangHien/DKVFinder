package com.hien.le.dkvfinder.feature.evcharging.poi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        poiAdapter = PoiAdapter(this)
        binding.recyclerViewPois.apply {
            adapter = poiAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupClickListeners() {
        binding.buttonRefresh.setOnClickListener {
            viewModel.refreshData()
        }
    }

    private fun setupObservers() {
        viewModel.poiStreamLiveData.observe(viewLifecycleOwner) { state ->
            handleUiState(state)
        }
        viewModel.isRefreshing.observe(viewLifecycleOwner) { isRefreshing ->
            binding.progressBar.isVisible = isRefreshing
        }
    }

    private fun handleUiState(state: PoiUiState) {
        binding.progressBar.isVisible = state is PoiUiState.Loading
        binding.recyclerViewPois.isVisible = state is PoiUiState.Success
        binding.layoutEmptyState.isVisible = state is PoiUiState.Empty
        binding.textViewError.isVisible = state is PoiUiState.Error

        when (state) {
            is PoiUiState.Loading -> {
                // ProgressBar is already handled by isVisible
            }

            is PoiUiState.Success -> {
                poiAdapter.submitList(state.pois)
            }

            is PoiUiState.Empty -> {
                binding.textViewEmptyMessage.text = state.message
                poiAdapter.submitList(emptyList()) // Clear the adapter
            }

            is PoiUiState.Error -> {
                poiAdapter.submitList(emptyList()) // Clear the adapter
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewPois.adapter =
            null // Clear adapter to avoid memory leaks with RecyclerView
        _binding = null
    }
}