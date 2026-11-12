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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.TextButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.juntavecinal_grupox.model.MovimientoFinanciero
import com.example.juntavecinal_grupox.model.TipoMovimiento
import com.example.juntavecinal_grupox.model.RolUsuario
import com.example.juntavecinal_grupox.ui.componentes.TituloSeccion
import com.example.juntavecinal_grupox.ui.componentes.TarjetaResumen
import com.example.juntavecinal_grupox.ui.componentes.formatearPesos
import com.example.juntavecinal_grupox.model.EstadoAplicacion

@Composable
fun PantallaFinanzas(
    estadoAplicacion: EstadoAplicacion,
    alCrearMovimiento: () -> Unit
) {
    var desplazamientoMes by rememberSaveable { mutableIntStateOf(0) }
    val mes = Calendar.getInstance().apply { add(Calendar.MONTH, desplazamientoMes) }.time
    val claveMes = SimpleDateFormat("MM/yyyy", Locale.ROOT).format(mes)
    val nombreMes = SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("es-CL")).format(mes)
    val rendicion = estadoAplicacion.copy(movimientos = estadoAplicacion.movimientos.filter { it.fecha.endsWith("/" + claveMes) })

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                TituloSeccion("Finanzas comunitarias")
                Text(
                    "Resumen informativo con montos ficticios para la demostración.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                TextButton(onClick = { desplazamientoMes-- }) { Text("Anterior") }
                Text(nombreMes)
                TextButton(onClick = { desplazamientoMes++ }) { Text("Siguiente") }
            }
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Saldo del mes", style = MaterialTheme.typography.titleMedium)
                    Text(formatearPesos(rendicion.saldo), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaResumen(
                    titulo = "Ingresos",
                    value = formatearPesos(rendicion.totalIngresos),
                    supportingText = "del mes",
                    modifier = Modifier.weight(1f)
                )
                TarjetaResumen(
                    titulo = "Gastos",
                    value = formatearPesos(rendicion.totalGastos),
                    supportingText = "del mes",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (estadoAplicacion.perfilActivo == RolUsuario.TESORERIA) {
            item {
                Button(onClick = alCrearMovimiento, modifier = Modifier.fillMaxWidth()) {
                    Text("Registrar ingreso o gasto")
                }
            }
        }

        item { TituloSeccion("Movimientos del mes") }

        if (rendicion.movimientos.isEmpty()) item { Text("Sin movimientos en este mes.") }
        items(rendicion.movimientos, key = { it.id }) { movimiento ->
            TarjetaMovimiento(movimiento)
        }
    }
}

@Composable
private fun TarjetaMovimiento(movimiento: MovimientoFinanciero) {
    val esIngreso = movimiento.tipo == TipoMovimiento.INGRESO
    val colorMonto = if (esIngreso) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    val signo = if (esIngreso) "+" else "−"

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(movimiento.titulo, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${movimiento.categoria} · ${movimiento.fecha}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text("$signo ${formatearPesos(movimiento.monto)}", style = MaterialTheme.typography.titleMedium, color = colorMonto)
        }
    }
}
