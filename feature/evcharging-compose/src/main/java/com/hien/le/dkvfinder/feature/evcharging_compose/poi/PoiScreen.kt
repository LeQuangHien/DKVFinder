package com.hien.le.dkvfinder.feature.evcharging_compose.poi

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PoiScreen(
    modifier: Modifier = Modifier,
    onItemClicked: (Int) -> Unit,
    viewModel: PoiViewModel = hiltViewModel()
) {
    val poiUiState = viewModel.poiStateFlow.collectAsStateWithLifecycle()
    PoiScreenScaffold(
        modifier = modifier,
        poiUiState = poiUiState.value,
        onFavoriteClicked = viewModel::toggleFavorite,
        onItemClicked = onItemClicked
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoiScreenScaffold(
    modifier: Modifier = Modifier,
    poiUiState: PoiUiState,
    onFavoriteClicked: (Int, Boolean) -> Unit,
    onItemClicked: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("EV Charging Stations") })
        }
    ) { padding ->
        // Determine alignment based on the UI state
        val boxAlignment = when (poiUiState) {
            is PoiUiState.Success -> Alignment.TopCenter
            else -> Alignment.Center
        }
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = boxAlignment
        ) {
            when (poiUiState) {
                is PoiUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is PoiUiState.Empty -> {
                    Text(text = poiUiState.message)
                }

                is PoiUiState.Success -> {
                    PoiList(
                        pois = poiUiState.pois,
                        onFavoriteClicked = onFavoriteClicked,
                        onItemClicked = onItemClicked
                    )
                }

                is PoiUiState.Error -> {
                    Text(text = "Error loading data")
                }
            }
        }
    }
}

@Composable
fun PoiList(
    modifier: Modifier = Modifier,
    pois: List<PoiItemUiState>,
    onFavoriteClicked: (Int, Boolean) -> Unit,
    onItemClicked: (Int) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(items = pois, key = { it.id ?: -1 }) { poi ->
            PoiItem(
                poi = poi,
                onFavoriteClicked = onFavoriteClicked,
                onItemClicked = onItemClicked
            )
        }
    }

}

@Composable
fun PoiItem(
    modifier: Modifier = Modifier,
    poi: PoiItemUiState,
    onFavoriteClicked: (Int, Boolean) -> Unit,
    onItemClicked: (Int) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = { poi.id?.let { onItemClicked(it) } }),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    top = 16.dp,
                    bottom = 16.dp,
                    end = 8.dp
                ), // Adjust padding for IconButton
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f) // Column takes available space
            ) {
                Text(text = (poi.title ?: "N/A"), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = poi.address ?: "Address not available",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = poi.town ?: "", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
                poi.telephone?.let {
                    Text(text = "Telephone: $it", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(4.dp))
                poi.distance?.let {
                    Text(
                        text = "Distance: $it ${poi.distanceUnit ?: ""}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            // Favorite Button
            IconButton(
                onClick = { poi.id?.let { onFavoriteClicked(it, poi.isFavorite) } },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = if (poi.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (poi.isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (poi.isFavorite) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
        }
    }
}

@Preview
@Composable
fun PoiItemPreview() {
    PoiItem(
        poi = PoiItemUiState(
            id = 1,
            title = "Charging Station Name",
            address = "123 Example Street, Suburb",
            town = "CityName",
            telephone = null,
            distance = null,
            distanceUnit = null,
            isFavorite = true,
        ),
        onFavoriteClicked = { _, _ -> },
        onItemClicked = {}
    )
}

@Preview
@Composable
fun PoiListPreview(modifier: Modifier = Modifier) {
    PoiList(
        modifier = modifier,
        pois = pois,
        onFavoriteClicked = { _, _ -> },
        onItemClicked = {}
    )
}

@Preview
@Composable
fun PoiScreenScaffoldPreview() {
    PoiScreenScaffold(
        poiUiState = PoiUiState.Success(pois),
        onFavoriteClicked = { _, _ -> },
        onItemClicked = {}
    )
}

private val pois = listOf(
    PoiItemUiState(
        id = 1,
        title = "Charging Station Lidl",
        address = "123 Example Street, 45128 Essen",
        town = "CityName",
        telephone = null,
        distance = null,
        distanceUnit = null,
        isFavorite = true,
    ),
    PoiItemUiState(
        id = 2,
        title = "Charging Station Name",
        address = "123 Example Street, Suburb",
        town = "CityName",
        telephone = null,
        distance = null,
        distanceUnit = null,
        isFavorite = true,
    )
)
