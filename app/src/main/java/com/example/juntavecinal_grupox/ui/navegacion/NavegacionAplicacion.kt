package com.example.juntavecinal_grupox.ui.navegacion

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.juntavecinal_grupox.ui.pantallas.PantallaAvisos
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.juntavecinal_grupox.ui.tema.AzulMarino
import com.example.juntavecinal_grupox.ui.tema.VerdeAgua
import com.example.juntavecinal_grupox.ui.tema.GrisCarbon
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NavegacionSesion(
    modeloPrincipal: PrincipalViewModel,
    controladorNavegacion: NavHostController = rememberNavController()
) {
    val estadoAplicacion by modeloPrincipal.estadoAplicacion.collectAsStateWithLifecycle()
    val resumenFinanciero = modeloPrincipal.obtenerResumenFinanciero(estadoAplicacion)
    val avisos = remember { SnackbarHostState() }
    val alcance = rememberCoroutineScope()
    fun confirmar(mensaje: String) {
        alcance.launch {
            avisos.currentSnackbarData?.dismiss()
            avisos.showSnackbar(mensaje)
        }
    }


    val entradaActual by controladorNavegacion.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route


    val opciones = listOf(
        OpcionNavegacion("Inicio", RutasAplicacion.Inicio, Icons.Default.Home),
        OpcionNavegacion("Reservas", RutasAplicacion.Reservas, Icons.Default.CalendarMonth),
        OpcionNavegacion(estadoAplicacion.perfilActivo.tituloFinanciero, RutasAplicacion.Finanzas, Icons.Default.AccountBalanceWallet),
        OpcionNavegacion("Perfil", RutasAplicacion.Perfil, Icons.Default.Person)
    )


    val mostrarBarraInferior = opciones.any { it.ruta.ruta == rutaActual } || rutaActual == RutasAplicacion.Avisos.ruta

    val actividad = LocalContext.current as? Activity
    val vista = LocalView.current
    val temaOscuro = isSystemInDarkTheme()
    DisposableEffect(mostrarBarraInferior, temaOscuro, actividad, vista) {
        val controlador = actividad?.window?.let { WindowCompat.getInsetsController(it, vista) }
        val aparienciaAnterior = controlador?.isAppearanceLightStatusBars
        controlador?.isAppearanceLightStatusBars = !mostrarBarraInferior && !temaOscuro
        onDispose {
            if (aparienciaAnterior != null) controlador?.isAppearanceLightStatusBars = aparienciaAnterior
        }
    }

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
        topBar = {
            if (mostrarBarraInferior) {
                TopAppBar(
                    title = { Text("Junta Vecinal") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = AzulMarino,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    ),
                    navigationIcon = {
                        if (rutaActual == RutasAplicacion.Avisos.ruta) {
                            IconButton(onClick = modeloPrincipal::volver) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { modeloPrincipal.navegarA(RutasAplicacion.Avisos) }) {
                            BadgedBox(badge = {
                                if (estadoAplicacion.avisosSinLeer > 0) {
                                    Badge(containerColor = VerdeAgua, contentColor = GrisCarbon) {
                                        Text(estadoAplicacion.avisosSinLeer.toString())
                                    }
                                }
                            }) {
                                Icon(Icons.Default.Notifications, contentDescription = "Avisos")
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (mostrarBarraInferior) {
                NavigationBar(
                    containerColor = AzulMarino,
                    contentColor = Color.White,
                    tonalElevation = 0.dp
                ) {
                    opciones.forEach { item ->
                        NavigationBarItem(
                            selected = rutaActual == item.ruta.ruta,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = GrisCarbon,
                                selectedTextColor = Color.White,
                                indicatorColor = VerdeAgua,
                                unselectedIconColor = Color.White,
                                unselectedTextColor = Color.White
                            ),
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
                    alRegistrarPago = { id, monto ->
                        val guardado = modeloPrincipal.registrarPago(id, monto)
                        confirmar(if (guardado) "Pago registrado" else "No se pudo registrar el pago. Revisa el estado de la reserva.")
                        guardado
                    },
                    alSolicitarReserva = { modeloPrincipal.navegarA(RutasAplicacion.FormularioReserva) },
                    alEditarReserva = modeloPrincipal::abrirEdicionReserva,
                    puedeModificarReserva = modeloPrincipal::puedeModificarReserva,
                    alCancelarReserva = { id ->
                        val cancelada = modeloPrincipal.cancelarReserva(id)
                        confirmar(if (cancelada) "Solicitud cancelada" else "La solicitud ya no se puede cancelar")
                        cancelada
                    },
                    alActualizarEstado = { id, estado ->
                        val actualizada = modeloPrincipal.actualizarEstadoReserva(id, estado)
                        val mensaje = if (!actualizada) "No se pudo actualizar. Revisa el estado y la disponibilidad."
                            else if (estado == com.example.juntavecinal_grupox.model.EstadoReserva.APROBADA) "Reserva aprobada"
                            else "Solicitud rechazada"
                        confirmar(mensaje)
                    }
                )
            }
            composable(RutasAplicacion.Avisos.ruta) {
                PantallaAvisos(
                    avisos = estadoAplicacion.avisos,
                    alMarcarLeido = modeloPrincipal::marcarAvisoLeido,
                    alMarcarTodosLeidos = modeloPrincipal::marcarTodosLosAvisosLeidos
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
                            confirmar("Solicitud enviada")
                        }
                        guardada
                    }
                )
            }
            composable(RutasAplicacion.EditarReserva.ruta) {
                val reserva = modeloPrincipal.obtenerReservaEnEdicion()
                if (reserva != null) {
                    key(reserva.id) {
                        PantallaFormularioReserva(
                            reservas = estadoAplicacion.reservas,
                            reservaInicial = reserva,
                            alVolver = modeloPrincipal::volver,
                            alEnviar = { fecha, hora, motivo ->
                                val guardada = modeloPrincipal.guardarEdicionReserva(fecha, hora, motivo)
                                if (guardada) confirmar("Solicitud actualizada")
                                guardada
                            }
                        )
                    }
                } else {
                    androidx.compose.foundation.layout.Column(Modifier.padding(20.dp)) {
                        Text("La solicitud ya no está disponible para editar.")
                        androidx.compose.material3.TextButton(onClick = modeloPrincipal::volver) { Text("Volver") }
                    }
                }
            }
            composable(RutasAplicacion.FormularioMovimiento.ruta) {
                if (modeloPrincipal.puedeAcceder(RutasAplicacion.FormularioMovimiento)) PantallaFormularioMovimiento(
                    alVolver = { modeloPrincipal.volver() },
                    alEnviar = { titulo, categoria, monto, tipo ->
                        val guardado = modeloPrincipal.registrarMovimiento(titulo, categoria, monto, tipo)
                        if (guardado) confirmar(if (tipo == com.example.juntavecinal_grupox.model.TipoMovimiento.INGRESO)
                            "Ingreso registrado" else "Gasto registrado")
                        guardado
                    }
                )
            }
        }
    }
}
