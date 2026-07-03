package com.viagens.ui.screens.menu

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.viagens.ui.components.PrimaryButton
import com.viagens.ui.components.TripItem
import com.viagens.ui.components.WaveHeader
import com.viagens.ui.navigation.Screen
import com.viagens.ui.theme.*
import com.viagens.viewmodel.AuthViewModel
import com.viagens.viewmodel.TripViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(navController: NavController) {
    val authViewModel: AuthViewModel = viewModel()
    val tripViewModel: TripViewModel = viewModel()
    
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    val nearestTrip by tripViewModel.nearestTrip.collectAsState()
    val currentCity by tripViewModel.currentCity.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            tripViewModel.requestLocationAndSearchTrip()
        }
    }

    LaunchedEffect(Unit) {
        val hasFineLocation = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        if (hasFineLocation) {
            tripViewModel.requestLocationAndSearchTrip()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    BackHandler(enabled = true) {
        (context as? Activity)?.finish()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = White,
                drawerTonalElevation = 0.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(TopGradientStart, TopGradientBase)
                            )
                        ),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "Viagens App",
                        color = White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                NavigationDrawerItem(
                    label = { Text("Nova Viagem", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.NewTrip.route)
                    },
                    icon = { Icon(Icons.Default.Add, contentDescription = null, tint = TopGradientMedium) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )

                NavigationDrawerItem(
                    label = { Text("Minhas Viagens", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.MyTrips.route)
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, tint = TopGradientMedium) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )

                NavigationDrawerItem(
                    label = { Text("Sobre", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.About.route)
                    },
                    icon = { Icon(Icons.Default.Info, contentDescription = null, tint = TopGradientMedium) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )

                Spacer(modifier = Modifier.weight(1f))

                NavigationDrawerItem(
                    label = { Text("Sair", color = Color.Red, fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.Red) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("HOME", fontWeight = FontWeight.Bold, color = White, fontSize = 18.sp) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = TopGradientStart
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WaveHeader(title = "Olá!")

                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = if (currentCity != null) "Você está em $currentCity" else "Buscando localização...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = PlaceholderGray
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "SUA PRÓXIMA VIAGEM",
                        style = MaterialTheme.typography.labelLarge,
                        color = TopGradientMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val trip = nearestTrip
                    if (trip != null) {
                        TripItem(
                            trip = trip,
                            onClick = {
                                navController.navigate(Screen.TripDetails.createRoute(trip.id))
                            }
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .background(InputBackground, RoundedCornerShape(24.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhuma viagem ativa para este local.",
                                color = PlaceholderGray,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    PrimaryButton(
                        text = "NOVA VIAGEM",
                        onClick = { navController.navigate(Screen.NewTrip.route) }
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
