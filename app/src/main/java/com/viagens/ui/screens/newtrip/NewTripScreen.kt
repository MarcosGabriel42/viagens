package com.viagens.ui.screens.newtrip

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.viagens.viewmodel.TripViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTripScreen(navController: NavController, tripId: Int? = null) {
    val viewModel: TripViewModel = viewModel()
    val context = LocalContext.current

    var destination by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Lazer") }
    var budget by remember { mutableStateOf("") }
    var startDate by remember { mutableLongStateOf(0L) }
    var endDate by remember { mutableLongStateOf(0L) }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Carregar dados se for edição
    LaunchedEffect(tripId) {
        if (tripId != null) {
            val trip = viewModel.getTripById(tripId)
            if (trip != null) {
                destination = trip.destination
                type = trip.type
                budget = trip.budget.toString()
                startDate = trip.startDate
                endDate = trip.endDate
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (tripId == null) "Nova Viagem" else "Editar Viagem") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                label = { Text("Destino") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text("Tipo de Viagem:", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = type == "Lazer", onClick = { type = "Lazer" })
                Text("Lazer", modifier = Modifier.padding(end = 16.dp))
                RadioButton(selected = type == "Negócios", onClick = { type = "Negócios" })
                Text("Negócios")
            }

            OutlinedButton(
                onClick = { showStartDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (startDate == 0L) "Selecionar Data Início" else "Início: ${dateFormatter.format(Date(startDate))}")
            }

            OutlinedButton(
                onClick = { showEndDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (endDate == 0L) "Selecionar Data Fim" else "Fim: ${dateFormatter.format(Date(endDate))}")
            }

            OutlinedTextField(
                value = budget,
                onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) budget = it },
                label = { Text("Orçamento (R$)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val budgetValue = budget.toDoubleOrNull()
                    if (destination.isBlank() || budgetValue == null || startDate == 0L || endDate == 0L) {
                        Toast.makeText(context, "Todos os campos são obrigatórios", Toast.LENGTH_SHORT).show()
                    } else if (endDate < startDate) {
                        Toast.makeText(context, "Data fim não pode ser anterior à data início", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.saveTrip(
                            id = tripId ?: 0,
                            destination = destination,
                            type = type,
                            startDate = startDate,
                            endDate = endDate,
                            budget = budgetValue,
                            onSuccess = {
                                Toast.makeText(context, "Viagem salva!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            },
                            onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (tripId == null) "Salvar Viagem" else "Atualizar Viagem")
            }
        }

        if (showStartDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showStartDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        startDate = datePickerState.selectedDateMillis ?: 0L
                        showStartDatePicker = false
                    }) { Text("OK") }
                }
            ) { DatePicker(state = datePickerState) }
        }

        if (showEndDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showEndDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        endDate = datePickerState.selectedDateMillis ?: 0L
                        showEndDatePicker = false
                    }) { Text("OK") }
                }
            ) { DatePicker(state = datePickerState) }
        }
    }
}
