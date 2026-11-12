package com.example.juntavecinal_grupox.viewmodel

import com.example.juntavecinal_grupox.model.ResumenFinanciero
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juntavecinal_grupox.model.EstadoAplicacion
import com.example.juntavecinal_grupox.model.TipoMovimiento
import com.example.juntavecinal_grupox.model.EstadoReserva
import com.example.juntavecinal_grupox.model.RolUsuario
import com.example.juntavecinal_grupox.repository.RepositorioJuntaVecinal
import com.example.juntavecinal_grupox.ui.navegacion.RutasAplicacion
import com.example.juntavecinal_grupox.ui.navegacion.EventoNavegacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class PrincipalViewModel : ViewModel() {
    private val repositorio = RepositorioJuntaVecinal()
    private val _estadoAplicacion = MutableStateFlow(
        EstadoAplicacion(reservas = repositorio.reservas, movimientos = repositorio.movimientos)
    )
    val estadoAplicacion = _estadoAplicacion.asStateFlow()

    val resumenFinanciero = estadoAplicacion.map { crearResumenFinanciero(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),
            crearResumenFinanciero(_estadoAplicacion.value))

    fun cambiarMesFinanciero(desplazamiento: Int) {
        _estadoAplicacion.update { it.copy(desplazamientoMes = it.desplazamientoMes + desplazamiento) }
    }

    private fun crearResumenFinanciero(estado: EstadoAplicacion): ResumenFinanciero {
        val mes = Calendar.getInstance().apply { add(Calendar.MONTH, estado.desplazamientoMes) }.time
        val claveMes = SimpleDateFormat("MM/yyyy", Locale.ROOT).format(mes)
        val nombreMes = SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("es-CL")).format(mes)
        val movimientos = estado.movimientos.filter { it.fecha.endsWith("/" + claveMes) }
        val ingresos = movimientos.filter { it.tipo == TipoMovimiento.INGRESO }
        val gastos = movimientos.filter { it.tipo == TipoMovimiento.GASTO }
        val perfil = estado.perfilActivo
        return ResumenFinanciero(
            titulo = perfil.tituloFinanciero,
            nombreMes = nombreMes,
            totalIngresos = ingresos.sumOf { it.monto },
            totalGastos = gastos.sumOf { it.monto },
            ingresosPorCategoria = ingresos.groupBy { it.categoria }.mapValues { (_, lista) -> lista.sumOf { it.monto } },
            gastosPorCategoria = gastos.groupBy { it.categoria }.mapValues { (_, lista) -> lista.sumOf { it.monto } },
            movimientos = if (perfil.puedeVerDetalleFinanciero) movimientos else emptyList(),
            puedeVerDetalle = perfil.puedeVerDetalleFinanciero,
            puedeRegistrar = perfil.puedeRegistrarMovimientos
        )
    }

    private val _eventoNavegacion = Channel<EventoNavegacion>()
    val eventoNavegacion = _eventoNavegacion.receiveAsFlow()

    fun navegarA(ruta: RutasAplicacion, volverHasta: RutasAplicacion? = null, incluirDestino: Boolean = false) {
        if (ruta == RutasAplicacion.FormularioMovimiento &&
            !_estadoAplicacion.value.perfilActivo.puedeRegistrarMovimientos) return
        viewModelScope.launch {
            _eventoNavegacion.send(
                EventoNavegacion.IrA(
                    ruta = ruta,
                    rutaHasta = volverHasta,
                    incluirDestino = incluirDestino
                )
            )
        }
    }

    fun volver() {
        viewModelScope.launch {
            _eventoNavegacion.send(EventoNavegacion.Volver)
        }
    }

    fun seleccionarPerfil(perfil: RolUsuario) {
        _estadoAplicacion.update { it.copy(perfilActivo = perfil) }
    }

    fun solicitarReserva(fecha: String, hora: String, motivo: String): Boolean {
        val agregada = repositorio.solicitarReserva(_estadoAplicacion.value.perfilActivo.nombreDemostracion, fecha, hora, motivo)
        if (agregada) {
            _estadoAplicacion.update { it.copy(reservas = repositorio.reservas) }
            navegarA(RutasAplicacion.Reservas, volverHasta = RutasAplicacion.Inicio)
        }
        return agregada
    }

    fun actualizarEstadoReserva(id: String, estado: EstadoReserva): Boolean {
        if (_estadoAplicacion.value.perfilActivo != RolUsuario.DIRECTIVA) return false
        val actualizada = repositorio.actualizarEstadoReserva(id, estado)
        _estadoAplicacion.update { it.copy(reservas = repositorio.reservas) }
        return actualizada
    }

    fun registrarPago(id: String, monto: Long): Boolean {
        if (_estadoAplicacion.value.perfilActivo != RolUsuario.TESORERIA) return false
        val pagada = repositorio.registrarPago(id, monto)
        _estadoAplicacion.update { it.copy(movimientos = repositorio.movimientos) }
        return pagada
    }

    fun registrarMovimiento(titulo: String, categoria: String, monto: Long, tipo: TipoMovimiento) {
        if (!_estadoAplicacion.value.perfilActivo.puedeRegistrarMovimientos) return
        repositorio.registrarMovimiento(titulo, categoria, monto, tipo)
        _estadoAplicacion.update { it.copy(movimientos = repositorio.movimientos) }
        volver()
    }
}
