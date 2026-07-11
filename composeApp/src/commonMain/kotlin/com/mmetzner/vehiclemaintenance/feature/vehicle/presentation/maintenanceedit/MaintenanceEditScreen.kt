package com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.maintenanceedit

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private val MaintenanceEditBlue = Color(0xFF0B5CFF)
private val MaintenanceEditBackground = Color(0xFFF7F8FA)
private val MaintenanceEditBorder = Color(0xFFD7DEEA)
private val MaintenanceEditDanger = Color(0xFFB42318)

@Composable
fun MaintenanceEditScreen(
    viewModel: MaintenanceEditViewModel,
    vehicleId: String,
    maintenanceId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(vehicleId, maintenanceId) {
        viewModel.load(vehicleId, maintenanceId)
    }

    LaunchedEffect(viewModel.uiEvent) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                MaintenanceEditUiEvent.NavigateBack -> onBack()
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Excluir manutencao") },
            text = { Text("A manutencao sera removida do app agora e sincronizada com o backend quando houver conexao.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        viewModel.delete()
                    }
                ) {
                    Text("Excluir", color = MaintenanceEditDanger)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        containerColor = MaintenanceEditBackground
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            MaintenanceEditTopBar(onBack = onBack)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MaintenanceEditBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaintenanceEditBlue)
                        }
                    }

                    state.maintenance == null -> {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = state.errorMessage ?: "Manutencao nao encontrada.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            OutlinedButton(onClick = onBack) {
                                Text("Voltar")
                            }
                        }
                    }

                    else -> {
                        MaintenanceEditForm(
                            state = state,
                            onDescriptionChanged = viewModel::onDescriptionChanged,
                            onDateChanged = viewModel::onDateChanged,
                            onMileageChanged = viewModel::onMileageChanged,
                            onTotalValueChanged = viewModel::onTotalValueChanged,
                            onSave = {
                                keyboardController?.hide()
                                viewModel.save()
                            },
                            onDelete = {
                                keyboardController?.hide()
                                showDeleteConfirmation = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MaintenanceEditTopBar(onBack: () -> Unit) {
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
            text = "Editar manutencao",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = Color(0xFF111827)
        )
    }
}

@Composable
private fun MaintenanceEditForm(
    state: MaintenanceEditState,
    onDescriptionChanged: (String) -> Unit,
    onDateChanged: (String) -> Unit,
    onMileageChanged: (String) -> Unit,
    onTotalValueChanged: (String) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        MaintenanceEditFieldGroup(label = "DATA DA MANUTENCAO") {
            MaintenanceEditTextField(
                value = state.date,
                onValueChange = onDateChanged,
                placeholder = "2026-07-11",
                trailingIcon = {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null)
                }
            )
        }

        MaintenanceEditFieldGroup(label = "ODOMETRO") {
            MaintenanceEditTextField(
                value = state.mileage,
                onValueChange = onMileageChanged,
                placeholder = "45200",
                trailingIcon = {
                    Icon(Icons.Default.Speed, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )
        }

        MaintenanceEditFieldGroup(label = "CUSTO TOTAL") {
            MaintenanceEditTextField(
                value = state.totalValue,
                onValueChange = onTotalValueChanged,
                placeholder = "250,00",
                leadingIcon = {
                    Icon(Icons.Default.Payments, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                )
            )
        }

        MaintenanceEditFieldGroup(label = "DESCRICAO DO SERVICO") {
            MaintenanceEditTextField(
                value = state.description,
                onValueChange = onDescriptionChanged,
                placeholder = "Troca de oleo, pastilhas de freio etc.",
                singleLine = false,
                minLines = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )
        }

        val errorMessage = state.errorMessage
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            enabled = !state.isSaving && !state.isDeleting,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaintenanceEditBlue,
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
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Salvar alteracoes", fontWeight = FontWeight.Bold)
            }
        }

        OutlinedButton(
            onClick = onDelete,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !state.isSaving && !state.isDeleting,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaintenanceEditDanger),
            border = BorderStroke(1.dp, MaintenanceEditDanger)
        ) {
            if (state.isDeleting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaintenanceEditDanger
                )
            } else {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Excluir manutencao", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MaintenanceEditFieldGroup(
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
private fun MaintenanceEditTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
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
        shape = RoundedCornerShape(8.dp),
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaintenanceEditBlue,
            unfocusedBorderColor = MaintenanceEditBorder,
            focusedContainerColor = Color(0xFFFBFCFF),
            unfocusedContainerColor = Color(0xFFFBFCFF),
            cursorColor = MaintenanceEditBlue
        )
    )
}
