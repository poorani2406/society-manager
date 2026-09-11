package com.horizon.societymanager.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.horizon.societymanager.feature.announcement.AddAnnouncementScreen
import com.horizon.societymanager.feature.announcement.AnnouncementDetailScreen
import com.horizon.societymanager.feature.announcement.AnnouncementListScreen
import com.horizon.societymanager.feature.auth.LoginScreen
import com.horizon.societymanager.feature.booking.FacilityBookingScreen
import com.horizon.societymanager.feature.complaint.ComplaintDetailScreen
import com.horizon.societymanager.feature.complaint.ComplaintListScreen
import com.horizon.societymanager.feature.complaint.CreateComplaintScreen
import com.horizon.societymanager.feature.dashboard.DashboardScreen
import com.horizon.societymanager.feature.profile.ProfileScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf(
        Screen.Dashboard.route,
        Screen.Announcements.route,
        Screen.Complaints.route,
        Screen.Bookings.route,
        Screen.Profile.route
    )

    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                HorizonBottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Login Screen
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // Dashboard Screen
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToAnnouncements = {
                        navController.navigate(Screen.Announcements.route)
                    },
                    onNavigateToAnnouncementDetail = { id ->
                        navController.navigate(Screen.AnnouncementDetail.createRoute(id))
                    },
                    onNavigateToComplaints = {
                        navController.navigate(Screen.Complaints.route)
                    },
                    onNavigateToCreateComplaint = {
                        navController.navigate(Screen.CreateComplaint.route)
                    },
                    onNavigateToBookings = {
                        navController.navigate(Screen.Bookings.route)
                    },
                    onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route)
                    }
                )
            }

            // Announcements List
            composable(Screen.Announcements.route) {
                AnnouncementListScreen(
                    onAnnouncementClick = { id ->
                        navController.navigate(Screen.AnnouncementDetail.createRoute(id))
                    },
                    onAddAnnouncementClick = {
                        navController.navigate(Screen.AddAnnouncement.route)
                    }
                )
            }

            // Announcement Detail
            composable(
                route = Screen.AnnouncementDetail.route,
                arguments = listOf(navArgument("announcementId") { type = NavType.StringType })
            ) { backStackEntry ->
                val announcementId = backStackEntry.arguments?.getString("announcementId") ?: ""
                AnnouncementDetailScreen(
                    announcementId = announcementId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Add Announcement (Admin)
            composable(Screen.AddAnnouncement.route) {
                AddAnnouncementScreen(
                    onBackClick = { navController.popBackStack() },
                    onSuccess = { navController.popBackStack() }
                )
            }

            // Complaints List
            composable(Screen.Complaints.route) {
                ComplaintListScreen(
                    onComplaintClick = { id ->
                        navController.navigate(Screen.ComplaintDetail.createRoute(id))
                    },
                    onCreateComplaintClick = {
                        navController.navigate(Screen.CreateComplaint.route)
                    }
                )
            }

            // Complaint Detail
            composable(
                route = Screen.ComplaintDetail.route,
                arguments = listOf(navArgument("complaintId") { type = NavType.StringType })
            ) { backStackEntry ->
                val complaintId = backStackEntry.arguments?.getString("complaintId") ?: ""
                ComplaintDetailScreen(
                    complaintId = complaintId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Create Complaint
            composable(Screen.CreateComplaint.route) {
                CreateComplaintScreen(
                    onBackClick = { navController.popBackStack() },
                    onSuccess = { navController.popBackStack() }
                )
            }

            // Facility Bookings
            composable(Screen.Bookings.route) {
                FacilityBookingScreen()
            }

            // Profile
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
