package com.example.juntavecinal_grupox.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.juntavecinal_grupox.ui.navegacion.RutasAplicacion
import com.example.juntavecinal_grupox.ui.pantallas.PantallaFinanzas
import com.example.juntavecinal_grupox.ui.pantallas.PantallaInicio
import com.example.juntavecinal_grupox.ui.pantallas.PantallaFormularioMovimiento
import com.example.juntavecinal_grupox.ui.pantallas.PantallaPerfil
import com.example.juntavecinal_grupox.ui.pantallas.PantallaFormularioReserva
import com.example.juntavecinal_grupox.ui.pantallas.PantallaReservas
import com.example.juntavecinal_grupox.viewmodel.PrincipalViewModel

private data class ElementoBarraInferior(
    val ruta: String,
    val etiqueta: String,
    val simbolo: String
)

private val elementosBarraInferior = listOf(
    ElementoBarraInferior(RutasAplicacion.Inicio.ruta, "Inicio", "⌂"),
    ElementoBarraInferior(RutasAplicacion.Reservas.ruta, "Reservas", "▤"),
    ElementoBarraInferior(RutasAplicacion.Finanzas.ruta, "Finanzas", "\$"),
    ElementoBarraInferior(RutasAplicacion.Perfil.ruta, "Perfil", "●")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AplicacionJuntaVecinal(modeloPrincipal: PrincipalViewModel = viewModel()) {
    val estadoAplicacion by modeloPrincipal.estadoAplicacion.collectAsStateWithLifecycle()
    val controladorNavegacion = rememberNavController()
    val entradaActual by controladorNavegacion.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route ?: RutasAplicacion.Inicio.ruta
    val mostrarBarraInferior = elementosBarraInferior.any { it.ruta == rutaActual }

    val avisos = remember { SnackbarHostState() }
    val alcance = rememberCoroutineScope()
    fun confirmar(mensaje: String) {
        alcance.launch { avisos.showSnackbar(mensaje) }
    }
    fun finalizarFormulario(ruta: String) {
        controladorNavegacion.popBackStack()
        controladorNavegacion.navigate(ruta) { launchSingleTop = true }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(avisos) },
        topBar = {
            TopAppBar(
                title = { Text("Junta Vecinal") },
                actions = {
                    TextButton(
                        onClick = {
                            controladorNavegacion.navigate(RutasAplicacion.Perfil.ruta) {
                                launchSingleTop = true
                            }
                        }
                    ) {
                        Text("Perfil")
                    }
                }
            )
        },
        bottomBar = {
            if (mostrarBarraInferior) {
                NavigationBar {
                    elementosBarraInferior.forEach { item ->
                        NavigationBarItem(
                            selected = rutaActual == item.ruta,
                            onClick = {
                                controladorNavegacion.navigate(item.ruta) {
                                    popUpTo(controladorNavegacion.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Text(item.simbolo) },
                            label = { Text(item.etiqueta) }
                        )
                    }
                }
            }
        }
    ) { margenInterior ->
        NavHost(
            navController = controladorNavegacion,
            startDestination = RutasAplicacion.Inicio.ruta,
            modifier = Modifier.padding(margenInterior)
        ) {
            composable(RutasAplicacion.Inicio.ruta) {
                PantallaInicio(
                    estadoAplicacion = estadoAplicacion,
                    alAbrirReservas = { controladorNavegacion.navigate(RutasAplicacion.Reservas.ruta) },
                    alAbrirFinanzas = { controladorNavegacion.navigate(RutasAplicacion.Finanzas.ruta) },
                    alSolicitarReserva = { controladorNavegacion.navigate(RutasAplicacion.FormularioReserva.ruta) },
                    alCrearMovimiento = { controladorNavegacion.navigate(RutasAplicacion.FormularioMovimiento.ruta) }
                )
            }
            composable(RutasAplicacion.Reservas.ruta) {
                PantallaReservas(
                    estadoAplicacion = estadoAplicacion,
                    alSolicitarReserva = { controladorNavegacion.navigate(RutasAplicacion.FormularioReserva.ruta) },
                    alRegistrarPago = { id, monto ->
                        val pagada = modeloPrincipal.registrarPago(id, monto)
                        confirmar(if (pagada) "Pago registrado" else "Revisa el estado del arriendo")
                        pagada
                    },
                    alActualizarEstado = { id, estado ->
                        val actualizada = modeloPrincipal.actualizarEstadoReserva(id, estado)
                        confirmar(if (actualizada) "Solicitud actualizada" else "El bloque ya está ocupado")
                    }
                )
            }
            composable(RutasAplicacion.Finanzas.ruta) {
                PantallaFinanzas(
                    estadoAplicacion = estadoAplicacion,
                    alCrearMovimiento = { controladorNavegacion.navigate(RutasAplicacion.FormularioMovimiento.ruta) }
                )
            }
            composable(RutasAplicacion.Perfil.ruta) {
                PantallaPerfil(
                    perfilActivo = estadoAplicacion.perfilActivo,
                    alSeleccionarPerfil = { perfil ->
                        modeloPrincipal.seleccionarPerfil(perfil)
                        controladorNavegacion.navigate(RutasAplicacion.Inicio.ruta) {
                            popUpTo(RutasAplicacion.Inicio.ruta) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(RutasAplicacion.FormularioReserva.ruta) {
                PantallaFormularioReserva(
                    reservas = estadoAplicacion.reservas,
                    alVolver = { controladorNavegacion.popBackStack() },
                    alEnviar = { fecha, hora, motivo ->
                        val agregada = modeloPrincipal.solicitarReserva(fecha, hora, motivo)
                        if (agregada) {
                            finalizarFormulario(RutasAplicacion.Reservas.ruta)
                            confirmar("Solicitud enviada")
                        }
                        agregada
                    }
                )
            }
            composable(RutasAplicacion.FormularioMovimiento.ruta) {
                PantallaFormularioMovimiento(
                    alVolver = { controladorNavegacion.popBackStack() },
                    alEnviar = { titulo, categoria, monto, tipo ->
                        modeloPrincipal.registrarMovimiento(titulo, categoria, monto, tipo)
                        finalizarFormulario(RutasAplicacion.Finanzas.ruta)
                        confirmar("Movimiento registrado")
                    }
                )
            }
        }
    }
}
