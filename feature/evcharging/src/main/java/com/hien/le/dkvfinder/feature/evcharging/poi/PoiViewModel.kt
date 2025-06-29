package com.hien.le.dkvfinder.feature.evcharging.poi

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.hien.le.dkvfinder.core.data.repository.PoiRepository
import com.hien.le.dkvfinder.core.model.data.PoiCompact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
    data class Empty(val message: String) : PoiUiState
    data object Error : PoiUiState
}

@HiltViewModel
class PoiViewModel @Inject constructor(
    private val poiRepository: PoiRepository
) : ViewModel() {

    companion object {
        private const val TAG = "PoiViewModel"
        const val DEFAULT_COUNTRY = "DE"
        const val DEFAULT_MAX_RESULTS = 20
        private const val DEFAULT_EMPTY_MESSAGE = "No charging stations found. Try refreshing."
    }

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    val poiStateFlow: StateFlow<PoiUiState> =
        poiRepository.getPoisStream()
            .catch { e ->
                Log.e(TAG, "Error in POI stream: ${e.message}")
                PoiUiState.Error
            }
            .map { pois ->
                if (pois.isEmpty()) {
                    PoiUiState.Empty(DEFAULT_EMPTY_MESSAGE) // Emit Empty if the stream from DB is empty
                } else {
                    PoiUiState.Success(mapToItemUiState(pois))
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PoiUiState.Loading
            )
    val poiStreamLiveData: LiveData<PoiUiState> = poiStateFlow.asLiveData()

    fun refreshData(country: String = DEFAULT_COUNTRY, maxResults: Int = DEFAULT_MAX_RESULTS) {
        _isRefreshing.value = true
        viewModelScope.launch {
            try {
                poiRepository.refreshPois(country, maxResults)
            } catch (e: Exception) {
                Log.e(TAG, "Error in refreshing data : ${e.message}")
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun mapToItemUiState(pois: List<PoiCompact>): List<PoiItemUiState> {
        return pois.map { poi ->
            PoiItemUiState(
                id = poi.id,
                title = poi.title,
                address = poi.address,
                town = poi.town,
                telephone = poi.telephone,
                distance = poi.distance,
                distanceUnit = poi.distanceUnit,
                isFavorite = poi.isFavorite
            )
        }
    }

    fun toggleFavorite(poiId: Int, isCurrentlyFavorite: Boolean) {
        viewModelScope.launch {
            poiRepository.updateFavoriteStatus(poiId, !isCurrentlyFavorite)
        }
    }
}