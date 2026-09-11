package com.horizon.societymanager.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Dashboard : Screen("dashboard")
    data object Announcements : Screen("announcements")
    data object AnnouncementDetail : Screen("announcements/{announcementId}") {
        fun createRoute(announcementId: String) = "announcements/$announcementId"
    }
    data object AddAnnouncement : Screen("announcements/add")
    
    data object Complaints : Screen("complaints")
    data object ComplaintDetail : Screen("complaints/{complaintId}") {
        fun createRoute(complaintId: String) = "complaints/$complaintId"
    }
    data object CreateComplaint : Screen("complaints/create")

    data object Bookings : Screen("bookings")
    data object Profile : Screen("profile")
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Dashboard : BottomNavItem(Screen.Dashboard.route, "Home", Icons.Default.Home)
    data object Announcements : BottomNavItem(Screen.Announcements.route, "Notices", Icons.Default.Notifications)
    data object Complaints : BottomNavItem(Screen.Complaints.route, "Complaints", Icons.Default.Warning)
    data object Bookings : BottomNavItem(Screen.Bookings.route, "Bookings", Icons.Default.DateRange)
    data object Profile : BottomNavItem(Screen.Profile.route, "Profile", Icons.Default.Person)
}

val bottomNavItems = listOf(
    BottomNavItem.Dashboard,
    BottomNavItem.Announcements,
    BottomNavItem.Complaints,
    BottomNavItem.Bookings,
    BottomNavItem.Profile
)
