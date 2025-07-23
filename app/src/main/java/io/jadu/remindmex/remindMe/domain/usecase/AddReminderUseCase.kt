package io.jadu.remindmex.remindMe.domain.usecase

import android.net.Uri
import io.jadu.remindmex.remindMe.data.models.Reminder
import io.jadu.remindmex.remindMe.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import org.koin.compose.koinInject

class AddReminderUseCase (
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(reminder: Reminder, imageUri: Uri?): Result<String> {
        return try {
            val imageUrl = if (imageUri != null) {
                val result = repository.uploadImage(imageUri)
                if (result.isSuccess) {
                    result.getOrNull() ?: ""
                } else {
                    return Result.failure(result.exceptionOrNull() ?: Exception("Image upload failed"))
                }
            } else ""
            val reminderWithImage = reminder.copy(imageUrl = imageUrl)
            repository.addReminder(reminderWithImage)
        } catch (e: Exception) {
            Result.failure(e)
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
