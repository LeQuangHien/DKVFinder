package com.hien.le.dkvfinder.feature.evcharging.poi

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

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
    fun `isRefreshing initial state`() =
        runTest {
            // Verify that isRefreshing LiveData is initially false.
            initViewModel()
            assertEquals(false, viewModel.isRefreshing.value)
        }

    @Test
    fun `isRefreshing updates during refreshData`() =
        runTest {
            // Verify that isRefreshing LiveData becomes true when refreshData is called and false when it completes.
            initViewModel()

            viewModel.refreshData()
            assertEquals(true, viewModel.isRefreshing.value)

            advanceUntilIdle()
            assertEquals(false, viewModel.isRefreshing.value)
        }

    @Test
    fun `isRefreshing remains false on refreshData failure`() =
        runTest {
            // Verify that isRefreshing LiveData becomes false even if poiRepository.refreshPois returns false.
            coEvery { mockPoiRepository.refreshPois(any(), any()) } returns false
            initViewModel()

            viewModel.refreshData()
            assertEquals(true, viewModel.isRefreshing.value)

            advanceUntilIdle()
            assertEquals(false, viewModel.isRefreshing.value)
        }

    @Test
    fun `getPoiStreamLiveData initial state is Loading`() =
        runTest {
            // Verify that poiStreamLiveData emits PoiUiState.Loading as its initial value.
            // Using MutableSharedFlow to control emissions precisely
            val poiFlow = MutableSharedFlow<List<PoiCompact>>()
            initViewModel(initialPoisFlow = poiFlow)

            viewModel.poiStateFlow.test {
                val initialState = awaitItem()
                assertEquals(initialState, PoiUiState.Loading)
                cancelAndConsumeRemainingEvents() // Important to stop collection
            }
        }

    @Test
    fun `getPoiStreamLiveData emits Success with data`() =
        runTest {
            // Verify that poiStreamLiveData emits PoiUiState.Success with mapped PoiItemUiState list when the repository provides data.
            // Adding replay = 1 ensures that if the collector (from stateIn) subscribes slightly after an emit ,
            // it still gets the last emitted value.
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
    fun `getPoiStreamLiveData emits Empty when repository provides no data`() =
        runTest {
            // Verify that poiStreamLiveData emits PoiUiState.Empty with the default message when the repository provides an empty list of POIs.
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

    /*
    @Test
    fun `getPoiStreamLiveData emits Error on repository stream error`() = runTest {
        // Verify that poiStreamLiveData emits PoiUiState.Error when the repository's getPoisStream flow emits an error.
        val errorFlow = flow<List<PoiCompact>> { // Assuming PoiCompact from previous context
            throw RuntimeException("Network Error From Test")
        }
        initViewModel(initialPoisFlow = errorFlow)
        advanceUntilIdle()

        viewModel.poiStateFlow.test {
            // Loading from initialValue or onStart
            // Depending on timing, it could be Loading or Error directly if the error happens fast
            // If onStart emits Loading, then we expect Error next.
            // Emit empty list to trigger error
            val firstExpectedItem = awaitItem()
            if (firstExpectedItem is PoiUiState.Loading) {
                assertEquals(PoiUiState.Error, awaitItem())
            } else {
                assertEquals(PoiUiState.Error, awaitItem())
            }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getPoiStreamLiveData subscribes and unsubscribes correctly`() = runTest {
        // Verify that the underlying flow from poiRepository.getPoisStream is started when poiStreamLiveData is observed and stopped when no longer observed (considering SharingStarted.WhileSubscribed).
        var subscriptions = 0
        val poiFlow = flow {
            subscriptions++
            emit(emptyList<PoiCompact>())
            // Keep flow alive for a bit to check unsubscription, not strictly needed with SharedFlow usually
            // kotlinx.coroutines.delay(100)
        }
        initViewModel(initialPoisFlow = poiFlow)

        val job1 = launch {
            viewModel.poiStateFlow.test {
                awaitItem() // Collect one item
                // subscriptions should be 1 after first collection starts
            }
        }
        advanceUntilIdle() // Ensure collection starts
        assertEquals(subscriptions, 1)
        job1.cancel() // Unsubscribe

        // After a delay (longer than SharingStarted.WhileSubscribed timeout of 5s),
        // the upstream flow should be cancelled if no other subscribers.
        // For WhileSubscribed(0), it would be immediate.
        // Testing the timeout precisely can be tricky and might require advanceTimeBy().
        // For simplicity, we'll check if a new subscription creates a new upstream subscription.

        advanceTimeBy(6000) // Advance past the 5s timeout

        val job2 = launch {
            viewModel.poiStateFlow.test {
                awaitItem()
            }
        }
        advanceUntilIdle()
        // If the previous flow was truly cancelled and restarted due to WhileSubscribed
        assertEquals(subscriptions, 2) // New subscription to upstream
        job2.cancel()
    }
     */

    @Test
    fun `refreshData calls repository with default parameters`() =
        runTest {
            // initViewModel needs to be called to instantiate viewModel
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
    fun `refreshData calls repository with provided parameters`() =
        runTest {
            initViewModel()
            val country = "US"
            val maxResults = 10
            viewModel.refreshData(country, maxResults)
            advanceUntilIdle()

            coVerify { mockPoiRepository.refreshPois(country, maxResults) }
        }

    /*@Test
    fun `refreshData handles successful repository refresh`() = runTest {
        coEvery { mockPoiRepository.refreshPois(any(), any()) } returns true
        val poiFlow = MutableSharedFlow<List<PoiCompact>>(replay = 1) // replay for initial state
        initViewModel(initialPoisFlow = poiFlow)


        viewModel.poiStateFlow.test {
            assert(awaitItem() is PoiUiState.Loading)
            assert(awaitItem() is PoiUiState.Empty) // // From initial poiFlow emit

            coEvery { mockPoiRepository.refreshPois(any(), any()) } coAnswers {
                poiFlow.emit(testPoiCompactList) // Simulate DB update and flow emission
                true
            }

            viewModel.refreshData()
            // The successState will be emitted by the poiFlow via the refreshPois mock
            advanceUntilIdle()

            val successState = awaitItem()
            assert(awaitItem() is PoiUiState.Success)
            assertEquals(PoiUiState.Success(testPoiItemUiStateList), successState)
            cancelAndConsumeRemainingEvents()
        }
        coVerify { mockPoiRepository.refreshPois(any(), any()) }
    } */

    @Test
    fun `refreshData handles failed repository refresh`() {
        // Verify that _isRefreshing is set to false after a failed call to poiRepository.refreshPois (returns false) and logs a warning.
        // TODO implement test
    }

    @Test
    fun `refreshData concurrent calls`() {
        // Verify behavior when refreshData is called multiple times concurrently. Ensure _isRefreshing state is managed correctly and repository calls are handled appropriately (e.g., last call wins or queuing).
        // TODO implement test
    }

    @Test
    fun `refreshData with empty country string`() {
        // Verify behavior when refreshData is called with an empty string for the country parameter. Check if repository handles it or if there's specific ViewModel logic.
        // TODO implement test
    }

    @Test
    fun `refreshData with negative maxResults`() {
        // Verify behavior when refreshData is called with a negative value for maxResults. Check if repository handles it or if there's specific ViewModel logic (e.g., defaults or throws error).
        // TODO implement test
    }

    @Test
    fun `refreshData with zero maxResults`() {
        // Verify behavior when refreshData is called with zero for maxResults.
        // TODO implement test
    }

    @Test
    fun `toggleFavorite calls repository to mark as favorite`() {
        // Verify that toggleFavorite calls poiRepository.updateFavoriteStatus with the correct poiId and 'true' when isCurrentlyFavorite is false.
        // TODO implement test
    }

    @Test
    fun `toggleFavorite calls repository to unmark as favorite`() {
        // Verify that toggleFavorite calls poiRepository.updateFavoriteStatus with the correct poiId and 'false' when isCurrentlyFavorite is true.
        // TODO implement test
    }

    @Test
    fun `toggleFavorite with non existent poiId`() {
        // Verify behavior when toggleFavorite is called with a poiId that does not exist in the repository. (Depends on repository behavior - should not crash).
        // TODO implement test
    }

    @Test
    fun `toggleFavorite concurrent calls for the same poiId`() {
        // Verify behavior when toggleFavorite is called multiple times concurrently for the same poiId. Ensure final state is consistent.
        // TODO implement test
    }

    @Test
    fun `ViewModelJob cancellation on ViewModel cleared`() {
        // Verify that any ongoing coroutines launched by viewModelScope (in refreshData, toggleFavorite, and the poiStreamLiveData collection) are cancelled when the ViewModel is cleared.
        // TODO implement test
    }

    @Test
    fun `mapToItemUiState with empty input list`() {
        // Verify that mapToItemUiState returns an empty list when given an empty list of PoiCompact.
        // TODO implement test
    }

    @Test
    fun `mapToItemUiState with null fields in PoiCompact`() {
        // If PoiCompact fields can be null, verify that mapToItemUiState handles them gracefully (e.g., maps to empty strings or default values).
        // TODO implement test
    }
}
