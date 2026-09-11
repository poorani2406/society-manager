package com.horizon.societymanager.feature.complaint

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.horizon.societymanager.core.ui.HorizonTopAppBar
import com.horizon.societymanager.data.model.ComplaintCategory
import com.horizon.societymanager.data.model.ComplaintPriority

@Composable
fun CreateComplaintScreen(
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ComplaintViewModel = hiltViewModel()
) {
    val createState by viewModel.createState.collectAsState()

    LaunchedEffect(createState.isSuccess) {
        if (createState.isSuccess) {
            viewModel.resetCreateState()
            onSuccess()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HorizonTopAppBar(
                title = "Raise New Issue",
                subtitle = "Submit maintenance or society request",
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
            // Category Selection
            Text(
                text = "Select Category *",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ComplaintCategory.entries.take(3).forEach { category ->
                    FilterChip(
                        selected = createState.category == category,
                        onClick = { viewModel.onCategoryChanged(category) },
                        label = { Text(category.displayName, fontSize = 12.sp) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ComplaintCategory.entries.drop(3).forEach { category ->
                    FilterChip(
                        selected = createState.category == category,
                        onClick = { viewModel.onCategoryChanged(category) },
                        label = { Text(category.displayName, fontSize = 12.sp) }
                    )
                }
            }

            // Priority Selection
            Text(
                text = "Priority Level",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ComplaintPriority.entries.forEach { priority ->
                    FilterChip(
                        selected = createState.priority == priority,
                        onClick = { viewModel.onPriorityChanged(priority) },
                        label = { Text(priority.displayName) }
                    )
                }
            }

            // Title Field
            OutlinedTextField(
                value = createState.title,
                onValueChange = viewModel::onTitleChanged,
                label = { Text("Issue Title / Summary *") },
                placeholder = { Text("e.g. Geyser switch sparking in bathroom") },
                isError = createState.titleError != null,
                supportingText = {
                    createState.titleError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Description Field
            OutlinedTextField(
                value = createState.description,
                onValueChange = viewModel::onDescriptionChanged,
                label = { Text("Detailed Description *") },
                placeholder = { Text("Please provide clear location and description of the issue...") },
                isError = createState.descriptionError != null,
                supportingText = {
                    createState.descriptionError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 6
            )

            if (createState.errorMessage != null) {
                Text(
                    text = createState.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Submit Button
            Button(
                onClick = viewModel::submitComplaint,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !createState.isSubmitting,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (createState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Submit Complaint",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
