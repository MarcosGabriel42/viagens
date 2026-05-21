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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.viagens.ui.components.*
import com.viagens.ui.theme.*
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
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        text = if (tripId == null) "NOVA VIAGEM" else "EDITAR VIAGEM", 
                        fontWeight = FontWeight.Bold, 
                        color = White, 
                        fontSize = 18.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = TopGradientStart
                )
            )
        },
        containerColor = White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WaveHeader(title = if (tripId == null) "Planeje sua\nViagem" else "Ajuste os\nDetalhes")

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Informações Gerais",
                    color = TopGradientMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                CustomTextField(
                    value = destination,
                    onValueChange = { destination = it },
                    label = "Destino (Ex: Paris, França)",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tipo de Viagem",
                    color = TextDark,
                    fontWeight = FontWeight.Medium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = type == "Lazer", 
                            onClick = { type = "Lazer" },
                            colors = RadioButtonDefaults.colors(selectedColor = ButtonGradientStart)
                        )
                        Text("Lazer", color = TextDark)
                    }
                    Spacer(modifier = Modifier.width(24.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = type == "Negócios", 
                            onClick = { type = "Negócios" },
                            colors = RadioButtonDefaults.colors(selectedColor = ButtonGradientStart)
                        )
                        Text("Negócios", color = TextDark)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Datas da Viagem",
                    color = TopGradientMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    SecondaryButton(
                        text = if (startDate == 0L) "INÍCIO" else dateFormatter.format(Date(startDate)),
                        onClick = { showStartDatePicker = true },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    SecondaryButton(
                        text = if (endDate == 0L) "FIM" else dateFormatter.format(Date(endDate)),
                        onClick = { showEndDatePicker = true },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Finanças",
                    color = TopGradientMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                CustomTextField(
                    value = budget,
                    onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) budget = it },
                    label = "Orçamento Previsto (R$)",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                Spacer(modifier = Modifier.height(32.dp))

                PrimaryButton(
                    text = if (tripId == null) "SALVAR VIAGEM" else "ATUALIZAR VIAGEM",
                    onClick = {
                        val budgetValue = budget.toDoubleOrNull()
                        if (destination.isBlank() || budgetValue == null || startDate == 0L || endDate == 0L) {
                            Toast.makeText(context, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
                        } else if (endDate < startDate) {
                            Toast.makeText(context, "A data de fim não pode ser anterior ao início", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.saveTrip(
                                id = tripId ?: 0,
                                destination = destination,
                                type = type,
                                startDate = startDate,
                                endDate = endDate,
                                budget = budgetValue,
                                onSuccess = {
                                    Toast.makeText(context, "Viagem salva com sucesso!", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                },
                                onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                            )
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(40.dp))
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
                    }) { Text("OK", color = ButtonGradientStart) }
                },
                dismissButton = {
                    TextButton(onClick = { showStartDatePicker = false }) { 
                        Text("CANCELAR", color = PlaceholderGray) 
                    }
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
                    }) { Text("OK", color = ButtonGradientStart) }
                },
                dismissButton = {
                    TextButton(onClick = { showEndDatePicker = false }) { 
                        Text("CANCELAR", color = PlaceholderGray) 
                    }
                }
            ) { DatePicker(state = datePickerState) }
        }
    }
}
