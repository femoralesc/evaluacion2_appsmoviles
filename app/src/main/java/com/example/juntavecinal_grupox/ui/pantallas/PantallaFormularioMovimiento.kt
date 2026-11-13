package com.example.juntavecinal_grupox.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.juntavecinal_grupox.model.TipoMovimiento
import com.example.juntavecinal_grupox.model.interpretarMontoEnPesos
import com.example.juntavecinal_grupox.ui.componentes.TituloSeccion

@Composable
fun PantallaFormularioMovimiento(
    alVolver: () -> Unit,
    alEnviar: (titulo: String, categoria: String, monto: Long, tipo: TipoMovimiento) -> Boolean
) {
    var titulo by rememberSaveable { mutableStateOf("") }
    var categoria by rememberSaveable { mutableStateOf("") }
    var monto by rememberSaveable { mutableStateOf("") }
    var tipo by rememberSaveable { mutableStateOf(TipoMovimiento.INGRESO) }
    var guardado by rememberSaveable { mutableStateOf(false) }
    var intentoEnviar by rememberSaveable { mutableStateOf(false) }
    var mensajeError by rememberSaveable { mutableStateOf<String?>(null) }
    val foco = LocalFocusManager.current
    val montoNumerico = interpretarMontoEnPesos(monto)

    fun enviar() {
        if (guardado) return
        val montoAlEnviar = interpretarMontoEnPesos(monto)
        intentoEnviar = true
        mensajeError = null
        if (titulo.isBlank() || categoria.isBlank() || montoAlEnviar == null) return
        foco.clearFocus()
        guardado = alEnviar(titulo.trim(), categoria.trim(), montoAlEnviar, tipo)
        if (!guardado) {
            mensajeError = "No se pudo registrar el movimiento. Revisa tus permisos e inténtalo nuevamente."
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TextButton(onClick = alVolver) { Text("← Volver") }
        TituloSeccion("Registrar movimiento")

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilterChip(selected = tipo == TipoMovimiento.INGRESO,
                onClick = { tipo = TipoMovimiento.INGRESO }, label = { Text("Ingreso") })
            FilterChip(selected = tipo == TipoMovimiento.GASTO,
                onClick = { tipo = TipoMovimiento.GASTO }, label = { Text("Gasto") })
        }
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it; mensajeError = null },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Descripción") },
            placeholder = { Text("Ej.: Consumo de agua") },
            singleLine = true,
            isError = intentoEnviar && titulo.isBlank(),
            supportingText = {
                if (intentoEnviar && titulo.isBlank()) Text("Ingresa una descripción.")
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        OutlinedTextField(
            value = categoria,
            onValueChange = { categoria = it; mensajeError = null },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Categoría") },
            placeholder = { Text("Ej.: Servicios básicos") },
            singleLine = true,
            isError = intentoEnviar && categoria.isBlank(),
            supportingText = {
                if (intentoEnviar && categoria.isBlank()) Text("Ingresa una categoría.")
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        OutlinedTextField(
            value = monto,
            onValueChange = { monto = it; mensajeError = null },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Monto en pesos") },
            placeholder = { Text("Ej.: 15000 o 15.000") },
            singleLine = true,
            isError = intentoEnviar && montoNumerico == null,
            supportingText = {
                Text(if (intentoEnviar && montoNumerico == null)
                    "Ingresa un monto válido mayor que cero, sin decimales."
                    else "Puedes usar puntos para separar los miles.")
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { enviar() })
        )
        mensajeError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }
        Button(onClick = { enviar() }, enabled = !guardado, modifier = Modifier.fillMaxWidth()) { Text("Guardar movimiento") }
    }
}
