package com.horizon.societymanager.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.horizon.societymanager.core.session.SessionManager
import com.horizon.societymanager.data.model.Announcement
import com.horizon.societymanager.data.model.Complaint
import com.horizon.societymanager.data.model.ComplaintStatus
import com.horizon.societymanager.data.model.User
import com.horizon.societymanager.data.repository.AnnouncementRepository
import com.horizon.societymanager.data.repository.BookingRepository
import com.horizon.societymanager.data.repository.ComplaintRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class DashboardUiState(
    val currentUser: User? = null,
    val totalAnnouncementsCount: Int = 0,
    val openComplaintsCount: Int = 0,
    val activeBookingsCount: Int = 0,
    val recentAnnouncements: List<Announcement> = emptyList(),
    val recentComplaints: List<Complaint> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    sessionManager: SessionManager,
    announcementRepository: AnnouncementRepository,
    complaintRepository: ComplaintRepository,
    bookingRepository: BookingRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        sessionManager.currentUser,
        announcementRepository.announcements,
        complaintRepository.complaints,
        bookingRepository.bookings
    ) { user, announcements, complaints, bookings ->
        val relevantComplaints = if (user?.role == com.horizon.societymanager.data.model.UserRole.ADMIN) {
            complaints
        } else {
            complaints.filter { it.residentId == user?.id }
        }

        val openCount = relevantComplaints.count { it.status != ComplaintStatus.RESOLVED }

        val myBookingsCount = if (user?.role == com.horizon.societymanager.data.model.UserRole.ADMIN) {
            bookings.size
        } else {
            bookings.count { it.residentId == user?.id }
        }

        DashboardUiState(
            currentUser = user,
            totalAnnouncementsCount = announcements.size,
            openComplaintsCount = openCount,
            activeBookingsCount = myBookingsCount,
            recentAnnouncements = announcements.take(3),
            recentComplaints = relevantComplaints.take(2),
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState(isLoading = true)
    )
}
