package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.addmaintenance

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payments
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mmetzner.vehiclemaintenance.core.ui.preview.PreviewVehicle
import com.mmetzner.vehiclemaintenance.core.ui.preview.PreviewVehicleRepository
import com.mmetzner.vehiclemaintenance.core.ui.theme.VehicleMaintenanceTheme
import org.jetbrains.compose.resources.stringResource
import vehiclemaintenance.composeapp.generated.resources.*

private val MaintenanceBlue = Color(0xFF0B5CFF)
private val MaintenanceBackground = Color(0xFFF7F8FA)
private val MaintenanceBorder = Color(0xFFD7DEEA)

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
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            MaintenanceTopBar(onBack = onBack)

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(Res.string.maintenance_new_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF111827)
                )
                Text(
                    text = plate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF475467)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MaintenanceBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                MaintenanceForm(
                    state = state,
                    onDateChanged = { viewModel.onEvent(AddMaintenanceEvent.UpdateDate(it)) },
                    onMileageChanged = { viewModel.onEvent(AddMaintenanceEvent.UpdateMileage(it.filter(Char::isDigit))) },
                    onTotalValueChanged = { viewModel.onEvent(AddMaintenanceEvent.UpdateValue(it)) },
                    onDescriptionChanged = { viewModel.onEvent(AddMaintenanceEvent.UpdateDescription(it)) },
                    onCancel = onBack,
                    onSave = {
                        keyboardController?.hide()
                        viewModel.onEvent(AddMaintenanceEvent.Save)
                    }
                )
            }
        }
    }
}

@Composable
private fun MaintenanceTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(Res.string.action_back),
                tint = Color(0xFF111827)
            )
        }
        Text(
            text = stringResource(Res.string.maintenance_add_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = Color(0xFF111827)
        )
    }
}

@Composable
private fun MaintenanceForm(
    state: AddMaintenanceState,
    onDateChanged: (String) -> Unit,
    onMileageChanged: (String) -> Unit,
    onTotalValueChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        MaintenanceFieldGroup(label = stringResource(Res.string.maintenance_field_date)) {
            MaintenanceTextField(
                value = state.date,
                onValueChange = onDateChanged,
                placeholder = stringResource(Res.string.maintenance_date_placeholder),
                trailingIcon = {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null)
                }
            )
        }

        MaintenanceFieldGroup(label = stringResource(Res.string.maintenance_field_odometer)) {
            MaintenanceTextField(
                value = state.mileage,
                onValueChange = onMileageChanged,
                placeholder = stringResource(Res.string.maintenance_odometer_placeholder),
                trailingIcon = {
                    Icon(Icons.Default.Speed, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )
        }

        MaintenanceFieldGroup(label = stringResource(Res.string.maintenance_field_total_cost)) {
            MaintenanceTextField(
                value = state.totalValue,
                onValueChange = onTotalValueChanged,
                placeholder = stringResource(Res.string.maintenance_cost_placeholder),
                leadingIcon = {
                    Icon(Icons.Default.Payments, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                )
            )
        }

        MaintenanceFieldGroup(label = stringResource(Res.string.maintenance_field_description)) {
            MaintenanceTextField(
                value = state.description,
                onValueChange = onDescriptionChanged,
                placeholder = stringResource(Res.string.maintenance_description_placeholder),
                singleLine = false,
                minLines = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { onSave() }
                )
            )
        }

        state.error?.let { error ->
            Text(
                text = stringResource(error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel, enabled = !state.isSaving) {
                Text(stringResource(Res.string.action_cancel))
            }

            Spacer(Modifier.width(8.dp))

            Button(
                onClick = onSave,
                enabled = !state.isSaving,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaintenanceBlue,
                    contentColor = Color.White
                )
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(stringResource(Res.string.action_save), fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                }
            }
        }
    }
}

@Composable
private fun MaintenanceFieldGroup(
    label: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            color = Color(0xFF344054)
        )
        content()
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
        maxLines = if (singleLine) 1 else 5,
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

@Preview
@Composable
private fun AddMaintenanceScreenPreview() {
    val viewModel = remember {
        AddMaintenanceViewModel(PreviewVehicleRepository).apply {
            onEvent(AddMaintenanceEvent.SetPlate(PreviewVehicle.plate))
            onEvent(AddMaintenanceEvent.UpdateDate("2026-07-27"))
            onEvent(AddMaintenanceEvent.UpdateMileage("45800"))
            onEvent(AddMaintenanceEvent.UpdateValue("320.00"))
            onEvent(AddMaintenanceEvent.UpdateDescription("Preventive maintenance"))
        }
    }

    VehicleMaintenanceTheme {
        AddMaintenanceScreen(
            viewModel = viewModel,
            onBack = {},
            onSuccess = {},
            plate = PreviewVehicle.plate
        )
    }
}
