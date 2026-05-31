package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.addvehicle

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

private val VehicleFormBlue = Color(0xFF0B5CFF)
private val VehicleFormBackgroundTop = Color(0xFFF7FAFF)
private val VehicleFormBackgroundBottom = Color(0xFFEFF4FB)
private val VehicleFormBorder = Color(0xFFD7DEEA)
private val VehicleFormMutedText = Color(0xFF6E7685)

@Composable
fun AddVehicleScreen(
    viewModel: AddVehicleViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    var plate by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var currentMileage by remember { mutableStateOf("0") }
    var color by remember { mutableStateOf("") }
    var fuelType by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(viewModel.uiEvent) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                AddVehicleUiEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(VehicleFormBackgroundTop, VehicleFormBackgroundBottom)
                    )
                )
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                VehicleFormBrandHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 420.dp)
                )

                Spacer(modifier = Modifier.height(42.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 420.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, VehicleFormBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        VehicleFormFieldGroup(label = "PLATE NUMBER") {
                            VehicleFormTextField(
                                value = plate,
                                onValueChange = { plate = it.uppercase().trim() },
                                placeholder = "ABC-1234",
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Characters,
                                    imeAction = ImeAction.Next
                                )
                            )
                        }

                        VehicleFormFieldGroup(label = "BRAND") {
                            VehicleFormDropdown(
                                value = brand,
                                placeholder = "Select Brand",
                                options = listOf("Toyota", "Honda", "Ford", "Chevrolet", "Volkswagen", "Fiat"),
                                onValueChange = { brand = it }
                            )
                        }

                        VehicleFormFieldGroup(label = "MODEL") {
                            VehicleFormTextField(
                                value = model,
                                onValueChange = { model = it },
                                placeholder = "e.g. Model S Plaid"
                            )
                        }

                        VehicleFormFieldGroup(label = "YEAR") {
                            VehicleFormTextField(
                                value = year,
                                onValueChange = { year = it.filter(Char::isDigit).take(4) },
                                placeholder = "2024",
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                )
                            )
                        }

                        VehicleFormFieldGroup(label = "CURRENT MILEAGE") {
                            VehicleFormTextField(
                                value = currentMileage,
                                onValueChange = { currentMileage = it.filter(Char::isDigit) },
                                placeholder = "0",
                                suffix = "KM",
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                )
                            )
                        }

                        VehicleFormFieldGroup(label = "COLOR") {
                            VehicleFormTextField(
                                value = color,
                                onValueChange = { color = it },
                                placeholder = "e.g. Midnight Silver"
                            )
                        }

                        VehicleFormFieldGroup(label = "FUEL TYPE") {
                            VehicleFormDropdown(
                                value = fuelType,
                                placeholder = "Select Fuel",
                                options = listOf("Gasoline", "Flex", "Diesel", "Hybrid", "Electric"),
                                onValueChange = { fuelType = it }
                            )
                        }

                        val errorMessage = state.error
                        if (errorMessage != null) {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Button(
                            onClick = {
                                keyboardController?.hide()
                                viewModel.saveVehicle(plate, model, brand, year)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            enabled = !state.isSaving,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VehicleFormBlue,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            if (state.isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.width(20.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                            } else {
                                Text(
                                    text = "Save Vehicle",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VehicleFormBrandHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.DirectionsCar,
            contentDescription = null,
            tint = VehicleFormBlue,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "AutoLog",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = VehicleFormBlue
        )
    }
}

@Composable
private fun VehicleFormFieldGroup(
    label: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            color = Color(0xFF24324A)
        )
        content()
    }
}

@Composable
private fun VehicleFormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    suffix: String? = null,
    readOnly: Boolean = false,
    trailingIcon: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFB1B8C6)
            )
        },
        trailingIcon = trailingIcon,
        suffix = suffix?.let {
            {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = VehicleFormMutedText
                )
            }
        },
        singleLine = true,
        readOnly = readOnly,
        shape = RoundedCornerShape(8.dp),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VehicleFormBlue,
            unfocusedBorderColor = VehicleFormBorder,
            focusedContainerColor = Color(0xFFF8FAFC),
            unfocusedContainerColor = Color(0xFFF8FAFC),
            cursorColor = VehicleFormBlue
        )
    )
}

@Composable
private fun VehicleFormDropdown(
    value: String,
    placeholder: String,
    options: List<String>,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        VehicleFormTextField(
            value = value,
            onValueChange = {},
            placeholder = placeholder,
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Open options",
                        tint = VehicleFormMutedText
                    )
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
