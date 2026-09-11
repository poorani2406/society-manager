package com.horizon.societymanager.feature.complaint

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.horizon.societymanager.core.ui.*
import com.horizon.societymanager.data.model.Complaint

@Composable
fun ComplaintListScreen(
    onComplaintClick: (String) -> Unit,
    onCreateComplaintClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ComplaintViewModel = hiltViewModel()
) {
    val uiState by viewModel.listUiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HorizonTopAppBar(
                title = if (uiState.isAdmin) "Society Helpdesk" else "My Complaints",
                subtitle = if (uiState.isAdmin) "Manage resident tickets & complaints" else "Track and raise maintenance requests"
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateComplaintClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Raise Issue") }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            LoadingStateView(modifier = Modifier.padding(innerPadding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Search bar
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    placeholder = { Text("Search by title, flat, category...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Status Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = uiState.currentFilter == ComplaintFilter.ALL,
                        onClick = { viewModel.onFilterSelected(ComplaintFilter.ALL) },
                        label = { Text("All (${uiState.complaints.size})", fontSize = 12.sp) }
                    )
                    FilterChip(
                        selected = uiState.currentFilter == ComplaintFilter.OPEN,
                        onClick = { viewModel.onFilterSelected(ComplaintFilter.OPEN) },
                        label = { Text("Open", fontSize = 12.sp) }
                    )
                    FilterChip(
                        selected = uiState.currentFilter == ComplaintFilter.IN_PROGRESS,
                        onClick = { viewModel.onFilterSelected(ComplaintFilter.IN_PROGRESS) },
                        label = { Text("In Progress", fontSize = 12.sp) }
                    )
                    FilterChip(
                        selected = uiState.currentFilter == ComplaintFilter.RESOLVED,
                        onClick = { viewModel.onFilterSelected(ComplaintFilter.RESOLVED) },
                        label = { Text("Resolved", fontSize = 12.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Complaint Items List
                if (uiState.filteredComplaints.isEmpty()) {
                    EmptyStateView(
                        title = "No Complaints Found",
                        message = if (uiState.searchQuery.isNotBlank())
                            "No complaints match '${uiState.searchQuery}'"
                        else "No complaints under the selected filter.",
                        icon = Icons.Default.Warning,
                        actionButtonText = "Raise New Issue",
                        onActionClick = onCreateComplaintClick
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.filteredComplaints,
                            key = { it.id }
                        ) { complaint ->
                            ComplaintItemCard(
                                complaint = complaint,
                                showResidentInfo = uiState.isAdmin,
                                onClick = { onComplaintClick(complaint.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ComplaintItemCard(
    complaint: Complaint,
    showResidentInfo: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = complaint.category.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                ComplaintStatusBadge(status = complaint.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = complaint.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = complaint.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showResidentInfo) {
                    Text(
                        text = "Flat ${complaint.flatNumber} • ${complaint.residentName}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                } else {
                    PriorityBadge(priority = complaint.priority)
                }

                Text(
                    text = complaint.createdAt,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}
