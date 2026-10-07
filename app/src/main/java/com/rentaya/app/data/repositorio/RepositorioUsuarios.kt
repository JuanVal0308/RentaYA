package com.rentaya.app.data.repositorio

import com.rentaya.app.data.SampleData
import com.rentaya.app.data.model.ResultadoAuth
import com.rentaya.app.data.model.User
import com.rentaya.app.data.remoto.ClienteSupabase

/**
 * Autenticación: Supabase Auth si hay claves; si no, usuarios en memoria (SampleData).
 */
object RepositorioUsuarios {

    fun usaSupabase(): Boolean = ClienteSupabase.estaConfigurado

    suspend fun iniciarSesion(correo: String, clave: String): Result<User> {
        if (ClienteSupabase.estaConfigurado) {
            val remoto = ClienteSupabase.iniciarSesion(correo, clave)
            if (remoto.isSuccess) return remoto
            // Fallback local solo si el remoto no es "correo sin confirmar".
            if (remoto.exceptionOrNull() is com.rentaya.app.data.model.CorreoNoConfirmadoException) {
                return remoto
            }
            val local = SampleData.validateLogin(correo, clave)
            if (local != null) return Result.success(local)
            return remoto
        }
        val local = SampleData.validateLogin(correo, clave)
        return if (local != null) {
            Result.success(local)
        } else {
            Result.failure(Exception("Correo o contraseña incorrectos"))
        }
    }

    suspend fun registrar(
        nombre: String,
        correo: String,
        telefono: String,
        clave: String
    ): Result<ResultadoAuth> {
        if (ClienteSupabase.estaConfigurado) {
            return ClienteSupabase.registrar(nombre, correo, telefono, clave)
        }
        if (SampleData.isEmailRegistered(correo)) {
            return Result.failure(Exception("Este correo ya está registrado"))
        }
        val usuario = User(correo, nombre, telefono, clave, emailConfirmed = true)
        SampleData.registerUser(usuario)
        return Result.success(ResultadoAuth(usuario, requiereConfirmacion = false))
    }

    suspend fun reenviarConfirmacion(correo: String): Result<Unit> {
        if (!ClienteSupabase.estaConfigurado) {
            return Result.success(Unit)
        }
        return ClienteSupabase.reenviarConfirmacion(correo)
    }

    fun correoRegistradoLocal(correo: String): Boolean =
        SampleData.isEmailRegistered(correo)
}
