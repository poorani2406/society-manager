package com.horizon.societymanager.feature.announcement

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.horizon.societymanager.core.ui.HorizonTopAppBar
import com.horizon.societymanager.data.model.AnnouncementPriority
import com.horizon.societymanager.data.model.AnnouncementType

@Composable
fun AddAnnouncementScreen(
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AnnouncementViewModel = hiltViewModel()
) {
    val addState by viewModel.addState.collectAsState()

    LaunchedEffect(addState.isSuccess) {
        if (addState.isSuccess) {
            viewModel.resetAddState()
            onSuccess()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HorizonTopAppBar(
                title = "Create Announcement",
                subtitle = "Post a notice or event for residents",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Type Selector (Notice vs Event)
            Text(
                text = "Announcement Type",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChip(
                    selected = addState.type == AnnouncementType.NOTICE,
                    onClick = { viewModel.onAddTypeChanged(AnnouncementType.NOTICE) },
                    label = { Text("General Notice") }
                )
                FilterChip(
                    selected = addState.type == AnnouncementType.EVENT,
                    onClick = { viewModel.onAddTypeChanged(AnnouncementType.EVENT) },
                    label = { Text("Society Event") }
                )
            }

            // Priority Selector
            Text(
                text = "Priority",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChip(
                    selected = addState.priority == AnnouncementPriority.NORMAL,
                    onClick = { viewModel.onAddPriorityChanged(AnnouncementPriority.NORMAL) },
                    label = { Text("Normal Priority") }
                )
                FilterChip(
                    selected = addState.priority == AnnouncementPriority.URGENT,
                    onClick = { viewModel.onAddPriorityChanged(AnnouncementPriority.URGENT) },
                    label = { Text("Urgent Alert") }
                )
            }

            // Title Field
            OutlinedTextField(
                value = addState.title,
                onValueChange = viewModel::onAddTitleChanged,
                label = { Text("Announcement Title *") },
                placeholder = { Text("e.g. Lift Maintenance Schedule") },
                isError = addState.titleError != null,
                supportingText = {
                    addState.titleError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Description Field
            OutlinedTextField(
                value = addState.description,
                onValueChange = viewModel::onAddDescriptionChanged,
                label = { Text("Description / Details *") },
                placeholder = { Text("Provide full details of the notice or event...") },
                isError = addState.descriptionError != null,
                supportingText = {
                    addState.descriptionError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 6
            )

            // Event Date (Optional / if Event)
            OutlinedTextField(
                value = addState.eventDate,
                onValueChange = viewModel::onAddEventDateChanged,
                label = { Text("Date & Time (Optional)") },
                placeholder = { Text("e.g. Sunday, 25 Sep at 05:00 PM") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Location (Optional)
            OutlinedTextField(
                value = addState.location,
                onValueChange = viewModel::onAddLocationChanged,
                label = { Text("Location / Venue (Optional)") },
                placeholder = { Text("e.g. Central Lawn / Tower A & B") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            if (addState.errorMessage != null) {
                Text(
                    text = addState.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = viewModel::submitAnnouncement,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !addState.isSaving,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (addState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Publish Announcement",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
