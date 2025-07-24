package io.jadu.remindmex.remindMe.presentation.screens

import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.copy
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.error
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import io.jadu.remindmex.R
import io.jadu.remindmex.remindMe.data.models.Reminder
import io.jadu.remindmex.remindMe.presentation.components.ui.showSnackBar
import io.jadu.remindmex.remindMe.presentation.screens.rememberItem.AddReminderDialog
import io.jadu.remindmex.remindMe.presentation.screens.rememberItem.ReminderItem
import io.jadu.remindmex.remindMe.presentation.utils.VSpacer
import io.jadu.remindmex.remindMe.presentation.viewModels.ReminderViewModel
import io.jadu.remindmex.ui.theme.BodyLarge
import io.jadu.remindmex.ui.theme.BodyNormal
import io.jadu.remindmex.ui.theme.BodySmall
import io.jadu.remindmex.ui.theme.ElementsColors
import io.jadu.remindmex.ui.theme.MajorColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


@Composable
fun HomeScreen(rootNavController:NavController) {
    val viewModel: ReminderViewModel = koinViewModel()
    val reminders by viewModel.reminders.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val isLoading = uiState.isLoading
    val showAddReminderDialog = remember { mutableStateOf(false) }
    ReminderScreen(
        reminders = reminders,
        isLoading = isLoading,
        onToggleComplete = { reminder ->
            viewModel.updateReminder(reminder.copy(isCompleted = !reminder.isCompleted))
        },
        onDelete = { reminder ->
            viewModel.deleteReminder(reminder.id)
            showSnackBar("Reminder deleted successfully")
        },
        onAddClick = {
            showAddReminderDialog.value = true
        }
    )

    if (showAddReminderDialog.value) {
        AddReminderDialog(
            onDismiss = { showAddReminderDialog.value = false },
            onAdd = { title, description, imageUri ->
                viewModel.addReminder(title, description, imageUri)
                showAddReminderDialog.value = false
            }
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ReminderScreen(
    reminders: List<Reminder>,
    isLoading: Boolean,
    onToggleComplete: (Reminder) -> Unit,
    onDelete: (Reminder) -> Unit,
    onAddClick: () -> Unit,
) {
    val selectedDate = remember { mutableStateOf(LocalDate.now()) }

    val filteredReminders = reminders.filter {
        it.timestamp.toLocalDate() == selectedDate.value
    }

    val completionRate = if (filteredReminders.isNotEmpty()) {
        filteredReminders.count { it.isCompleted } / filteredReminders.size.toFloat()
    } else 0f

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Reminder")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("Hi, You completing plan!", style = BodyNormal().copy(fontWeight = FontWeight.Bold))
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                progress = { completionRate },
            )
            Text(
                "${filteredReminders.count { !it.isCompleted }} more meds to go",
                style = BodySmall()
            )

            Spacer(Modifier.height(16.dp))

            DateSelector(
                selectedDate = selectedDate.value,
                onDateSelected = { selectedDate.value = it }
            )

            Spacer(Modifier.height(16.dp))

            AnimatedContent(
                targetState = when {
                    isLoading -> "loading"
                    filteredReminders.isEmpty() -> "empty"
                    else -> "list"
                },
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) { state ->
                when (state) {
                    "loading" -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }

                    "empty" -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No reminders for today", style = MaterialTheme.typography.bodyMedium)
                    }

                    "list" -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        filteredReminders.sortedBy { it.timestamp }.groupBy {
                            it.timestamp.toLocalDate()
                        }.forEach { (date, groupedReminders) ->
                            item {
                                Text(
                                    date.format(DateTimeFormatter.ofPattern("d MMMM yyyy")),
                                    style = BodyLarge()
                                )
                            }

                            item {
                                VSpacer(12.dp)
                            }

                            itemsIndexed(groupedReminders, key = { _, it -> it.id }) { index, reminder ->
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    // Timeline Column
                                    Column(
                                        modifier = Modifier
                                            .width(24.dp)
                                            .fillMaxHeight(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        if (index > 0) {
                                            Box(
                                                modifier = Modifier
                                                    .width(2.dp)
                                                    .weight(1f)
                                                    .background(ElementsColors.DarkGray.color)
                                            )
                                        } else {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }

                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .background(Color.Green, shape = CircleShape)
                                        )

                                        if (index < groupedReminders.lastIndex) {
                                            Box(
                                                modifier = Modifier
                                                    .width(2.dp)
                                                    .weight(1f)
                                                    .background(ElementsColors.DarkGray.color)
                                            )
                                        } else {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }

                                    // Reminder Content
                                    Column(modifier = Modifier.padding(start = 8.dp)) {
                                        Text(reminder.timestamp.toLocalTimeString(), style = BodySmall())
                                        VSpacer(8.dp)
                                        ReminderItem(
                                            reminder = reminder,
                                            onToggleComplete = onToggleComplete,
                                            onDelete = { onDelete(reminder) }
                                        )
                                    }
                                }
                            }

                        }
                    }

                }
            }
        }
    }
}

@Composable
fun TimelineMarker(
    modifier: Modifier = Modifier,
    isFirst: Boolean,
    isLast: Boolean,
    color: Color = Color.Green,
    lineColor: Color = ElementsColors.DarkGray.color,
) {
    Canvas(
        modifier = modifier
            .size(width = 24.dp, height = 80.dp) // match card height
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = 6.dp.toPx()

        if (!isFirst) {
            drawLine(
                color = lineColor,
                start = Offset(centerX, 0f),
                end = Offset(centerX, centerY - radius),
                strokeWidth = 4f
            )
        }

        // Draw dot
        drawCircle(
            color = color,
            radius = radius,
            center = Offset(centerX, centerY)
        )

        if (!isLast) {
            drawLine(
                color = lineColor,
                start = Offset(centerX, centerY + radius),
                end = Offset(centerX, size.height),
                strokeWidth = 4f
            )
        }
    }
}


@Composable
fun DateSelector(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    val dates = (0..6).map { today.plusDays(it.toLong()) }

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(dates) { date ->
            val isSelected = date == selectedDate
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) ElementsColors.DarkGray.color else Color.Transparent)
                    .clickable { onDateSelected(date) }
                    .padding(vertical = 8.dp, horizontal = 12.dp)
            ) {
                Text(date.dayOfWeek.name.take(3), style = BodySmall())
                Text(date.dayOfMonth.toString(), style = BodyNormal())
            }
        }
    }
}

fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
}

fun Long.toLocalTimeString(): String {
    val time = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalTime()
    return time.truncatedTo(ChronoUnit.MINUTES).toString() // "13:30"
}