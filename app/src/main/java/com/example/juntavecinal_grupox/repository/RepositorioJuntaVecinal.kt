package com.example.juntavecinal_grupox.repository

import com.example.juntavecinal_grupox.model.MovimientoFinanciero
import com.example.juntavecinal_grupox.model.TipoMovimiento
import com.example.juntavecinal_grupox.model.Reserva
import com.example.juntavecinal_grupox.model.EstadoReserva
import com.example.juntavecinal_grupox.model.RolUsuario
import com.example.juntavecinal_grupox.model.estaOcupado
import com.example.juntavecinal_grupox.model.bloquesHorarios
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class RepositorioJuntaVecinal {
    var reservas = reservasDeEjemplo()
        private set

    var movimientos = movimientosDeEjemplo()
        private set

    fun solicitarReserva(nombreSolicitante: String, fecha: String, hora: String, motivo: String): Boolean {
        if (hora !in bloquesHorarios || motivo.isBlank() || reservas.estaOcupado(fecha, hora)) return false
        val reserva = Reserva(
            id = UUID.randomUUID().toString(),
            nombreSolicitante = nombreSolicitante,
            fecha = fecha,
            hora = hora,
            motivo = motivo,
            estado = EstadoReserva.PENDIENTE
        )

        reservas = listOf(reserva) + reservas
        return true
    }

    fun actualizarEstadoReserva(id: String, estado: EstadoReserva): Boolean {
        val solicitud = reservas.find { it.id == id && it.estado == EstadoReserva.PENDIENTE } ?: return false
        if (estado == EstadoReserva.APROBADA && reservas.estaOcupado(solicitud.fecha, solicitud.hora)) return false
        reservas = reservas.map { reserva ->
            if (reserva.id == id) reserva.copy(estado = estado) else reserva
        }
        return true
    }

    fun registrarPago(id: String, monto: Long): Boolean {
        val reserva = reservas.find { it.id == id && it.estado == EstadoReserva.APROBADA } ?: return false
        if (monto <= 0 || movimientos.any { it.idReserva == id }) return false
        registrarMovimiento("Arriendo · " + reserva.motivo, "Pago de arriendo", monto, TipoMovimiento.INGRESO, id)
        return true
    }

    fun registrarMovimiento(titulo: String, categoria: String, monto: Long, tipo: TipoMovimiento, idReserva: String? = null) {
        if (monto <= 0) return

        val movimiento = MovimientoFinanciero(
            id = UUID.randomUUID().toString(),
            titulo = titulo,
            categoria = categoria,
            monto = monto,
            fecha = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("es-CL")).format(Date()),
            tipo = tipo,
            idReserva = idReserva
        )

        movimientos = listOf(movimiento) + movimientos
    }
}

private fun reservasDeEjemplo() = listOf(
    Reserva(
        id = "R-001",
        nombreSolicitante = RolUsuario.VECINO.nombreDemostracion,
        fecha = "14/11/2026",
        hora = "16:00",
        motivo = "Taller comunitario",
        estado = EstadoReserva.APROBADA
    ),
    Reserva(
        id = "R-002",
        nombreSolicitante = "Vecino Demo 2",
        fecha = "18/11/2026",
        hora = "18:30",
        motivo = "Reunión vecinal",
        estado = EstadoReserva.PENDIENTE
    ),
    Reserva(
        id = "R-003",
        nombreSolicitante = "Vecina Demo 3",
        fecha = "21/11/2026",
        hora = "15:00",
        motivo = "Actividad familiar",
        estado = EstadoReserva.PENDIENTE
    )
)

private fun movimientosDeEjemplo() = listOf(
    MovimientoFinanciero(
        id = "M-001",
        titulo = "Arriendo de sede",
        categoria = "Ingreso por arriendo",
        monto = 35000,
        fecha = "04/11/2026",
        tipo = TipoMovimiento.INGRESO
    ),
    MovimientoFinanciero(
        id = "M-002",
        titulo = "Consumo eléctrico",
        categoria = "Servicios básicos",
        monto = 18500,
        fecha = "03/11/2026",
        tipo = TipoMovimiento.GASTO
    ),
    MovimientoFinanciero(
        id = "M-003",
        titulo = "Arriendo de sede",
        categoria = "Ingreso por arriendo",
        monto = 30000,
        fecha = "31/10/2026",
        tipo = TipoMovimiento.INGRESO
    )
)
