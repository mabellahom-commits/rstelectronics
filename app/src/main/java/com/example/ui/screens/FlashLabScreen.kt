package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GeminiResult
import com.example.data.model.ScienceCategory
import com.example.data.model.ScienceSkill
import com.example.data.model.Task
import com.example.data.model.TaskPriority
import com.example.data.remote.GeminiService
import com.example.data.repository.SciSyncRepository
import com.example.ui.theme.BioEmerald
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.SpaceNavyDark
import com.example.ui.theme.SpaceNavyLight
import com.example.ui.theme.SpaceNavySurface
import com.example.ui.theme.SpaceNavySurfaceVariant
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlashLabScreen(
    userId: String,
    geminiService: GeminiService,
    repository: SciSyncRepository,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var promptInput by remember { mutableStateOf("") }
    var responseText by remember { mutableStateOf<String?>(null) }
    var latencyMs by remember { mutableStateOf<Long?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var taskCreatedNotification by remember { mutableStateOf<String?>(null) }

    fun executeQuery(query: String) {
        if (query.isBlank() || isLoading) return
        isLoading = true
        taskCreatedNotification = null
        coroutineScope.launch {
            when (val result = geminiService.getLowLatencyResponse(query)) {
                is GeminiResult.Success -> {
                    responseText = result.data
                    latencyMs = result.latencyMs
                }
                is GeminiResult.Error -> {
                    responseText = result.message
                    latencyMs = result.latencyMs
                }
            }
            isLoading = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SpaceNavyDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Model Banner: Low-latency gemini-3.1-flash-lite
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SpaceNavySurface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFFFD600).copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = Color(0xFFFFD600),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Flash Lab AI",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Model: gemini-3.1-flash-lite",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFFFD600),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (latencyMs != null) {
                        Box(
                            modifier = Modifier
                                .background(BioEmerald.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .border(1.dp, BioEmerald.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = BioEmerald,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${latencyMs}ms",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BioEmerald,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Ultra low-latency responses for instant lab conversions, dilution math, hypothesis checks, and immediate task breakdowns.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF90A4AE),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Lab Calculators & Inquiries
        Text(
            text = "Instant Scientific Actions:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = ElectricCyan
        )
        Spacer(modifier = Modifier.height(6.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickActionPill("💧 Dilution C₁V₁ = C₂V₂") {
                promptInput = "Calculate volume needed to make 500 mL of 0.15 M NaOH from 2.0 M stock solution using C1V1=C2V2."
                executeQuery(promptInput)
            }
            QuickActionPill("⚖️ Molarity: 0.1 M NaCl") {
                promptInput = "How many grams of NaCl are required to prepare 250 mL of 0.1 M solution?"
                executeQuery(promptInput)
            }
            QuickActionPill("🔬 Formulate Hypothesis") {
                promptInput = "Formulate a testable scientific hypothesis testing how light wavelength impacts chlorophyll photosynthesis rate."
                executeQuery(promptInput)
            }
            QuickActionPill("📊 Error % Calculation") {
                promptInput = "Calculate percent error if experimental boiling point is 98.4°C and theoretical is 100.0°C."
                executeQuery(promptInput)
            }
            QuickActionPill("🧪 Buffer pH (Henderson-Hasselbalch)") {
                promptInput = "Explain Henderson-Hasselbalch equation and calculate pH for 0.1 M acetic acid and 0.1 M sodium acetate."
                executeQuery(promptInput)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Query Input Field
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SpaceNavySurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                OutlinedTextField(
                    value = promptInput,
                    onValueChange = { promptInput = it },
                    placeholder = { Text("Ask a fast scientific question or calculation...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("flash_lab_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = SpaceNavyLight,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedPlaceholderColor = Color(0xFF78909C),
                        unfocusedPlaceholderColor = Color(0xFF78909C)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { executeQuery(promptInput) },
                        enabled = promptInput.isNotBlank() && !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricCyan,
                            contentColor = SpaceNavyDark
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("flash_lab_send_button")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = SpaceNavyDark,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Querying Flash Lite...")
                        } else {
                            Icon(imageVector = Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Get Low-Latency Response", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Response Presentation Box
        if (responseText != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SpaceNavySurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "⚡ Instant AI Response:",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = ElectricCyan
                        )

                        if (latencyMs != null) {
                            Text(
                                text = "Latency: ${latencyMs}ms",
                                style = MaterialTheme.typography.labelSmall,
                                color = BioEmerald,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = responseText ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFECEFF1),
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // "Create Task From Answer" Button
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val newTask = Task(
                                    userId = userId,
                                    title = promptInput.take(60).ifBlank { "Flash Lab Calculation" },
                                    description = responseText?.take(500) ?: "",
                                    category = ScienceCategory.CHEMISTRY.name,
                                    scienceSkill = ScienceSkill.DATA_COLLECTION.name,
                                    priority = TaskPriority.HIGH.name
                                )
                                repository.createTask(newTask)
                                taskCreatedNotification = "Task created and synced to all devices!"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BioEmerald,
                            contentColor = SpaceNavyDark
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("create_task_from_flash_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add to Synced Tasks Hub", fontWeight = FontWeight.Bold)
                    }

                    if (taskCreatedNotification != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BioEmerald.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = BioEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = taskCreatedNotification ?: "",
                                style = MaterialTheme.typography.labelSmall,
                                color = BioEmerald,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionPill(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(SpaceNavySurfaceVariant, RoundedCornerShape(10.dp))
            .border(1.dp, ElectricCyan.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = ElectricCyan,
            fontWeight = FontWeight.Medium
        )
    }
}
