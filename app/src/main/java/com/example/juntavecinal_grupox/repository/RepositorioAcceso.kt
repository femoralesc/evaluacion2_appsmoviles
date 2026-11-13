package com.example.juntavecinal_grupox.repository

import com.example.juntavecinal_grupox.model.RolUsuario
import com.example.juntavecinal_grupox.model.Usuario

// Cuentas ficticias para presentar el Front de la aplicación.
class RepositorioAcceso {
    private data class CuentaDemostracion(val usuario: Usuario, val contrasena: String)

    private val cuentas = listOf(
        CuentaDemostracion(
            Usuario("U-001", "vecino", RolUsuario.VECINO.nombreDemostracion, RolUsuario.VECINO),
            "Vecino123"
        ),
        CuentaDemostracion(
            Usuario("U-002", "directiva", RolUsuario.DIRECTIVA.nombreDemostracion, RolUsuario.DIRECTIVA),
            "Directiva123"
        ),
        CuentaDemostracion(
            Usuario("U-003", "tesoreria", RolUsuario.TESORERIA.nombreDemostracion, RolUsuario.TESORERIA),
            "Tesoreria123"
        )
    )

    fun obtenerUsuarioPorRol(rol: RolUsuario): Usuario? =
        cuentas.find { it.usuario.rol == rol }?.usuario

    fun obtenerUsuarioPorNombre(nombre: String): Usuario? =
        cuentas.find { it.usuario.nombre == nombre }?.usuario

    fun autenticar(nombreUsuario: String, contrasena: String): Usuario? {
        return cuentas.find {
            it.usuario.nombreUsuario == nombreUsuario.trim().lowercase() &&
                it.contrasena == contrasena
        }?.usuario
    }
}
