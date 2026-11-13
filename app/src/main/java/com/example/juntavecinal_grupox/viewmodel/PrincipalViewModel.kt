package com.example.juntavecinal_grupox.viewmodel

import com.example.juntavecinal_grupox.model.ResumenFinanciero
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.lifecycle.ViewModel
import com.example.juntavecinal_grupox.model.EstadoAplicacion
import com.example.juntavecinal_grupox.model.TipoMovimiento
import com.example.juntavecinal_grupox.model.EstadoReserva
import com.example.juntavecinal_grupox.model.RolUsuario
import com.example.juntavecinal_grupox.model.Reserva
import com.example.juntavecinal_grupox.repository.RepositorioAvisos
import com.example.juntavecinal_grupox.repository.RepositorioAcceso
import com.example.juntavecinal_grupox.repository.RepositorioJuntaVecinal
import com.example.juntavecinal_grupox.ui.navegacion.RutasAplicacion
import com.example.juntavecinal_grupox.ui.navegacion.EventoNavegacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class PrincipalViewModel : ViewModel() {
    private val repositorioAvisos = RepositorioAvisos()
    private val repositorioAcceso = RepositorioAcceso()
    private val repositorio = RepositorioJuntaVecinal()
    private val _estadoAplicacion = MutableStateFlow(
        EstadoAplicacion(reservas = repositorio.reservas, movimientos = repositorio.movimientos)
    )
    val estadoAplicacion = _estadoAplicacion.asStateFlow()

    private fun actualizarAvisos() {
        val usuario = _estadoAplicacion.value.usuarioActual ?: return
        _estadoAplicacion.update { it.copy(avisos = repositorioAvisos.obtenerAvisos(usuario.id)) }
    }

    private fun crearAviso(idDestinatario: String?, titulo: String, mensaje: String) {
        if (idDestinatario == null) return
        repositorioAvisos.agregarAviso(idDestinatario, titulo, mensaje)
        actualizarAvisos()
    }

    private fun avisarAlRol(rol: RolUsuario, titulo: String, mensaje: String) {
        crearAviso(repositorioAcceso.obtenerUsuarioPorRol(rol)?.id, titulo, mensaje)
    }

    fun marcarAvisoLeido(id: String) {
        val usuario = _estadoAplicacion.value.usuarioActual ?: return
        repositorioAvisos.marcarLeido(id, usuario.id)
        actualizarAvisos()
    }

    fun marcarTodosLosAvisosLeidos() {
        val usuario = _estadoAplicacion.value.usuarioActual ?: return
        repositorioAvisos.marcarTodosLeidos(usuario.id)
        actualizarAvisos()
    }

    fun cambiarMesFinanciero(desplazamiento: Int) {
        if (_estadoAplicacion.value.usuarioActual == null) return
        _estadoAplicacion.update { it.copy(desplazamientoMes = it.desplazamientoMes + desplazamiento) }
    }

    fun obtenerResumenFinanciero(estado: EstadoAplicacion): ResumenFinanciero {
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

    private val _eventoNavegacion = Channel<EventoNavegacion>(Channel.BUFFERED)
    val eventoNavegacion = _eventoNavegacion.receiveAsFlow()

    fun iniciarSesion(nombreUsuario: String, contrasena: String): Boolean {
        if (nombreUsuario.isBlank() || contrasena.isBlank()) {
            _estadoAplicacion.update { it.copy(errorAcceso = "Ingresa tu usuario y contraseña.") }
            return false
        }
        val usuario = repositorioAcceso.autenticar(nombreUsuario, contrasena)
        if (usuario == null) {
            _estadoAplicacion.update { it.copy(errorAcceso = "Usuario o contraseña incorrectos.") }
            return false
        }
        _estadoAplicacion.value = EstadoAplicacion(
            usuarioActual = usuario,
            avisos = repositorioAvisos.obtenerAvisos(usuario.id),
            reservas = repositorio.reservas,
            movimientos = repositorio.movimientos
        )
        return true
    }

    fun limpiarErrorAcceso() {
        _estadoAplicacion.update { it.copy(errorAcceso = null) }
    }

    fun cerrarSesion() {
        while (_eventoNavegacion.tryReceive().isSuccess) {
            // Descarta los eventos de la sesión anterior.
        }
        _estadoAplicacion.value = EstadoAplicacion(
            reservas = repositorio.reservas,
            movimientos = repositorio.movimientos
        )
    }

    fun puedeAcceder(ruta: RutasAplicacion): Boolean {
        val usuario = _estadoAplicacion.value.usuarioActual ?: return false
        return when (ruta) {
            RutasAplicacion.FormularioReserva -> usuario.rol == RolUsuario.VECINO
            RutasAplicacion.EditarReserva -> obtenerReservaEnEdicion() != null
            RutasAplicacion.FormularioMovimiento -> usuario.rol.puedeRegistrarMovimientos
            else -> true
        }
    }

    fun navegarA(ruta: RutasAplicacion, volverHasta: RutasAplicacion? = null, incluirDestino: Boolean = false) {
        if (!puedeAcceder(ruta)) return
        _eventoNavegacion.trySend(
            EventoNavegacion.IrA(
                ruta = ruta,
                rutaHasta = volverHasta,
                incluirDestino = incluirDestino
            )
        )
    }

    fun volver() {
        if (_estadoAplicacion.value.usuarioActual == null) return
        _eventoNavegacion.trySend(EventoNavegacion.Volver)
    }

    fun solicitarReserva(fecha: String, hora: String, motivo: String): Boolean {
        val usuario = _estadoAplicacion.value.usuarioActual ?: return false
        if (usuario.rol != RolUsuario.VECINO) return false
        val agregada = repositorio.solicitarReserva(usuario.nombre, fecha, hora, motivo)
        if (agregada) {
            _estadoAplicacion.update { it.copy(reservas = repositorio.reservas) }
            crearAviso(usuario.id, "Solicitud enviada", "Tu solicitud de " + motivo + " para el " + fecha + " a las " + hora + " está pendiente.")
            avisarAlRol(RolUsuario.DIRECTIVA, "Nueva solicitud de arriendo",
                usuario.nombre + " solicitó la sede para el " + fecha + " a las " + hora + ".")
            navegarA(RutasAplicacion.Reservas, volverHasta = RutasAplicacion.Inicio)
        }
        return agregada
    }

    fun puedeModificarReserva(reserva: Reserva): Boolean {
        val usuario = _estadoAplicacion.value.usuarioActual ?: return false
        return usuario.rol == RolUsuario.VECINO &&
            reserva.nombreSolicitante == usuario.nombre && reserva.estado == EstadoReserva.PENDIENTE
    }

    fun abrirEdicionReserva(id: String) {
        val reserva = _estadoAplicacion.value.reservas.find { it.id == id } ?: return
        if (!puedeModificarReserva(reserva)) return
        _estadoAplicacion.update { it.copy(idReservaEnEdicion = id) }
        navegarA(RutasAplicacion.EditarReserva)
    }

    fun obtenerReservaEnEdicion(): Reserva? {
        val estado = _estadoAplicacion.value
        return estado.reservas.find { it.id == estado.idReservaEnEdicion && puedeModificarReserva(it) }
    }

    fun guardarEdicionReserva(fecha: String, hora: String, motivo: String): Boolean {
        val reserva = obtenerReservaEnEdicion() ?: return false
        val nombre = _estadoAplicacion.value.usuarioActual?.nombre ?: return false
        if (!repositorio.editarReserva(reserva.id, nombre, fecha, hora, motivo)) return false
        _estadoAplicacion.update { it.copy(reservas = repositorio.reservas, idReservaEnEdicion = null) }
        crearAviso(_estadoAplicacion.value.usuarioActual?.id, "Solicitud actualizada",
            "Actualizaste tu solicitud de " + motivo + " para el " + fecha + " a las " + hora + ".")
        avisarAlRol(RolUsuario.DIRECTIVA, "Solicitud actualizada",
            nombre + " modificó su solicitud. Revisa la nueva fecha y horario.")
        navegarA(RutasAplicacion.Reservas, volverHasta = RutasAplicacion.Inicio)
        return true
    }

    fun cancelarReserva(id: String): Boolean {
        val reserva = _estadoAplicacion.value.reservas.find { it.id == id } ?: return false
        if (!puedeModificarReserva(reserva)) return false
        val nombre = _estadoAplicacion.value.usuarioActual?.nombre ?: return false
        if (!repositorio.cancelarReserva(id, nombre)) return false
        _estadoAplicacion.update { it.copy(reservas = repositorio.reservas, idReservaEnEdicion = null) }
        crearAviso(_estadoAplicacion.value.usuarioActual?.id, "Solicitud cancelada",
            "Cancelaste tu solicitud de " + reserva.motivo + " del " + reserva.fecha + ".")
        avisarAlRol(RolUsuario.DIRECTIVA, "Solicitud cancelada",
            nombre + " canceló su solicitud de " + reserva.motivo + " del " + reserva.fecha + ".")
        return true
    }

    fun actualizarEstadoReserva(id: String, estado: EstadoReserva): Boolean {
        if (_estadoAplicacion.value.usuarioActual?.rol != RolUsuario.DIRECTIVA) return false
        val actualizada = repositorio.actualizarEstadoReserva(id, estado)
        _estadoAplicacion.update { it.copy(reservas = repositorio.reservas) }
        if (actualizada) {
            val reserva = repositorio.reservas.first { it.id == id }
            val titulo = if (estado == EstadoReserva.APROBADA) "Reserva aprobada" else "Solicitud rechazada"
            val mensaje = reserva.motivo + " · " + reserva.fecha + " · " + reserva.hora
            crearAviso(repositorioAcceso.obtenerUsuarioPorNombre(reserva.nombreSolicitante)?.id, titulo, mensaje)
            crearAviso(_estadoAplicacion.value.usuarioActual?.id, titulo, mensaje)
            if (estado == EstadoReserva.APROBADA) {
                avisarAlRol(RolUsuario.TESORERIA, "Arriendo pendiente de pago", mensaje)
            }
        }
        return actualizada
    }

    fun registrarPago(id: String, monto: Long): Boolean {
        if (_estadoAplicacion.value.usuarioActual?.rol != RolUsuario.TESORERIA) return false
        val pagada = repositorio.registrarPago(id, monto)
        _estadoAplicacion.update { it.copy(movimientos = repositorio.movimientos) }
        if (pagada) {
            val reserva = repositorio.reservas.first { it.id == id }
            val mensaje = "Pago de " + monto + " pesos registrado para " + reserva.motivo + " del " + reserva.fecha + "."
            crearAviso(repositorioAcceso.obtenerUsuarioPorNombre(reserva.nombreSolicitante)?.id, "Pago registrado", mensaje)
            crearAviso(_estadoAplicacion.value.usuarioActual?.id, "Pago registrado", mensaje)
        }
        return pagada
    }

    fun registrarMovimiento(titulo: String, categoria: String, monto: Long, tipo: TipoMovimiento): Boolean {
        if (_estadoAplicacion.value.usuarioActual?.rol?.puedeRegistrarMovimientos != true) return false
        if (!repositorio.registrarMovimiento(titulo.trim(), categoria.trim(), monto, tipo)) return false
        _estadoAplicacion.update { it.copy(movimientos = repositorio.movimientos) }
        val tituloAviso = if (tipo == TipoMovimiento.INGRESO) "Ingreso registrado" else "Gasto registrado"
        val mensaje = titulo + " · " + categoria + " · " + monto + " pesos."
        crearAviso(_estadoAplicacion.value.usuarioActual?.id, tituloAviso, mensaje)
        avisarAlRol(RolUsuario.DIRECTIVA, tituloAviso, mensaje)
        navegarA(RutasAplicacion.Finanzas, volverHasta = RutasAplicacion.Inicio)
        return true
    }
}
