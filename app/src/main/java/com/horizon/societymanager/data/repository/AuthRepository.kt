package com.horizon.societymanager.data.repository

import com.horizon.societymanager.core.session.SessionManager
import com.horizon.societymanager.data.model.User
import com.horizon.societymanager.data.model.UserRole
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val sessionManager: SessionManager
) {

    // Predefined demo accounts
    val demoResident = User(
        id = "usr_res_01",
        name = "Aarav Sharma",
        email = "resident@horizon.com",
        phone = "+91 98765 43210",
        flatNumber = "A-402",
        role = UserRole.RESIDENT,
        block = "Tower A"
    )

    val demoAdmin = User(
        id = "usr_adm_01",
        name = "Society Admin (Priya Mehta)",
        email = "admin@horizon.com",
        phone = "+91 91234 56789",
        flatNumber = "Clubhouse Admin Office",
        role = UserRole.ADMIN,
        block = "Administration"
    )

    val currentUser: StateFlow<User?> = sessionManager.currentUser

    suspend fun login(email: String, password: String): Result<User> {
        delay(600) // Realistic UI feedback
        val cleanEmail = email.trim().lowercase()

        return when {
            cleanEmail == "resident@horizon.com" && password == "resident123" -> {
                sessionManager.setUser(demoResident)
                Result.success(demoResident)
            }
            cleanEmail == "admin@horizon.com" && password == "admin123" -> {
                sessionManager.setUser(demoAdmin)
                Result.success(demoAdmin)
            }
            // Allow friendly fallback if password matches role
            cleanEmail.contains("resident") && password.isNotEmpty() -> {
                val customResident = demoResident.copy(email = cleanEmail)
                sessionManager.setUser(customResident)
                Result.success(customResident)
            }
            cleanEmail.contains("admin") && password.isNotEmpty() -> {
                val customAdmin = demoAdmin.copy(email = cleanEmail)
                sessionManager.setUser(customAdmin)
                Result.success(customAdmin)
            }
            else -> {
                Result.failure(IllegalArgumentException("Invalid email or password. Use demo credentials or quick login."))
            }
        }
    }

    suspend fun quickLogin(role: UserRole): User {
        delay(300)
        val user = if (role == UserRole.ADMIN) demoAdmin else demoResident
        sessionManager.setUser(user)
        return user
    }

    fun logout() {
        sessionManager.clearSession()
    }

    fun getCurrentUser(): User? = sessionManager.currentUser.value
}
