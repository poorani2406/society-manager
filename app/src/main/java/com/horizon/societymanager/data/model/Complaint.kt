package com.horizon.societymanager.data.model

enum class ComplaintCategory {
    PLUMBING,
    ELECTRICAL,
    SECURITY,
    MAINTENANCE,
    NOISE,
    OTHER;

    val displayName: String
        get() = when (this) {
            PLUMBING -> "Plumbing"
            ELECTRICAL -> "Electrical"
            SECURITY -> "Security"
            MAINTENANCE -> "Maintenance"
            NOISE -> "Noise Issue"
            OTHER -> "Other"
        }
}

enum class ComplaintStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED;

    val displayName: String
        get() = when (this) {
            OPEN -> "Open"
            IN_PROGRESS -> "In Progress"
            RESOLVED -> "Resolved"
        }
}

enum class ComplaintPriority {
    LOW,
    MEDIUM,
    HIGH;

    val displayName: String
        get() = when (this) {
            LOW -> "Low"
            MEDIUM -> "Medium"
            HIGH -> "High"
        }
}

data class Complaint(
    val id: String,
    val residentId: String,
    val residentName: String,
    val flatNumber: String,
    val category: ComplaintCategory,
    val title: String,
    val description: String,
    val status: ComplaintStatus = ComplaintStatus.OPEN,
    val priority: ComplaintPriority = ComplaintPriority.MEDIUM,
    val createdAt: String,
    val updatedAt: String? = null,
    val adminNotes: String? = null
)
