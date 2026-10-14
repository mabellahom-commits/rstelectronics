package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Experiment
import com.example.data.model.ExperimentStatus
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.SpaceNavyDark
import com.example.ui.theme.SpaceNavyLight
import com.example.ui.theme.SpaceNavySurface
import com.example.ui.theme.SpaceNavySurfaceVariant

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateExperimentDialog(
    initialExperiment: Experiment? = null,
    onDismiss: () -> Unit,
    onSave: (Experiment) -> Unit
) {
    var title by remember { mutableStateOf(initialExperiment?.title ?: "") }
    var researchQuestion by remember { mutableStateOf(initialExperiment?.researchQuestion ?: "") }
    var hypothesis by remember { mutableStateOf(initialExperiment?.hypothesis ?: "") }
    var methodology by remember { mutableStateOf(initialExperiment?.methodology ?: "") }
    var safetyNotes by remember { mutableStateOf(initialExperiment?.safetyNotes ?: "") }
    var selectedStatus by remember {
        mutableStateOf(
            try { ExperimentStatus.valueOf(initialExperiment?.status ?: "") }
            catch (e: Exception) { ExperimentStatus.PLANNING }
        )
    }

    val isEditing = initialExperiment != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEditing) "Edit Experiment Project" else "New Experiment Project",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = ElectricCyan
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Experiment Title *") },
                    placeholder = { Text("e.g. Enzyme Catalysis vs pH Profile") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("experiment_title_input"),
                    colors = outlinedColors(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = researchQuestion,
                    onValueChange = { researchQuestion = it },
                    label = { Text("Core Scientific Question *") },
                    placeholder = { Text("e.g. Does increasing buffer ionic strength alter Km?") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedColors(),
                    minLines = 1,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = hypothesis,
                    onValueChange = { hypothesis = it },
                    label = { Text("Scientific Hypothesis *") },
                    placeholder = { Text("If [IV] is altered, then [DV] will...") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedColors(),
                    minLines = 2,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = methodology,
                    onValueChange = { methodology = it },
                    label = { Text("Methodology & Experimental Protocol") },
                    placeholder = { Text("Apparatus, reagents, controls, step-by-step procedures") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedColors(),
                    minLines = 2,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = safetyNotes,
                    onValueChange = { safetyNotes = it },
                    label = { Text("Lab Safety & Hazard Mitigation") },
                    placeholder = { Text("PPE, corrosive/toxic chemicals, disposal instructions") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedColors(),
                    minLines = 1,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Project Status:",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFB0BEC5),
                    fontWeight = FontWeight.SemiBold
                )

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ExperimentStatus.values().forEach { st ->
                        val isSelected = selectedStatus == st
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) ElectricCyan.copy(alpha = 0.2f) else SpaceNavySurfaceVariant,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) ElectricCyan else SpaceNavyLight,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedStatus = st }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = st.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) ElectricCyan else Color(0xFFECEFF1)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && researchQuestion.isNotBlank() && hypothesis.isNotBlank()) {
                        val expToSave = (initialExperiment ?: Experiment()).copy(
                            title = title.trim(),
                            researchQuestion = researchQuestion.trim(),
                            hypothesis = hypothesis.trim(),
                            methodology = methodology.trim(),
                            safetyNotes = safetyNotes.trim(),
                            status = selectedStatus.name
                        )
                        onSave(expToSave)
                    }
                },
                enabled = title.isNotBlank() && researchQuestion.isNotBlank() && hypothesis.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricCyan,
                    contentColor = SpaceNavyDark
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("save_experiment_button")
            ) {
                Text(
                    text = if (isEditing) "Update Experiment" else "Save & Sync",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", color = Color(0xFF90A4AE))
            }
        },
        containerColor = SpaceNavySurface,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun outlinedColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ElectricCyan,
    unfocusedBorderColor = SpaceNavyLight,
    focusedLabelColor = ElectricCyan,
    unfocusedLabelColor = Color(0xFF90A4AE),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = ElectricCyan
)
