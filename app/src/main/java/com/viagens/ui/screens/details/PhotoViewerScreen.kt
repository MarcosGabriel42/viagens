package com.viagens.ui.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.viagens.viewmodel.TripDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoViewerScreen(
    navController: NavController,
    tripId: Int,
    initialPhotoId: Int
) {
    val viewModel: TripDetailsViewModel = viewModel()
    val photos by viewModel.photos.collectAsState()
    
    // Filtramos para garantir que as fotos do tripId correto estejam carregadas
    // Se não estiverem (ex: deep link), carregamos
    if (photos.isEmpty()) {
        viewModel.loadTripDetails(tripId)
    }

    val initialIndex = photos.indexOfFirst { it.id == initialPhotoId }.coerceAtLeast(0)
    
    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { photos.size }
    )

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { 
                    if (photos.isNotEmpty()) {
                        Text(
                            text = "${pagerState.currentPage + 1} / ${photos.size}",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.6f),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (photos.isNotEmpty()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    pageSpacing = 16.dp
                ) { page ->
                    AsyncImage(
                        model = photos[page].imageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            } else {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}
