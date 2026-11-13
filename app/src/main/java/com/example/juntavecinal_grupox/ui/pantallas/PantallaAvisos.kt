package com.example.juntavecinal_grupox.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.juntavecinal_grupox.model.Aviso
import com.example.juntavecinal_grupox.ui.componentes.EstadoVacio
import com.example.juntavecinal_grupox.ui.componentes.TituloSeccion

@Composable
fun PantallaAvisos(
    avisos: List<Aviso>,
    alMarcarLeido: (String) -> Unit,
    alMarcarTodosLeidos: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            TituloSeccion("Mis avisos")
        }
        if (avisos.isEmpty()) {
            item {
                EstadoVacio("Todavía no tienes avisos", "Aquí aparecerán las novedades de tus reservas y pagos.")
            }
        } else {
            item {
                TextButton(
                    onClick = alMarcarTodosLeidos,
                    enabled = avisos.any { !it.leido }
                ) {
                    Text("Marcar todos como leídos")
                }
            }
            items(avisos, key = { it.id }) { aviso ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (aviso.leido) MaterialTheme.colorScheme.surface
                            else MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(aviso.titulo, style = MaterialTheme.typography.titleMedium)
                        Text(aviso.mensaje, style = MaterialTheme.typography.bodyMedium)
                        Text(aviso.fecha, style = MaterialTheme.typography.bodySmall)
                        if (aviso.leido) {
                            Text("Leído", style = MaterialTheme.typography.labelMedium)
                        } else {
                            TextButton(onClick = { alMarcarLeido(aviso.id) }) {
                                Text("Marcar como leído")
                            }
                        }
                    }
                }
            }
        }
    }
}
