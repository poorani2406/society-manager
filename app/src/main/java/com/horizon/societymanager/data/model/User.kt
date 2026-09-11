package com.horizon.societymanager.data.model

enum class UserRole {
    RESIDENT,
    ADMIN;

    val displayName: String
        get() = when (this) {
            RESIDENT -> "Resident"
            ADMIN -> "Society Admin"
        }
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val flatNumber: String,
    val role: UserRole,
    val block: String = "Block A"
)
