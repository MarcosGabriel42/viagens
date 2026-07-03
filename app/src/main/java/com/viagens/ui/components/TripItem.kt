package com.viagens.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.viagens.data.local.entity.Trip
import com.viagens.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TripItem(
    trip: Trip,
    modifier: Modifier = Modifier,
    onDelete: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = InputBackground),
        elevation = CardDefaults.cardElevation(0.dp),
        onClick = { onClick?.invoke() }
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(White, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (trip.type == "Lazer") Icons.Default.BeachAccess else Icons.Default.BusinessCenter,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = TopGradientMedium
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trip.destination,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = PlaceholderGray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${dateFormatter.format(Date(trip.startDate))} - ${dateFormatter.format(Date(trip.endDate))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = PlaceholderGray
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Orçamento: R$ ${String.format(Locale.getDefault(), "%.2f", trip.budget)}",
                    fontWeight = FontWeight.Medium,
                    color = TextDark,
                    fontSize = 12.sp
                )

                Text(
                    text = "Gasto Total: R$ ${String.format(Locale.getDefault(), "%.2f", trip.totalSpent)}",
                    fontWeight = FontWeight.ExtraBold,
                    color = ButtonGradientStart,
                    fontSize = 14.sp
                )
            }

            if (onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = Color.Red.copy(alpha = 0.6f))
                }
            }
        }
    }
}
