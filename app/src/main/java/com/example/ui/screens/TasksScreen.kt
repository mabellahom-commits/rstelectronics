package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ScienceCategory
import com.example.data.model.Task
import com.example.data.model.TaskStatus
import com.example.data.repository.SciSyncRepository
import com.example.ui.components.CreateTaskDialog
import com.example.ui.components.TaskItemCard
import com.example.ui.theme.BioEmerald
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.SpaceNavyDark
import com.example.ui.theme.SpaceNavyLight
import com.example.ui.theme.SpaceNavySurface
import com.example.ui.theme.SpaceNavySurfaceVariant
import kotlinx.coroutines.launch

@Composable
fun TasksScreen(
    userId: String,
    repository: SciSyncRepository,
    tasks: List<Task>,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf<String?>("ALL") }
    var selectedCategoryFilter by remember { mutableStateOf<ScienceCategory?>(null) }

    var isCreateDialogOpen by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }

    val filteredTasks = remember(tasks, searchQuery, selectedStatusFilter, selectedCategoryFilter) {
        tasks.filter { task ->
            val matchesSearch = searchQuery.isBlank() ||
                task.title.contains(searchQuery, ignoreCase = true) ||
                task.description.contains(searchQuery, ignoreCase = true) ||
                task.hypothesis.contains(searchQuery, ignoreCase = true)

            val matchesStatus = when (selectedStatusFilter) {
                "ALL" -> true
                "PENDING" -> !task.isCompleted
                "COMPLETED" -> task.isCompleted
                else -> task.status == selectedStatusFilter
            }

            val matchesCategory = selectedCategoryFilter == null ||
                task.category == selectedCategoryFilter?.name

            matchesSearch && matchesStatus && matchesCategory
        }
    }

    val completedCount = remember(tasks) { tasks.count { it.isCompleted } }
    val pendingCount = remember(tasks) { tasks.count { !it.isCompleted } }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SpaceNavyDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isCreateDialogOpen = true },
                containerColor = ElectricCyan,
                contentColor = SpaceNavyDark,
                modifier = Modifier.testTag("add_task_fab"),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Scientific Task",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Real-Time Sync & Stats Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SpaceNavySurface)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(BioEmerald, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Real-Time Synced Across Devices",
                        style = MaterialTheme.typography.labelSmall,
                        color = BioEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "$pendingCount Active",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricCyan,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF78909C)
                    )
                    Text(
                        text = "$completedCount Done",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF90A4AE)
                    )
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search protocols, hypotheses, variables...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = ElectricCyan
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("search_tasks_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricCyan,
                    unfocusedBorderColor = SpaceNavyLight,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedPlaceholderColor = Color(0xFF78909C),
                    unfocusedPlaceholderColor = Color(0xFF78909C)
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Status Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val statusTabs = listOf(
                    "ALL" to "All Tasks (${tasks.size})",
                    "PENDING" to "Active ($pendingCount)",
                    TaskStatus.IN_PROGRESS.name to "In Progress",
                    TaskStatus.REVIEW.name to "Peer Review",
                    "COMPLETED" to "Completed ($completedCount)"
                )

                items(statusTabs) { (key, label) ->
                    val isSelected = selectedStatusFilter == key
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) ElectricCyan else SpaceNavySurfaceVariant,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedStatusFilter = key }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) SpaceNavyDark else Color.White
                        )
                    }
                }
            }

            // Category Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    val isSelected = selectedCategoryFilter == null
                    Box(
                        modifier = Modifier
                            .border(
                                1.dp,
                                if (isSelected) ElectricCyan else SpaceNavyLight,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedCategoryFilter = null }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "All Disciplines",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) ElectricCyan else Color(0xFF90A4AE),
                            fontSize = 11.sp
                        )
                    }
                }

                items(ScienceCategory.values().toList()) { cat ->
                    val isSelected = selectedCategoryFilter == cat
                    Box(
                        modifier = Modifier
                            .border(
                                1.dp,
                                if (isSelected) ElectricCyan else SpaceNavyLight,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedCategoryFilter = cat }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${cat.iconEmoji} ${cat.displayName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) ElectricCyan else Color(0xFF90A4AE),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Main Tasks List or Empty State
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ElectricCyan)
                }
            } else if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = null,
                            tint = ElectricCyan.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "No scientific tasks match '$searchQuery'" else "No scientific tasks yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Add custom lab tasks or populate initial scientific inquiry templates.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF90A4AE),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    repository.seedInitialScienceTasks(userId)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BioEmerald,
                                contentColor = SpaceNavyDark
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Populate Science Templates", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .testTag("tasks_lazy_column"),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredTasks, key = { it.id }) { task ->
                        TaskItemCard(
                            task = task,
                            onToggleComplete = {
                                coroutineScope.launch {
                                    repository.toggleTaskComplete(task)
                                }
                            },
                            onEdit = {
                                editingTask = task
                            },
                            onDelete = {
                                coroutineScope.launch {
                                    repository.deleteTask(task.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Create / Edit Dialog
    if (isCreateDialogOpen || editingTask != null) {
        CreateTaskDialog(
            initialTask = editingTask,
            onDismiss = {
                isCreateDialogOpen = false
                editingTask = null
            },
            onSave = { taskToSave ->
                coroutineScope.launch {
                    if (editingTask != null) {
                        repository.updateTask(taskToSave)
                    } else {
                        repository.createTask(taskToSave.copy(userId = userId))
                    }
                    isCreateDialogOpen = false
                    editingTask = null
                }
            }
        )
    }
}
