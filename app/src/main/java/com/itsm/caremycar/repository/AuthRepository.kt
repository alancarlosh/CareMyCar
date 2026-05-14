package com.itsm.caremycar.repository


import com.itsm.caremycar.api.ApiService
import com.itsm.caremycar.classes.User
import com.itsm.caremycar.classes.toUser
import com.itsm.caremycar.session.LoginRequest
import com.itsm.caremycar.session.RegisterRequest
import com.itsm.caremycar.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApiService: ApiService,
    private val tokenManager: TokenManager
) {
    suspend fun login(email: String, password: String): Resource<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApiService.login(
                    LoginRequest(
                        email = email.trim().lowercase(),
                        password = password
                    )
                )

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    val user = loginResponse.user.toUser()

                    // Guardar token
                    tokenManager.saveToken(loginResponse.accessToken)
                    tokenManager.saveUser(user)

                    // Convertir y retornar usuario
                    Resource.Success(user)
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "Credenciales inválidas"
                        else -> "Error al iniciar sesión"
                    }
                    Resource.Error(errorMsg)
                }
            } catch (e: Exception) {
                Resource.Error(e.localizedMessage ?: "Error de conexión")
            }
        }
    }

    suspend fun register(
        email: String,
        password: String,
        name: String?
    ): Resource<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApiService.register(
                    RegisterRequest(
                        email = email.trim().lowercase(),
                        password = password,
                        name = name?.trim()?.takeIf { it.isNotEmpty() }
                    )
                )

                if (response.isSuccessful && response.body() != null) {
                    val registerResponse = response.body()!!

                    Resource.Success(registerResponse.user.toUser())
                } else {
                    val errorMsg = when (response.code()) {
                        400 -> "Email o contraseña inválidos"
                        409 -> "Correo ya registrado"
                        else -> "Error al registrar usuario"
                    }
                    Resource.Error(errorMsg)
                }
            } catch (e: Exception) {
                Resource.Error(e.localizedMessage ?: "Error de conexión")
            }
        }
    }

    suspend fun logout() {
        withContext(Dispatchers.IO) {
            tokenManager.clearToken()
        }
    }

    fun isLoggedIn(): Boolean = tokenManager.getToken() != null

    fun getSavedUserRole(): String? = tokenManager.getUserRole()
}
