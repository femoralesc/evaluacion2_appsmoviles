package com.example.juntavecinal_grupox

import com.example.juntavecinal_grupox.model.EstadoReserva
import com.example.juntavecinal_grupox.model.RolUsuario
import com.example.juntavecinal_grupox.repository.RepositorioJuntaVecinal
import com.example.juntavecinal_grupox.ui.navegacion.RutasAplicacion
import com.example.juntavecinal_grupox.viewmodel.PrincipalViewModel
import org.junit.Assert.*
import org.junit.Test

class PruebasGestionReservas {
    private val nombreVecino = RolUsuario.VECINO.nombreDemostracion

    private fun crearSolicitud(repositorio: RepositorioJuntaVecinal): String {
        assertTrue(repositorio.solicitarReserva(nombreVecino, "25/11/2026", "09:00", "Ensayo"))
        return repositorio.reservas.first().id
    }

    @Test
    fun editarConservaIdentidadPropietarioYEstadoPendiente() {
        val repositorio = RepositorioJuntaVecinal()
        val id = crearSolicitud(repositorio)
        val cantidad = repositorio.reservas.size
        val movimientos = repositorio.movimientos

        assertTrue(repositorio.editarReserva(id, nombreVecino, "26/11/2026", "11:00", "Taller"))
        val editada = repositorio.reservas.first()
        assertEquals(id, editada.id)
        assertEquals(nombreVecino, editada.nombreSolicitante)
        assertEquals(EstadoReserva.PENDIENTE, editada.estado)
        assertEquals("26/11/2026", editada.fecha)
        assertEquals("11:00", editada.hora)
        assertEquals("Taller", editada.motivo)
        assertEquals(cantidad, repositorio.reservas.size)
        assertEquals(movimientos, repositorio.movimientos)
    }

    @Test
    fun cambioABloqueOcupadoODatosIncompletosConservaSolicitudOriginal() {
        val repositorio = RepositorioJuntaVecinal()
        val id = crearSolicitud(repositorio)
        val originales = repositorio.reservas

        assertFalse(repositorio.editarReserva(id, nombreVecino, "14/11/2026", "16:00", "Taller"))
        assertFalse(repositorio.editarReserva(id, nombreVecino, "", "09:00", "Taller"))
        assertFalse(repositorio.editarReserva(id, nombreVecino, "26/11/2026", "10:25", "Taller"))
        assertFalse(repositorio.editarReserva(id, nombreVecino, "26/11/2026", "09:00", " "))
        assertEquals(originales, repositorio.reservas)
    }

    @Test
    fun cancelarConservaHistorialEImpideRepetirEditarAprobarOPagar() {
        val repositorio = RepositorioJuntaVecinal()
        val id = crearSolicitud(repositorio)
        val cantidad = repositorio.reservas.size

        assertTrue(repositorio.cancelarReserva(id, nombreVecino))
        assertEquals(cantidad, repositorio.reservas.size)
        assertEquals(EstadoReserva.CANCELADA, repositorio.reservas.first().estado)
        assertFalse(repositorio.cancelarReserva(id, nombreVecino))
        assertFalse(repositorio.editarReserva(id, nombreVecino, "26/11/2026", "09:00", "Taller"))
        assertFalse(repositorio.actualizarEstadoReserva(id, EstadoReserva.APROBADA))
        assertFalse(repositorio.registrarPago(id, 15000))
    }

    @Test
    fun otroSolicitanteNoPuedeEditarNiCancelar() {
        val repositorio = RepositorioJuntaVecinal()
        val id = crearSolicitud(repositorio)
        val originales = repositorio.reservas

        assertFalse(repositorio.editarReserva(id, "Otra persona", "26/11/2026", "09:00", "Taller"))
        assertFalse(repositorio.cancelarReserva(id, "Otra persona"))
        assertEquals(originales, repositorio.reservas)
    }

    @Test
    fun solicitudResueltaMientrasSeEditabaNoAceptaCambios() {
        for (estado in listOf(EstadoReserva.APROBADA, EstadoReserva.RECHAZADA)) {
            val repositorio = RepositorioJuntaVecinal()
            val id = crearSolicitud(repositorio)
            assertTrue(repositorio.actualizarEstadoReserva(id, estado))
            assertFalse(repositorio.editarReserva(id, nombreVecino, "26/11/2026", "09:00", "Taller"))
            assertFalse(repositorio.cancelarReserva(id, nombreVecino))
        }
    }

    @Test
    fun viewModelRestringeEdicionYCancelacionAlVecinoPropietario() {
        val modelo = PrincipalViewModel()
        assertFalse(modelo.cancelarReserva("R-002"))
        modelo.iniciarSesion("vecino", "Vecino123")
        modelo.abrirEdicionReserva("R-002")
        assertFalse(modelo.puedeAcceder(RutasAplicacion.EditarReserva))
        assertFalse(modelo.cancelarReserva("R-002"))
        assertFalse(modelo.cancelarReserva("R-001"))
        modelo.solicitarReserva("25/11/2026", "09:00", "Ensayo")
        val id = modelo.estadoAplicacion.value.reservas.first().id
        modelo.abrirEdicionReserva(id)
        assertTrue(modelo.puedeAcceder(RutasAplicacion.EditarReserva))
        assertTrue(modelo.guardarEdicionReserva("26/11/2026", "11:00", "Taller"))
        assertNull(modelo.obtenerReservaEnEdicion())

        modelo.iniciarSesion("directiva", "Directiva123")
        assertFalse(modelo.cancelarReserva(id))
        modelo.abrirEdicionReserva(id)
        assertNull(modelo.obtenerReservaEnEdicion())
        modelo.iniciarSesion("tesoreria", "Tesoreria123")
        assertFalse(modelo.cancelarReserva(id))
        modelo.iniciarSesion("vecino", "Vecino123")
        modelo.abrirEdicionReserva(id)
        modelo.cerrarSesion()
        assertFalse(modelo.guardarEdicionReserva("27/11/2026", "09:00", "Otro taller"))
        assertFalse(modelo.cancelarReserva(id))
    }

    @Test
    fun vecinoPuedeCancelarSuSolicitudDesdeElViewModel() {
        val modelo = PrincipalViewModel()
        modelo.iniciarSesion("vecino", "Vecino123")
        modelo.solicitarReserva("25/11/2026", "09:00", "Ensayo")
        val id = modelo.estadoAplicacion.value.reservas.first().id

        assertTrue(modelo.cancelarReserva(id))
        assertEquals(EstadoReserva.CANCELADA, modelo.estadoAplicacion.value.reservas.first().estado)
        assertFalse(modelo.puedeModificarReserva(modelo.estadoAplicacion.value.reservas.first()))
    }
}
