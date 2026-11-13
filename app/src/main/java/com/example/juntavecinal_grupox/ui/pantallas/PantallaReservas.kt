package com.example.juntavecinal_grupox.ui.pantallas

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
import com.example.juntavecinal_grupox.model.estaOcupado
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.juntavecinal_grupox.model.Reserva
import com.example.juntavecinal_grupox.model.EstadoReserva
import com.example.juntavecinal_grupox.model.RolUsuario
import com.example.juntavecinal_grupox.ui.componentes.IndicadorEstadoReserva
import com.example.juntavecinal_grupox.ui.componentes.TituloSeccion
import com.example.juntavecinal_grupox.model.EstadoAplicacion

@Composable
fun PantallaReservas(
    estadoAplicacion: EstadoAplicacion,
    alRegistrarPago: (String, Long) -> Boolean,
    alSolicitarReserva: () -> Unit,
    alEditarReserva: (String) -> Unit,
    alCancelarReserva: (String) -> Boolean,
    puedeModificarReserva: (Reserva) -> Boolean,
    alActualizarEstado: (String, EstadoReserva) -> Unit
) {
    val esVecino = estadoAplicacion.perfilActivo == RolUsuario.VECINO
    val reservas = if (esVecino) {
        estadoAplicacion.reservas.filter { it.nombreSolicitante == estadoAplicacion.usuarioActual?.nombre }
    } else {
        estadoAplicacion.reservas
    }

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                TituloSeccion("Reservas de la sede")
                Text(
                    if (esVecino) "Consulta el estado de tus solicitudes." else "Revisa la agenda y gestiona las solicitudes pendientes.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (esVecino) {
            item {
                Button(onClick = alSolicitarReserva, modifier = Modifier.fillMaxWidth()) {
                    Text("Nueva solicitud de arriendo")
                }
            }
        }

        if (reservas.isEmpty()) {
            item {
                Text("Todavía no tienes solicitudes. Cuando envíes una, aparecerá aquí.")
            }
        }

        items(reservas, key = { it.id }) { reserva ->
            TarjetaReserva(
                reserva = reserva,
                puedeModificar = puedeModificarReserva(reserva),
                alEditarReserva = alEditarReserva,
                alCancelarReserva = alCancelarReserva,
                pagada = estadoAplicacion.movimientos.any { it.idReserva == reserva.id },
                ocupado = estadoAplicacion.reservas.estaOcupado(reserva.fecha, reserva.hora),
                alRegistrarPago = if (estadoAplicacion.perfilActivo == RolUsuario.TESORERIA) alRegistrarPago else null,
                puedeGestionar = estadoAplicacion.perfilActivo == RolUsuario.DIRECTIVA,
                alActualizarEstado = alActualizarEstado
            )
        }
    }
}

@Composable
private fun TarjetaReserva(
    reserva: Reserva,
    puedeModificar: Boolean,
    alEditarReserva: (String) -> Unit,
    alCancelarReserva: (String) -> Boolean,
    pagada: Boolean,
    ocupado: Boolean,
    alRegistrarPago: ((String, Long) -> Boolean)?,
    puedeGestionar: Boolean,
    alActualizarEstado: (String, EstadoReserva) -> Unit
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
                    reserva.motivo,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium
                )
                IndicadorEstadoReserva(reserva.estado)
            }
            Text("Solicita: ${reserva.nombreSolicitante}", style = MaterialTheme.typography.bodyMedium)
            Text("${reserva.fecha} · ${reserva.hora} · Sede comunitaria", style = MaterialTheme.typography.bodyMedium)

            if (puedeModificar) {
                var confirmarCancelacion by rememberSaveable { mutableStateOf(false) }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextButton(onClick = { alEditarReserva(reserva.id) }) { Text("Editar") }
                    TextButton(onClick = { confirmarCancelacion = true }) { Text("Cancelar solicitud") }
                }
                if (confirmarCancelacion) {
                    AlertDialog(
                        onDismissRequest = { confirmarCancelacion = false },
                        title = { Text("¿Cancelar solicitud?") },
                        text = { Text("Se cancelará la solicitud de " + reserva.motivo + " del " + reserva.fecha + ".") },
                        confirmButton = {
                            Button(onClick = {
                                alCancelarReserva(reserva.id)
                                confirmarCancelacion = false
                            }) { Text("Sí, cancelar") }
                        },
                        dismissButton = {
                            TextButton(onClick = { confirmarCancelacion = false }) { Text("Conservar solicitud") }
                        }
                    )
                }
            }

            if (reserva.estado == EstadoReserva.APROBADA) {
                Text(if (pagada) "Pagada" else "Pago pendiente")
                if (!pagada && alRegistrarPago != null) {
                    var mostrarDialogo by rememberSaveable { mutableStateOf(false) }
                    var monto by rememberSaveable { mutableStateOf("") }
                    TextButton(onClick = { mostrarDialogo = true }) { Text("Registrar pago") }
                    if (mostrarDialogo) {
                        val pesos = monto.toLongOrNull()
                        AlertDialog(onDismissRequest = { mostrarDialogo = false },
                            title = { Text("Pago de arriendo") },
                            text = { OutlinedTextField(value = monto,
                                onValueChange = { if (it.all(Char::isDigit)) monto = it },
                                label = { Text("Monto en pesos") }, singleLine = true) },
                            confirmButton = {
                                Button(enabled = pesos != null && pesos > 0,
                                    onClick = { if (pesos != null && alRegistrarPago(reserva.id, pesos)) mostrarDialogo = false }
                                ) { Text("Guardar pago") }
                            },
                            dismissButton = { TextButton(onClick = { mostrarDialogo = false }) { Text("Cancelar") } })
                    }
                }
            }

            if (puedeGestionar && reserva.estado == EstadoReserva.PENDIENTE) {
                if (ocupado) Text("Bloque ocupado por otra reserva aprobada")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilledTonalButton(
                        onClick = { alActualizarEstado(reserva.id, EstadoReserva.RECHAZADA) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Rechazar")
                    }
                    Button(
                        enabled = !ocupado,
                        onClick = { alActualizarEstado(reserva.id, EstadoReserva.APROBADA) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Aprobar")
                    }
                }
            }
        }
    }
}
