package com.horizon.societymanager.data.repository

import com.horizon.societymanager.data.model.Complaint
import com.horizon.societymanager.data.model.ComplaintCategory
import com.horizon.societymanager.data.model.ComplaintPriority
import com.horizon.societymanager.data.model.ComplaintStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ComplaintRepository @Inject constructor() {

    private val _complaints = MutableStateFlow<List<Complaint>>(
        listOf(
            Complaint(
                id = "cmp_1",
                residentId = "usr_res_01",
                residentName = "Aarav Sharma",
                flatNumber = "A-402",
                category = ComplaintCategory.PLUMBING,
                title = "Water leakage in Master Bathroom ceiling",
                description = "There is a continuous minor seepage and water dripping from the ceiling above the master bathroom. It looks like seepage from Flat A-502. Please inspect as soon as possible.",
                status = ComplaintStatus.IN_PROGRESS,
                priority = ComplaintPriority.HIGH,
                createdAt = "10 Sep 2026, 11:30 AM",
                updatedAt = "11 Sep 2026, 09:15 AM",
                adminNotes = "Assigned to society plumbing supervisor (Ramesh). Inspection scheduled for today at 3 PM."
            ),
            Complaint(
                id = "cmp_2",
                residentId = "usr_res_01",
                residentName = "Aarav Sharma",
                flatNumber = "A-402",
                category = ComplaintCategory.ELECTRICAL,
                title = "Corridor light flickering outside Flat A-402",
                description = "The tube light in the 4th floor corridor near flat A-402 is constantly flickering and making a buzzing noise.",
                status = ComplaintStatus.OPEN,
                priority = ComplaintPriority.MEDIUM,
                createdAt = "11 Sep 2026, 08:20 AM"
            ),
            Complaint(
                id = "cmp_3",
                residentId = "usr_res_02",
                residentName = "Vikram Patel",
                flatNumber = "B-204",
                category = ComplaintCategory.MAINTENANCE,
                title = "Lift B2 making vibrating noise",
                description = "Passenger lift B2 is producing a loud grinding sound while moving between the 2nd and 5th floors.",
                status = ComplaintStatus.RESOLVED,
                priority = ComplaintPriority.HIGH,
                createdAt = "06 Sep 2026, 10:00 AM",
                updatedAt = "08 Sep 2026, 05:00 PM",
                adminNotes = "Otis elevator technician inspected and lubricated the guide rails. Issue resolved."
            ),
            Complaint(
                id = "cmp_4",
                residentId = "usr_res_03",
                residentName = "Neha Gupta",
                flatNumber = "C-101",
                category = ComplaintCategory.NOISE,
                title = "Late night renovation noise from upper floor",
                description = "Heavy drilling and hammering sound after 10 PM. Society bylaws state interior work must stop by 7 PM.",
                status = ComplaintStatus.RESOLVED,
                priority = ComplaintPriority.LOW,
                createdAt = "04 Sep 2026, 10:45 PM",
                updatedAt = "05 Sep 2026, 10:00 AM",
                adminNotes = "Security sent to caution the contractor. Work stopped immediately."
            ),
            Complaint(
                id = "cmp_5",
                residentId = "usr_res_01",
                residentName = "Aarav Sharma",
                flatNumber = "A-402",
                category = ComplaintCategory.SECURITY,
                title = "Request for replacement RFID access tag",
                description = "Lost vehicle RFID tag during car servicing. Requesting a new tag for vehicle KA-03-MB-4512.",
                status = ComplaintStatus.OPEN,
                priority = ComplaintPriority.LOW,
                createdAt = "11 Sep 2026, 02:15 PM"
            )
        )
    )

    val complaints: StateFlow<List<Complaint>> = _complaints.asStateFlow()

    suspend fun getComplaintById(id: String): Complaint? {
        delay(200)
        return _complaints.value.find { it.id == id }
    }

    suspend fun createComplaint(
        residentId: String,
        residentName: String,
        flatNumber: String,
        category: ComplaintCategory,
        title: String,
        description: String,
        priority: ComplaintPriority = ComplaintPriority.MEDIUM
    ): Result<Complaint> {
        delay(400)
        val newComplaint = Complaint(
            id = "cmp_${UUID.randomUUID().toString().take(8)}",
            residentId = residentId,
            residentName = residentName,
            flatNumber = flatNumber,
            category = category,
            title = title.trim(),
            description = description.trim(),
            status = ComplaintStatus.OPEN,
            priority = priority,
            createdAt = "Just now"
        )

        _complaints.update { current ->
            listOf(newComplaint) + current
        }
        return Result.success(newComplaint)
    }

    suspend fun updateComplaintStatus(
        complaintId: String,
        newStatus: ComplaintStatus,
        adminNotes: String? = null
    ): Result<Complaint> {
        delay(350)
        var updatedComplaint: Complaint? = null

        _complaints.update { current ->
            current.map { complaint ->
                if (complaint.id == complaintId) {
                    val updated = complaint.copy(
                        status = newStatus,
                        updatedAt = "Updated just now",
                        adminNotes = adminNotes ?: complaint.adminNotes
                    )
                    updatedComplaint = updated
                    updated
                } else {
                    complaint
                }
            }
        }

        return updatedComplaint?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException("Complaint not found"))
    }
}
