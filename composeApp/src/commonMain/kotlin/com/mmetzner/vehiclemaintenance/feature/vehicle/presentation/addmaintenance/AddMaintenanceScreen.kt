package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.addmaintenance

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private val MaintenanceBlue = Color(0xFF0B5CFF)
private val MaintenanceBackground = Color(0xFFF7F8FA)
private val MaintenanceBorder = Color(0xFFD7DEEA)
private val MaintenanceMuted = Color(0xFF667085)

@Composable
fun AddMaintenanceScreen(
    viewModel: AddMaintenanceViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    plate: String,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(plate) {
        viewModel.onEvent(AddMaintenanceEvent.SetPlate(plate))
    }

    LaunchedEffect(state.success) {
        if (state.success) {
            onSuccess()
        }
    }

    Scaffold(
        containerColor = MaintenanceBackground
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 6.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            MaintenanceTopBar(onBack = onBack)

            Column(
                modifier = Modifier.padding(horizontal = 0.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Add Maintenance",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF111827)
                )
                Text(
                    text = "Keep your vehicle's health log up to date.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF25324A)
                )
            }

            FormSection(
                title = "Service Details",
                icon = Icons.Default.Speed
            ) {
                FieldLabel("SERVICE TYPE")
                MaintenanceTextField(
                    value = state.serviceType,
                    onValueChange = { viewModel.onEvent(AddMaintenanceEvent.UpdateServiceType(it)) },
                    placeholder = "Select service type",
                    trailingIcon = {
                        Icon(Icons.Default.ExpandMore, contentDescription = null)
                    }
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ServiceChip("Oil Change")
                    ServiceChip("Brakes")
                    ServiceChip("Tires")
                }

                Spacer(Modifier.height(4.dp))

                FieldLabel("CURRENT MILEAGE (km/mi)")
                MaintenanceTextField(
                    value = state.mileage,
                    onValueChange = { viewModel.onEvent(AddMaintenanceEvent.UpdateMileage(it.filter(Char::isDigit))) },
                    placeholder = "e.g. 45,200",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )

                FieldLabel("TOTAL COST ($)")
                MaintenanceTextField(
                    value = state.totalValue,
                    onValueChange = { viewModel.onEvent(AddMaintenanceEvent.UpdateValue(it)) },
                    placeholder = "$ 0.00",
                    leadingIcon = {
                        Icon(Icons.Default.Payments, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    )
                )

                FieldLabel("SERVICE DATE")
                MaintenanceTextField(
                    value = state.date,
                    onValueChange = { viewModel.onEvent(AddMaintenanceEvent.UpdateDate(it)) },
                    placeholder = "dd/mm/aaaa",
                    trailingIcon = {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null)
                    }
                )
            }

            FormSection(
                title = "Additional Information",
                icon = Icons.Default.Description
            ) {
                FieldLabel("REPLACED PARTS")
                MaintenanceTextField(
                    value = state.replacedParts,
                    onValueChange = { viewModel.onEvent(AddMaintenanceEvent.UpdateReplacedParts(it)) },
                    placeholder = "Oil filter, Air filter..."
                )

                FieldLabel("SERVICE NOTES")
                MaintenanceTextField(
                    value = state.notes,
                    onValueChange = { viewModel.onEvent(AddMaintenanceEvent.UpdateNotes(it)) },
                    placeholder = "Mention any findings or future recommendations...",
                    singleLine = false,
                    minLines = 4
                )
            }

            FormSection(
                title = "Service Center",
                icon = Icons.Default.Store
            ) {
                FieldLabel("WORKSHOP NAME")
                MaintenanceTextField(
                    value = state.workshopName,
                    onValueChange = { viewModel.onEvent(AddMaintenanceEvent.UpdateWorkshop(it)) },
                    placeholder = "Search or enter name",
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            viewModel.onEvent(AddMaintenanceEvent.Save)
                        }
                    )
                )

                WorkshopMapCard()
            }

            if (state.error != null) {
                MaintenanceErrorMessage(message = state.error!!)
            }

            Button(
                onClick = {
                    keyboardController?.hide()
                    viewModel.onEvent(AddMaintenanceEvent.Save)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !state.isSaving,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaintenanceBlue,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Save Maintenance",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                }
            }
        }
    }
}

@Composable
private fun MaintenanceTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(34.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF111827)
                )
            }
            Text(
                text = "AutoLog",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = MaintenanceBlue
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsNone,
                contentDescription = null,
                tint = Color(0xFF52606D),
                modifier = Modifier.size(20.dp)
            )
            Surface(
                modifier = Modifier.size(25.dp),
                shape = CircleShape,
                color = Color(0xFFE5EEF8)
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null,
                    tint = MaintenanceBlue,
                    modifier = Modifier.padding(5.dp)
                )
            }
        }
    }
}

@Composable
private fun FormSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MaintenanceBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaintenanceBlue,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF111827)
                )
            }

            Spacer(Modifier.height(2.dp))

            content()
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF344054)
    )
}

@Composable
private fun ServiceChip(text: String) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (text == "Oil Change") Color(0xFFE3EAFF) else Color(0xFFE5E7EB),
        contentColor = if (text == "Oil Change") MaintenanceBlue else Color(0xFF667085)
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
private fun MaintenanceTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF667085)
            )
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        singleLine = singleLine,
        minLines = minLines,
        shape = RoundedCornerShape(7.dp),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaintenanceBlue,
            unfocusedBorderColor = MaintenanceBorder,
            focusedContainerColor = Color(0xFFFBFCFF),
            unfocusedContainerColor = Color(0xFFFBFCFF),
            cursorColor = MaintenanceBlue
        )
    )
}

@Composable
private fun WorkshopMapCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(162.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF0F2830), Color(0xFFEEE4D3))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val roadColor = Color(0xFFE8DEC8)
            val blockColor = Color(0xFFEFF1DF)
            val parkColor = Color(0xFFB9D9C3)

            drawRect(
                color = blockColor,
                topLeft = Offset(20f, size.height * 0.56f),
                size = Size(size.width - 40f, size.height * 0.34f)
            )

            repeat(5) { index ->
                val y = size.height * (0.58f + index * 0.07f)
                drawLine(
                    color = roadColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y + 26f),
                    strokeWidth = 5f
                )
            }

            repeat(4) { index ->
                val x = size.width * (0.18f + index * 0.18f)
                drawLine(
                    color = roadColor,
                    start = Offset(x, size.height * 0.48f),
                    end = Offset(x + 35f, size.height),
                    strokeWidth = 4f
                )
            }

            drawOval(
                color = parkColor,
                topLeft = Offset(28f, size.height * 0.70f),
                size = Size(75f, 28f)
            )
            drawOval(
                color = parkColor,
                topLeft = Offset(size.width * 0.08f, size.height * 0.82f),
                size = Size(96f, 24f)
            )

            val pin = Path().apply {
                moveTo(size.width * 0.52f, size.height * 0.24f)
                cubicTo(
                    size.width * 0.38f,
                    size.height * 0.28f,
                    size.width * 0.39f,
                    size.height * 0.52f,
                    size.width * 0.52f,
                    size.height * 0.70f
                )
                cubicTo(
                    size.width * 0.65f,
                    size.height * 0.52f,
                    size.width * 0.66f,
                    size.height * 0.28f,
                    size.width * 0.52f,
                    size.height * 0.24f
                )
                close()
            }
            drawPath(pin, color = Color(0xFF1D8BAE))
            drawCircle(
                color = Color.White,
                radius = 10f,
                center = Offset(size.width * 0.52f, size.height * 0.40f)
            )
            drawCircle(
                color = Color(0xFF0F6D8D),
                radius = 5f,
                center = Offset(size.width * 0.52f, size.height * 0.40f)
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 10.dp),
            shape = RoundedCornerShape(4.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = MaintenanceBlue,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Precision Auto Care",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF667085)
                )
            }
        }
    }
}

@Composable
private fun MaintenanceErrorMessage(message: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(12.dp),
            textAlign = TextAlign.Center
        )
    }
}
