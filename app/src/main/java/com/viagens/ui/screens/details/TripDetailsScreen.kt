package com.viagens.ui.screens.details

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.viagens.ui.navigation.Screen
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.viagens.ui.theme.*
import com.viagens.viewmodel.TripDetailsViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailsScreen(navController: NavController, tripId: Int) {
    val viewModel: TripDetailsViewModel = viewModel()
    val trip by viewModel.trip.collectAsState()
    val photos by viewModel.photos.collectAsState()
    val location by viewModel.location.collectAsState()
    
    var selectedTab by remember { mutableIntStateOf(1) } // 0: Roteiro, 1: Fotos
    
    val context = LocalContext.current
    
    LaunchedEffect(tripId) {
        viewModel.loadTripDetails(tripId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(trip?.destination?.uppercase() ?: "DETALHES", fontWeight = FontWeight.Bold, color = White, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = TopGradientStart)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = White) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Route, contentDescription = null) },
                    label = { Text("Roteiro") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ButtonGradientStart,
                        selectedTextColor = ButtonGradientStart,
                        indicatorColor = InputBackground
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.PhotoLibrary, contentDescription = null) },
                    label = { Text("Fotos") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ButtonGradientStart,
                        selectedTextColor = ButtonGradientStart,
                        indicatorColor = InputBackground
                    )
                )
            }
        },
        floatingActionButton = {
            if (selectedTab == 1) {
                AddPhotoFAB(onPhotoAdded = { uri -> 
                    viewModel.addPhoto(tripId, uri)
                    Toast.makeText(context, "Foto adicionada com sucesso!", Toast.LENGTH_SHORT).show()
                })
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (selectedTab == 0) {
                RoteiroContent(trip, location)
            } else {
                PhotosContent(photos, tripId, navController)
            }
        }
    }
}

@Composable
fun RoteiroContent(trip: com.viagens.data.local.entity.Trip?, location: LatLng?) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
            val destination = location ?: LatLng(-23.5505, -46.6333) // Default SP if null
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(destination, 10f)
            }
            
            LaunchedEffect(location) {
                location?.let {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 10f)
                }
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = rememberMarkerState(position = destination),
                    title = trip?.destination
                )
            }
        }
        
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Informações da Viagem", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            trip?.let {
                val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                Text("Período: ${df.format(Date(it.startDate))} - ${df.format(Date(it.endDate))}")
                Text("Tipo: ${it.type}")
                Text("Orçamento: R$ ${String.format("%.2f", it.budget)}")
                Text("Gasto Total: R$ ${String.format("%.2f", it.totalSpent)}")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Roteiro em breve...", color = PlaceholderGray)
        }
    }
}

@Composable
fun PhotosContent(
    photos: List<com.viagens.data.local.entity.Photo>,
    tripId: Int,
    navController: NavController
) {
    if (photos.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Nenhuma foto adicionada.", color = PlaceholderGray)
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(photos) { photo ->
                AsyncImage(
                    model = photo.imageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            navController.navigate(Screen.PhotoViewer.createRoute(tripId, photo.id))
                        },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun AddPhotoFAB(onPhotoAdded: (Uri) -> Unit) {
    val context = LocalContext.current
    var showOptions by remember { mutableStateOf(false) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let { onPhotoAdded(it) } }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                tempUri?.let { onPhotoAdded(it) }
            }
        }
    )

    Column(horizontalAlignment = Alignment.End) {
        if (showOptions) {
            SmallFloatingActionButton(
                onClick = {
                    showOptions = false
                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                containerColor = White,
                contentColor = ButtonGradientStart,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = "Galeria")
            }
            SmallFloatingActionButton(
                onClick = {
                    showOptions = false
                    val uri = createTempPictureUri(context)
                    tempUri = uri
                    cameraLauncher.launch(uri)
                },
                containerColor = White,
                contentColor = ButtonGradientStart,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Câmera")
            }
        }
        
        FloatingActionButton(
            onClick = { showOptions = !showOptions },
            containerColor = ButtonGradientStart,
            contentColor = White,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Adicionar Foto")
        }
    }
}

private fun createTempPictureUri(context: Context): Uri {
    val tempFile = File.createTempFile(
        "JPEG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}_",
        ".jpg",
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    )
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tempFile)
}
