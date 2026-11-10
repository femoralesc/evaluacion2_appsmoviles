package com.example.juntavecinal_grupox.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.juntavecinal_grupox.data.model.MovementType
import com.example.juntavecinal_grupox.ui.components.SectionTitle

@Composable
fun MovementFormScreen(
    onBack: () -> Unit,
    onSubmit: (title: String, category: String, amount: Long, type: MovementType) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(MovementType.INGRESO) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TextButton(onClick = onBack) { Text("← Volver a finanzas") }
        SectionTitle("Registrar movimiento")
        Text("Agrega un ingreso o gasto ficticio para la demostración.")

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilterChip(
                selected = type == MovementType.INGRESO,
                onClick = { type = MovementType.INGRESO },
                label = { Text("Ingreso") }
            )
            FilterChip(
                selected = type == MovementType.GASTO,
                onClick = { type = MovementType.GASTO },
                label = { Text("Gasto") }
            )
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it; errorMessage = null },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Descripción") },
            placeholder = { Text("Ej.: Consumo de agua") },
            singleLine = true
        )
        OutlinedTextField(
            value = category,
            onValueChange = { category = it; errorMessage = null },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Categoría") },
            placeholder = { Text("Ej.: Servicios básicos") },
            singleLine = true
        )
        OutlinedTextField(
            value = amount,
            onValueChange = { input ->
                if (input.all { it.isDigit() || it == '.' }) amount = input
                errorMessage = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Monto en pesos") },
            placeholder = { Text("Ej.: 15000") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        errorMessage?.let { message ->
            Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }

        Button(
            onClick = {
                val parsedAmount = amount.replace(".", "").toLongOrNull()
                when {
                    title.isBlank() -> errorMessage = "Ingresa una descripción."
                    category.isBlank() -> errorMessage = "Ingresa una categoría."
                    parsedAmount == null || parsedAmount <= 0 -> errorMessage = "Ingresa un monto mayor que cero."
                    else -> onSubmit(title.trim(), category.trim(), parsedAmount, type)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar movimiento")
        }
    }
}
