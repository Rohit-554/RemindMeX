package io.jadu.remindmex.remindMe.presentation.screens.itemComponents

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController
import io.jadu.remindmex.remindMe.data.models.Reminder
import io.jadu.remindmex.remindMe.presentation.components.ui.CustomDatePickerDialog
import io.jadu.remindmex.remindMe.presentation.components.ui.CustomTimePickerDialog
import io.jadu.remindmex.remindMe.presentation.components.ui.showSnackBar
import io.jadu.remindmex.remindMe.presentation.viewModels.ReminderViewModel
import io.jadu.remindmex.ui.theme.BodyLarge
import io.jadu.remindmex.ui.theme.BodyNormal
import io.jadu.remindmex.ui.theme.BodyXLarge
import io.jadu.remindmex.ui.theme.ElementsColors
import io.jadu.remindmex.ui.theme.MajorColors
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderScreen(
    navController: NavController,
    viewModel: ReminderViewModel = koinViewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf("") }
    var selectedTimeValue by remember { mutableStateOf<Long?>(null) }
    var showTimePicker by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val time = if(selectedTimeValue != null) {
        selectedTimeValue
    }else {
        selectedDate
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add New Note",
                        style = BodyXLarge()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            showTimePicker = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timelapse,
                            contentDescription = "Select Date"
                        )
                    }
                    IconButton(
                        onClick = {
                            showDatePicker = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select Date"
                        )
                    }
                    IconButton(
                        onClick = {
                            if (title.isNotBlank()) {
                                viewModel.addReminder(
                                    title,
                                    description,
                                    selectedImageUri,
                                    time = time
                                )
                                showSnackBar(
                                    message = "Reminder added successfully",
                                    positiveMessage = true
                                )
                                navController.navigateUp()
                            }
                        },
                        enabled = title.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    launcher.launch("image/*")
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Select Image"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Remind me...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                ),
                textStyle = BodyXLarge()
            )
            
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Something to remind me") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),

                ),
                textStyle = BodyNormal()
            )
            
            selectedImageUri?.let { uri ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Selected image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { selectedImageUri = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove image",
                                tint = Color.White,
                                modifier = Modifier
                                    .background(
                                        Color.Black.copy(alpha = 0.5f),
                                        CircleShape
                                    )
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }

    if (showDatePicker) {
        CustomDatePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateSelected = { millis ->
                selectedDate = millis
                showDatePicker = false
            },
            initialDate = selectedDate ?: System.currentTimeMillis()
        )
    }

    if (showTimePicker) {
        CustomTimePickerDialog(
            onDismiss = { showTimePicker = false },
            onTimeSelected = { timeMillis ->
                val cal = Calendar.getInstance().apply { timeInMillis = timeMillis }
                val hour = cal.get(Calendar.HOUR_OF_DAY)
                val minute = cal.get(Calendar.MINUTE)
                selectedTimeValue = timeMillis
                selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                showTimePicker = false
            },
            initialTime = calendar.timeInMillis
        )
    }
}