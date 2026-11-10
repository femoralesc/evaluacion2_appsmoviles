package com.example.juntavecinal_grupox.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.juntavecinal_grupox.ui.components.SectionTitle

@Composable
fun ReservationFormScreen(
    onBack: () -> Unit,
    onSubmit: (date: String, time: String, purpose: String) -> Unit
) {
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var purpose by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TextButton(onClick = onBack) { Text("← Volver a reservas") }
        SectionTitle("Solicitar arriendo")
        Text("Completa los datos para enviar la solicitud de uso de la sede comunitaria.")

        OutlinedTextField(
            value = date,
            onValueChange = { date = it; errorMessage = null },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Fecha") },
            placeholder = { Text("DD/MM/AAAA") },
            supportingText = { Text("Por ejemplo: 20/11/2026") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
        )
        OutlinedTextField(
            value = time,
            onValueChange = { time = it; errorMessage = null },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Hora de inicio") },
            placeholder = { Text("HH:MM") },
            supportingText = { Text("Usa formato de 24 horas") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
        )
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
                val dateIsValid = date.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))
                val timeIsValid = time.matches(Regex("(?:[01]\\d|2[0-3]):[0-5]\\d"))
                when {
                    !dateIsValid -> errorMessage = "Ingresa la fecha con formato DD/MM/AAAA."
                    !timeIsValid -> errorMessage = "Ingresa la hora con formato HH:MM, entre 00:00 y 23:59."
                    purpose.isBlank() -> errorMessage = "Cuéntanos brevemente el motivo de la solicitud."
                    else -> onSubmit(date.trim(), time.trim(), purpose.trim())
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar solicitud")
        }
    }
}
