package com.example.juntavecinal_grupox.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.juntavecinal_grupox.model.RolUsuario
import com.example.juntavecinal_grupox.ui.componentes.TituloSeccion

@Composable
fun PantallaPerfil(
    perfilActivo: RolUsuario,
    alSeleccionarPerfil: (RolUsuario) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                TituloSeccion("Perfil de demostración")
                Text(
                    "Cambia de perfil para presentar las distintas funciones de la aplicación.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(RolUsuario.entries) { rol ->
            val selected = rol == perfilActivo

            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { alSeleccionarPerfil(rol) },
                colors = CardDefaults.cardColors(
                    containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RadioButton(selected = selected, onClick = { alSeleccionarPerfil(rol) })
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(rol.titulo, style = MaterialTheme.typography.titleMedium)
                        Text(rol.descripcion, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        item {
            Text(
                "Este prototipo no usa cuentas reales ni guarda datos al cerrar la aplicación.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
