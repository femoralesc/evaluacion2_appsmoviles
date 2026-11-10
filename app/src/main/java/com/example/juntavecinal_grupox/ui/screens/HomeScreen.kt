package com.example.juntavecinal_grupox.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.juntavecinal_grupox.data.model.ReservationStatus
import com.example.juntavecinal_grupox.data.model.UserRole
import com.example.juntavecinal_grupox.ui.components.SummaryCard
import com.example.juntavecinal_grupox.ui.components.formatClp
import com.example.juntavecinal_grupox.ui.viewmodels.AppUiState

@Composable
fun HomeScreen(
    uiState: AppUiState,
    onOpenReservations: () -> Unit,
    onOpenFinance: () -> Unit,
    onNewReservation: () -> Unit,
    onNewMovement: () -> Unit
) {
    val pendingReservations = uiState.reservations.count { it.status == ReservationStatus.PENDIENTE }
    val actionLabel = when (uiState.activeProfile) {
        UserRole.VECINO -> "Solicitar arriendo"
        UserRole.DIRECTIVA -> "Revisar solicitudes"
        UserRole.TESORERIA -> "Registrar movimiento"
    }
    val primaryAction = when (uiState.activeProfile) {
        UserRole.VECINO -> onNewReservation
        UserRole.DIRECTIVA -> onOpenReservations
        UserRole.TESORERIA -> onNewMovement
    }

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Hola, ${uiState.activeProfile.demoName}", style = MaterialTheme.typography.headlineSmall)
                Text(
                    uiState.activeProfile.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "La sede, en orden",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        "Consulta reservas y comparte información clara sobre los recursos de la comunidad.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Button(onClick = primaryAction) {
                        Text(actionLabel)
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard(
                    title = "Por revisar",
                    value = pendingReservations.toString(),
                    supportingText = "solicitudes pendientes",
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Saldo demo",
                    value = formatClp(uiState.balance),
                    supportingText = "monto ficticio",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Accesos rápidos", style = MaterialTheme.typography.titleMedium)
                Button(onClick = onOpenReservations, modifier = Modifier.fillMaxWidth()) {
                    Text("Ver reservas")
                }
                Button(onClick = onOpenFinance, modifier = Modifier.fillMaxWidth()) {
                    Text("Consultar finanzas")
                }
            }
        }

        item {
            Text(
                "Datos de demostración: los nombres y montos son ficticios.",
                modifier = Modifier.padding(bottom = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
