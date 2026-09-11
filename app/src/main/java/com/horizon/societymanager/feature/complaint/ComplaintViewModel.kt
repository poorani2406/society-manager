package com.horizon.societymanager.feature.complaint

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.horizon.societymanager.core.session.SessionManager
import com.horizon.societymanager.data.model.Complaint
import com.horizon.societymanager.data.model.ComplaintCategory
import com.horizon.societymanager.data.model.ComplaintPriority
import com.horizon.societymanager.data.model.ComplaintStatus
import com.horizon.societymanager.data.model.UserRole
import com.horizon.societymanager.data.repository.ComplaintRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ComplaintFilter {
    ALL,
    OPEN,
    IN_PROGRESS,
    RESOLVED
}

data class ComplaintListUiState(
    val complaints: List<Complaint> = emptyList(),
    val filteredComplaints: List<Complaint> = emptyList(),
    val currentFilter: ComplaintFilter = ComplaintFilter.ALL,
    val searchQuery: String = "",
    val isAdmin: Boolean = false,
    val currentUserId: String? = null,
    val isLoading: Boolean = false
)

data class CreateComplaintUiState(
    val title: String = "",
    val description: String = "",
    val category: ComplaintCategory = ComplaintCategory.PLUMBING,
    val priority: ComplaintPriority = ComplaintPriority.MEDIUM,
    val titleError: String? = null,
    val descriptionError: String? = null,
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ComplaintViewModel @Inject constructor(
    private val complaintRepository: ComplaintRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _currentFilter = MutableStateFlow(ComplaintFilter.ALL)
    private val _searchQuery = MutableStateFlow("")
    private val _detailComplaint = MutableStateFlow<Complaint?>(null)
    val detailComplaint: StateFlow<Complaint?> = _detailComplaint.asStateFlow()

    private val _createState = MutableStateFlow(CreateComplaintUiState())
    val createState: StateFlow<CreateComplaintUiState> = _createState.asStateFlow()

    val listUiState: StateFlow<ComplaintListUiState> = combine(
        complaintRepository.complaints,
        _currentFilter,
        _searchQuery,
        sessionManager.currentUser
    ) { allComplaints, filter, query, user ->
        val userComplaints = if (user?.role == UserRole.ADMIN) {
            allComplaints
        } else {
            allComplaints.filter { it.residentId == user?.id }
        }

        val filtered = userComplaints.filter { complaint ->
            val matchesFilter = when (filter) {
                ComplaintFilter.ALL -> true
                ComplaintFilter.OPEN -> complaint.status == ComplaintStatus.OPEN
                ComplaintFilter.IN_PROGRESS -> complaint.status == ComplaintStatus.IN_PROGRESS
                ComplaintFilter.RESOLVED -> complaint.status == ComplaintStatus.RESOLVED
            }

            val matchesQuery = query.isBlank() ||
                    complaint.title.contains(query, ignoreCase = true) ||
                    complaint.description.contains(query, ignoreCase = true) ||
                    complaint.category.displayName.contains(query, ignoreCase = true) ||
                    complaint.flatNumber.contains(query, ignoreCase = true)

            matchesFilter && matchesQuery
        }

        ComplaintListUiState(
            complaints = userComplaints,
            filteredComplaints = filtered,
            currentFilter = filter,
            searchQuery = query,
            isAdmin = user?.role == UserRole.ADMIN,
            currentUserId = user?.id,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ComplaintListUiState(isLoading = true)
    )

    fun onFilterSelected(filter: ComplaintFilter) {
        _currentFilter.value = filter
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun loadComplaintDetail(id: String) {
        viewModelScope.launch {
            val complaint = complaintRepository.getComplaintById(id)
            _detailComplaint.value = complaint
        }
    }

    fun updateStatus(id: String, newStatus: ComplaintStatus, adminNotes: String? = null) {
        viewModelScope.launch {
            val result = complaintRepository.updateComplaintStatus(id, newStatus, adminNotes)
            result.onSuccess { updated ->
                _detailComplaint.value = updated
            }
        }
    }

    // Create Complaint Form Handling
    fun onTitleChanged(title: String) {
        _createState.update { it.copy(title = title, titleError = null) }
    }

    fun onDescriptionChanged(desc: String) {
        _createState.update { it.copy(description = desc, descriptionError = null) }
    }

    fun onCategoryChanged(category: ComplaintCategory) {
        _createState.update { it.copy(category = category) }
    }

    fun onPriorityChanged(priority: ComplaintPriority) {
        _createState.update { it.copy(priority = priority) }
    }

    fun submitComplaint() {
        val title = _createState.value.title.trim()
        val desc = _createState.value.description.trim()

        var hasError = false
        if (title.isEmpty()) {
            _createState.update { it.copy(titleError = "Please summarize the issue") }
            hasError = true
        }
        if (desc.isEmpty()) {
            _createState.update { it.copy(descriptionError = "Please describe the complaint details") }
            hasError = true
        }

        if (hasError) return

        val currentUser = sessionManager.currentUser.value ?: return

        viewModelScope.launch {
            _createState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val result = complaintRepository.createComplaint(
                residentId = currentUser.id,
                residentName = currentUser.name,
                flatNumber = currentUser.flatNumber,
                category = _createState.value.category,
                title = title,
                description = desc,
                priority = _createState.value.priority
            )

            result.onSuccess {
                _createState.update { it.copy(isSubmitting = false, isSuccess = true) }
            }.onFailure { error ->
                _createState.update {
                    it.copy(isSubmitting = false, errorMessage = error.message ?: "Failed to raise complaint")
                }
            }
        }
    }

    fun resetCreateState() {
        _createState.value = CreateComplaintUiState()
    }
}
