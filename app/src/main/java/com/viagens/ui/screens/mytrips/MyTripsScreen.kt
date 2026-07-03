package com.viagens.ui.screens.mytrips

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.viagens.data.local.entity.Trip
import com.viagens.ui.components.TripItem
import com.viagens.ui.navigation.Screen
import com.viagens.ui.theme.*
import com.viagens.viewmodel.TripViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTripsScreen(navController: NavController) {
    val viewModel: TripViewModel = viewModel()
    val trips by viewModel.trips.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("MINHAS VIAGENS", fontWeight = FontWeight.Bold, color = White, fontSize = 18.sp) },
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
        if (trips.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Nenhuma viagem cadastrada.", color = PlaceholderGray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(trips) { trip ->
                    TripItem(
                        trip = trip, 
                        onDelete = { viewModel.deleteTrip(trip) },
                        onClick = {
                            navController.navigate(Screen.TripDetails.createRoute(trip.id))
                        }
                    )
                }
            }
        }
    }
}
