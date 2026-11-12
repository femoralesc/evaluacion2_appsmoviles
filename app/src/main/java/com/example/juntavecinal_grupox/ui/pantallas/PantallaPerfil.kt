package com.example.juntavecinal_grupox.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.juntavecinal_grupox.model.Usuario
import com.example.juntavecinal_grupox.ui.componentes.TituloSeccion

@Composable
fun PantallaPerfil(usuario: Usuario, alCerrarSesion: () -> Unit) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TituloSeccion("Mi perfil")
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(usuario.nombre, style = MaterialTheme.typography.titleLarge)
                Text("Usuario: " + usuario.nombreUsuario)
                Text(usuario.rol.titulo, style = MaterialTheme.typography.titleMedium)
                Text(usuario.rol.descripcion)
            }
        }
        Button(onClick = alCerrarSesion, modifier = Modifier.fillMaxWidth()) {
            Text("Cerrar sesión")
        }
        Text(
            "Para acceder con otra cuenta, cierra la sesión actual.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
