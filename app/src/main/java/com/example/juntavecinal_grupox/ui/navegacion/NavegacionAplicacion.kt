package com.example.juntavecinal_grupox.ui.navegacion

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import com.example.juntavecinal_grupox.ui.pantallas.PantallaAcceso
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.juntavecinal_grupox.ui.pantallas.PantallaFinanzas
import com.example.juntavecinal_grupox.ui.pantallas.PantallaInicio
import com.example.juntavecinal_grupox.ui.pantallas.PantallaFormularioMovimiento
import com.example.juntavecinal_grupox.ui.pantallas.PantallaPerfil
import com.example.juntavecinal_grupox.ui.pantallas.PantallaFormularioReserva
import com.example.juntavecinal_grupox.ui.pantallas.PantallaReservas
import com.example.juntavecinal_grupox.viewmodel.PrincipalViewModel

private data class OpcionNavegacion(
    val etiqueta: String,
    val ruta: RutasAplicacion,
    val icono: ImageVector
)

@Composable
fun NavegacionAplicacion(modeloPrincipal: PrincipalViewModel) {
    val estado by modeloPrincipal.estadoAplicacion.collectAsStateWithLifecycle()
    val usuario = estado.usuarioActual
    if (usuario == null) {
        PantallaAcceso(
            mensajeError = estado.errorAcceso,
            alCambiarDatos = modeloPrincipal::limpiarErrorAcceso,
            alIngresar = modeloPrincipal::iniciarSesion
        )
    } else {
        key(usuario.id) {
            NavegacionSesion(modeloPrincipal)
        }
    }
}

@Composable
private fun NavegacionSesion(
    modeloPrincipal: PrincipalViewModel,
    controladorNavegacion: NavHostController = rememberNavController()
) {
    val estadoAplicacion by modeloPrincipal.estadoAplicacion.collectAsStateWithLifecycle()
    val resumenFinanciero = modeloPrincipal.obtenerResumenFinanciero(estadoAplicacion)
    val avisos = remember { SnackbarHostState() }
    val alcance = rememberCoroutineScope()


    val entradaActual by controladorNavegacion.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route


    val opciones = listOf(
        OpcionNavegacion("Inicio", RutasAplicacion.Inicio, Icons.Default.Home),
        OpcionNavegacion("Reservas", RutasAplicacion.Reservas, Icons.Default.CalendarMonth),
        OpcionNavegacion(estadoAplicacion.perfilActivo.tituloFinanciero, RutasAplicacion.Finanzas, Icons.Default.AccountBalanceWallet),
        OpcionNavegacion("Perfil", RutasAplicacion.Perfil, Icons.Default.Person)
    )


    val mostrarBarraInferior = opciones.any { it.ruta.ruta == rutaActual }

    LaunchedEffect(Unit) {
        modeloPrincipal.eventoNavegacion.collect { evento ->
            when (evento) {
                is EventoNavegacion.IrA -> {
                    if (modeloPrincipal.puedeAcceder(evento.ruta)) controladorNavegacion.navigate(evento.ruta.ruta) {
                        evento.rutaHasta?.let { rutaDestino ->
                            popUpTo(rutaDestino.ruta) {
                                inclusive = evento.incluirDestino
                            }
                        }
                        launchSingleTop = evento.destinoUnico
                    }
                }
                is EventoNavegacion.Volver -> controladorNavegacion.popBackStack()
                is EventoNavegacion.Subir -> controladorNavegacion.navigateUp()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(avisos) },
        bottomBar = {
            if (mostrarBarraInferior) {
                NavigationBar {
                    opciones.forEach { item ->
                        NavigationBarItem(
                            selected = rutaActual == item.ruta.ruta,
                            onClick = {
                                if (rutaActual != item.ruta.ruta) {
                                    modeloPrincipal.navegarA(item.ruta, volverHasta = RutasAplicacion.Inicio)
                                }
                            },
                            icon = { Icon(imageVector = item.icono, contentDescription = item.etiqueta) },
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
                    alAbrirReservas = { modeloPrincipal.navegarA(RutasAplicacion.Reservas) },
                    alAbrirFinanzas = { modeloPrincipal.navegarA(RutasAplicacion.Finanzas) },
                    alSolicitarReserva = { modeloPrincipal.navegarA(RutasAplicacion.FormularioReserva) },
                    alCrearMovimiento = { modeloPrincipal.navegarA(RutasAplicacion.FormularioMovimiento) }
                )
            }
            composable(RutasAplicacion.Reservas.ruta) {
                PantallaReservas(
                    estadoAplicacion = estadoAplicacion,
                    alRegistrarPago = { id, monto -> modeloPrincipal.registrarPago(id, monto) },
                    alSolicitarReserva = { modeloPrincipal.navegarA(RutasAplicacion.FormularioReserva) },
                    alActualizarEstado = { id, estado -> modeloPrincipal.actualizarEstadoReserva(id, estado) }
                )
            }
            composable(RutasAplicacion.Finanzas.ruta) {
                PantallaFinanzas(
                    resumen = resumenFinanciero,
                    alCambiarMes = modeloPrincipal::cambiarMesFinanciero,
                    alCrearMovimiento = { modeloPrincipal.navegarA(RutasAplicacion.FormularioMovimiento) }
                )
            }
            composable(RutasAplicacion.Perfil.ruta) {
                estadoAplicacion.usuarioActual?.let { usuario ->
                    PantallaPerfil(
                        usuario = usuario,
                        alCerrarSesion = modeloPrincipal::cerrarSesion
                    )
                }
            }
            composable(RutasAplicacion.FormularioReserva.ruta) {
                if (modeloPrincipal.puedeAcceder(RutasAplicacion.FormularioReserva)) PantallaFormularioReserva(
                    reservas = estadoAplicacion.reservas,
                    alVolver = { modeloPrincipal.volver() },
                    alEnviar = { fecha, hora, motivo ->
                        val guardada = modeloPrincipal.solicitarReserva(fecha, hora, motivo)
                        if (guardada) {
                            alcance.launch { avisos.showSnackbar("Solicitud enviada") }
                        }
                        guardada
                    }
                )
            }
            composable(RutasAplicacion.FormularioMovimiento.ruta) {
                if (modeloPrincipal.puedeAcceder(RutasAplicacion.FormularioMovimiento)) PantallaFormularioMovimiento(
                    alVolver = { modeloPrincipal.volver() },
                    alEnviar = { titulo, categoria, monto, tipo ->
                        modeloPrincipal.registrarMovimiento(titulo, categoria, monto, tipo)
                    }
                )
            }
        }
    }
}
