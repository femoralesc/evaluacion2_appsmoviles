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
import androidx.compose.runtime.Composable
import com.example.juntavecinal_grupox.model.ResumenFinanciero
import androidx.compose.material3.TextButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.juntavecinal_grupox.model.MovimientoFinanciero
import com.example.juntavecinal_grupox.model.TipoMovimiento
import com.example.juntavecinal_grupox.ui.componentes.TituloSeccion
import com.example.juntavecinal_grupox.ui.componentes.TarjetaResumen
import com.example.juntavecinal_grupox.ui.componentes.formatearPesos

@Composable
fun PantallaFinanzas(
    resumen: ResumenFinanciero,
    alCambiarMes: (Int) -> Unit,
    alCrearMovimiento: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                TituloSeccion(resumen.titulo)
                Text(
                    if (resumen.puedeVerDetalle) "Consulta los movimientos y recursos de la comunidad."
                    else "Consulta la rendición mensual de la comunidad.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                TextButton(onClick = { alCambiarMes(-1) }) { Text("Anterior") }
                Text(resumen.nombreMes)
                TextButton(onClick = { alCambiarMes(1) }) { Text("Siguiente") }
            }
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Saldo del mes", style = MaterialTheme.typography.titleMedium)
                    Text(formatearPesos(resumen.saldo), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaResumen(
                    titulo = "Ingresos",
                    value = formatearPesos(resumen.totalIngresos),
                    supportingText = "del mes",
                    modifier = Modifier.weight(1f)
                )
                TarjetaResumen(
                    titulo = "Gastos",
                    value = formatearPesos(resumen.totalGastos),
                    supportingText = "del mes",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (resumen.puedeRegistrar) {
            item {
                Button(onClick = alCrearMovimiento, modifier = Modifier.fillMaxWidth()) {
                    Text("Registrar ingreso o gasto")
                }
            }
        }

        if (resumen.puedeVerDetalle) {
            item { TituloSeccion("Movimientos del mes") }
            if (resumen.movimientos.isEmpty()) {
                item { Text("Sin movimientos en este mes.") }
            }
            items(resumen.movimientos, key = { it.id }) { movimiento ->
                TarjetaMovimiento(movimiento)
            }
        } else {
            item { TituloSeccion("Rendición mensual") }
            if (resumen.ingresosPorCategoria.isEmpty() && resumen.gastosPorCategoria.isEmpty()) {
                item { Text("Sin movimientos en este mes.") }
            }
            item {
                ResumenCategorias("Ingresos por categoría", resumen.ingresosPorCategoria)
            }
            item {
                ResumenCategorias("Gastos por categoría", resumen.gastosPorCategoria)
            }
        }
    }
}

@Composable
private fun TarjetaMovimiento(movimiento: MovimientoFinanciero) {
    val esIngreso = movimiento.tipo == TipoMovimiento.INGRESO
    val colorMonto = if (esIngreso) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
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

@Composable
private fun ResumenCategorias(titulo: String, totales: Map<String, Long>) {
    if (totales.isEmpty()) return
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            totales.forEach { (categoria, total) ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(categoria, modifier = Modifier.weight(1f))
                    Text(formatearPesos(total))
                }
            }
        }
    }
}
