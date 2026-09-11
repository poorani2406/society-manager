package com.horizon.societymanager.feature.announcement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.horizon.societymanager.core.session.SessionManager
import com.horizon.societymanager.data.model.Announcement
import com.horizon.societymanager.data.model.AnnouncementPriority
import com.horizon.societymanager.data.model.AnnouncementType
import com.horizon.societymanager.data.model.UserRole
import com.horizon.societymanager.data.repository.AnnouncementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AnnouncementFilter {
    ALL,
    NOTICES,
    EVENTS
}

data class AnnouncementListUiState(
    val announcements: List<Announcement> = emptyList(),
    val filteredAnnouncements: List<Announcement> = emptyList(),
    val currentFilter: AnnouncementFilter = AnnouncementFilter.ALL,
    val searchQuery: String = "",
    val isAdmin: Boolean = false,
    val isLoading: Boolean = false
)

data class AddAnnouncementUiState(
    val title: String = "",
    val description: String = "",
    val type: AnnouncementType = AnnouncementType.NOTICE,
    val priority: AnnouncementPriority = AnnouncementPriority.NORMAL,
    val eventDate: String = "",
    val location: String = "",
    val titleError: String? = null,
    val descriptionError: String? = null,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AnnouncementViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _currentFilter = MutableStateFlow(AnnouncementFilter.ALL)
    private val _searchQuery = MutableStateFlow("")
    private val _detailAnnouncement = MutableStateFlow<Announcement?>(null)
    val detailAnnouncement: StateFlow<Announcement?> = _detailAnnouncement.asStateFlow()

    private val _addState = MutableStateFlow(AddAnnouncementUiState())
    val addState: StateFlow<AddAnnouncementUiState> = _addState.asStateFlow()

    val listUiState: StateFlow<AnnouncementListUiState> = combine(
        announcementRepository.announcements,
        _currentFilter,
        _searchQuery,
        sessionManager.currentUser
    ) { allAnnouncements, filter, query, user ->
        val filtered = allAnnouncements.filter { announcement ->
            val matchesFilter = when (filter) {
                AnnouncementFilter.ALL -> true
                AnnouncementFilter.NOTICES -> announcement.type == AnnouncementType.NOTICE
                AnnouncementFilter.EVENTS -> announcement.type == AnnouncementType.EVENT
            }
            val matchesQuery = query.isBlank() ||
                    announcement.title.contains(query, ignoreCase = true) ||
                    announcement.description.contains(query, ignoreCase = true)

            matchesFilter && matchesQuery
        }

        AnnouncementListUiState(
            announcements = allAnnouncements,
            filteredAnnouncements = filtered,
            currentFilter = filter,
            searchQuery = query,
            isAdmin = user?.role == UserRole.ADMIN,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnnouncementListUiState(isLoading = true)
    )

    fun onFilterSelected(filter: AnnouncementFilter) {
        _currentFilter.value = filter
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun loadAnnouncementDetail(id: String) {
        viewModelScope.launch {
            val announcement = announcementRepository.getAnnouncementById(id)
            _detailAnnouncement.value = announcement
        }
    }

    // Add Announcement Form Handling
    fun onAddTitleChanged(title: String) {
        _addState.update { it.copy(title = title, titleError = null) }
    }

    fun onAddDescriptionChanged(desc: String) {
        _addState.update { it.copy(description = desc, descriptionError = null) }
    }

    fun onAddTypeChanged(type: AnnouncementType) {
        _addState.update { it.copy(type = type) }
    }

    fun onAddPriorityChanged(priority: AnnouncementPriority) {
        _addState.update { it.copy(priority = priority) }
    }

    fun onAddEventDateChanged(date: String) {
        _addState.update { it.copy(eventDate = date) }
    }

    fun onAddLocationChanged(location: String) {
        _addState.update { it.copy(location = location) }
    }

    fun submitAnnouncement() {
        val title = _addState.value.title.trim()
        val desc = _addState.value.description.trim()

        var hasError = false
        if (title.isEmpty()) {
            _addState.update { it.copy(titleError = "Title is required") }
            hasError = true
        }
        if (desc.isEmpty()) {
            _addState.update { it.copy(descriptionError = "Description is required") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _addState.update { it.copy(isSaving = true, errorMessage = null) }
            val author = sessionManager.currentUser.value?.name ?: "Society Admin"
            val result = announcementRepository.addAnnouncement(
                title = title,
                description = desc,
                type = _addState.value.type,
                authorName = author,
                eventDate = _addState.value.eventDate,
                location = _addState.value.location,
                priority = _addState.value.priority
            )

            result.onSuccess {
                _addState.update { it.copy(isSaving = false, isSuccess = true) }
            }.onFailure { error ->
                _addState.update {
                    it.copy(isSaving = false, errorMessage = error.message ?: "Failed to post announcement")
                }
            }
        }
    }

    fun resetAddState() {
        _addState.value = AddAnnouncementUiState()
    }
}
