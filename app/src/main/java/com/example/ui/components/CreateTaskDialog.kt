package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ScienceCategory
import com.example.data.model.ScienceSkill
import com.example.data.model.Task
import com.example.data.model.TaskPriority
import com.example.data.model.TaskStatus
import com.example.ui.theme.BioEmerald
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.QuantumIndigoLight
import com.example.ui.theme.SpaceNavyDark
import com.example.ui.theme.SpaceNavyLight
import com.example.ui.theme.SpaceNavySurface
import com.example.ui.theme.SpaceNavySurfaceVariant

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateTaskDialog(
    initialTask: Task? = null,
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit
) {
    var title by remember { mutableStateOf(initialTask?.title ?: "") }
    var description by remember { mutableStateOf(initialTask?.description ?: "") }
    var selectedCategory by remember {
        mutableStateOf(
            try { ScienceCategory.valueOf(initialTask?.category ?: "") }
            catch (e: Exception) { ScienceCategory.GENERAL_LAB }
        )
    }
    var selectedSkill by remember {
        mutableStateOf(
            try { ScienceSkill.valueOf(initialTask?.scienceSkill ?: "") }
            catch (e: Exception) { ScienceSkill.DATA_COLLECTION }
        )
    }
    var selectedPriority by remember {
        mutableStateOf(
            try { TaskPriority.valueOf(initialTask?.priority ?: "") }
            catch (e: Exception) { TaskPriority.MEDIUM }
        )
    }
    var selectedStatus by remember {
        mutableStateOf(
            try { TaskStatus.valueOf(initialTask?.status ?: "") }
            catch (e: Exception) { TaskStatus.TODO }
        )
    }
    var hypothesis by remember { mutableStateOf(initialTask?.hypothesis ?: "") }
    var independentVariable by remember { mutableStateOf(initialTask?.independentVariable ?: "") }
    var dependentVariable by remember { mutableStateOf(initialTask?.dependentVariable ?: "") }
    var dueDate by remember { mutableStateOf(initialTask?.dueDate ?: "") }

    val isEditing = initialTask != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEditing) "Edit Scientific Task" else "Create New Scientific Task",
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
                // Quick science templates row for fast creation
                if (!isEditing) {
                    Text(
                        text = "Quick Science Templates:",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF90A4AE),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        TemplateChip(label = "🧬 PCR Gene Run") {
                            title = "PCR Thermal Cycling & Primer Annealing"
                            description = "Prepare 50 uL master mix; set denaturation at 95°C, annealing at 58°C (30 cycles)."
                            selectedCategory = ScienceCategory.BIOLOGY
                            selectedSkill = ScienceSkill.EXPERIMENT_DESIGN
                            selectedPriority = TaskPriority.HIGH
                            hypothesis = "Optimizing annealing at 58°C yields single specific 450bp amplicon."
                            independentVariable = "Annealing temperature (°C)"
                            dependentVariable = "Band intensity on agarose gel"
                        }
                        TemplateChip(label = "⚗️ Solution Dilution") {
                            title = "Stock Solution Serial Dilution"
                            description = "Standardize 0.5 M solution using calibrated micropipette and volumetric flask."
                            selectedCategory = ScienceCategory.CHEMISTRY
                            selectedSkill = ScienceSkill.DATA_COLLECTION
                            selectedPriority = TaskPriority.MEDIUM
                            independentVariable = "Dilution factor"
                            dependentVariable = "Absorbance at 520 nm"
                        }
                        TemplateChip(label = "🛡️ Lab Safety Check") {
                            title = "Biohazard & Eye-Wash Hygiene Audit"
                            description = "Inspect PPE inventory, chemical spill kit integrity, and eyewash flow rate."
                            selectedCategory = ScienceCategory.GENERAL_LAB
                            selectedSkill = ScienceSkill.LAB_SAFETY
                            selectedPriority = TaskPriority.URGENT
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title *") },
                    placeholder = { Text("e.g. Enzyme Reaction Kinetics Run") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_title_input"),
                    colors = outlinedFieldColors(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Description Input
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Procedure & Details") },
                    placeholder = { Text("Step-by-step protocol instructions, quantities, or observations") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedFieldColors(),
                    minLines = 2,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Science Category Selector
                Text(
                    text = "Discipline / Category:",
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
                    ScienceCategory.values().forEach { cat ->
                        SelectableChip(
                            label = "${cat.iconEmoji} ${cat.displayName}",
                            isSelected = selectedCategory == cat,
                            selectedColor = ElectricCyan,
                            onClick = { selectedCategory = cat }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Science Skill Selector
                Text(
                    text = "Science Skill Applied:",
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
                    ScienceSkill.values().forEach { sk ->
                        SelectableChip(
                            label = sk.displayName,
                            isSelected = selectedSkill == sk,
                            selectedColor = QuantumIndigoLight,
                            onClick = { selectedSkill = sk }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Priority Selector
                Text(
                    text = "Priority:",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFB0BEC5),
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskPriority.values().forEach { pri ->
                        SelectableChip(
                            label = pri.displayName,
                            isSelected = selectedPriority == pri,
                            selectedColor = when (pri) {
                                TaskPriority.URGENT -> Color(0xFFFF5252)
                                TaskPriority.HIGH -> Color(0xFFFFAB00)
                                TaskPriority.MEDIUM -> ElectricCyan
                                TaskPriority.LOW -> BioEmerald
                            },
                            onClick = { selectedPriority = pri }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scientific Inquiry Inputs Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SpaceNavySurfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "🔬 Scientific Variables & Hypothesis (Optional)",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = ElectricCyan
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = hypothesis,
                            onValueChange = { hypothesis = it },
                            label = { Text("Hypothesis Statement") },
                            placeholder = { Text("If [IV] changes, then [DV] will...") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = outlinedFieldColors(),
                            minLines = 1,
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = independentVariable,
                                onValueChange = { independentVariable = it },
                                label = { Text("Indep. Var (IV)") },
                                placeholder = { Text("e.g. Conc. (mM)") },
                                modifier = Modifier.weight(1f),
                                colors = outlinedFieldColors(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = dependentVariable,
                                onValueChange = { dependentVariable = it },
                                label = { Text("Dep. Var (DV)") },
                                placeholder = { Text("e.g. Rate (mol/s)") },
                                modifier = Modifier.weight(1f),
                                colors = outlinedFieldColors(),
                                singleLine = true
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Due Date input
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Target Due Date") },
                    placeholder = { Text("YYYY-MM-DD") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedFieldColors(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val taskToSave = (initialTask ?: Task()).copy(
                            title = title.trim(),
                            description = description.trim(),
                            category = selectedCategory.name,
                            scienceSkill = selectedSkill.name,
                            priority = selectedPriority.name,
                            status = selectedStatus.name,
                            hypothesis = hypothesis.trim(),
                            independentVariable = independentVariable.trim(),
                            dependentVariable = dependentVariable.trim(),
                            dueDate = dueDate.trim()
                        )
                        onSave(taskToSave)
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricCyan,
                    contentColor = SpaceNavyDark
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("save_task_button")
            ) {
                Text(
                    text = if (isEditing) "Update Task" else "Save & Sync Task",
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
private fun TemplateChip(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(SpaceNavySurfaceVariant, RoundedCornerShape(8.dp))
            .border(1.dp, ElectricCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = ElectricCyan,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun SelectableChip(
    label: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                if (isSelected) selectedColor.copy(alpha = 0.2f) else SpaceNavySurfaceVariant,
                RoundedCornerShape(8.dp)
            )
            .border(
                1.dp,
                if (isSelected) selectedColor else SpaceNavyLight,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (isSelected) selectedColor else Color(0xFFECEFF1)
        )
    }
}

@Composable
private fun outlinedFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ElectricCyan,
    unfocusedBorderColor = SpaceNavyLight,
    focusedLabelColor = ElectricCyan,
    unfocusedLabelColor = Color(0xFF90A4AE),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = ElectricCyan
)
