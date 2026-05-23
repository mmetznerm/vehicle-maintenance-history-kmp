package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.addmaintenance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMaintenanceScreen(
    viewModel: AddMaintenanceViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    plate: String,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(plate) {
        viewModel.onEvent(AddMaintenanceEvent.SetPlate(plate))
    }

    LaunchedEffect(state.success) {
        if (state.success) {
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nova Manutenção") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Veículo: ${state.vehiclePlate}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            OutlinedTextField(
                value = state.date,
                onValueChange = { viewModel.onEvent(AddMaintenanceEvent.UpdateDate(it)) },
                label = { Text("Data (dd/mm/aaaa)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.mileage,
                onValueChange = { viewModel.onEvent(AddMaintenanceEvent.UpdateMileage(it)) },
                label = { Text("Quilometragem (KM)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )

            OutlinedTextField(
                value = state.totalValue,
                onValueChange = { viewModel.onEvent(AddMaintenanceEvent.UpdateValue(it)) },
                label = { Text("Valor Total (R$)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal)
            )

            OutlinedTextField(
                value = state.workshopName,
                onValueChange = { viewModel.onEvent(AddMaintenanceEvent.UpdateWorkshop(it)) },
                label = { Text("Oficina / Mecânico") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onEvent(AddMaintenanceEvent.UpdateDescription(it)) },
                label = { Text("Descrição do Serviço") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.onEvent(AddMaintenanceEvent.Save) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSaving
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Salvar Manutenção")
                }
            }
        }
    }
}



