package com.horizon.societymanager

import com.horizon.societymanager.data.repository.BookingRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class BookingRepositoryTest {

    private lateinit var bookingRepository: BookingRepository

    @Before
    fun setUp() {
        bookingRepository = BookingRepository()
    }

    @Test
    fun bookSlot_succeedsAndMarksSlotAsBooked() = runTest {
        val facility = bookingRepository.facilities.value.first()
        val date = "Tomorrow"
        val timeSlot = "04:00 PM - 06:00 PM"

        val result = bookingRepository.bookSlot(
            facilityId = facility.id,
            facilityName = facility.name,
            residentId = "usr_res_01",
            residentName = "Aarav Sharma",
            flatNumber = "A-402",
            date = date,
            timeSlot = timeSlot
        )

        assertTrue(result.isSuccess)
        val booking = result.getOrNull()
        assertNotNull(booking)
        assertEquals(facility.id, booking?.facilityId)

        val slots = bookingRepository.getSlotsForFacility(facility.id, date)
        val bookedSlot = slots.find { it.timeRange == timeSlot }
        assertNotNull(bookedSlot)
        assertTrue(bookedSlot?.isBooked == true)
        assertEquals("usr_res_01", bookedSlot?.bookedByResidentId)
    }

    @Test
    fun bookSlot_whenAlreadyBooked_fails() = runTest {
        val facility = bookingRepository.facilities.value.first()
        val date = "Today"
        val timeSlot = "06:00 PM - 08:00 PM" // Already pre-booked in mock data for bk_1

        val result = bookingRepository.bookSlot(
            facilityId = facility.id,
            facilityName = facility.name,
            residentId = "usr_res_01",
            residentName = "Aarav Sharma",
            flatNumber = "A-402",
            date = date,
            timeSlot = timeSlot
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun cancelBooking_removesBooking() = runTest {
        val existingBooking = bookingRepository.bookings.value.first()

        val result = bookingRepository.cancelBooking(existingBooking.id)

        assertTrue(result.isSuccess)
        assertFalse(bookingRepository.bookings.value.any { it.id == existingBooking.id })
    }
}
