package io.jadu.remindmex.remindMe.presentation.components.ui

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import java.io.File
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import io.jadu.remindmex.remindMe.data.models.Reminder
import io.jadu.remindmex.remindMe.presentation.utils.ButtonUI
import io.jadu.remindmex.remindMe.presentation.utils.CronosButton
import io.jadu.remindmex.remindMe.presentation.utils.HSpacer
import io.jadu.remindmex.remindMe.presentation.utils.VSpacer
import io.jadu.remindmex.remindMe.presentation.utils.bounceClickable
import io.jadu.remindmex.ui.theme.BodyLarge
import io.jadu.remindmex.ui.theme.BodyNormal
import io.jadu.remindmex.ui.theme.BodySmall
import io.jadu.remindmex.ui.theme.ElementsColors
import io.jadu.remindmex.ui.theme.MajorColors
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar

@Composable
fun CustomDatePickerDialog(
    onDismiss: () -> Unit,
    onDateSelected: (Long) -> Unit,
    initialDate: Long? = null
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate ?: System.currentTimeMillis()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(millis)
                    }
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTimePickerDialog(
    onDismiss: () -> Unit,
    onTimeSelected: (Long) -> Unit,
    initialTime: Long? = null
) {
    val timePickerState = rememberTimePickerState(
        initialHour = if (initialTime != null) {
            // Convert milliseconds since epoch to LocalDateTime, then extract hour
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(initialTime),
                ZoneId.systemDefault()
            ).hour
        } else 12,
        initialMinute = if (initialTime != null) {
            // Convert milliseconds since epoch to LocalDateTime, then extract minute
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(initialTime),
                ZoneId.systemDefault()
            ).minute
        } else 0
    )

    TimePickerDialog(
        title = {
            Text(
                text = "Select Time",
                style = BodyLarge(),
                modifier = Modifier.padding(16.dp)
            )
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    // Create a Calendar instance with current date but selected time
                    val calendar = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        set(Calendar.MINUTE, timePickerState.minute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    onTimeSelected(calendar.timeInMillis)
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        TimePicker(
            state = timePickerState,
            modifier = Modifier.padding(16.dp)
        )
    }
}