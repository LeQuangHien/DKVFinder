package com.hien.le.dkvfinder.feature.evcharging_compose.poi

import app.cash.turbine.test
import com.hien.le.dkvfinder.core.common.network.testing.MainDispatcherRule
import com.hien.le.dkvfinder.core.data.repository.PoiRepository
import com.hien.le.dkvfinder.core.model.data.PoiCompact
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class PoiViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var mockPoiRepository: PoiRepository

    private lateinit var viewModel: PoiViewModel

    // Test Data
    private val testPoiCompact1 =
        PoiCompact(
            id = 1,
            title = "Test Title 1",
            address = "Test Address 1",
            town = "Test Town 1",
            telephone = "123456789",
            distance = 10.0,
            distanceUnit = 1,
            isFavorite = false,
        )
    private val testPoiCompact2 =
        PoiCompact(
            id = 2,
            title = "Test Title 2",
            address = "Test Address 2",
            town = "Test Town 2",
            telephone = "987654321",
            distance = 20.0,
            distanceUnit = 2,
            isFavorite = true,
        )
    private val testPoiCompactList = listOf(testPoiCompact1, testPoiCompact2)

    private val testPoiItemUiState1 =
        PoiItemUiState(
            id = 1,
            title = "Test Title 1",
            address = "Test Address 1",
            town = "Test Town 1",
            telephone = "123456789",
            distance = 10.0,
            distanceUnit = 1,
            isFavorite = false,
        )
    private val testPoiItemUiState2 =
        PoiItemUiState(
            id = 2,
            title = "Test Title 2",
            address = "Test Address 2",
            town = "Test Town 2",
            telephone = "987654321",
            distance = 20.0,
            distanceUnit = 2,
            isFavorite = true,
        )
    private val testPoiItemUiStateList = listOf(testPoiItemUiState1, testPoiItemUiState2)

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        mockPoiRepository =
            mockk<PoiRepository> {
                coEvery { refreshPois(any(), any()) } returns true
                coEvery { updateFavoriteStatus(any(), any()) } returns Unit
            }
    }

    private fun initViewModel(initialPoisFlow: Flow<List<PoiCompact>> = flowOf(emptyList())) {
        every { mockPoiRepository.getPoisStream() } returns initialPoisFlow
        viewModel = PoiViewModel(mockPoiRepository)
    }

    @Test
    fun `isRefreshing initial state`() = runTest {
        // Verify that `isRefreshing` StateFlow is initially `false`.
        initViewModel()
        assertEquals(false, viewModel.isRefreshing.value)
    }

    @Test
    fun `isRefreshing state during refreshData success`() = runTest {
        // Verify `isRefreshing` becomes `true` when `refreshData` starts and `false` when it successfully completes.
        initViewModel()

        viewModel.refreshData()
        assertEquals(true, viewModel.isRefreshing.value)

        advanceUntilIdle()
        assertEquals(false, viewModel.isRefreshing.value)
    }

    @Test
    fun `isRefreshing state during refreshData failure`() = runTest {
        // Verify `isRefreshing` becomes `true` when `refreshData` starts and `false` even if an exception occurs during the refresh.
        coEvery { mockPoiRepository.refreshPois(any(), any()) } returns false
        initViewModel()

        viewModel.refreshData()
        assertEquals(true, viewModel.isRefreshing.value)

        advanceUntilIdle()
        assertEquals(false, viewModel.isRefreshing.value)
    }

    @Test
    fun `getPoiStateFlow initial state`() = runTest {
        // Verify that `poiStateFlow` initially emits `PoiUiState.Loading`.
        val poiFlow = MutableSharedFlow<List<PoiCompact>>()
        initViewModel(initialPoisFlow = poiFlow)

        viewModel.poiStateFlow.test {
            val initialState = awaitItem()
            assertEquals(initialState, PoiUiState.Loading)
            cancelAndConsumeRemainingEvents() // Important to stop collection
        }
    }

    @Test
    fun `getPoiStateFlow emits Success with data`() = runTest {
        val poiFlow = MutableSharedFlow<List<PoiCompact>>(replay = 1)
        initViewModel(initialPoisFlow = poiFlow)

        viewModel.poiStateFlow.test {
            assertEquals(PoiUiState.Loading, awaitItem()) // From onStart or initialValue

            poiFlow.emit(testPoiCompactList)
            advanceUntilIdle()

            val successState = awaitItem()
            assertEquals(PoiUiState.Success(testPoiItemUiStateList), successState)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getPoiStateFlow emits Empty`() = runTest {
        // Verify `poiStateFlow` emits `PoiUiState.Empty` with the default message when the repository stream provides an empty list of POIs.
        val poiFlow = MutableSharedFlow<List<PoiCompact>>(replay = 1)
        initViewModel(initialPoisFlow = poiFlow)

        viewModel.poiStateFlow.test {
            assertEquals(PoiUiState.Loading, awaitItem())

            poiFlow.emit(emptyList()) // Emit empty list
            advanceUntilIdle()

            val emptyState = awaitItem()
            assertEquals(
                PoiUiState.Empty("No charging stations found. Try refreshing."),
                emptyState,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getPoiStateFlow emits Error on stream exception`() {
        // Verify `poiStateFlow` emits `PoiUiState.Error` when an exception occurs in the `poiRepository.getPoisStream()`.
        // TODO implement test
    }

    @Test
    fun `refreshData calls repository with default parameters`() = runTest {
        // Verify `poiRepository.refreshPois` is called with `DEFAULT_COUNTRY` and `DEFAULT_MAX_RESULTS` when `refreshData` is called without arguments.
        initViewModel() // Default flow for getPoisStream
        viewModel.refreshData()
        advanceUntilIdle()

        coVerify {
            mockPoiRepository.refreshPois(
                PoiViewModel.DEFAULT_COUNTRY,
                PoiViewModel.DEFAULT_MAX_RESULTS,
            )
        }
    }

    @Test
    fun `refreshData calls repository with provided parameters`() = runTest {
        // Verify `poiRepository.refreshPois` is called with the specified `country` and `maxResults` when `refreshData` is called with arguments.
        initViewModel()
        val country = "US"
        val maxResults = 10
        viewModel.refreshData(country, maxResults)
        advanceUntilIdle()

        coVerify { mockPoiRepository.refreshPois(country, maxResults) }
    }

    @Test
    fun `refreshData updates data and emits Success`() = runTest {
        val poiFlow = MutableSharedFlow<List<PoiCompact>>(replay = 1)
        initViewModel(initialPoisFlow = poiFlow)

        viewModel.poiStateFlow.test {
            assertEquals(PoiUiState.Loading, awaitItem())

            viewModel.refreshData()
            advanceUntilIdle()

            poiFlow.emit(testPoiCompactList) // Simulate DB update and flow emission
            advanceUntilIdle()

            val successState = awaitItem()
            assertEquals(PoiUiState.Success(testPoiItemUiStateList), successState)
            cancelAndConsumeRemainingEvents()
        }
        coVerify { mockPoiRepository.refreshPois(any(), any()) }
    }

    @Test
    fun `refreshData with empty country string`() = runTest {
        // Test how `refreshData` (and consequently the repository) handles an empty string for the `country` parameter.
        coEvery { mockPoiRepository.refreshPois(any(), any()) } returns false
        initViewModel()

        val country = ""
        val maxResults = 10
        viewModel.refreshData(country, maxResults)
        assertEquals(true, viewModel.isRefreshing.value)

        advanceUntilIdle()

        assertEquals(false, viewModel.isRefreshing.value)

        coVerify { mockPoiRepository.refreshPois(country, maxResults) }
    }

    @Test
    fun `refreshData with invalid country string`() = runTest(mainDispatcherRule.testDispatcher) {
        // Test how `refreshData` (and consequently the repository) handles an invalid or non-existent country code for the `country` parameter.
        val invalidCountry = "XXYYZZ" // A clearly invalid country code
        val maxResults = 15

        coEvery { mockPoiRepository.refreshPois(invalidCountry, maxResults) } returns false
        initViewModel()

        viewModel.refreshData(country = invalidCountry, maxResults = maxResults)

        assertEquals(true, viewModel.isRefreshing.value)

        advanceUntilIdle()

        assertEquals(false, viewModel.isRefreshing.value)

        coVerify { mockPoiRepository.refreshPois(invalidCountry, maxResults) }
    }


    @Test
    fun `refreshData with zero maxResults`() = runTest(mainDispatcherRule.testDispatcher) {
        val country = "US"
        val zeroMaxResults = 0

        coEvery { mockPoiRepository.refreshPois(country, zeroMaxResults) } returns true
        initViewModel()

        viewModel.refreshData(country = country, maxResults = zeroMaxResults)

        assertEquals(true, viewModel.isRefreshing.value)

        advanceUntilIdle()

        assertEquals(false, viewModel.isRefreshing.value)

        coVerify { mockPoiRepository.refreshPois(country, zeroMaxResults) }
    }

    @Test
    fun `refreshData with negative maxResults`() = runTest {
        // Test how `refreshData` handles a negative value for `maxResults`.
        val country = "DE"
        val negativeMaxResults = -20

        coEvery { mockPoiRepository.refreshPois(country, negativeMaxResults) } returns false
        initViewModel()

        viewModel.refreshData(country = country, maxResults = negativeMaxResults)

        assertEquals(true, viewModel.isRefreshing.value)

        advanceUntilIdle()

        assertEquals(false, viewModel.isRefreshing.value)

        coVerify { mockPoiRepository.refreshPois(country, negativeMaxResults) }
    }


    @Test
    fun `toggleFavorite calls repository to update status  favorite to unfavorite `() = runTest {
        // Verify `poiRepository.updateFavoriteStatus` is called with the correct `poiId` and `false` when `isCurrentlyFavorite` is `true`.
        val poiId = 1
        val isCurrentlyFavorite = true

        coEvery { mockPoiRepository.updateFavoriteStatus(poiId, false) } returns Unit
        initViewModel()

        viewModel.toggleFavorite(poiId, isCurrentlyFavorite)
        advanceUntilIdle()

        coVerify { mockPoiRepository.updateFavoriteStatus(poiId, false) }
    }

    @Test
    fun `toggleFavorite calls repository to update status  unfavorite to favorite `() = runTest {
        // Verify `poiRepository.updateFavoriteStatus` is called with the correct `poiId` and `true` when `isCurrentlyFavorite` is `false`.
        val poiId = 1
        val isCurrentlyFavorite = false

        coEvery { mockPoiRepository.updateFavoriteStatus(poiId, true) } returns Unit
        initViewModel()

        viewModel.toggleFavorite(poiId, isCurrentlyFavorite)
        advanceUntilIdle()

        coVerify { mockPoiRepository.updateFavoriteStatus(poiId, true) }
    }
}
