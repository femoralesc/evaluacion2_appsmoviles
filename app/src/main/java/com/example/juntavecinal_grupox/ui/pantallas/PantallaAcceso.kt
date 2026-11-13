package com.example.juntavecinal_grupox.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun PantallaAcceso(
    mensajeError: String?,
    alCambiarDatos: () -> Unit,
    alIngresar: (String, String) -> Boolean
) {
    var nombreUsuario by rememberSaveable { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mostrarContrasena by remember { mutableStateOf(false) }
    val foco = LocalFocusManager.current

    fun ingresar() {
        foco.clearFocus()
        alIngresar(nombreUsuario, contrasena)
    }

    Scaffold { margenInterior ->
        Column(
            modifier = Modifier.padding(margenInterior).fillMaxSize().imePadding()
                .verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.widthIn(max = 460.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Junta Vecinal", style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary)
                Text("Población Zaror", style = MaterialTheme.typography.titleMedium)
                Text("Iniciar sesión", style = MaterialTheme.typography.headlineSmall)
                OutlinedTextField(
                    value = nombreUsuario,
                    onValueChange = { nombreUsuario = it; alCambiarDatos() },
                    label = { Text("Usuario") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = mensajeError != null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Ascii,
                        imeAction = ImeAction.Next
                    )
                )
                OutlinedTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it; alCambiarDatos() },
                    label = { Text("Contraseña") },
                    supportingText = {
                        mensajeError?.let { Text(it) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = mensajeError != null,
                    visualTransformation = if (mostrarContrasena) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(onClick = { mostrarContrasena = !mostrarContrasena }) {
                            Text(if (mostrarContrasena) "Ocultar" else "Mostrar")
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { ingresar() })
                )
                Button(onClick = { ingresar() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Ingresar")
                }
                Text("Acceso con cuentas de demostración.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
