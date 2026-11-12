package com.example.juntavecinal_grupox

import com.example.juntavecinal_grupox.model.EstadoReserva
import com.example.juntavecinal_grupox.model.TipoMovimiento
import com.example.juntavecinal_grupox.ui.navegacion.RutasAplicacion
import com.example.juntavecinal_grupox.viewmodel.PrincipalViewModel
import org.junit.Assert.*
import org.junit.Test

class PruebasAccesoPorPerfil {
    @Test
    fun sinSesionNoPermiteNavegarNiModificarDatos() {
        val modelo = PrincipalViewModel()
        val inicial = modelo.estadoAplicacion.value

        assertFalse(modelo.puedeAcceder(RutasAplicacion.Inicio))
        assertFalse(modelo.solicitarReserva("25/11/2026", "09:00", "Ensayo"))
        assertFalse(modelo.actualizarEstadoReserva("R-002", EstadoReserva.APROBADA))
        assertFalse(modelo.registrarPago("R-001", 15000))
        modelo.registrarMovimiento("Prueba", "Arriendo", 15000, TipoMovimiento.INGRESO)

        assertEquals(inicial, modelo.estadoAplicacion.value)
    }

    @Test
    fun vecinoConsultaResumenYSolicitaSuReserva() {
        val modelo = PrincipalViewModel()
        modelo.iniciarSesion("vecino", "Vecino123")
        val movimientos = modelo.estadoAplicacion.value.movimientos

        assertFalse(modelo.puedeAcceder(RutasAplicacion.FormularioMovimiento))
        assertFalse(modelo.actualizarEstadoReserva("R-002", EstadoReserva.APROBADA))
        assertFalse(modelo.registrarPago("R-001", 15000))
        modelo.registrarMovimiento("Prueba", "Arriendo", 15000, TipoMovimiento.INGRESO)
        assertEquals(movimientos, modelo.estadoAplicacion.value.movimientos)
        assertTrue(modelo.obtenerResumenFinanciero(modelo.estadoAplicacion.value).movimientos.isEmpty())
        assertTrue(modelo.solicitarReserva("25/11/2026", "09:00", "Ensayo"))
        assertEquals(modelo.estadoAplicacion.value.usuarioActual?.nombre,
            modelo.estadoAplicacion.value.reservas.first().nombreSolicitante)
    }

    @Test
    fun directivaApruebaReservasPeroNoRegistraDinero() {
        val modelo = PrincipalViewModel()
        modelo.iniciarSesion("directiva", "Directiva123")
        val movimientos = modelo.estadoAplicacion.value.movimientos

        assertTrue(modelo.actualizarEstadoReserva("R-002", EstadoReserva.APROBADA))
        assertFalse(modelo.puedeAcceder(RutasAplicacion.FormularioMovimiento))
        assertFalse(modelo.solicitarReserva("25/11/2026", "09:00", "Ensayo"))
        assertFalse(modelo.registrarPago("R-001", 15000))
        modelo.registrarMovimiento("Prueba", "Arriendo", 15000, TipoMovimiento.INGRESO)
        assertEquals(movimientos, modelo.estadoAplicacion.value.movimientos)
        assertTrue(modelo.obtenerResumenFinanciero(modelo.estadoAplicacion.value).puedeVerDetalle)
    }

    @Test
    fun tesoreriaRegistraPagoYMovimientoSinAprobarReservas() {
        val modelo = PrincipalViewModel()
        modelo.iniciarSesion("tesoreria", "Tesoreria123")
        val cantidadInicial = modelo.estadoAplicacion.value.movimientos.size

        assertTrue(modelo.puedeAcceder(RutasAplicacion.FormularioMovimiento))
        assertFalse(modelo.puedeAcceder(RutasAplicacion.FormularioReserva))
        assertFalse(modelo.actualizarEstadoReserva("R-002", EstadoReserva.APROBADA))
        assertTrue(modelo.registrarPago("R-001", 15000))
        assertFalse(modelo.registrarPago("R-001", 15000))
        modelo.registrarMovimiento("Consumo", "Servicios básicos", 5000, TipoMovimiento.GASTO)
        assertEquals(cantidadInicial + 2, modelo.estadoAplicacion.value.movimientos.size)
    }

    @Test
    fun cerrarSesionRevocaLosPermisosYDescartaElDetalleFinanciero() {
        val modelo = PrincipalViewModel()
        modelo.iniciarSesion("tesoreria", "Tesoreria123")
        modelo.cerrarSesion()

        assertNull(modelo.estadoAplicacion.value.usuarioActual)
        assertFalse(modelo.puedeAcceder(RutasAplicacion.Finanzas))
        assertFalse(modelo.registrarPago("R-001", 15000))
        modelo.iniciarSesion("vecino", "Vecino123")
        val resumen = modelo.obtenerResumenFinanciero(modelo.estadoAplicacion.value)
        assertFalse(resumen.puedeRegistrar)
        assertFalse(resumen.puedeVerDetalle)
        assertTrue(resumen.movimientos.isEmpty())
    }
}
