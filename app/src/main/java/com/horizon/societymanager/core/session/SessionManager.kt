package com.horizon.societymanager.core.session

import com.horizon.societymanager.data.model.User
import com.horizon.societymanager.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory session manager for handling the current user login state.
 */
@Singleton
class SessionManager @Inject constructor() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    val isLoggedIn: Boolean
        get() = _currentUser.value != null

    val isAdmin: Boolean
        get() = _currentUser.value?.role == UserRole.ADMIN

    val isResident: Boolean
        get() = _currentUser.value?.role == UserRole.RESIDENT

    fun setUser(user: User) {
        _currentUser.value = user
    }

    fun clearSession() {
        _currentUser.value = null
    }
}
