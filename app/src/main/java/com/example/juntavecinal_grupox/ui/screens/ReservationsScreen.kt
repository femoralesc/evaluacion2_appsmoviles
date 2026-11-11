package com.example.juntavecinal_grupox.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.juntavecinal_grupox.model.Reservation
import com.example.juntavecinal_grupox.model.ReservationStatus
import com.example.juntavecinal_grupox.model.UserRole
import com.example.juntavecinal_grupox.ui.components.ReservationStatusBadge
import com.example.juntavecinal_grupox.ui.components.SectionTitle
import com.example.juntavecinal_grupox.model.AppUiState

@Composable
fun ReservationsScreen(
    uiState: AppUiState,
    onNewReservation: () -> Unit,
    onUpdateStatus: (String, ReservationStatus) -> Unit
) {
    val isNeighbor = uiState.activeProfile == UserRole.VECINO
    val reservations = if (isNeighbor) {
        uiState.reservations.filter { it.requesterName == uiState.activeProfile.demoName }
    } else {
        uiState.reservations
    }

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SectionTitle("Reservas de la sede")
                Text(
                    if (isNeighbor) "Consulta el estado de tus solicitudes." else "Revisa la agenda y gestiona las solicitudes pendientes.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (isNeighbor) {
            item {
                Button(onClick = onNewReservation, modifier = Modifier.fillMaxWidth()) {
                    Text("Nueva solicitud de arriendo")
                }
            }
        }

        if (reservations.isEmpty()) {
            item {
                Text("Todavía no tienes solicitudes. Cuando envíes una, aparecerá aquí.")
            }
        }

        items(reservations, key = { it.id }) { reservation ->
            ReservationCard(
                reservation = reservation,
                canManage = uiState.activeProfile == UserRole.DIRECTIVA,
                onUpdateStatus = onUpdateStatus
            )
        }
    }
}

@Composable
private fun ReservationCard(
    reservation: Reservation,
    canManage: Boolean,
    onUpdateStatus: (String, ReservationStatus) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    reservation.purpose,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium
                )
                ReservationStatusBadge(reservation.status)
            }
            Text("Solicita: ${reservation.requesterName}", style = MaterialTheme.typography.bodyMedium)
            Text("${reservation.date} · ${reservation.time} · Sede comunitaria", style = MaterialTheme.typography.bodyMedium)

            if (canManage && reservation.status == ReservationStatus.PENDIENTE) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilledTonalButton(
                        onClick = { onUpdateStatus(reservation.id, ReservationStatus.RECHAZADA) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Rechazar")
                    }
                    Button(
                        onClick = { onUpdateStatus(reservation.id, ReservationStatus.APROBADA) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Aprobar")
                    }
                }
            }
        }
    }
}
