package io.jadu.remindmex.remindMe.domain.usecase

import android.net.Uri
import android.util.Log
import io.jadu.remindmex.remindMe.data.models.Reminder
import io.jadu.remindmex.remindMe.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.compose.koinInject

class AddReminderUseCase(
    private val repository: ReminderRepository
) {
    operator fun invoke(reminder: Reminder, imageUri: Uri?): Flow<UploadState> = flow {
        emit(UploadState.Uploading)

        val imageUrl = if (imageUri != null) {
            val result = repository.uploadImage(imageUri)
            if (result.isSuccess) {
                result.getOrNull() ?: ""
            } else {
                emit(UploadState.Error(result.exceptionOrNull() ?: Exception("Image upload failed")))
                return@flow
            }
        } else ""

        val reminderWithImage = reminder.copy(imageUrl = imageUrl)
        val result = repository.addReminder(reminderWithImage)

        if (result.isSuccess) {
            emit(UploadState.Success("Reminder added successfully"))
        } else {
            emit(UploadState.Error(result.exceptionOrNull() ?: Exception("Reminder upload failed")))
        }
    }
}

class GetRemindersUseCase (
    private val repository: ReminderRepository
) {
    operator fun invoke(): Flow<List<Reminder>> {
        return repository.getReminders()
    }
}

class UpdateReminderUseCase (
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(reminder: Reminder): Result<Unit> {
        return repository.updateReminder(reminder)
    }
}

class DeleteReminderUseCase (
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repository.deleteReminder(id)
    }
}

sealed class UploadState {
    object Uploading : UploadState()
    data class Success(val message: String) : UploadState()
    data class Error(val throwable: Throwable) : UploadState()
}