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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.juntavecinal_grupox.model.FinancialMovement
import com.example.juntavecinal_grupox.model.MovementType
import com.example.juntavecinal_grupox.model.UserRole
import com.example.juntavecinal_grupox.ui.components.SectionTitle
import com.example.juntavecinal_grupox.ui.components.SummaryCard
import com.example.juntavecinal_grupox.ui.components.formatClp
import com.example.juntavecinal_grupox.model.AppUiState

@Composable
fun FinanceScreen(
    uiState: AppUiState,
    onNewMovement: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SectionTitle("Finanzas comunitarias")
                Text(
                    "Resumen informativo con montos ficticios para la demostración.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Saldo disponible (demo)", style = MaterialTheme.typography.titleMedium)
                    Text(formatClp(uiState.balance), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard(
                    title = "Ingresos",
                    value = formatClp(uiState.totalIncome),
                    supportingText = "acumulados (demo)",
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Gastos",
                    value = formatClp(uiState.totalExpenses),
                    supportingText = "acumulados (demo)",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (uiState.activeProfile == UserRole.TESORERIA) {
            item {
                Button(onClick = onNewMovement, modifier = Modifier.fillMaxWidth()) {
                    Text("Registrar ingreso o gasto")
                }
            }
        }

        item { SectionTitle("Movimientos recientes") }

        items(uiState.movements, key = { it.id }) { movement ->
            MovementCard(movement)
        }
    }
}

@Composable
private fun MovementCard(movement: FinancialMovement) {
    val isIncome = movement.type == MovementType.INGRESO
    val amountColor = if (isIncome) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    val sign = if (isIncome) "+" else "−"

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(movement.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${movement.category} · ${movement.date}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text("$sign ${formatClp(movement.amount)}", style = MaterialTheme.typography.titleMedium, color = amountColor)
        }
    }
}
