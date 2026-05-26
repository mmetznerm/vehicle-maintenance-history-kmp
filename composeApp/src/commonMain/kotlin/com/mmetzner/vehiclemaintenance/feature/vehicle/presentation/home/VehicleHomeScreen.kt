package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Maintenance
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model.Vehicle
import org.jetbrains.compose.resources.painterResource
import vehiclemaintenance.composeapp.generated.resources.Res
import vehiclemaintenance.composeapp.generated.resources.car_hero

private val HomeBlue = Color(0xFF0B5CFF)
private val HomeBackground = Color(0xFFF7F8FA)
private val HomeBorder = Color(0xFFE1E6EF)
private val HomeMuted = Color(0xFF667085)

@Composable
fun VehicleHomeScreen(
    viewModel: VehicleHomeViewModel,
    onRegisterVehicle: () -> Unit,
    onAddMaintenance: (String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val uriHandler = LocalUriHandler.current

    Scaffold(
        containerColor = HomeBackground,
        bottomBar = { HomeBottomBar() }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val currentState = state) {
                VehicleHomeState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = HomeBlue
                    )
                }

                VehicleHomeState.Empty -> {
                    EmptyHomeState(
                        onRegisterVehicle = onRegisterVehicle,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is VehicleHomeState.Error -> {
                    ErrorState(
                        message = currentState.message,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is VehicleHomeState.Content -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        HomeTopBar(onLogout = onLogout)

                        VehicleHeroCard(
                            vehicle = currentState.vehicle,
                            onAddMaintenance = { onAddMaintenance(currentState.vehicle.plate) },
                            onShareHistory = {
                                uriHandler.openUri(currentState.vehicle.toWhatsAppShareUrl())
                            }
                        )

                        MaintenanceTimeline(
                            maintenances = currentState.vehicle.maintenances.orEmpty()
                        )
                    }
                }
            }
        }
    }
}

private fun Vehicle.toWhatsAppShareUrl(): String {
    val maintenanceCount = maintenances?.size ?: 0
    val message = buildString {
        appendLine("Maintenance history")
        appendLine("$year $brand $model")
        appendLine("Plate: $plate")
        append("Registered maintenance records: $maintenanceCount")
    }

    return "https://wa.me/?text=${message.urlEncoded()}"
}

private fun String.urlEncoded(): String = buildString {
    this@urlEncoded.encodeToByteArray().forEach { byte ->
        val value = byte.toInt() and 0xFF
        val char = value.toChar()
        when {
            char in 'A'..'Z' || char in 'a'..'z' || char in '0'..'9' -> append(char)
            char == '-' || char == '_' || char == '.' || char == '~' -> append(char)
            char == ' ' -> append("%20")
            else -> {
                append('%')
                append(value.toString(16).uppercase().padStart(2, '0'))
            }
        }
    }
}

@Composable
private fun HomeTopBar(onLogout: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier.size(28.dp),
                shape = CircleShape,
                color = Color(0xFFE6EEF9)
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null,
                    tint = HomeBlue,
                    modifier = Modifier.padding(6.dp)
                )
            }
            Text(
                text = "AutoLog",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = HomeBlue
            )
        }

        IconButton(onClick = onLogout) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Logout",
                tint = HomeBlue
            )
        }
    }
}

@Composable
private fun VehicleHeroCard(
    vehicle: Vehicle,
    onAddMaintenance: () -> Unit,
    onShareHistory: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, HomeBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = painterResource(Res.drawable.car_hero),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.82f)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "${vehicle.year} ${vehicle.brand} ${vehicle.model}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF111827)
                    )
                    Text(
                        text = "PLATE ${vehicle.plate}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = HomeMuted
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${vehicle.currentOdometerText()} km",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = HomeBlue
                    )
                    Text(
                        text = "Current Odometer",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF111827)
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Maintenance Health",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF111827)
                )
                Text(
                    text = "82%",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = HomeBlue
                )
            }

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { 0.82f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = HomeBlue,
                trackColor = Color(0xFFE7ECF5)
            )

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = onAddMaintenance,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(7.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HomeBlue,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Add Maintenance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = onShareHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(7.dp),
                border = BorderStroke(1.dp, Color(0xFFD3DAE6)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFFFBFCFF),
                    contentColor = HomeBlue
                )
            ) {
                Icon(Icons.Default.IosShare, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Share History",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun Vehicle.currentOdometerText(): String {
    val maxMileage = maintenances
        ?.mapNotNull { it.mileage }
        ?.maxOrNull()
        ?: 45_000

    return maxMileage.toString()
        .reversed()
        .chunked(3)
        .joinToString(".")
        .reversed()
}

@Composable
private fun MaintenanceTimeline(maintenances: List<Maintenance>) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Maintenance History",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF111827)
        )

        val items = if (maintenances.isEmpty()) {
            sampleMaintenanceItems()
        } else {
            maintenances.map { TimelineMaintenance.Real(it) }
        }

        items.forEachIndexed { index, item ->
            TimelineItem(
                item = item,
                isFirst = index == 0
            )
        }
    }
}

@Composable
private fun TimelineItem(
    item: TimelineMaintenance,
    isFirst: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(28.dp)
        ) {
            Surface(
                modifier = Modifier.size(if (isFirst) 18.dp else 15.dp),
                shape = CircleShape,
                color = if (isFirst) HomeBlue else Color(0xFFF2F4F7),
                contentColor = if (isFirst) Color.White else HomeMuted,
                border = if (isFirst) null else BorderStroke(1.dp, HomeBorder)
            ) {
                Icon(
                    imageVector = if (isFirst) Icons.Default.Settings else Icons.Default.History,
                    contentDescription = null,
                    modifier = Modifier.padding(if (isFirst) 4.dp else 3.dp)
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            if (isFirst) {
                Box(
                    modifier = Modifier
                        .padding(start = 10.dp, top = 18.dp)
                        .width(2.dp)
                        .height(112.dp)
                        .background(HomeBlue)
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = if (isFirst) 18.dp else 0.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, HomeBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (item is TimelineMaintenance.Scheduled) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            StatusPill("Scheduled")
                            Text(
                                text = "Next: ${item.nextDate}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF111827),
                                modifier = Modifier.widthIn(max = 112.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF111827)
                            )
                            Text(
                                text = item.subtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF111827)
                            )
                        }

                        if (item.amount != null || item.workshop != null) {
                            Column(horizontalAlignment = Alignment.End) {
                                item.amount?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = HomeBlue
                                    )
                                }
                                item.workshop?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF111827),
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.widthIn(max = 100.dp)
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF25324A),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (item.tags.isNotEmpty()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item.tags.forEach { tag ->
                                StatusPill(tag, muted = true)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusPill(
    text: String,
    muted: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (muted) Color(0xFFE5E7EB) else Color(0xFFE3EAFF),
        contentColor = if (muted) Color(0xFF667085) else HomeBlue
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun EmptyHomeState(
    onRegisterVehicle: () -> Unit,
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
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFFE3EAFF),
            contentColor = HomeBlue
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = null,
                modifier = Modifier.padding(16.dp)
            )
        }

        Text(
            text = "Register your vehicle",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Your maintenance history stays private and can be shared only when you choose.",
            style = MaterialTheme.typography.bodyMedium,
            color = HomeMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 360.dp)
        )

        Button(
            onClick = onRegisterVehicle,
            colors = ButtonDefaults.buttonColors(containerColor = HomeBlue)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Register vehicle")
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onErrorContainer,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun HomeBottomBar() {
    Surface(
        color = Color.White,
        shadowElevation = 6.dp,
        border = BorderStroke(1.dp, HomeBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarItem(
                selected = true,
                icon = Icons.Default.DirectionsCar,
                label = "Garage"
            )
            BottomBarItem(
                selected = false,
                icon = Icons.Default.History,
                label = "History"
            )
            BottomBarItem(
                selected = false,
                icon = Icons.Default.ShoppingBag,
                label = "Market"
            )
            BottomBarItem(
                selected = false,
                icon = Icons.Default.Settings,
                label = "Settings"
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    selected: Boolean,
    icon: ImageVector,
    label: String
) {
    Column(
        modifier = Modifier
            .width(62.dp)
            .clickable(onClick = {}),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Surface(
            modifier = Modifier
                .width(if (selected) 52.dp else 36.dp)
                .height(28.dp),
            shape = RoundedCornerShape(18.dp),
            color = if (selected) HomeBlue else Color.Transparent,
            contentColor = if (selected) Color.White else Color(0xFF1F2937)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (selected) HomeBlue else Color(0xFF1F2937)
        )
    }
}

private sealed interface TimelineMaintenance {
    val title: String
    val subtitle: String
    val description: String
    val amount: String?
    val workshop: String?
    val tags: List<String>

    data class Scheduled(
        override val title: String,
        override val subtitle: String,
        override val description: String,
        val nextDate: String,
        override val tags: List<String> = emptyList()
    ) : TimelineMaintenance {
        override val amount: String? = null
        override val workshop: String? = null
    }

    data class Real(
        val maintenance: Maintenance
    ) : TimelineMaintenance {
        override val title: String = maintenance.description
        override val subtitle: String = buildString {
            append(maintenance.date)
            maintenance.mileage?.let { append(" • $it km") }
        }
        override val description: String = maintenance.description
        override val amount: String? = maintenance.totalValue?.let { "$${it}" }
        override val workshop: String? = maintenance.workshopName
        override val tags: List<String> = listOf("Routine")
    }

    data class Sample(
        override val title: String,
        override val subtitle: String,
        override val description: String,
        override val amount: String?,
        override val workshop: String?,
        override val tags: List<String>
    ) : TimelineMaintenance
}

private fun sampleMaintenanceItems(): List<TimelineMaintenance> = listOf(
    TimelineMaintenance.Scheduled(
        title = "Annual Inspection",
        subtitle = "",
        description = "Recommended multi-point inspection to maintain warranty standards and vehicle safety.",
        nextDate = "Dec 15, 2023"
    ),
    TimelineMaintenance.Sample(
        title = "Oil & Filter Change",
        subtitle = "Oct 12, 2023 • 42,000 km",
        description = "Standard synthetic oil change including new oil filter and fluid top-up.",
        amount = "$85.00",
        workshop = "City Auto Care",
        tags = listOf("Engine", "Routine")
    ),
    TimelineMaintenance.Sample(
        title = "Brake Pad Replacement",
        subtitle = "July 05, 2023 • 38,500 km",
        description = "Front brake pads replaced with ceramic components. Rotors inspected.",
        amount = "$210.00",
        workshop = "Brake Pros HQ",
        tags = listOf("Safety", "Repair")
    ),
    TimelineMaintenance.Sample(
        title = "Tire Rotation & Balance",
        subtitle = "March 14, 2023 • 35,000 km",
        description = "Standard tire rotation to ensure even wear across all four tires.",
        amount = "$45.00",
        workshop = "Quick Service Express",
        tags = listOf("Wheels")
    )
)
