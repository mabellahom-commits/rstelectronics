package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class TaskStatus(val displayName: String) {
    TODO("To Do"),
    IN_PROGRESS("In Progress"),
    REVIEW("Peer Review"),
    COMPLETED("Completed")
}

enum class TaskPriority(val displayName: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    URGENT("Urgent")
}

enum class ScienceCategory(val displayName: String, val iconEmoji: String) {
    BIOLOGY("Biology", "🧬"),
    CHEMISTRY("Chemistry", "⚗️"),
    PHYSICS("Physics", "⚛️"),
    DATA_SCIENCE("Data & STEM", "📊"),
    GENERAL_LAB("Lab Protocol", "🔬")
}

enum class ScienceSkill(val displayName: String, val description: String) {
    HYPOTHESIS("Hypothesis Formulation", "Formulate testable, falsifiable scientific predictions"),
    EXPERIMENT_DESIGN("Experimental Design", "Define controls, independent & dependent variables"),
    DATA_COLLECTION("Data & Measurement", "Quantitative measurement, trials & observation logs"),
    ANALYSIS_CONCLUSION("Analysis & Conclusions", "Statistical significance, error analysis & deduction"),
    LAB_SAFETY("Lab Safety & Protocols", "Chemical hygiene, hazard controls & PPE checklist")
}

@IgnoreExtraProperties
data class Task(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val status: String = TaskStatus.TODO.name,
    val priority: String = TaskPriority.MEDIUM.name,
    val category: String = ScienceCategory.GENERAL_LAB.name,
    val scienceSkill: String = ScienceSkill.DATA_COLLECTION.name,
    val dueDate: String = "",
    val isCompleted: Boolean = false,
    val hypothesis: String = "",
    val independentVariable: String = "",
    val dependentVariable: String = "",
    val experimentId: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
