package com.horizon.societymanager.data.model

data class TimeSlot(
    val id: String,
    val timeRange: String,
    val isBooked: Boolean = false,
    val bookedByResidentId: String? = null,
    val bookedByResidentName: String? = null
)

data class Facility(
    val id: String,
    val name: String,
    val description: String,
    val location: String,
    val capacity: String,
    val openingHours: String,
    val availableSlots: List<TimeSlot> = emptyList()
)

data class Booking(
    val id: String,
    val facilityId: String,
    val facilityName: String,
    val residentId: String,
    val residentName: String,
    val flatNumber: String,
    val bookingDate: String,
    val timeSlot: String,
    val bookingTime: String,
    val status: String = "CONFIRMED"
)
