package com.rentaya.app.data.model

data class User(
    val email: String,
    val name: String,
    val phone: String,
    val password: String,
    val emailConfirmed: Boolean = true
)

data class ResultadoAuth(
    val usuario: User,
    val requiereConfirmacion: Boolean = false
)

class CorreoNoConfirmadoException(
    val correo: String,
    mensaje: String = "Debes confirmar tu correo antes de continuar. Revisa tu bandeja de entrada."
) : Exception(mensaje)
