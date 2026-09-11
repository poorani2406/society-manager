package com.horizon.societymanager.feature.complaint

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.horizon.societymanager.core.ui.*
import com.horizon.societymanager.data.model.ComplaintPriority
import com.horizon.societymanager.data.model.ComplaintStatus
import com.horizon.societymanager.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintDetailScreen(
    complaintId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ComplaintViewModel = hiltViewModel()
) {
    val listUiState by viewModel.listUiState.collectAsState()
    val complaint by viewModel.detailComplaint.collectAsState()
    var showStatusDialog by remember { mutableStateOf(false) }
    var adminNoteText by remember { mutableStateOf("") }
    var selectedNewStatus by remember { mutableStateOf(ComplaintStatus.IN_PROGRESS) }

    LaunchedEffect(complaintId) {
        viewModel.loadComplaintDetail(complaintId)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HorizonTopAppBar(
                title = "Complaint Details",
                subtitle = "Ticket #${complaintId.takeLast(6).uppercase()}",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        if (complaint == null) {
            LoadingStateView(
                message = "Loading complaint details...",
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            val item = complaint!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Status Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Current Status",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            ComplaintStatusBadge(status = item.status)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Status Progression Stepper / Indicators
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusStepItem(
                                label = "Raised",
                                isReached = true,
                                isCurrent = item.status == ComplaintStatus.OPEN
                            )

                            HorizontalDivider(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp),
                                color = if (item.status != ComplaintStatus.OPEN) MaterialTheme.colorScheme.primary else BorderLight
                            )

                            StatusStepItem(
                                label = "In Progress",
                                isReached = item.status == ComplaintStatus.IN_PROGRESS || item.status == ComplaintStatus.RESOLVED,
                                isCurrent = item.status == ComplaintStatus.IN_PROGRESS
                            )

                            HorizontalDivider(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp),
                                color = if (item.status == ComplaintStatus.RESOLVED) StatusResolved else BorderLight
                            )

                            StatusStepItem(
                                label = "Resolved",
                                isReached = item.status == ComplaintStatus.RESOLVED,
                                isCurrent = item.status == ComplaintStatus.RESOLVED
                            )
                        }
                    }
                }

                // Title & Priority
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = item.category.displayName,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    PriorityBadge(priority = item.priority)
                }

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                // Resident Details Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Resident: ${item.residentName} (Flat ${item.flatNumber})",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Reported: ${item.createdAt}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        if (item.updatedAt != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = StatusResolved,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Last Update: ${item.updatedAt}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                }

                // Description
                Text(
                    text = "Description of Issue",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp)
                )

                // Admin Updates / Notes
                if (!item.adminNotes.isNullOrBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF86EFAC)))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = StatusResolved,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Admin Resolution Notes",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = StatusResolved,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = item.adminNotes,
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF14532D))
                            )
                        }
                    }
                }

                // Admin Action Button
                if (listUiState.isAdmin) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            selectedNewStatus = item.status
                            adminNoteText = item.adminNotes ?: ""
                            showStatusDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Update Status / Add Note (Admin)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }

    // Admin Status Update Modal Dialog
    if (showStatusDialog && complaint != null) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("Update Ticket Status", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Select new ticket status:")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ComplaintStatus.entries.forEach { status ->
                            FilterChip(
                                selected = selectedNewStatus == status,
                                onClick = { selectedNewStatus = status },
                                label = { Text(status.displayName, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = adminNoteText,
                        onValueChange = { adminNoteText = it },
                        label = { Text("Resolution notes / Action taken") },
                        placeholder = { Text("e.g. Technician dispatched, parts ordered...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateStatus(complaintId, selectedNewStatus, adminNoteText)
                        showStatusDialog = false
                    }
                ) {
                    Text("Save Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStatusDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun StatusStepItem(
    label: String,
    isReached: Boolean,
    isCurrent: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    if (isCurrent) MaterialTheme.colorScheme.primary
                    else if (isReached) StatusResolved
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isReached && !isCurrent) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                fontSize = 11.sp,
                color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}
