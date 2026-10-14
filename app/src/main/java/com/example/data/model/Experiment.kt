package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class ExperimentStatus(val displayName: String) {
    PLANNING("Planning & Hypothesis"),
    IN_PROGRESS("Active Testing"),
    ANALYZING("Data Analysis"),
    CONCLUDED("Concluded & Verified")
}

@IgnoreExtraProperties
data class Experiment(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val researchQuestion: String = "",
    val hypothesis: String = "",
    val methodology: String = "",
    val status: String = ExperimentStatus.PLANNING.name,
    val safetyNotes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
