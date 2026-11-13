package com.example.juntavecinal_grupox

import com.example.juntavecinal_grupox.model.EstadoReserva
import com.example.juntavecinal_grupox.model.TipoMovimiento
import com.example.juntavecinal_grupox.repository.RepositorioAvisos
import com.example.juntavecinal_grupox.viewmodel.PrincipalViewModel
import org.junit.Assert.*
import org.junit.Test

class PruebasAvisos {
    private fun crearSolicitud(modelo: PrincipalViewModel): String {
        modelo.iniciarSesion("vecino", "Vecino123")
        assertTrue(modelo.solicitarReserva("25/11/2026", "09:00", "Taller de prueba"))
        return modelo.estadoAplicacion.value.reservas.first().id
    }

    @Test
    fun solicitudGeneraAvisosSeparadosParaVecinoYDirectiva() {
        val modelo = PrincipalViewModel()
        crearSolicitud(modelo)
        val avisoVecino = modelo.estadoAplicacion.value.avisos.single()
        assertEquals("Solicitud enviada", avisoVecino.titulo)
        modelo.iniciarSesion("directiva", "Directiva123")
        val avisoDirectiva = modelo.estadoAplicacion.value.avisos.single()
        assertEquals("Nueva solicitud de arriendo", avisoDirectiva.titulo)
        assertNotEquals(avisoVecino.idDestinatario, avisoDirectiva.idDestinatario)
        modelo.iniciarSesion("tesoreria", "Tesoreria123")
        assertTrue(modelo.estadoAplicacion.value.avisos.isEmpty())
    }

    @Test
    fun aprobacionAvisaAlSolicitanteYATesoreria() {
        val modelo = PrincipalViewModel()
        val id = crearSolicitud(modelo)
        modelo.iniciarSesion("directiva", "Directiva123")
        assertTrue(modelo.actualizarEstadoReserva(id, EstadoReserva.APROBADA))
        modelo.iniciarSesion("vecino", "Vecino123")
        assertEquals("Reserva aprobada", modelo.estadoAplicacion.value.avisos.first().titulo)
        modelo.iniciarSesion("tesoreria", "Tesoreria123")
        assertEquals("Arriendo pendiente de pago", modelo.estadoAplicacion.value.avisos.single().titulo)
    }

    @Test
    fun rechazoInformaUnaSolaVez() {
        val modelo = PrincipalViewModel()
        val id = crearSolicitud(modelo)
        modelo.iniciarSesion("directiva", "Directiva123")
        assertTrue(modelo.actualizarEstadoReserva(id, EstadoReserva.RECHAZADA))
        val avisos = modelo.estadoAplicacion.value.avisos
        assertFalse(modelo.actualizarEstadoReserva(id, EstadoReserva.RECHAZADA))
        assertEquals(avisos, modelo.estadoAplicacion.value.avisos)
        modelo.iniciarSesion("vecino", "Vecino123")
        assertEquals(1, modelo.estadoAplicacion.value.avisos.count { it.titulo == "Solicitud rechazada" })
    }

    @Test
    fun pagoSoloInformaCuandoSeRegistraCorrectamente() {
        val modelo = PrincipalViewModel()
        modelo.iniciarSesion("tesoreria", "Tesoreria123")
        assertTrue(modelo.registrarPago("R-001", 15000))
        assertFalse(modelo.registrarPago("R-001", 15000))
        assertEquals(1, modelo.estadoAplicacion.value.avisos.size)
        modelo.iniciarSesion("vecino", "Vecino123")
        assertEquals("Pago registrado", modelo.estadoAplicacion.value.avisos.single().titulo)
    }

    @Test
    fun editarYCancelarNotificanSinGenerarAvisosPorReintentosFallidos() {
        val modelo = PrincipalViewModel()
        val id = crearSolicitud(modelo)
        modelo.abrirEdicionReserva(id)
        assertTrue(modelo.guardarEdicionReserva("26/11/2026", "11:00", "Taller actualizado"))
        assertEquals("Solicitud actualizada", modelo.estadoAplicacion.value.avisos.first().titulo)
        assertTrue(modelo.cancelarReserva(id))
        val avisos = modelo.estadoAplicacion.value.avisos
        assertEquals("Solicitud cancelada", avisos.first().titulo)
        assertFalse(modelo.cancelarReserva(id))
        assertEquals(avisos, modelo.estadoAplicacion.value.avisos)
    }

    @Test
    fun lecturaPermaneceAlCerrarSesionYSoloAfectaAlDestinatario() {
        val modelo = PrincipalViewModel()
        crearSolicitud(modelo)
        val idVecino = modelo.estadoAplicacion.value.avisos.single().id
        modelo.iniciarSesion("directiva", "Directiva123")
        modelo.marcarAvisoLeido(idVecino)
        modelo.marcarTodosLosAvisosLeidos()
        assertEquals(0, modelo.estadoAplicacion.value.avisosSinLeer)
        modelo.cerrarSesion()
        assertTrue(modelo.estadoAplicacion.value.avisos.isEmpty())
        modelo.iniciarSesion("vecino", "Vecino123")
        assertEquals(1, modelo.estadoAplicacion.value.avisosSinLeer)
        modelo.marcarAvisoLeido(idVecino)
        modelo.cerrarSesion()
        modelo.iniciarSesion("vecino", "Vecino123")
        assertEquals(0, modelo.estadoAplicacion.value.avisosSinLeer)
    }

    @Test
    fun movimientoFinancieroInformaADirectivaYTesoreriaYNoAlVecino() {
        val modelo = PrincipalViewModel()
        modelo.iniciarSesion("tesoreria", "Tesoreria123")
        modelo.registrarMovimiento("Agua", "Servicios básicos", 5000, TipoMovimiento.GASTO)
        assertEquals("Gasto registrado", modelo.estadoAplicacion.value.avisos.single().titulo)
        modelo.registrarMovimiento("Agua", "Servicios básicos", 0, TipoMovimiento.GASTO)
        assertEquals(1, modelo.estadoAplicacion.value.avisos.size)
        modelo.iniciarSesion("directiva", "Directiva123")
        assertEquals("Gasto registrado", modelo.estadoAplicacion.value.avisos.single().titulo)
        modelo.iniciarSesion("vecino", "Vecino123")
        assertTrue(modelo.estadoAplicacion.value.avisos.isEmpty())
    }

    @Test
    fun repositorioNoPermiteMarcarAvisosDeOtraCuenta() {
        val repositorio = RepositorioAvisos()
        repositorio.agregarAviso("vecino", "Reserva", "Detalle")
        repositorio.agregarAviso("tesorero", "Pago", "Detalle")
        val aviso = repositorio.obtenerAvisos("vecino").single()
        repositorio.marcarLeido(aviso.id, "tesorero")
        repositorio.marcarTodosLeidos("tesorero")
        assertFalse(repositorio.obtenerAvisos("vecino").single().leido)
        assertTrue(repositorio.obtenerAvisos("tesorero").single().leido)
    }
}
