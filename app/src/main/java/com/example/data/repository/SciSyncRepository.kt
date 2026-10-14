package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.Experiment
import com.example.data.model.ExperimentStatus
import com.example.data.model.ScienceCategory
import com.example.data.model.ScienceSkill
import com.example.data.model.Task
import com.example.data.model.TaskPriority
import com.example.data.model.TaskStatus
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class SciSyncRepository(private val firestore: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in before accessing real-time task database.")
    }

    /**
     * Real-time observation of tasks using snapshot listeners.
     * Changes made on ANY connected device immediately sync here in real-time!
     */
    fun observeTasks(userId: String): Flow<List<Task>> {
        val collection = firestore.collection("users").document(userId).collection("tasks")
        return collection
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Task::class.java)?.copy(id = doc.id)
                }.sortedByDescending { it.createdAt?.seconds ?: 0L }
            }
            .catch { e ->
                Log.e("SciSyncRepository", "Real-time task snapshot listener error for user $userId", e)
                throw e
            }
    }

    /**
     * Real-time observation of experiments across all devices.
     */
    fun observeExperiments(userId: String): Flow<List<Experiment>> {
        val collection = firestore.collection("users").document(userId).collection("experiments")
        return collection
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Experiment::class.java)?.copy(id = doc.id)
                }.sortedByDescending { it.createdAt?.seconds ?: 0L }
            }
            .catch { e ->
                Log.e("SciSyncRepository", "Real-time experiment snapshot listener error", e)
                throw e
            }
    }

    suspend fun createTask(task: Task): String {
        val uid = requireUserId()
        val taskId = if (task.id.isNotBlank()) task.id else UUID.randomUUID().toString()
        val docRef = firestore.collection("users").document(uid).collection("tasks").document(taskId)

        val payload = hashMapOf<String, Any?>(
            "id" to taskId,
            "userId" to uid,
            "title" to task.title,
            "description" to task.description,
            "status" to task.status,
            "priority" to task.priority,
            "category" to task.category,
            "scienceSkill" to task.scienceSkill,
            "dueDate" to task.dueDate,
            "isCompleted" to task.isCompleted,
            "hypothesis" to task.hypothesis,
            "independentVariable" to task.independentVariable,
            "dependentVariable" to task.dependentVariable,
            "experimentId" to task.experimentId,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        docRef.set(payload).await()
        return taskId
    }

    suspend fun updateTask(task: Task) {
        val uid = requireUserId()
        require(task.id.isNotBlank()) { "Task ID must not be blank for update" }
        val docRef = firestore.collection("users").document(uid).collection("tasks").document(task.id)

        val updates = hashMapOf<String, Any?>(
            "title" to task.title,
            "description" to task.description,
            "status" to task.status,
            "priority" to task.priority,
            "category" to task.category,
            "scienceSkill" to task.scienceSkill,
            "dueDate" to task.dueDate,
            "isCompleted" to task.isCompleted,
            "hypothesis" to task.hypothesis,
            "independentVariable" to task.independentVariable,
            "dependentVariable" to task.dependentVariable,
            "experimentId" to task.experimentId,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        docRef.update(updates).await()
    }

    suspend fun toggleTaskComplete(task: Task) {
        val nextCompleted = !task.isCompleted
        val nextStatus = if (nextCompleted) TaskStatus.COMPLETED.name else TaskStatus.IN_PROGRESS.name
        updateTask(task.copy(isCompleted = nextCompleted, status = nextStatus))
    }

    suspend fun deleteTask(taskId: String) {
        val uid = requireUserId()
        firestore.collection("users").document(uid).collection("tasks").document(taskId).delete().await()
    }

    suspend fun createExperiment(experiment: Experiment): String {
        val uid = requireUserId()
        val expId = if (experiment.id.isNotBlank()) experiment.id else UUID.randomUUID().toString()
        val docRef = firestore.collection("users").document(uid).collection("experiments").document(expId)

        val payload = hashMapOf<String, Any?>(
            "id" to expId,
            "userId" to uid,
            "title" to experiment.title,
            "researchQuestion" to experiment.researchQuestion,
            "hypothesis" to experiment.hypothesis,
            "methodology" to experiment.methodology,
            "status" to experiment.status,
            "safetyNotes" to experiment.safetyNotes,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        docRef.set(payload).await()
        return expId
    }

    suspend fun updateExperiment(experiment: Experiment) {
        val uid = requireUserId()
        require(experiment.id.isNotBlank()) { "Experiment ID must not be blank" }
        val docRef = firestore.collection("users").document(uid).collection("experiments").document(experiment.id)

        val updates = hashMapOf<String, Any?>(
            "title" to experiment.title,
            "researchQuestion" to experiment.researchQuestion,
            "hypothesis" to experiment.hypothesis,
            "methodology" to experiment.methodology,
            "status" to experiment.status,
            "safetyNotes" to experiment.safetyNotes,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        docRef.update(updates).await()
    }

    suspend fun deleteExperiment(experimentId: String) {
        val uid = requireUserId()
        firestore.collection("users").document(uid).collection("experiments").document(experimentId).delete().await()
    }

    /**
     * Seeds initial real-world scientific inquiry tasks to demonstrate
     * science skills and instant real-time sync capabilities across devices.
     */
    suspend fun seedInitialScienceTasks(userId: String) {
        val sampleTasks = listOf(
            Task(
                id = UUID.randomUUID().toString(),
                userId = userId,
                title = "CRISPR-Cas9 Gene Knockout Protocol",
                description = "Design guide RNA (gRNA) sequences targeting Exon 3; verify target cleavage efficiency with T7E1 endonuclease assay.",
                status = TaskStatus.IN_PROGRESS.name,
                priority = TaskPriority.URGENT.name,
                category = ScienceCategory.BIOLOGY.name,
                scienceSkill = ScienceSkill.EXPERIMENT_DESIGN.name,
                hypothesis = "Targeting exon 3 results in >65% frameshift knockout frequency.",
                independentVariable = "gRNA GC content percentage",
                dependentVariable = "Cleavage efficiency (%)",
                dueDate = "2026-10-18"
            ),
            Task(
                id = UUID.randomUUID().toString(),
                userId = userId,
                title = "Acid-Base Equivalence Curve Titration",
                description = "Standardize 0.1 M NaOH with primary standard KHP; measure potentiometric pH curve and calculate Ka.",
                status = TaskStatus.TODO.name,
                priority = TaskPriority.HIGH.name,
                category = ScienceCategory.CHEMISTRY.name,
                scienceSkill = ScienceSkill.DATA_COLLECTION.name,
                hypothesis = "Equivalence inflection point occurs at pH 8.72 ± 0.05.",
                independentVariable = "Volume of titrant added (mL)",
                dependentVariable = "pH reading from calibrated electrode",
                dueDate = "2026-10-15"
            ),
            Task(
                id = UUID.randomUUID().toString(),
                userId = userId,
                title = "Quantum Double-Slit Photon Interference",
                description = "Calibrate He-Ne laser (λ = 632.8 nm); record fringe spacing (y) at varying screen distances (L) to calculate slit separation (d).",
                status = TaskStatus.REVIEW.name,
                priority = TaskPriority.MEDIUM.name,
                category = ScienceCategory.PHYSICS.name,
                scienceSkill = ScienceSkill.ANALYSIS_CONCLUSION.name,
                hypothesis = "Fringe width is directly proportional to laser wavelength and screen distance.",
                independentVariable = "Distance L to projection screen (m)",
                dependentVariable = "Interference fringe separation Δy (mm)",
                dueDate = "2026-10-22"
            ),
            Task(
                id = UUID.randomUUID().toString(),
                userId = userId,
                title = "Chemical Fume Hood Hygiene & PPE Verification",
                description = "Inspect face velocity (>100 fpm), check eyewash station expiration, replenish nitrile gloves and chemical splash goggles.",
                status = TaskStatus.COMPLETED.name,
                priority = TaskPriority.HIGH.name,
                category = ScienceCategory.GENERAL_LAB.name,
                scienceSkill = ScienceSkill.LAB_SAFETY.name,
                isCompleted = true,
                dueDate = "2026-10-11"
            )
        )

        for (task in sampleTasks) {
            createTask(task)
        }

        // Also seed a parent experiment
        createExperiment(
            Experiment(
                id = UUID.randomUUID().toString(),
                userId = userId,
                title = "Enzyme Catalytic Velocity vs Temperature Profile",
                researchQuestion = "What is the optimal thermal range for catalase breakdown of hydrogen peroxide (H2O2)?",
                hypothesis = "Enzymatic velocity peaks at 37°C and drops precipitously past 55°C due to irreversible protein denaturation.",
                methodology = "Spectrophotometric assay measuring oxygen evolution rate at 5°C intervals between 20°C and 65°C in triplicate.",
                status = ExperimentStatus.IN_PROGRESS.name,
                safetyNotes = "30% H2O2 is a strong oxidizer. Wear full face shield and nitrile gloves. Avoid organic contact."
            )
        )
    }
}
