package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.Experiment
import com.example.data.model.ExperimentStatus
import com.example.data.repository.SciSyncRepository
import com.example.ui.components.CreateExperimentDialog
import com.example.ui.components.ExperimentCard
import com.example.ui.theme.BioEmerald
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.SpaceNavyDark
import com.example.ui.theme.SpaceNavySurface
import com.example.ui.theme.SpaceNavySurfaceVariant
import kotlinx.coroutines.launch

@Composable
fun ExperimentsScreen(
    userId: String,
    repository: SciSyncRepository,
    experiments: List<Experiment>,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedStageFilter by remember { mutableStateOf<String?>("ALL") }
    var isCreateDialogOpen by remember { mutableStateOf(false) }
    var editingExperiment by remember { mutableStateOf<Experiment?>(null) }

    val filteredExperiments = remember(experiments, selectedStageFilter) {
        if (selectedStageFilter == "ALL") {
            experiments
        } else {
            experiments.filter { it.status == selectedStageFilter }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SpaceNavyDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isCreateDialogOpen = true },
                containerColor = ElectricCyan,
                contentColor = SpaceNavyDark,
                modifier = Modifier.testTag("add_experiment_fab"),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Experiment Project",
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
            // Stage Status Filter Bar
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SpaceNavySurface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    val isSelected = selectedStageFilter == "ALL"
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) ElectricCyan else SpaceNavySurfaceVariant,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedStageFilter = "ALL" }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "All Projects (${experiments.size})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) SpaceNavyDark else Color.White
                        )
                    }
                }

                items(ExperimentStatus.values().toList()) { st ->
                    val isSelected = selectedStageFilter == st.name
                    val count = experiments.count { it.status == st.name }
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) ElectricCyan else SpaceNavySurfaceVariant,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedStageFilter = st.name }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "${st.displayName} ($count)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) SpaceNavyDark else Color.White
                        )
                    }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ElectricCyan)
                }
            } else if (filteredExperiments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Biotech,
                            contentDescription = null,
                            tint = ElectricCyan.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No experiment projects in this stage",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Track your scientific method projects with testable hypotheses, protocols, and safety notes.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF90A4AE),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { isCreateDialogOpen = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricCyan,
                                contentColor = SpaceNavyDark
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Create Experiment Project", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredExperiments, key = { it.id }) { experiment ->
                        ExperimentCard(
                            experiment = experiment,
                            onEdit = { editingExperiment = experiment },
                            onDelete = {
                                coroutineScope.launch {
                                    repository.deleteExperiment(experiment.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (isCreateDialogOpen || editingExperiment != null) {
        CreateExperimentDialog(
            initialExperiment = editingExperiment,
            onDismiss = {
                isCreateDialogOpen = false
                editingExperiment = null
            },
            onSave = { expToSave ->
                coroutineScope.launch {
                    if (editingExperiment != null) {
                        repository.updateExperiment(expToSave)
                    } else {
                        repository.createExperiment(expToSave.copy(userId = userId))
                    }
                    isCreateDialogOpen = false
                    editingExperiment = null
                }
            }
        )
    }
}
