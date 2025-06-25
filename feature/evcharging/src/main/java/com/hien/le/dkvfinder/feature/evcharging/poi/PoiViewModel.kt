package com.hien.le.dkvfinder.feature.evcharging.poi

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hien.le.dkvfinder.core.data.repository.PoiRepository
import com.hien.le.dkvfinder.core.model.data.AddressInfo
import com.hien.le.dkvfinder.core.model.data.Poi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PoiItemUiState(
    val id: Int?,
    val title: String?,
    val address: String?,
    val town: String?,
    val telephone: String?,
    val distance: Double?,
    val distanceUnit: Int?,
    val isFavorite: Boolean = false
)

sealed interface PoiUiState {
    data object Loading : PoiUiState
    data class Success(val pois: List<PoiItemUiState>) : PoiUiState
    data object Error : PoiUiState
}

@HiltViewModel
class PoiViewModel @Inject constructor(
    private val poiRepository: PoiRepository
) : ViewModel() {

    companion object {
        private const val TAG = "PoiViewModel"
        private const val DEFAULT_COUNTRY = "DE"
        private const val DEFAULT_MAX_RESULTS = 20
    }

    private val _poiState = MutableLiveData<PoiUiState>()
    val poiState: LiveData<PoiUiState> = _poiState

    fun fetchPois(country: String = DEFAULT_COUNTRY, maxResults: Int = DEFAULT_MAX_RESULTS) {
        viewModelScope.launch {
            _poiState.value = PoiUiState.Loading

            try {
                val result = poiRepository.getListPoi(country = country, maxResults = maxResults)
                result.fold(
                    ifLeft = { error ->
                        Log.e(TAG, "Error fetching POIs: $error")
                        _poiState.value = PoiUiState.Error
                    },
                    ifRight = { poisFromRepo ->
                        if (poisFromRepo.isEmpty()) {
                            _poiState.value = PoiUiState.Success(emptyList())
                        } else {
                            _poiState.value = PoiUiState.Success(mapToItemUiState(poisFromRepo))
                        }
                    }
                )
            } catch (e: Exception) {
                // Catch any other exceptions (e.g., unexpected network issues, mapping errors)
                Log.e(TAG, "Exception fetching POIs: ${e.message}", e)
                _poiState.value = PoiUiState.Error
            }
        }
    }

    // Example of a function to load favorite POIs - can be adapted similarly
    fun fetchFavoritePois() {
        viewModelScope.launch {
            _poiState.value = PoiUiState.Loading
            // ... implementation to load favorites from repository ...
            // try {
            //     val favoritePois = poiRepository.getFavoritePois()
            //     _poiState.value = PoiUiState.Success(mapToItemUiState(favoritePois))
            // } catch (e: Exception) {
            //     _poiState.value = PoiUiState.Error
            // }
        }
    }

    // Mapper function to convert your domain/data model (Poi) to UI state model (PoiItemUiState)
    private fun mapToItemUiState(pois: List<Poi>): List<PoiItemUiState> {
        return pois.map { poi ->
            PoiItemUiState(
                id = poi.id,
                title = poi.addressInfo?.title,
                address = formatAddress(poi.addressInfo),
                town = poi.addressInfo?.town,
                telephone = poi.operatorInfo?.phonePrimaryContact,
                distance = poi.addressInfo?.distance,
                distanceUnit = poi.addressInfo?.distanceUnit
            )
        }
    }

    // Helper function to format address string (example)
    private fun formatAddress(addressInfo: AddressInfo?): String {
        return listOfNotNull(
            addressInfo?.addressLine1,
            addressInfo?.addressLine2,
            addressInfo?.postcode,
            addressInfo?.town
        ).joinToString(separator = ", ").ifEmpty { "Address not available" }
    }

    // Example function to handle a favorite toggle action from the UI
    /*    fun toggleFavorite(poiId: Int, isCurrentlyFavorite: Boolean) {
            viewModelScope.launch {
                // Optimistically update the UI first for better responsiveness
                val currentSuccessState = _poiState.value as? PoiUiState.Success
                currentSuccessState?.let { successState ->
                    val updatedPois = successState.pois.map {
                        if (it.id == poiId) it.copy(isFavorite = !isCurrentlyFavorite) else it
                    }
                    _poiState.value = PoiUiState.Success(updatedPois)
                }

                // Then, call the repository to persist the change
                try {
                    if (isCurrentlyFavorite) {
                        poiRepository.unmarkAsFavorite(poiId)
                    } else {
                        poiRepository.markAsFavorite(poiId)
                    }
                    // Optionally, re-fetch or confirm the state from the repository
                    // fetchPois() // Or a more targeted update
                } catch (e: Exception) {
                    // Revert optimistic update on error
                    // Log.e(TAG, "Error toggling favorite: ${e.message}")
                    currentSuccessState?.let { _poiState.value = it } // Revert to previous success state
                    // Potentially show an error message to the user
                }
            }
        } */
}