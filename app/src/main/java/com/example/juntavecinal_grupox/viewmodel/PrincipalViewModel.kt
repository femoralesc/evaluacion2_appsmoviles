package com.example.juntavecinal_grupox.viewmodel

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

    private val _eventoNavegacion = Channel<EventoNavegacion>()
    val eventoNavegacion = _eventoNavegacion.receiveAsFlow()

    fun navegarA(ruta: RutasAplicacion, volverHasta: RutasAplicacion? = null, incluirDestino: Boolean = false) {
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
        repositorio.registrarMovimiento(titulo, categoria, monto, tipo)
        _estadoAplicacion.update { it.copy(movimientos = repositorio.movimientos) }
        volver()
    }
}
