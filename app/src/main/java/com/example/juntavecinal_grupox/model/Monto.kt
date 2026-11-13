package com.example.juntavecinal_grupox.model

fun interpretarMontoEnPesos(texto: String): Long? {
    val monto = texto.trim()
    val formatoValido = monto.matches(Regex("[0-9]+|[0-9]{1,3}(\\.[0-9]{3})+"))
    if (!formatoValido) return null
    return monto.replace(".", "").toLongOrNull()?.takeIf { it > 0 }
}
