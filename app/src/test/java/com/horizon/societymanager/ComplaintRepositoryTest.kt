package com.horizon.societymanager

import com.horizon.societymanager.data.model.ComplaintCategory
import com.horizon.societymanager.data.model.ComplaintPriority
import com.horizon.societymanager.data.model.ComplaintStatus
import com.horizon.societymanager.data.repository.ComplaintRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ComplaintRepositoryTest {

    private lateinit var complaintRepository: ComplaintRepository

    @Before
    fun setUp() {
        complaintRepository = ComplaintRepository()
    }

    @Test
    fun createComplaint_addsNewComplaintToTop() = runTest {
        val initialSize = complaintRepository.complaints.value.size

        val result = complaintRepository.createComplaint(
            residentId = "usr_res_01",
            residentName = "Aarav Sharma",
            flatNumber = "A-402",
            category = ComplaintCategory.PLUMBING,
            title = "Test plumbing issue",
            description = "Dripping tap in kitchen",
            priority = ComplaintPriority.MEDIUM
        )

        assertTrue(result.isSuccess)
        val created = result.getOrNull()
        assertNotNull(created)
        assertEquals(ComplaintStatus.OPEN, created?.status)
        assertEquals("Test plumbing issue", created?.title)

        val updatedList = complaintRepository.complaints.value
        assertEquals(initialSize + 1, updatedList.size)
        assertEquals(created?.id, updatedList.first().id)
    }

    @Test
    fun updateComplaintStatus_updatesStatusAndNotes() = runTest {
        val initialComplaint = complaintRepository.complaints.value.first()

        val result = complaintRepository.updateComplaintStatus(
            complaintId = initialComplaint.id,
            newStatus = ComplaintStatus.RESOLVED,
            adminNotes = "Fixed by society maintenance"
        )

        assertTrue(result.isSuccess)
        val updated = result.getOrNull()
        assertNotNull(updated)
        assertEquals(ComplaintStatus.RESOLVED, updated?.status)
        assertEquals("Fixed by society maintenance", updated?.adminNotes)

        val itemInFlow = complaintRepository.getComplaintById(initialComplaint.id)
        assertEquals(ComplaintStatus.RESOLVED, itemInFlow?.status)
    }
}
