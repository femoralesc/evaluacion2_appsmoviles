package com.example.juntavecinal_grupox

import com.example.juntavecinal_grupox.model.RolUsuario
import com.example.juntavecinal_grupox.repository.RepositorioAcceso
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PruebasRepositorioAcceso {
    private val repositorio = RepositorioAcceso()

    @Test
    fun cadaCuentaObtieneSuRol() {
        assertEquals(RolUsuario.VECINO, repositorio.autenticar("vecino", "Vecino123")?.rol)
        assertEquals(RolUsuario.DIRECTIVA, repositorio.autenticar("directiva", "Directiva123")?.rol)
        assertEquals(RolUsuario.TESORERIA, repositorio.autenticar("tesoreria", "Tesoreria123")?.rol)
    }

    @Test
    fun rechazaContrasenaDeOtraCuenta() {
        assertNull(repositorio.autenticar("vecino", "Tesoreria123"))
        assertNull(repositorio.autenticar("directiva", "Vecino123"))
    }

    @Test
    fun rechazaUsuarioDesconocidoYContrasenaIncorrecta() {
        assertNull(repositorio.autenticar("administrador", "Directiva123"))
        assertNull(repositorio.autenticar("tesoreria", "incorrecta"))
        assertNull(repositorio.autenticar("tesoreria", ""))
    }

    @Test
    fun usuarioAdmiteEspaciosPeroContrasenaDistingueMayusculas() {
        assertEquals("vecino", repositorio.autenticar(" VECINO ", "Vecino123")?.nombreUsuario)
        assertNull(repositorio.autenticar("vecino", "vecino123"))
    }
}
