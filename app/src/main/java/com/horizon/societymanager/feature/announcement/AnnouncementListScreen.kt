package com.horizon.societymanager.feature.announcement

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.horizon.societymanager.core.ui.EmptyStateView
import com.horizon.societymanager.core.ui.HorizonTopAppBar
import com.horizon.societymanager.core.ui.LoadingStateView
import com.horizon.societymanager.feature.dashboard.AnnouncementItemCard

@Composable
fun AnnouncementListScreen(
    onAnnouncementClick: (String) -> Unit,
    onAddAnnouncementClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AnnouncementViewModel = hiltViewModel()
) {
    val uiState by viewModel.listUiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HorizonTopAppBar(
                title = "Announcements",
                subtitle = "Society notices & upcoming events"
            )
        },
        floatingActionButton = {
            // Admin only add button
            if (uiState.isAdmin) {
                FloatingActionButton(
                    onClick = onAddAnnouncementClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Post Announcement"
                    )
                }
            }
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
                    placeholder = { Text("Search notices and events...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Filter Tabs (All / Notices / Events)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = uiState.currentFilter == AnnouncementFilter.ALL,
                        onClick = { viewModel.onFilterSelected(AnnouncementFilter.ALL) },
                        label = { Text("All (${uiState.announcements.size})") }
                    )
                    FilterChip(
                        selected = uiState.currentFilter == AnnouncementFilter.NOTICES,
                        onClick = { viewModel.onFilterSelected(AnnouncementFilter.NOTICES) },
                        label = { Text("Notices") }
                    )
                    FilterChip(
                        selected = uiState.currentFilter == AnnouncementFilter.EVENTS,
                        onClick = { viewModel.onFilterSelected(AnnouncementFilter.EVENTS) },
                        label = { Text("Events") }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // List
                if (uiState.filteredAnnouncements.isEmpty()) {
                    EmptyStateView(
                        title = "No Announcements Found",
                        message = if (uiState.searchQuery.isNotBlank())
                            "No announcements match '${uiState.searchQuery}'"
                        else "There are currently no announcements in this category.",
                        icon = Icons.Default.Notifications,
                        actionButtonText = if (uiState.isAdmin) "Post an Announcement" else null,
                        onActionClick = if (uiState.isAdmin) onAddAnnouncementClick else null
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.filteredAnnouncements,
                            key = { it.id }
                        ) { announcement ->
                            AnnouncementItemCard(
                                announcement = announcement,
                                onClick = { onAnnouncementClick(announcement.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
