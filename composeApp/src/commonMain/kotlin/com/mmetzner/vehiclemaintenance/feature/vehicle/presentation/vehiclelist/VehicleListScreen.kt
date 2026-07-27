package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.vehiclelist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mmetzner.vehiclemaintenance.core.ui.branding.VehicleHistoryMark
import com.mmetzner.vehiclemaintenance.core.ui.components.LogoutOverflowMenu
import com.mmetzner.vehiclemaintenance.core.ui.preview.PreviewVehicleRepository
import com.mmetzner.vehiclemaintenance.core.ui.theme.VehicleMaintenanceTheme
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Vehicle
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import vehiclemaintenance.composeapp.generated.resources.*

private val ListBlue = Color(0xFF0B5CFF)
private val ListBackground = Color(0xFFF7F8FA)
private val ListBorder = Color(0xFFE1E6EF)
private val ListMuted = Color(0xFF667085)

@Composable
fun VehicleListScreen(
    viewModel: VehicleListViewModel,
    onRegisterVehicle: () -> Unit,
    onOpenVehicle: (Vehicle) -> Unit,
    onAddMaintenance: (Vehicle) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = ListBackground,
        topBar = {
            VehicleListTopBar(
                isRefreshing = state.isRefreshing,
                onRefresh = viewModel::refreshVehicles,
                onLogout = onLogout
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = ListBlue
                    )
                }

                state.vehicles.isEmpty() -> {
                    EmptyVehicleListState(
                        errorMessage = state.errorMessage,
                        onRegisterVehicle = onRegisterVehicle,
                        onRetry = viewModel::refreshVehicles,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    VehicleListContent(
                        state = state,
                        onRegisterVehicle = onRegisterVehicle,
                        onOpenVehicle = onOpenVehicle,
                        onAddMaintenance = onAddMaintenance,
                        onRetry = viewModel::refreshVehicles
                    )
                }
            }
        }
    }
}

@Composable
private fun VehicleListTopBar(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onLogout: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            VehicleHistoryMark(
                modifier = Modifier.size(30.dp),
                elevation = 4.dp
            )
            Text(
                text = stringResource(Res.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = ListBlue
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(
                onClick = onRefresh,
                enabled = !isRefreshing
            ) {
                if (isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = ListBlue
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(Res.string.action_refresh),
                        tint = ListBlue
                    )
                }
            }
            LogoutOverflowMenu(
                onLogout = onLogout,
                iconTint = ListBlue
            )
        }
    }
}

@Composable
private fun VehicleListContent(
    state: VehicleListState,
    onRegisterVehicle: () -> Unit,
    onOpenVehicle: (Vehicle) -> Unit,
    onAddMaintenance: (Vehicle) -> Unit,
    onRetry: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 16.dp,
            top = 8.dp,
            end = 16.dp,
            bottom = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            VehicleListHeader(
                vehicleCount = state.vehicles.size,
                onRegisterVehicle = onRegisterVehicle
            )
        }

        if (state.errorMessage != null) {
            item {
                InlineErrorCard(
                    message = state.errorMessage,
                    onRetry = onRetry
                )
            }
        }

        items(
            items = state.vehicles,
            key = { vehicle -> vehicle.id ?: vehicle.plate }
        ) { vehicle ->
            VehicleListCard(
                vehicle = vehicle,
                onOpenVehicle = { onOpenVehicle(vehicle) },
                onAddMaintenance = { onAddMaintenance(vehicle) }
            )
        }
    }
}

@Composable
private fun VehicleListHeader(
    vehicleCount: Int,
    onRegisterVehicle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.vehicles_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = Color(0xFF111827)
            )
            Text(
                text = pluralStringResource(
                    Res.plurals.registered_vehicle_count,
                    vehicleCount,
                    vehicleCount
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = ListMuted
            )
        }

        Button(
            onClick = onRegisterVehicle,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ListBlue)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text(stringResource(Res.string.action_add))
        }
    }
}

@Composable
private fun VehicleListCard(
    vehicle: Vehicle,
    onOpenVehicle: () -> Unit,
    onAddMaintenance: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ListBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(46.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE6EEF9),
                    contentColor = ListBlue
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(
                            Res.string.vehicle_name,
                            vehicle.brand,
                            vehicle.model
                        ).trim()
                            .ifBlank { stringResource(Res.string.vehicle_fallback_name) },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PlateBadge(vehicle.plate)
                        VehicleMeta(Icons.Default.CalendarMonth, vehicle.year.toString())
                        if (vehicle.color.isNotBlank()) {
                            VehicleMeta(Icons.Default.Palette, vehicle.color)
                        }
                    }

                    VehicleMeta(
                        icon = Icons.Default.Speed,
                        text = vehicle.currentOdometerText()
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                FilledTonalButton(
                    onClick = onOpenVehicle,
                    enabled = vehicle.id != null,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        stringResource(
                            if (vehicle.id == null) {
                                Res.string.vehicle_syncing
                            } else {
                                Res.string.action_details
                            }
                        )
                    )
                }

                Spacer(Modifier.width(8.dp))

                FilledTonalButton(
                    onClick = onAddMaintenance,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(Res.string.action_new_maintenance))
                }
            }
        }
    }
}

@Composable
private fun PlateBadge(plate: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFF111827),
        contentColor = Color.White
    ) {
        Text(
            text = plate,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun VehicleMeta(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ListMuted,
            modifier = Modifier.size(15.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = ListMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun EmptyVehicleListState(
    errorMessage: StringResource?,
    onRegisterVehicle: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFE3EAFF),
            contentColor = ListBlue
        ) {
            Icon(
                imageVector = if (errorMessage == null) Icons.Default.DirectionsCar else Icons.Default.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.padding(16.dp)
            )
        }

        Text(
            text = stringResource(
                if (errorMessage == null) {
                    Res.string.vehicle_no_registered
                } else {
                    Res.string.vehicle_could_not_refresh
                }
            ),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = errorMessage?.let { stringResource(it) }
                ?: stringResource(Res.string.vehicle_empty_description),
            style = MaterialTheme.typography.bodyMedium,
            color = ListMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 360.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (errorMessage != null) {
                FilledTonalButton(onClick = onRetry) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(Res.string.action_try_again))
                }
            }

            Button(
                onClick = onRegisterVehicle,
                colors = ButtonDefaults.buttonColors(containerColor = ListBlue)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text(stringResource(Res.string.action_add_vehicle))
            }
        }
    }
}

@Composable
private fun InlineErrorCard(
    message: StringResource,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = stringResource(message),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            FilledTonalButton(onClick = onRetry) {
                Text(stringResource(Res.string.action_refresh))
            }
        }
    }
}

@Composable
private fun Vehicle.currentOdometerText(): String {
    val currentOdometer = maintenances
        ?.mapNotNull { it.mileage }
        ?.maxOrNull()

    return if (currentOdometer == null) {
        stringResource(Res.string.vehicle_no_odometer_reading)
    } else {
        stringResource(Res.string.vehicle_odometer_km_number, currentOdometer)
    }
}

@Preview
@Composable
private fun VehicleListScreenPreview() {
    val viewModel = remember { VehicleListViewModel(PreviewVehicleRepository) }

    VehicleMaintenanceTheme {
        VehicleListScreen(
            viewModel = viewModel,
            onRegisterVehicle = {},
            onOpenVehicle = {},
            onAddMaintenance = {},
            onLogout = {}
        )
    }
}
