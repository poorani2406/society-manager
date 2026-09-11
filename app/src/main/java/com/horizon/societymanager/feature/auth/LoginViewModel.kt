package com.horizon.societymanager.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.horizon.societymanager.data.model.User
import com.horizon.societymanager.data.model.UserRole
import com.horizon.societymanager.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "resident@horizon.com",
    val password: String = "resident123",
    val emailError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null,
    val isLoading: Boolean = false,
    val loggedInUser: User? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.update {
            it.copy(email = email, emailError = null, generalError = null)
        }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update {
            it.copy(password = password, passwordError = null, generalError = null)
        }
    }

    fun fillDemoResident() {
        _uiState.update {
            it.copy(
                email = "resident@horizon.com",
                password = "resident123",
                emailError = null,
                passwordError = null,
                generalError = null
            )
        }
    }

    fun fillDemoAdmin() {
        _uiState.update {
            it.copy(
                email = "admin@horizon.com",
                password = "admin123",
                emailError = null,
                passwordError = null,
                generalError = null
            )
        }
    }

    fun login() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        var hasError = false
        if (email.isEmpty()) {
            _uiState.update { it.copy(emailError = "Please enter your email address") }
            hasError = true
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(emailError = "Please enter a valid email address") }
            hasError = true
        }

        if (password.isEmpty()) {
            _uiState.update { it.copy(passwordError = "Please enter your password") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }
            val result = authRepository.login(email, password)
            result.onSuccess { user ->
                _uiState.update { it.copy(isLoading = false, loggedInUser = user) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isLoading = false, generalError = error.message ?: "Login failed. Check credentials.")
                }
            }
        }
    }

    fun quickLogin(role: UserRole) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }
            val user = authRepository.quickLogin(role)
            _uiState.update { it.copy(isLoading = false, loggedInUser = user) }
        }
    }

    fun resetLoggedInState() {
        _uiState.update { it.copy(loggedInUser = null) }
    }
}
