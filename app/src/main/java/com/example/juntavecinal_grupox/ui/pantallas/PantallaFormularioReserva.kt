package com.example.juntavecinal_grupox.ui.pantallas

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.juntavecinal_grupox.model.Reserva
import com.example.juntavecinal_grupox.model.bloquesHorarios
import com.example.juntavecinal_grupox.model.estaOcupado
import com.example.juntavecinal_grupox.ui.componentes.TituloSeccion
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PantallaFormularioReserva(
    reservas: List<Reserva>,
    reservaInicial: Reserva? = null,
    alVolver: () -> Unit,
    alEnviar: (fecha: String, hora: String, motivo: String) -> Boolean
) {
    var fecha by rememberSaveable { mutableStateOf(reservaInicial?.fecha.orEmpty()) }
    var hora by rememberSaveable { mutableStateOf(reservaInicial?.hora.orEmpty()) }
    var motivo by rememberSaveable { mutableStateOf(reservaInicial?.motivo.orEmpty()) }
    var mensajeError by rememberSaveable { mutableStateOf<String?>(null) }

    val contexto = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TextButton(onClick = alVolver) { Text("← Volver") }
        TituloSeccion(if (reservaInicial == null) "Solicitar arriendo" else "Editar solicitud")
        Text(if (reservaInicial == null) "Completa los datos para enviar la solicitud de uso de la sede comunitaria."
            else "Actualiza la fecha, el bloque o el motivo de tu solicitud pendiente.")

        Button(onClick = {
            val hoy = Calendar.getInstance()
            val fechaElegida = runCatching {
                java.text.SimpleDateFormat("dd/MM/yyyy", Locale.ROOT).parse(fecha)
            }.getOrNull()
            if (fechaElegida != null) hoy.time = fechaElegida
            DatePickerDialog(
                contexto,
                { _, anio, mes, dia ->
                    fecha = String.format(Locale.ROOT, "%02d/%02d/%04d", dia, mes + 1, anio)
                    hora = ""
                    mensajeError = null
                },
                hoy.get(Calendar.YEAR),
                hoy.get(Calendar.MONTH),
                hoy.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.minDate = System.currentTimeMillis()
                show()
            }
        }) {
            Text(fecha.ifEmpty { "Seleccionar fecha" })
        }

        if (fecha.isNotEmpty()) {
            Text("Bloques de una hora", style = MaterialTheme.typography.titleSmall)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                bloquesHorarios.forEach { bloque ->
                    val ocupado = reservas.estaOcupado(fecha, bloque)
                    FilterChip(
                        selected = hora == bloque,
                        enabled = !ocupado,
                        onClick = { hora = bloque; mensajeError = null },
                        label = { Text(if (ocupado) "$bloque · Ocupado" else "$bloque · Disponible") }
                    )
                }
            }
        }

        OutlinedTextField(
            value = motivo,
            onValueChange = { motivo = it; mensajeError = null },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Motivo de la solicitud") },
            minLines = 3,
            maxLines = 5
        )

        mensajeError?.let { mensaje ->
            Text(mensaje, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }

        Button(
            onClick = {
                when {
                    fecha.isEmpty() -> mensajeError = "Selecciona una fecha."
                    hora.isEmpty() -> mensajeError = "Selecciona un bloque disponible."
                    motivo.isBlank() -> mensajeError = "Cuéntanos brevemente el motivo de la solicitud."
                    !alEnviar(fecha, hora, motivo.trim()) -> mensajeError = if (reservaInicial == null) "El bloque ya está ocupado."
                        else "No se pudo guardar. Revisa la disponibilidad y que la solicitud siga pendiente."
                    else -> Unit
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (reservaInicial == null) "Enviar solicitud" else "Guardar cambios")
        }
    }
}