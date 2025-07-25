package io.jadu.remindmex.remindMe.presentation.viewModels

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.jadu.remindmex.remindMe.data.models.Reminder
import io.jadu.remindmex.remindMe.domain.usecase.AddReminderUseCase
import io.jadu.remindmex.remindMe.domain.usecase.DeleteReminderUseCase
import io.jadu.remindmex.remindMe.domain.usecase.GenerateGreetingUseCase
import io.jadu.remindmex.remindMe.domain.usecase.GetRemindersUseCase
import io.jadu.remindmex.remindMe.domain.usecase.UpdateReminderUseCase
import io.jadu.remindmex.remindMe.domain.usecase.UploadState
import io.jadu.remindmex.remindMe.presentation.components.ui.showSnackBar
import io.jadu.remindmex.remindMe.services.ReminderReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.net.URLEncoder

class ReminderViewModel(
    private val addReminderUseCase: AddReminderUseCase,
    private val getRemindersUseCase: GetRemindersUseCase,
    private val updateReminderUseCase: UpdateReminderUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase,
    private val generateGreetingUseCase: GenerateGreetingUseCase
) : ViewModel() {

    /*
    * check for the logic where the reminders are empty
    * */
    private val _uiState = MutableStateFlow(ReminderUiState())
    val uiState: StateFlow<ReminderUiState> = _uiState.asStateFlow()

    private val _aiMessage = mutableStateOf("")
    val aiMessage: State<String> = _aiMessage

    val reminders = getRemindersUseCase()
        .onStart { _uiState.value = _uiState.value.copy(isLoading = true) }
        .onEach { _uiState.value = _uiState.value.copy(isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun scheduleReminder(context: Context, reminder: Reminder) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("title", reminder.title)
            putExtra("message", reminder.description)
            putExtra("reminder_id", reminder.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmClockInfo = AlarmManager.AlarmClockInfo(
            reminder.timestamp,
            pendingIntent
        )
        try {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            reminder.timestamp,
                            pendingIntent
                        )
                    } else {
                        // Fallback to inexact alarm
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            reminder.timestamp,
                            pendingIntent
                        )
                    }
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminder.timestamp,
                        pendingIntent
                    )
                }
                else -> {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        reminder.timestamp,
                        pendingIntent
                    )
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun fetchAndShareAiGreeting(context: Context) {
        viewModelScope.launch {
            try {
                val prompt = "Write a motivational message for completing a task"
                val aiMessage = fetchAiMessage(prompt)
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, aiMessage)
                }
                context.startActivity(Intent.createChooser(intent, "Share via"))
            } catch (e: Exception) {
                e.printStackTrace()
                showSnackBar(
                    "Something went wrong while fetching AI message",
                )
            }
        }
    }

    fun generateMessage(prompt: String, context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val message = generateGreetingUseCase.execute(prompt)
                _aiMessage.value = message
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, message)
                }
                context.startActivity(Intent.createChooser(intent, "Share via"))
            } catch (e: Exception) {
                showSnackBar(
                    "Something went wrong while generating message: ${e.message}",
                )
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private suspend fun fetchAiMessage(prompt: String): String {
        val encodedPrompt = URLEncoder.encode(prompt, "UTF-8")
        val url = URL("https://text.pollinations.ai/prompt/$encodedPrompt")

        return withContext(Dispatchers.IO) {
            url.readText() // Simple HTTP GET
        }
    }

    fun addReminder(context:Context, title: String, description: String, imageUri: Uri?, time: Long? = null) {

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val reminder = Reminder(
                title = title,
                description = description,
                timestamp = time ?: System.currentTimeMillis(),
            )

            var compressedUri: Uri? = null
            if (imageUri != null) {
                withContext(Dispatchers.IO) {
                    val inputStream = context.contentResolver.openInputStream(imageUri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()

                    val file = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
                    val outputStream = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
                    outputStream.flush()
                    outputStream.close()
                    compressedUri = Uri.fromFile(file)
                }
            }

            val result = addReminderUseCase(reminder, compressedUri).collect{state->
                when (state) {
                    is UploadState.Uploading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                    is UploadState.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            message = state.message
                        )
                        scheduleReminder(context, reminder)
                    }
                    is UploadState.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = state.throwable.message ?: "Failed to add reminder"
                        )
                    }
                }

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
