package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.addvehicle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleScreen(
    viewModel: AddVehicleViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var plate by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }

    LaunchedEffect(viewModel.uiEvent) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                AddVehicleUiEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cadastrar Veículo") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = plate,
                onValueChange = { plate = it },
                label = { Text("Placa") },
                placeholder = { Text("ABC1D23") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = brand,
                onValueChange = { brand = it },
                label = { Text("Marca") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                label = { Text("Modelo") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = year,
                onValueChange = { year = it },
                label = { Text("Ano") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.saveVehicle(plate, model, brand, year) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Salvar Veículo")
            }
        }
    }
}


