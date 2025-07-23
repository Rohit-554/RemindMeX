package io.jadu.remindmex.remindMe.presentation.viewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.jadu.remindmex.remindMe.data.models.Reminder
import io.jadu.remindmex.remindMe.domain.usecase.AddReminderUseCase
import io.jadu.remindmex.remindMe.domain.usecase.DeleteReminderUseCase
import io.jadu.remindmex.remindMe.domain.usecase.GetRemindersUseCase
import io.jadu.remindmex.remindMe.domain.usecase.UpdateReminderUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReminderViewModel(
    private val addReminderUseCase: AddReminderUseCase,
    private val getRemindersUseCase: GetRemindersUseCase,
    private val updateReminderUseCase: UpdateReminderUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase
) : ViewModel() {

    /*
    * check for the logic where the reminders are empty
    * */
    private val _uiState = MutableStateFlow(ReminderUiState())
    val uiState: StateFlow<ReminderUiState> = _uiState.asStateFlow()

    val reminders = getRemindersUseCase()
        .onStart { _uiState.value = _uiState.value.copy(isLoading = true) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        ).also {
            viewModelScope.launch {
                it.collect { list ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                    )
                }
            }
        }
    
    fun addReminder(title: String, description: String, imageUri: Uri?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val reminder = Reminder(
                title = title,
                description = description
            )

            val result = addReminderUseCase(reminder, imageUri)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Reminder added successfully"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Failed to add reminder"
                )
            }
        }
    }
    
    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch {
            val result = updateReminderUseCase(reminder)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    message = "Reminder updated successfully"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    error = result.exceptionOrNull()?.message ?: "Failed to update reminder"
                )
            }
        }
    }
    
    fun deleteReminder(id: String) {
        viewModelScope.launch {
            val result = deleteReminderUseCase(id)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    message = "Reminder deleted successfully"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    error = result.exceptionOrNull()?.message ?: "Failed to delete reminder"
                )
            }
        }
    }
    
    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null, error = null)
    }
}

data class ReminderUiState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val error: String? = null,
    val isEmpty: Boolean? = false
)
