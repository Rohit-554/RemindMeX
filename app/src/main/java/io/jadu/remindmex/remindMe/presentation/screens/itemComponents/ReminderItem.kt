package io.jadu.remindmex.remindMe.presentation.screens.itemComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import io.jadu.remindmex.R
import io.jadu.remindmex.remindMe.data.models.Reminder
import io.jadu.remindmex.remindMe.presentation.utils.bounceClickable
import io.jadu.remindmex.ui.theme.BodyNormal
import io.jadu.remindmex.ui.theme.MajorColors

@Composable
fun ReminderItem(
    reminder: Reminder,
    onToggleComplete: (Reminder) -> Unit,
    onDelete: (String) -> Unit,
    onTapped: (Reminder) -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val draggableState = rememberDraggableState { delta ->
        offsetX += delta
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ){
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.toInt(), 0) }
                .bounceClickable {
                    onTapped(reminder)
                }
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    onDragStopped = {
                        offsetX = 0f
                    }
                ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = reminder.title,
                            style = BodyNormal(),
                            fontWeight = FontWeight.Bold,
                            textDecoration = if (reminder.isCompleted) TextDecoration.LineThrough else null
                        )

                        if (reminder.description.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = reminder.description,
                                style = BodyNormal(),
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                                .format(Date(reminder.timestamp)),
                            style = BodyNormal(),
                        )
                    }

                    Row {
                        IconButton(onClick = { onDelete(reminder.id) }) {
                            Box(
                                modifier = Modifier.background(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    shape = CircleShape
                                ).size(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    painter = painterResource(R.drawable.delete),
                                    tint = MajorColors.RoseRed.color,
                                    contentDescription = ""
                                )
                            }
                        }
                    }
                }

                // Display image if available
                if (reminder.imageUrl.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    AsyncImage(
                        model = reminder.imageUrl,
                        contentDescription = "Reminder image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }

}