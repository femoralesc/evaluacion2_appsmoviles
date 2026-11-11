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
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import com.example.juntavecinal_grupox.model.isBooked
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
    onPayment: (String, Long) -> Boolean,
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
                paid = uiState.movements.any { it.reservationId == reservation.id },
                occupied = uiState.reservations.isBooked(reservation.date, reservation.time),
                onPayment = if (uiState.activeProfile == UserRole.TESORERIA) onPayment else null,
                canManage = uiState.activeProfile == UserRole.DIRECTIVA,
                onUpdateStatus = onUpdateStatus
            )
        }
    }
}

@Composable
private fun ReservationCard(
    reservation: Reservation,
    paid: Boolean,
    occupied: Boolean,
    onPayment: ((String, Long) -> Boolean)?,
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

            if (reservation.status == ReservationStatus.APROBADA) {
                Text(if (paid) "Pagada" else "Pago pendiente")
                if (!paid && onPayment != null) {
                    var showDialog by rememberSaveable { mutableStateOf(false) }
                    var amount by rememberSaveable { mutableStateOf("") }
                    TextButton(onClick = { showDialog = true }) { Text("Registrar pago") }
                    if (showDialog) {
                        val pesos = amount.toLongOrNull()
                        AlertDialog(onDismissRequest = { showDialog = false },
                            title = { Text("Pago de arriendo") },
                            text = { OutlinedTextField(value = amount,
                                onValueChange = { if (it.all(Char::isDigit)) amount = it },
                                label = { Text("Monto en pesos") }, singleLine = true) },
                            confirmButton = {
                                Button(enabled = pesos != null && pesos > 0,
                                    onClick = { if (pesos != null && onPayment(reservation.id, pesos)) showDialog = false }
                                ) { Text("Guardar pago") }
                            },
                            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancelar") } })
                    }
                }
            }

            if (canManage && reservation.status == ReservationStatus.PENDIENTE) {
                if (occupied) Text("Bloque ocupado por otra reserva aprobada")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilledTonalButton(
                        onClick = { onUpdateStatus(reservation.id, ReservationStatus.RECHAZADA) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Rechazar")
                    }
                    Button(
                        enabled = !occupied,
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
