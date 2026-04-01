package com.viagens.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.viagens.R

@Composable
fun AppLogo() {
    Image(
        painter = painterResource(id = R.drawable.logometal),
        contentDescription = "Logo",
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        contentScale = ContentScale.Fit
    )

    Spacer(modifier = Modifier.height(20.dp))
}