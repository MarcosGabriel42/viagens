package com.viagens.ui.screens.details

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.viagens.ui.navigation.Screen
import coil.compose.AsyncImage
import org.osmdroid.util.GeoPoint
import com.viagens.ui.components.OSMMapView
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
    val itinerary by viewModel.itinerary.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Roteiro, 1: Fotos
    
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
                RoteiroContent(
                    trip = trip,
                    location = location,
                    itinerary = itinerary,
                    isGenerating = isGenerating,
                    onGenerate = { interests -> viewModel.generateItinerary(tripId, interests) }
                )
            } else {
                PhotosContent(photos, tripId, navController)
            }
        }
    }
}

@Composable
fun RoteiroContent(
    trip: com.viagens.data.local.entity.Trip?,
    location: GeoPoint?,
    itinerary: com.viagens.data.local.entity.Itinerary?,
    isGenerating: Boolean,
    onGenerate: (String) -> Unit
) {
    var interests by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (location != null) {
                    val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val period = trip?.let { "${df.format(Date(it.startDate))} - ${df.format(Date(it.endDate))}" } ?: ""
                    
                    OSMMapView(
                        modifier = Modifier.fillMaxSize(),
                        location = location,
                        title = trip?.destination,
                        snippet = period
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(InputBackground), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ButtonGradientStart)
                    }
                }
            }
        }
        
        Column(modifier = Modifier.padding(24.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Route, contentDescription = null, tint = ButtonGradientStart, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Detalhes da Viagem", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    trip?.let {
                        val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        
                        DetailRow("📅 Período", "${df.format(Date(it.startDate))} - ${df.format(Date(it.endDate))}")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = InputBackground.copy(alpha = 0.5f))
                        DetailRow("🏷️ Tipo", it.type)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = InputBackground.copy(alpha = 0.5f))
                        DetailRow("💰 Orçamento", "R$ ${String.format(Locale.getDefault(), "%.2f", it.budget)}")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text("Roteiro Personalizado", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            if (isGenerating) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = InputBackground.copy(alpha = 0.5f))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally, 
                        modifier = Modifier.fillMaxWidth().padding(24.dp)
                    ) {
                        CircularProgressIndicator(color = ButtonGradientStart)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("A IA está criando seu roteiro perfeito...", color = TextDark, textAlign = TextAlign.Center)
                    }
                }
            } else if (itinerary?.generatedText != null) {
                val days = itinerary.generatedText!!.split(Regex("DIA\\s+\\d+", RegexOption.IGNORE_CASE))
                    .filter { it.isNotBlank() }
                
                days.forEachIndexed { index, dayContent ->
                    Card(
                        modifier = Modifier.padding(vertical = 10.dp).fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = InputBackground)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = ButtonGradientStart,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "DIA ${index + 1}",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        fontWeight = FontWeight.Bold,
                                        color = White,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = dayContent.trim(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextDark,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = InputBackground.copy(alpha = 0.3f))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(24.dp)
                    ) {
                        Text(
                            "Não foi possível carregar o roteiro.", 
                            color = PlaceholderGray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { onGenerate(interests) },
                            colors = ButtonDefaults.buttonColors(containerColor = ButtonGradientStart)
                        ) {
                            Text("TENTAR GERAR NOVAMENTE")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(color = InputBackground)
            Spacer(modifier = Modifier.height(24.dp))

            Text("Ajustar Preferências", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Deseja incluir algo específico no roteiro?", fontSize = 12.sp, color = PlaceholderGray)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = interests,
                onValueChange = { interests = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ex: Trilhas, Museus, Compras...") },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ButtonGradientStart,
                    unfocusedBorderColor = InputBackground
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { onGenerate(interests) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                enabled = !isGenerating,
                colors = ButtonDefaults.buttonColors(containerColor = ButtonGradientStart)
            ) {
                Text("REGENERAR ROTEIRO COM IA", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = PlaceholderGray, fontSize = 14.sp)
        Text(value, color = TextDark, fontWeight = FontWeight.Medium, fontSize = 14.sp)
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
