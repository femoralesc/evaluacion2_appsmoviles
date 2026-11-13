package com.example.juntavecinal_grupox

import com.example.juntavecinal_grupox.model.TipoMovimiento
import com.example.juntavecinal_grupox.model.interpretarMontoEnPesos
import com.example.juntavecinal_grupox.viewmodel.PrincipalViewModel
import org.junit.Assert.*
import org.junit.Test

class PruebasValidacionMontos {
    @Test
    fun aceptaPesosEnterosConOSinSeparadorDeMiles() {
        assertEquals(15000L, interpretarMontoEnPesos("15000"))
        assertEquals(15000L, interpretarMontoEnPesos("15.000"))
        assertEquals(1500000L, interpretarMontoEnPesos("1.500.000"))
        assertEquals(15000L, interpretarMontoEnPesos(" 15.000 "))
    }

    @Test
    fun rechazaVaciosCeroDecimalesYSeparadoresIncorrectos() {
        for (texto in listOf("", " ", "0", "000", "-15000", "15..000", "15.00", "15,50", "abc", "15 000")) {
            assertNull(texto, interpretarMontoEnPesos(texto))
        }
    }

    @Test
    fun rechazaMontosFueraDelRangoAdmitido() {
        assertNull(interpretarMontoEnPesos("99999999999999999999"))
        assertNull(interpretarMontoEnPesos("99.999.999.999.999.999.999"))
    }

    @Test
    fun formularioNoRecibeExitoSiElRegistroFueRechazado() {
        val modelo = PrincipalViewModel()
        modelo.iniciarSesion("tesoreria", "Tesoreria123")
        val original = modelo.estadoAplicacion.value
        assertFalse(modelo.registrarMovimiento("", "Servicios", 15000, TipoMovimiento.GASTO))
        assertFalse(modelo.registrarMovimiento("Agua", " ", 15000, TipoMovimiento.GASTO))
        assertFalse(modelo.registrarMovimiento("Agua", "Servicios", 0, TipoMovimiento.GASTO))
        assertEquals(original, modelo.estadoAplicacion.value)
        assertTrue(modelo.registrarMovimiento("Agua", "Servicios", 15000, TipoMovimiento.GASTO))
        assertEquals(15000L, modelo.estadoAplicacion.value.movimientos.first().monto)
    }
}
