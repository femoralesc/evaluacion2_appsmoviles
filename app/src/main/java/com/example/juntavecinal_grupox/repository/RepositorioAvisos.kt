package com.example.juntavecinal_grupox.repository

import com.example.juntavecinal_grupox.model.Aviso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class RepositorioAvisos {
    private var avisos: List<Aviso> = emptyList()

    fun agregarAviso(idDestinatario: String, titulo: String, mensaje: String) {
        val aviso = Aviso(
            id = UUID.randomUUID().toString(),
            idDestinatario = idDestinatario,
            titulo = titulo,
            mensaje = mensaje,
            fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("es-CL")).format(Date())
        )
        avisos = listOf(aviso) + avisos
    }

    fun obtenerAvisos(idUsuario: String): List<Aviso> =
        avisos.filter { it.idDestinatario == idUsuario }

    fun marcarLeido(idAviso: String, idUsuario: String) {
        avisos = avisos.map {
            if (it.id == idAviso && it.idDestinatario == idUsuario) it.copy(leido = true) else it
        }
    }

    fun marcarTodosLeidos(idUsuario: String) {
        avisos = avisos.map {
            if (it.idDestinatario == idUsuario) it.copy(leido = true) else it
        }
    }
}
