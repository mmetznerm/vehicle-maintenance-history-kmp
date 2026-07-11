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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

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
                    text = "Nova manutencao",
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
                contentDescription = "Voltar",
                tint = Color(0xFF111827)
            )
        }
        Text(
            text = "Adicionar manutencao",
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
        MaintenanceFieldGroup(label = "DATA DA MANUTENCAO") {
            MaintenanceTextField(
                value = state.date,
                onValueChange = onDateChanged,
                placeholder = "2026-07-11",
                trailingIcon = {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null)
                }
            )
        }

        MaintenanceFieldGroup(label = "ODOMETRO") {
            MaintenanceTextField(
                value = state.mileage,
                onValueChange = onMileageChanged,
                placeholder = "45000",
                trailingIcon = {
                    Icon(Icons.Default.Speed, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )
        }

        MaintenanceFieldGroup(label = "CUSTO TOTAL") {
            MaintenanceTextField(
                value = state.totalValue,
                onValueChange = onTotalValueChanged,
                placeholder = "0,00",
                leadingIcon = {
                    Icon(Icons.Default.Payments, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                )
            )
        }

        MaintenanceFieldGroup(label = "DESCRICAO DO SERVICO") {
            MaintenanceTextField(
                value = state.description,
                onValueChange = onDescriptionChanged,
                placeholder = "Troca de oleo, pastilhas de freio etc.",
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
                text = error,
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
                Text("Cancelar")
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
                    Text("Salvar", fontWeight = FontWeight.Bold)
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
