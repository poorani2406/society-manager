package com.horizon.societymanager.data.repository

import com.horizon.societymanager.data.model.Announcement
import com.horizon.societymanager.data.model.AnnouncementPriority
import com.horizon.societymanager.data.model.AnnouncementType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnnouncementRepository @Inject constructor() {

    private val _announcements = MutableStateFlow<List<Announcement>>(
        listOf(
            Announcement(
                id = "ann_1",
                title = "Scheduled Water Supply Interruption",
                description = "Please be informed that regular overhead tank cleaning and maintenance will take place this Saturday from 10:00 AM to 02:00 PM. Water supply across Tower A and Tower B will be briefly interrupted. Kindly store sufficient water for your daily needs.",
                type = AnnouncementType.NOTICE,
                postedDate = "11 Sep 2026, 09:30 AM",
                authorName = "Maintenance Committee",
                location = "Tower A & B Tanks",
                priority = AnnouncementPriority.URGENT
            ),
            Announcement(
                id = "ann_2",
                title = "Annual General Body Meeting (AGM 2026)",
                description = "All society members are cordially invited to attend the Annual General Body Meeting (AGM). Key agenda items include annual budget approvals, security enhancements, EV charging station proposals, and committee elections. Refreshments will be served.",
                type = AnnouncementType.EVENT,
                postedDate = "10 Sep 2026, 04:15 PM",
                authorName = "Management Committee",
                eventDate = "Sunday, 20 Sep 2026 at 10:30 AM",
                location = "Community Clubhouse (Hall 1)",
                priority = AnnouncementPriority.NORMAL
            ),
            Announcement(
                id = "ann_3",
                title = "Diwali Cultural Night & Food Festival",
                description = "Join us for an exciting evening of festive celebrations, music performances by society kids, games, and authentic food stalls! Interested participants for dance and music performances can register at the Clubhouse desk.",
                type = AnnouncementType.EVENT,
                postedDate = "08 Sep 2026, 11:00 AM",
                authorName = "Cultural Committee",
                eventDate = "Saturday, 26 Oct 2026 at 06:00 PM",
                location = "Central Lawn & Amphitheatre",
                priority = AnnouncementPriority.NORMAL
            ),
            Announcement(
                id = "ann_4",
                title = "New Security Guidelines for Visitor Parking",
                description = "To ensure smooth movement of emergency vehicles, please ensure all guest vehicles are parked only in designated visitor bays (Slots V1 to V25). Please enter visitor details in the security register at the main gate.",
                type = AnnouncementType.NOTICE,
                postedDate = "05 Sep 2026, 02:00 PM",
                authorName = "Security Office",
                location = "Basement Level 1",
                priority = AnnouncementPriority.NORMAL
            )
        )
    )

    val announcements: StateFlow<List<Announcement>> = _announcements.asStateFlow()

    suspend fun getAnnouncementById(id: String): Announcement? {
        delay(200)
        return _announcements.value.find { it.id == id }
    }

    suspend fun addAnnouncement(
        title: String,
        description: String,
        type: AnnouncementType,
        authorName: String,
        eventDate: String? = null,
        location: String? = null,
        priority: AnnouncementPriority = AnnouncementPriority.NORMAL
    ): Result<Announcement> {
        delay(400)
        val newAnnouncement = Announcement(
            id = "ann_${UUID.randomUUID().toString().take(8)}",
            title = title.trim(),
            description = description.trim(),
            type = type,
            postedDate = "Just now",
            authorName = authorName,
            eventDate = eventDate?.takeIf { it.isNotBlank() },
            location = location?.takeIf { it.isNotBlank() },
            priority = priority
        )

        _announcements.update { currentList ->
            listOf(newAnnouncement) + currentList
        }
        return Result.success(newAnnouncement)
    }
}
