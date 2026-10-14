package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.data.model.Experiment
import com.example.data.model.ExperimentStatus
import com.example.ui.theme.BioEmerald
import com.example.ui.theme.CriticalRed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.QuantumIndigoLight
import com.example.ui.theme.SpaceNavySurface
import com.example.ui.theme.SpaceNavySurfaceVariant

@Composable
fun ExperimentCard(
    experiment: Experiment,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    val status = remember(experiment.status) {
        try { ExperimentStatus.valueOf(experiment.status) }
        catch (e: Exception) { ExperimentStatus.PLANNING }
    }

    val statusColor = when (status) {
        ExperimentStatus.PLANNING -> ElectricCyan
        ExperimentStatus.IN_PROGRESS -> BioEmerald
        ExperimentStatus.ANALYZING -> QuantumIndigoLight
        ExperimentStatus.CONCLUDED -> Color(0xFF64B5F6)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("experiment_card_${experiment.id}"),
        colors = CardDefaults.cardColors(containerColor = SpaceNavySurface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Experiment Project",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF90A4AE)
                    )
                }

                Box(
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = status.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = experiment.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            if (experiment.researchQuestion.isNotBlank()) {
                Text(
                    text = "Q: ${experiment.researchQuestion}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ElectricCyan,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isExpanded) "Hide Details" else "View Methodology & Safety Protocol",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF90A4AE)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color(0xFF90A4AE),
                    modifier = Modifier.size(20.dp)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .background(SpaceNavySurfaceVariant, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    if (experiment.hypothesis.isNotBlank()) {
                        Text(
                            text = "Hypothesis:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = BioEmerald
                        )
                        Text(
                            text = experiment.hypothesis,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFECEFF1),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    if (experiment.methodology.isNotBlank()) {
                        Text(
                            text = "Methodology & Protocol:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = QuantumIndigoLight
                        )
                        Text(
                            text = experiment.methodology,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFECEFF1),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    if (experiment.safetyNotes.isNotBlank()) {
                        Text(
                            text = "Lab Safety & Hazards:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFFFAB00)
                        )
                        Text(
                            text = experiment.safetyNotes,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFECEFF1)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = ElectricCyan)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = CriticalRed)
                        }
                    }
                }
            }
        }
    }
}
