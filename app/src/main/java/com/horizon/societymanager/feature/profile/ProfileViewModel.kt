package com.horizon.societymanager.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.horizon.societymanager.core.session.SessionManager
import com.horizon.societymanager.data.model.User
import com.horizon.societymanager.data.repository.AuthRepository
import com.horizon.societymanager.data.repository.BookingRepository
import com.horizon.societymanager.data.repository.ComplaintRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val complaintsCount: Int = 0,
    val bookingsCount: Int = 0,
    val isLoggedOut: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    sessionManager: SessionManager,
    complaintRepository: ComplaintRepository,
    bookingRepository: BookingRepository
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        sessionManager.currentUser,
        complaintRepository.complaints,
        bookingRepository.bookings
    ) { user, complaints, bookings ->
        val userComplaints = complaints.filter { it.residentId == user?.id }
        val userBookings = bookings.filter { it.residentId == user?.id }

        ProfileUiState(
            user = user,
            complaintsCount = if (user?.role == com.horizon.societymanager.data.model.UserRole.ADMIN) complaints.size else userComplaints.size,
            bookingsCount = if (user?.role == com.horizon.societymanager.data.model.UserRole.ADMIN) bookings.size else userBookings.size,
            isLoggedOut = user == null
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState()
    )

    fun logout() {
        authRepository.logout()
    }
}
