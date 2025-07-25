package io.jadu.remindmex.remindMe.presentation.screens

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import io.jadu.remindmex.remindMe.data.models.Reminder
import io.jadu.remindmex.remindMe.presentation.components.ui.showSnackBar
import io.jadu.remindmex.remindMe.presentation.route.NavRoute
import io.jadu.remindmex.remindMe.presentation.screens.itemComponents.ReminderItem
import io.jadu.remindmex.remindMe.presentation.utils.VSpacer
import io.jadu.remindmex.remindMe.presentation.utils.bounceClickable
import io.jadu.remindmex.remindMe.presentation.viewModels.LoginViewModel
import io.jadu.remindmex.remindMe.presentation.viewModels.ReminderViewModel
import io.jadu.remindmex.remindMe.presentation.viewModels.ThemeViewModel
import io.jadu.remindmex.ui.theme.BodyLarge
import io.jadu.remindmex.ui.theme.BodyNormal
import io.jadu.remindmex.ui.theme.BodySmall
import io.jadu.remindmex.ui.theme.ElementsColors
import io.jadu.remindmex.ui.theme.H1TextStyle
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


enum class ThemeOption {
    SYSTEM, LIGHT, DARK
}

@Composable
fun HomeScreen(rootNavController:NavController) {
    val viewModel: ReminderViewModel = koinViewModel()
    val reminders by viewModel.reminders.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val isLoading = uiState.isLoading
    val showAddReminderDialog = remember { mutableStateOf(false) }

    if (reminders.isNotEmpty()) {
        reminders.forEach { reminder ->
            if (!reminder.isCompleted && reminder.timestamp > System.currentTimeMillis()) {
                viewModel.scheduleReminder(context, reminder)
            }
        }
    }

    val requestPermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions.launch(
                arrayOf(
                    POST_NOTIFICATIONS
                )
            )
        }
    }

    ReminderScreen(
        reminders = reminders,
        isLoading = isLoading,
        onToggleComplete = { reminder ->
            viewModel.updateReminder(reminder.copy(isCompleted = !reminder.isCompleted))
        },
        onDelete = { reminder ->
            viewModel.deleteReminder(reminder.id)
            showSnackBar("Reminder deleted successfully", positiveMessage = true, duration = SnackbarDuration.Long)
        },
        onAddClick = {
            showAddReminderDialog.value = true
        },
        onShareMessage = {
            viewModel.generateMessage(context = context, prompt = "Write a short birthday wish for Prashant.")
        },
        navController = rootNavController
    )

    if (showAddReminderDialog.value) {
        rootNavController.navigate(NavRoute.AddReminder)
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun ReminderScreen(
    reminders: List<Reminder>,
    isLoading: Boolean,
    onToggleComplete: (Reminder) -> Unit,
    onDelete: (Reminder) -> Unit,
    onAddClick: () -> Unit,
    onShareMessage: () -> Unit,
    themeViewModel: ThemeViewModel = koinViewModel(),
    authViewModel: LoginViewModel = koinViewModel(),
    navController: NavController
) {
    val userName = Firebase.auth.currentUser?.displayName ?: "User"
    val selectedDate = remember { mutableStateOf(LocalDate.now()) }
    var expanded by remember { mutableStateOf(false) }
    val filteredReminders = reminders.filter {
        it.timestamp.toLocalDate() == selectedDate.value
    }

    val completionRate = if (filteredReminders.isNotEmpty()) {
        filteredReminders.count { it.isCompleted } / filteredReminders.size.toFloat()
    } else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Chronos",
                        style = H1TextStyle().copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                },
                actions = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Box {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.Settings, contentDescription = "Theme Options")
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Follow System", style = BodyNormal()) },
                                    onClick = {
                                        themeViewModel.setTheme(ThemeOption.SYSTEM)
                                        expanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Light Theme", style = BodyNormal()) },
                                    onClick = {
                                        themeViewModel.setTheme(ThemeOption.LIGHT)
                                        expanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Dark Theme", style = BodyNormal()) },
                                    onClick = {
                                        themeViewModel.setTheme(ThemeOption.DARK)
                                        expanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Logout", style = BodyNormal()) },
                                    onClick = {
                                        authViewModel.logout()
                                        expanded = false
                                        navController.navigate(NavRoute.Login) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FloatingActionButton(onClick = onShareMessage) {
                    Icon(Icons.Default.Share, contentDescription = "Add Reminder")
                }
                VSpacer()
                FloatingActionButton(onClick = onAddClick) {
                    Icon(Icons.Default.Add, contentDescription = "Add Reminder")
                }
            }

        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("Hi ${userName}, You completing plan!", style = BodyNormal().copy(fontWeight = FontWeight.Bold))
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
                        CircularWavyProgressIndicator()
                    }

                    "empty" -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No reminders for today", style = MaterialTheme.typography.bodyMedium)
                    }

                    "list" -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        filteredReminders.sortedByDescending { it.timestamp }.groupBy {
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
                                Column(modifier = Modifier.padding(start = 8.dp)) {
                                    Text(reminder.timestamp.toLocalTimeString(), style = BodySmall())
                                    VSpacer(8.dp)
                                    ReminderItem(
                                        reminder = reminder,
                                        onToggleComplete = onToggleComplete,
                                        onDelete = { onDelete(reminder) },
                                        onTapped = { selectedReminder ->
                                            navController.navigate("add_reminder/${selectedReminder.id}")
                                        }
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

@RequiresApi(Build.VERSION_CODES.S)
private fun requestExactAlarmPermission(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    if (!alarmManager.canScheduleExactAlarms()) {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        context.startActivity(intent)
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