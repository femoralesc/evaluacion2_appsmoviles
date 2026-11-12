package com.example.juntavecinal_grupox.ui.screens

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
import com.example.juntavecinal_grupox.model.Reservation
import com.example.juntavecinal_grupox.model.bookingSlots
import com.example.juntavecinal_grupox.model.isBooked
import com.example.juntavecinal_grupox.ui.components.SectionTitle
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReservationFormScreen(
    reservations: List<Reservation>,
    onBack: () -> Unit,
    onSubmit: (date: String, time: String, purpose: String) -> Boolean
) {
    var date by rememberSaveable { mutableStateOf("") }
    var time by rememberSaveable { mutableStateOf("") }
    var purpose by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TextButton(onClick = onBack) { Text("← Volver") }
        SectionTitle("Solicitar arriendo")
        Text("Completa los datos para enviar la solicitud de uso de la sede comunitaria.")

        Button(onClick = {
            val today = Calendar.getInstance()
            DatePickerDialog(
                context,
                { _, year, month, day ->
                    date = String.format(Locale.ROOT, "%02d/%02d/%04d", day, month + 1, year)
                    time = ""
                    errorMessage = null
                },
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.minDate = System.currentTimeMillis()
                show()
            }
        }) {
            Text(date.ifEmpty { "Seleccionar fecha" })
        }

        if (date.isNotEmpty()) {
            Text("Bloques de una hora", style = MaterialTheme.typography.titleSmall)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                bookingSlots.forEach { slot ->
                    val occupied = reservations.isBooked(date, slot)
                    FilterChip(
                        selected = time == slot,
                        enabled = !occupied,
                        onClick = { time = slot; errorMessage = null },
                        label = { Text(if (occupied) "$slot · Ocupado" else "$slot · Disponible") }
                    )
                }
            }
        }

        OutlinedTextField(
            value = purpose,
            onValueChange = { purpose = it; errorMessage = null },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Motivo de la solicitud") },
            minLines = 3,
            maxLines = 5
        )

        errorMessage?.let { message ->
            Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }

        Button(
            onClick = {
                when {
                    date.isEmpty() -> errorMessage = "Selecciona una fecha."
                    time.isEmpty() -> errorMessage = "Selecciona un bloque disponible."
                    purpose.isBlank() -> errorMessage = "Cuéntanos brevemente el motivo de la solicitud."
                    !onSubmit(date, time, purpose.trim()) -> errorMessage = "El bloque ya está ocupado."
                    else -> Unit
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar solicitud")
        }
    }
}