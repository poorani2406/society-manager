package com.horizon.societymanager.feature.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.horizon.societymanager.core.ui.EmptyStateView
import com.horizon.societymanager.core.ui.HorizonTopAppBar
import com.horizon.societymanager.core.ui.LoadingStateView
import com.horizon.societymanager.data.model.Booking
import com.horizon.societymanager.data.model.Facility
import com.horizon.societymanager.data.model.TimeSlot
import com.horizon.societymanager.ui.theme.BorderLight
import com.horizon.societymanager.ui.theme.StatusOpen
import com.horizon.societymanager.ui.theme.StatusResolved

@Composable
fun FacilityBookingScreen(
    modifier: Modifier = Modifier,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HorizonTopAppBar(
                title = "Facility Reservations",
                subtitle = "Book society amenities & sports spaces"
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            LoadingStateView(modifier = Modifier.padding(innerPadding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Success / Error alerts
                if (uiState.bookingSuccessMessage != null) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = StatusResolved)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = uiState.bookingSuccessMessage ?: "",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF14532D),
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(onClick = viewModel::clearMessages) {
                                    Text("Dismiss", color = StatusResolved)
                                }
                            }
                        }
                    }
                }

                if (uiState.errorMessage != null) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE4E6))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = StatusOpen)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = uiState.errorMessage ?: "",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = StatusOpen,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(onClick = viewModel::clearMessages) {
                                    Text("Dismiss", color = StatusOpen)
                                }
                            }
                        }
                    }
                }

                // 1. Select Facility Chips
                item {
                    Text(
                        text = "1. Select Facility",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.facilities) { facility ->
                            val isSelected = uiState.selectedFacility?.id == facility.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectFacility(facility) },
                                label = { Text(facility.name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) }
                            )
                        }
                    }
                }

                // Selected Facility Info Card
                uiState.selectedFacility?.let { facility ->
                    item {
                        FacilityInfoCard(facility = facility)
                    }
                }

                // 2. Select Date
                item {
                    Text(
                        text = "2. Select Date",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        uiState.availableDates.forEach { date ->
                            FilterChip(
                                selected = uiState.selectedDate == date,
                                onClick = { viewModel.selectDate(date) },
                                label = { Text(date) }
                            )
                        }
                    }
                }

                // 3. Time Slots
                item {
                    Text(
                        text = "3. Select Time Slot",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        uiState.slots.chunked(2).forEach { rowSlots ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowSlots.forEach { slot ->
                                    SlotCard(
                                        slot = slot,
                                        isSelected = uiState.selectedSlot?.id == slot.id,
                                        onClick = { viewModel.selectSlot(slot) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (rowSlots.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                // Confirm Booking Button
                item {
                    Button(
                        onClick = viewModel::confirmBooking,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = uiState.selectedSlot != null && !uiState.isBooking,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState.isBooking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (uiState.selectedSlot != null)
                                    "Confirm Booking (${uiState.selectedSlot?.timeRange})"
                                else "Select an Available Slot",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                // 4. My Active Bookings Section
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Active Reservations (${uiState.myBookings.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                if (uiState.myBookings.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "You have no active facility bookings.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(uiState.myBookings) { booking ->
                        BookingItemCard(
                            booking = booking,
                            onCancel = { viewModel.cancelBooking(booking.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FacilityInfoCard(
    facility: Facility,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = facility.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = facility.description,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "📍 ${facility.location}",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = "👥 ${facility.capacity}",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.secondary)
                )
            }
        }
    }
}

@Composable
fun SlotCard(
    slot: TimeSlot,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = when {
        slot.isBooked -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        slot.isBooked -> Color.Transparent
        else -> BorderLight
    }

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(enabled = !slot.isBooked, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = slot.timeRange,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (slot.isBooked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (slot.isBooked) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFE4E6)
                ) {
                    Text(
                        text = "Booked",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusOpen
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            } else if (isSelected) {
                Text(
                    text = "Selected",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            } else {
                Text(
                    text = "Available",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = StatusResolved,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
fun BookingItemCard(
    booking: Booking,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = booking.facilityName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "🗓 ${booking.bookingDate} • ${booking.timeSlot}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text = "Resident: ${booking.residentName} (${booking.flatNumber})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            TextButton(
                onClick = onCancel,
                colors = ButtonDefaults.textButtonColors(contentColor = StatusOpen)
            ) {
                Text("Cancel", fontSize = 12.sp)
            }
        }
    }
}
