package com.horizon.societymanager.feature.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.horizon.societymanager.core.session.SessionManager
import com.horizon.societymanager.data.model.Booking
import com.horizon.societymanager.data.model.Facility
import com.horizon.societymanager.data.model.TimeSlot
import com.horizon.societymanager.data.model.UserRole
import com.horizon.societymanager.data.repository.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookingUiState(
    val facilities: List<Facility> = emptyList(),
    val selectedFacility: Facility? = null,
    val selectedDate: String = "Today",
    val availableDates: List<String> = listOf("Today", "Tomorrow", "Sunday, 14 Sep"),
    val slots: List<TimeSlot> = emptyList(),
    val selectedSlot: TimeSlot? = null,
    val myBookings: List<Booking> = emptyList(),
    val isBooking: Boolean = false,
    val bookingSuccessMessage: String? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingUiState(isLoading = true))
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    init {
        // Observe facilities
        viewModelScope.launch {
            bookingRepository.facilities.collectLatest { facilitiesList ->
                _uiState.update { current ->
                    val initialFacility = current.selectedFacility ?: facilitiesList.firstOrNull()
                    current.copy(
                        facilities = facilitiesList,
                        selectedFacility = initialFacility,
                        isLoading = false
                    )
                }
                _uiState.value.selectedFacility?.let {
                    loadSlots(it.id, _uiState.value.selectedDate)
                }
            }
        }

        // Observe bookings
        viewModelScope.launch {
            combine(bookingRepository.bookings, sessionManager.currentUser) { allBookings, user ->
                if (user?.role == UserRole.ADMIN) {
                    allBookings
                } else {
                    allBookings.filter { it.residentId == user?.id }
                }
            }.collectLatest { filteredBookings ->
                _uiState.update { it.copy(myBookings = filteredBookings) }
                _uiState.value.selectedFacility?.let {
                    loadSlots(it.id, _uiState.value.selectedDate)
                }
            }
        }
    }

    fun selectFacility(facility: Facility) {
        _uiState.update {
            it.copy(selectedFacility = facility, selectedSlot = null)
        }
        loadSlots(facility.id, _uiState.value.selectedDate)
    }

    fun selectDate(date: String) {
        _uiState.update {
            it.copy(selectedDate = date, selectedSlot = null)
        }
        _uiState.value.selectedFacility?.let { facility ->
            loadSlots(facility.id, date)
        }
    }

    fun selectSlot(slot: TimeSlot) {
        if (!slot.isBooked) {
            _uiState.update { it.copy(selectedSlot = slot, errorMessage = null) }
        }
    }

    private fun loadSlots(facilityId: String, date: String) {
        viewModelScope.launch {
            val updatedSlots = bookingRepository.getSlotsForFacility(facilityId, date)
            _uiState.update { it.copy(slots = updatedSlots) }
        }
    }

    fun confirmBooking() {
        val currentState = _uiState.value
        val facility = currentState.selectedFacility ?: return
        val slot = currentState.selectedSlot ?: return
        val user = sessionManager.currentUser.value ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isBooking = true, errorMessage = null) }

            val result = bookingRepository.bookSlot(
                facilityId = facility.id,
                facilityName = facility.name,
                residentId = user.id,
                residentName = user.name,
                flatNumber = user.flatNumber,
                date = currentState.selectedDate,
                timeSlot = slot.timeRange
            )

            result.onSuccess {
                _uiState.update { state ->
                    state.copy(
                        isBooking = false,
                        bookingSuccessMessage = "Successfully booked ${facility.name} for ${state.selectedDate} (${slot.timeRange})!",
                        selectedSlot = null
                    )
                }
                loadSlots(facility.id, _uiState.value.selectedDate)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isBooking = false, errorMessage = error.message ?: "Could not complete booking")
                }
            }
        }
    }

    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            bookingRepository.cancelBooking(bookingId)
            _uiState.value.selectedFacility?.let {
                loadSlots(it.id, _uiState.value.selectedDate)
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(bookingSuccessMessage = null, errorMessage = null) }
    }
}
