package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.vehicledetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Maintenance
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Vehicle

private val DetailsBlue = Color(0xFF0B5CFF)
private val DetailsBackground = Color(0xFFF7F8FA)
private val DetailsBorder = Color(0xFFE1E6EF)
private val DetailsMuted = Color(0xFF667085)

@Composable
fun VehicleDetailsScreen(
    viewModel: VehicleDetailsViewModel,
    vehicleId: String,
    onBack: () -> Unit,
    onEditVehicle: (Vehicle) -> Unit,
    onAddMaintenance: (Vehicle) -> Unit,
    onEditMaintenance: (Vehicle, Maintenance) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(vehicleId) {
        viewModel.load(vehicleId)
    }

    Scaffold(
        containerColor = DetailsBackground,
        topBar = {
            VehicleDetailsTopBar(
                isRefreshing = state.isRefreshing,
                onBack = onBack,
                onRefresh = viewModel::refresh
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
                        color = DetailsBlue
                    )
                }

                state.vehicle == null -> {
                    VehicleDetailsError(
                        message = state.errorMessage ?: "Veiculo nao encontrado.",
                        onRetry = viewModel::refresh,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    VehicleDetailsContent(
                        vehicle = state.vehicle!!,
                        errorMessage = state.errorMessage,
                        onRetry = viewModel::refresh,
                        onEditVehicle = { onEditVehicle(state.vehicle!!) },
                        onAddMaintenance = { onAddMaintenance(state.vehicle!!) },
                        onEditMaintenance = { maintenance ->
                            onEditMaintenance(state.vehicle!!, maintenance)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun VehicleDetailsTopBar(
    isRefreshing: Boolean,
    onBack: () -> Unit,
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color(0xFF111827)
                )
            }
            Text(
                text = "Detalhes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = Color(0xFF111827)
            )
        }

        IconButton(
            onClick = onRefresh,
            enabled = !isRefreshing
        ) {
            if (isRefreshing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = DetailsBlue
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Atualizar",
                    tint = DetailsBlue
                )
            }
        }
    }
}

@Composable
private fun VehicleDetailsContent(
    vehicle: Vehicle,
    errorMessage: String?,
    onRetry: () -> Unit,
    onEditVehicle: () -> Unit,
    onAddMaintenance: () -> Unit,
    onEditMaintenance: (Maintenance) -> Unit
) {
    val maintenances = vehicle.maintenances.orEmpty()
        .sortedByDescending { it.date }

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
            VehicleSummaryCard(
                vehicle = vehicle,
                onEditVehicle = onEditVehicle,
                onAddMaintenance = onAddMaintenance
            )
        }

        if (errorMessage != null) {
            item {
                InlineErrorCard(
                    message = errorMessage,
                    onRetry = onRetry
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Historico de manutencoes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
                Text(
                    text = "${maintenances.size} registro${if (maintenances.size == 1) "" else "s"}",
                    style = MaterialTheme.typography.labelMedium,
                    color = DetailsMuted
                )
            }
        }

        if (maintenances.isEmpty()) {
            item {
                EmptyMaintenanceCard(onAddMaintenance = onAddMaintenance)
            }
        } else {
            items(
                items = maintenances,
                key = { maintenance -> maintenance.remoteId ?: maintenance.id }
            ) { maintenance ->
                MaintenanceCard(
                    maintenance = maintenance,
                    onEditMaintenance = { onEditMaintenance(maintenance) }
                )
            }
        }
    }
}

@Composable
private fun VehicleSummaryCard(
    vehicle: Vehicle,
    onEditVehicle: () -> Unit,
    onAddMaintenance: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, DetailsBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(52.dp),
                    shape = CircleShape,
                    color = Color(0xFFE6EEF9),
                    contentColor = DetailsBlue
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${vehicle.brand} ${vehicle.model}".trim().ifBlank { "Veiculo" },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF111827),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = vehicle.plate,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = DetailsBlue
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryMetric(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.CalendarMonth,
                    label = "Ano",
                    value = vehicle.year.toString()
                )
                SummaryMetric(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Speed,
                    label = "Odometro",
                    value = vehicle.currentOdometerText()
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilledTonalButton(
                    onClick = onEditVehicle,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Editar")
                }

                Button(
                    onClick = onAddMaintenance,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DetailsBlue)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Manutencao")
                }
            }
        }
    }
}

@Composable
private fun SummaryMetric(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, DetailsBorder)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DetailsBlue,
                modifier = Modifier.size(18.dp)
            )
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = DetailsMuted
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun MaintenanceCard(
    maintenance: Maintenance,
    onEditMaintenance: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, DetailsBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = maintenance.description,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = maintenance.totalValue.toCurrencyText(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = DetailsBlue
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MaintenanceMeta(Icons.Default.CalendarMonth, maintenance.date)
                MaintenanceMeta(Icons.Default.Speed, "${maintenance.mileage ?: 0} km")
            }

            FilledTonalButton(
                onClick = onEditMaintenance,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Editar manutencao")
            }
        }
    }
}

@Composable
private fun MaintenanceMeta(
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
            tint = DetailsMuted,
            modifier = Modifier.size(15.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = DetailsMuted
        )
    }
}

@Composable
private fun EmptyMaintenanceCard(onAddMaintenance: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, DetailsBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                tint = DetailsBlue,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = "Nenhuma manutencao cadastrada",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Cadastre a primeira manutencao para acompanhar custos, datas e quilometragem.",
                style = MaterialTheme.typography.bodyMedium,
                color = DetailsMuted,
                textAlign = TextAlign.Center
            )
            FilledTonalButton(onClick = onAddMaintenance) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Adicionar")
            }
        }
    }
}

@Composable
private fun VehicleDetailsError(
    message: String,
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
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(42.dp)
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = DetailsMuted,
            textAlign = TextAlign.Center
        )
        FilledTonalButton(onClick = onRetry) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("Tentar novamente")
        }
    }
}

@Composable
private fun InlineErrorCard(
    message: String,
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
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            FilledTonalButton(onClick = onRetry) {
                Text("Atualizar")
            }
        }
    }
}

private fun Vehicle.currentOdometerText(): String {
    val currentOdometer = maintenances
        ?.mapNotNull { it.mileage }
        ?.maxOrNull()

    return currentOdometer?.let { "$it km" } ?: "Sem registro"
}

private fun Double?.toCurrencyText(): String {
    return this?.let { "R$ ${it}" } ?: "R$ 0.00"
}
