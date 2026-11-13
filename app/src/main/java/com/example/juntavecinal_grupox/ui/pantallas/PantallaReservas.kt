package com.example.juntavecinal_grupox.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import com.example.juntavecinal_grupox.model.interpretarMontoEnPesos
import com.example.juntavecinal_grupox.ui.componentes.EstadoVacio
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
                EstadoVacio(
                    "Sin reservas",
                    if (esVecino) "Todavía no tienes solicitudes. Usa Nueva solicitud de arriendo para comenzar."
                    else "Las solicitudes de uso de la sede aparecerán aquí."
                )
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

@OptIn(ExperimentalLayoutApi::class)
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
                FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { alEditarReserva(reserva.id) }) { Text("Editar") }
                    TextButton(onClick = { confirmarCancelacion = true }) { Text("Cancelar solicitud") }
                }
                if (confirmarCancelacion) {
                    AlertDialog(
                        onDismissRequest = { confirmarCancelacion = false },
                        title = { Text("¿Cancelar solicitud?") },
                        text = { Text("Se cancelará la solicitud de " + reserva.motivo + " del " + reserva.fecha + ".",
                            modifier = Modifier.verticalScroll(rememberScrollState())) },
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
                    var intentoGuardar by rememberSaveable { mutableStateOf(false) }
                    var errorPago by rememberSaveable { mutableStateOf<String?>(null) }
                    val pesos = interpretarMontoEnPesos(monto)

                    fun guardarPago() {
                        if (!mostrarDialogo) return
                        val montoAlGuardar = interpretarMontoEnPesos(monto)
                        intentoGuardar = true
                        if (montoAlGuardar == null) return
                        if (alRegistrarPago(reserva.id, montoAlGuardar)) mostrarDialogo = false
                        else errorPago = "No se pudo registrar. Revisa que la reserva siga aprobada y pendiente de pago."
                    }

                    TextButton(onClick = {
                        monto = ""
                        intentoGuardar = false
                        errorPago = null
                        mostrarDialogo = true
                    }) { Text("Registrar pago") }
                    if (mostrarDialogo) {
                        AlertDialog(
                            onDismissRequest = { mostrarDialogo = false },
                            title = { Text("Pago de arriendo") },
                            text = {
                                Column(
                                    modifier = Modifier.verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(reserva.motivo + " · " + reserva.fecha)
                                    OutlinedTextField(
                                        value = monto,
                                        onValueChange = { monto = it; errorPago = null },
                                        label = { Text("Monto en pesos") },
                                        singleLine = true,
                                        isError = intentoGuardar && pesos == null,
                                        supportingText = {
                                            Text(if (intentoGuardar && pesos == null)
                                                "Ingresa un monto válido mayor que cero, sin decimales."
                                                else "Ej.: 15000 o 15.000")
                                        },
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Number,
                                            imeAction = ImeAction.Done
                                        ),
                                        keyboardActions = KeyboardActions(onDone = { guardarPago() })
                                    )
                                    errorPago?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                                }
                            },
                            confirmButton = {
                                Button(onClick = { guardarPago() }) { Text("Guardar pago") }
                            },
                            dismissButton = {
                                TextButton(onClick = { mostrarDialogo = false }) { Text("Cancelar") }
                            }
                        )
                    }
                }
            }

            if (puedeGestionar && reserva.estado == EstadoReserva.PENDIENTE) {
                if (ocupado) Text("Bloque ocupado por otra reserva aprobada")
                var confirmarRechazo by rememberSaveable { mutableStateOf(false) }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilledTonalButton(
                        onClick = { confirmarRechazo = true },
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
                if (confirmarRechazo) {
                    AlertDialog(
                        onDismissRequest = { confirmarRechazo = false },
                        title = { Text("¿Rechazar solicitud?") },
                        text = {
                            Text(reserva.motivo + " · " + reserva.fecha + " · " + reserva.hora,
                                modifier = Modifier.verticalScroll(rememberScrollState()))
                        },
                        confirmButton = {
                            Button(onClick = {
                                alActualizarEstado(reserva.id, EstadoReserva.RECHAZADA)
                                confirmarRechazo = false
                            }) { Text("Sí, rechazar") }
                        },
                        dismissButton = {
                            TextButton(onClick = { confirmarRechazo = false }) { Text("Conservar solicitud") }
                        }
                    )
                }
            }
        }
    }
}
