package com.horizon.societymanager.data.repository

import com.horizon.societymanager.data.model.Booking
import com.horizon.societymanager.data.model.Facility
import com.horizon.societymanager.data.model.TimeSlot
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepository @Inject constructor() {

    private val defaultTimeSlots = listOf(
        TimeSlot("slot_1", "06:00 AM - 08:00 AM"),
        TimeSlot("slot_2", "08:00 AM - 10:00 AM"),
        TimeSlot("slot_3", "10:00 AM - 12:00 PM"),
        TimeSlot("slot_4", "04:00 PM - 06:00 PM"),
        TimeSlot("slot_5", "06:00 PM - 08:00 PM"),
        TimeSlot("slot_6", "08:00 PM - 10:00 PM")
    )

    private val _facilities = MutableStateFlow<List<Facility>>(
        listOf(
            Facility(
                id = "fac_1",
                name = "Clubhouse Multi-Purpose Hall",
                description = "Air-conditioned banquet hall with sound system and seating up to 100 people. Ideal for birthday parties, family gatherings, and community events.",
                location = "Clubhouse 1st Floor",
                capacity = "Max 100 Guests",
                openingHours = "06:00 AM - 10:00 PM",
                availableSlots = defaultTimeSlots
            ),
            Facility(
                id = "fac_2",
                name = "Olympic-size Swimming Pool",
                description = "Temperature-controlled swimming pool with dedicated kids pool area and on-duty certified lifeguard.",
                location = "Sports Complex Area",
                capacity = "Max 25 Swimmers",
                openingHours = "06:00 AM - 09:00 PM",
                availableSlots = defaultTimeSlots
            ),
            Facility(
                id = "fac_3",
                name = "Indoor Badminton Court",
                description = "Synthetic mat professional badminton court with LED floodlighting and spectator seating.",
                location = "Clubhouse Ground Floor",
                capacity = "Max 4 Players",
                openingHours = "06:00 AM - 10:00 PM",
                availableSlots = defaultTimeSlots
            ),
            Facility(
                id = "fac_4",
                name = "Society Gymnasium",
                description = "Fully equipped fitness center with cardio stations, free weights, and cross-trainer machines.",
                location = "Clubhouse 2nd Floor",
                capacity = "Max 20 Members",
                openingHours = "05:30 AM - 10:30 PM",
                availableSlots = defaultTimeSlots
            )
        )
    )

    val facilities: StateFlow<List<Facility>> = _facilities.asStateFlow()

    private val _bookings = MutableStateFlow<List<Booking>>(
        listOf(
            Booking(
                id = "bk_1",
                facilityId = "fac_1",
                facilityName = "Clubhouse Multi-Purpose Hall",
                residentId = "usr_res_02",
                residentName = "Vikram Patel",
                flatNumber = "B-204",
                bookingDate = "Today",
                timeSlot = "06:00 PM - 08:00 PM",
                bookingTime = "10 Sep 2026, 05:30 PM"
            ),
            Booking(
                id = "bk_2",
                facilityId = "fac_3",
                facilityName = "Indoor Badminton Court",
                residentId = "usr_res_01",
                residentName = "Aarav Sharma",
                flatNumber = "A-402",
                bookingDate = "Tomorrow",
                timeSlot = "08:00 AM - 10:00 AM",
                bookingTime = "11 Sep 2026, 11:00 AM"
            )
        )
    )

    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    suspend fun getSlotsForFacility(facilityId: String, date: String): List<TimeSlot> {
        delay(150)
        val bookedSlotsForDateAndFacility = _bookings.value.filter {
            it.facilityId == facilityId && it.bookingDate == date
        }

        return defaultTimeSlots.map { slot ->
            val existingBooking = bookedSlotsForDateAndFacility.find { it.timeSlot == slot.timeRange }
            if (existingBooking != null) {
                slot.copy(
                    isBooked = true,
                    bookedByResidentId = existingBooking.residentId,
                    bookedByResidentName = "${existingBooking.residentName} (${existingBooking.flatNumber})"
                )
            } else {
                slot.copy(isBooked = false, bookedByResidentId = null, bookedByResidentName = null)
            }
        }
    }

    suspend fun bookSlot(
        facilityId: String,
        facilityName: String,
        residentId: String,
        residentName: String,
        flatNumber: String,
        date: String,
        timeSlot: String
    ): Result<Booking> {
        delay(400)
        val isAlreadyBooked = _bookings.value.any {
            it.facilityId == facilityId && it.bookingDate == date && it.timeSlot == timeSlot
        }

        if (isAlreadyBooked) {
            return Result.failure(IllegalStateException("This slot has just been booked. Please choose another time slot."))
        }

        val newBooking = Booking(
            id = "bk_${UUID.randomUUID().toString().take(8)}",
            facilityId = facilityId,
            facilityName = facilityName,
            residentId = residentId,
            residentName = residentName,
            flatNumber = flatNumber,
            bookingDate = date,
            timeSlot = timeSlot,
            bookingTime = "Just now"
        )

        _bookings.update { current ->
            listOf(newBooking) + current
        }

        return Result.success(newBooking)
    }

    suspend fun cancelBooking(bookingId: String): Result<Unit> {
        delay(300)
        _bookings.update { current ->
            current.filterNot { it.id == bookingId }
        }
        return Result.success(Unit)
    }
}
