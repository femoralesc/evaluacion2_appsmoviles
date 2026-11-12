package com.example.juntavecinal_grupox.ui.pantallas

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
import com.example.juntavecinal_grupox.model.EstadoReserva
import com.example.juntavecinal_grupox.model.RolUsuario
import com.example.juntavecinal_grupox.ui.componentes.TarjetaResumen
import com.example.juntavecinal_grupox.ui.componentes.formatearPesos
import com.example.juntavecinal_grupox.model.EstadoAplicacion

@Composable
fun PantallaInicio(
    estadoAplicacion: EstadoAplicacion,
    alAbrirReservas: () -> Unit,
    alAbrirFinanzas: () -> Unit,
    alSolicitarReserva: () -> Unit,
    alCrearMovimiento: () -> Unit
) {
    val reservasPendientes = estadoAplicacion.reservas.count { it.estado == EstadoReserva.PENDIENTE }
    val textoAccion = when (estadoAplicacion.perfilActivo) {
        RolUsuario.VECINO -> "Solicitar arriendo"
        RolUsuario.DIRECTIVA -> "Revisar solicitudes"
        RolUsuario.TESORERIA -> "Registrar movimiento"
    }
    val accionPrincipal = when (estadoAplicacion.perfilActivo) {
        RolUsuario.VECINO -> alSolicitarReserva
        RolUsuario.DIRECTIVA -> alAbrirReservas
        RolUsuario.TESORERIA -> alCrearMovimiento
    }

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Hola, ${estadoAplicacion.perfilActivo.nombreDemostracion}", style = MaterialTheme.typography.headlineSmall)
                Text(
                    estadoAplicacion.perfilActivo.titulo,
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
                    Button(onClick = accionPrincipal) {
                        Text(textoAccion)
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaResumen(
                    titulo = "Por revisar",
                    value = reservasPendientes.toString(),
                    supportingText = "solicitudes pendientes",
                    modifier = Modifier.weight(1f)
                )
                TarjetaResumen(
                    titulo = "Saldo demo",
                    value = formatearPesos(estadoAplicacion.saldo),
                    supportingText = "monto ficticio",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Accesos rápidos", style = MaterialTheme.typography.titleMedium)
                Button(onClick = alAbrirReservas, modifier = Modifier.fillMaxWidth()) {
                    Text("Ver reservas")
                }
                Button(onClick = alAbrirFinanzas, modifier = Modifier.fillMaxWidth()) {
                    Text("Consultar ${estadoAplicacion.perfilActivo.tituloFinanciero.lowercase()}")
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
