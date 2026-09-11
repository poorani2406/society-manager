package com.horizon.societymanager.data.model

enum class AnnouncementType {
    NOTICE,
    EVENT;

    val displayName: String
        get() = when (this) {
            NOTICE -> "Notice"
            EVENT -> "Event"
        }
}

enum class AnnouncementPriority {
    NORMAL,
    URGENT
}

data class Announcement(
    val id: String,
    val title: String,
    val description: String,
    val type: AnnouncementType,
    val postedDate: String,
    val authorName: String,
    val eventDate: String? = null,
    val location: String? = null,
    val priority: AnnouncementPriority = AnnouncementPriority.NORMAL
)
