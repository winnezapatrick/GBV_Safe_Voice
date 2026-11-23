package com.example.gbvapp.data.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

fun canTransition(from: String, to: String): Boolean {
    return (from == "Assigned" && to == "Open") ||
            (from == "Open" && to == "Resolved") ||
            ((from == "Open" || from == "Resolved") && to == "Closed")
}

data class CaseNote(
    val id: String = "",
    val reference: String,
    val noteText: String,
    val authorId: String,
    val authorName: String,
    val timestamp: Any? = null
)

data class CaseReferral(
    val id: String = "",
    val reference: String,
    val referralType: String,
    val targetDepartment: String,
    val notes: String,
    val officerId: String,
    val officerName: String,
    val timestamp: Any? = null
)

data class CaseUpdateRecord(
    val id: String = "",
    val reference: String,
    val type: String,
    val status: String,
    val assignedOfficerId: String? = null,
    val updatedBy: String,
    val timestamp: Any? = null
)

data class AuditLogRecord(
    val id: String = "",
    val action: String,
    val category: String,
    val performedBy: String,
    val targetId: String,
    val details: String,
    val timestamp: Any? = null
)

sealed class CaseOperationResult {
    object Success : CaseOperationResult()
    data class Error(val message: String) : CaseOperationResult()
}

object CaseManagementRepository {

    suspend fun updateCaseStatus(reference: String, newStatus: String): CaseOperationResult {
        val currentUid = FirebaseManager.auth.currentUser?.uid
            ?: return CaseOperationResult.Error("You must be logged in to update a case.")

        return try {
            val firestore = FirebaseManager.firestore
            val docRef = firestore.collection("reports").document(reference)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                if (!snapshot.exists()) {
                    throw IllegalStateException("Case not found.")
                }
                val currentStatus = snapshot.getString("status") ?: "Pending"
                val assignedId = snapshot.getString("assignedOfficerId")

                if (assignedId != currentUid) {
                    throw IllegalStateException("Only the assigned officer can update this case.")
                }

                if (!canTransition(currentStatus, newStatus)) {
                    throw IllegalStateException("Cannot transition status from $currentStatus to $newStatus.")
                }

                val updates = hashMapOf<String, Any?>(
                    "status" to newStatus,
                    "lastUpdatedAt" to FieldValue.serverTimestamp()
                )
                if (newStatus == "Closed") {
                    updates["closureDate"] = FieldValue.serverTimestamp()
                }
                transaction.update(docRef, updates)

                val updateRef = firestore.collection("caseUpdates").document()
                val updateData = hashMapOf<String, Any?>(
                    "reference" to reference,
                    "type" to "status",
                    "status" to newStatus,
                    "updatedBy" to currentUid,
                    "timestamp" to FieldValue.serverTimestamp()
                )
                transaction.set(updateRef, updateData)
            }.await()

            logAudit(
                action = "CASE_STATUS_UPDATED",
                category = "Case Management",
                performedBy = currentUid,
                targetId = reference,
                details = "Updated case $reference status to $newStatus."
            )

            CaseOperationResult.Success
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            CaseOperationResult.Error(e.message ?: "Could not update the case. Please try again.")
        }
    }

    suspend fun assignReport(reference: String, officerId: String, officerName: String): CaseOperationResult {
        val currentUid = FirebaseManager.auth.currentUser?.uid
            ?: return CaseOperationResult.Error("You must be logged in as admin to assign a case.")

        return try {
            val firestore = FirebaseManager.firestore
            val docRef = firestore.collection("reports").document(reference)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                if (!snapshot.exists()) {
                    throw IllegalStateException("Report not found.")
                }
                val currentStatus = snapshot.getString("status") ?: "Pending"
                if (currentStatus != "Pending" && currentStatus != "Assigned") {
                    throw IllegalStateException("Case cannot be assigned once work has started.")
                }

                val updates = hashMapOf<String, Any?>(
                    "assignedOfficerId" to officerId,
                    "assignedOfficerName" to officerName,
                    "assignedAt" to FieldValue.serverTimestamp(),
                    "status" to "Assigned",
                    "lastUpdatedAt" to FieldValue.serverTimestamp()
                )
                transaction.update(docRef, updates)

                val updateRef = firestore.collection("caseUpdates").document()
                val updateData = hashMapOf<String, Any?>(
                    "reference" to reference,
                    "type" to "assignment",
                    "status" to "Assigned",
                    "assignedOfficerId" to officerId,
                    "updatedBy" to currentUid,
                    "timestamp" to FieldValue.serverTimestamp()
                )
                transaction.set(updateRef, updateData)
            }.await()

            logAudit(
                action = "REPORT_ASSIGNED",
                category = "Assignment",
                performedBy = currentUid,
                targetId = reference,
                details = "Assigned case $reference to Officer $officerName ($officerId)."
            )

            CaseOperationResult.Success
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            CaseOperationResult.Error(e.message ?: "Could not assign report. Please try again.")
        }
    }

    suspend fun addCaseNote(reference: String, noteText: String, authorName: String): Boolean {
        val currentUid = FirebaseManager.auth.currentUser?.uid ?: return false
        return try {
            val noteData = hashMapOf(
                "reference" to reference,
                "noteText" to noteText.trim(),
                "authorId" to currentUid,
                "authorName" to authorName,
                "timestamp" to FieldValue.serverTimestamp()
            )

            FirebaseManager.firestore.collection("caseNotes")
                .add(noteData)
                .await()

            logAudit(
                action = "CASE_NOTE_ADDED",
                category = "Case Management",
                performedBy = currentUid,
                targetId = reference,
                details = "Added confidential note to case $reference."
            )

            true
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            false
        }
    }

    suspend fun getCaseNotes(reference: String): List<CaseNote> {
        return try {
            val snapshot = FirebaseManager.firestore.collection("caseNotes")
                .whereEqualTo("reference", reference)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val ref = doc.getString("reference") ?: reference
                val text = doc.getString("noteText") ?: ""
                val authId = doc.getString("authorId") ?: ""
                val authName = doc.getString("authorName") ?: "Officer"
                val ts = doc.get("timestamp")

                CaseNote(
                    id = doc.id,
                    reference = ref,
                    noteText = text,
                    authorId = authId,
                    authorName = authName,
                    timestamp = ts
                )
            }.sortedByDescending { note ->
                (note.timestamp as? Timestamp)?.seconds ?: 0L
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            emptyList()
        }
    }

    suspend fun addReferral(
        reference: String,
        referralType: String,
        targetDepartment: String,
        notes: String,
        officerName: String
    ): Boolean {
        val currentUid = FirebaseManager.auth.currentUser?.uid ?: return false
        return try {
            val referralData = hashMapOf(
                "reference" to reference,
                "referralType" to referralType.trim(),
                "targetDepartment" to targetDepartment.trim(),
                "notes" to notes.trim(),
                "officerId" to currentUid,
                "officerName" to officerName,
                "timestamp" to FieldValue.serverTimestamp()
            )

            FirebaseManager.firestore.collection("referrals")
                .add(referralData)
                .await()

            logAudit(
                action = "CASE_REFERRAL_RECORDED",
                category = "Support & Referrals",
                performedBy = currentUid,
                targetId = reference,
                details = "Recorded $referralType referral to $targetDepartment for case $reference."
            )

            true
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            false
        }
    }

    suspend fun getReferrals(reference: String): List<CaseReferral> {
        return try {
            val snapshot = FirebaseManager.firestore.collection("referrals")
                .whereEqualTo("reference", reference)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val ref = doc.getString("reference") ?: reference
                val type = doc.getString("referralType") ?: "Support Service"
                val dept = doc.getString("targetDepartment") ?: "Counseling"
                val notes = doc.getString("notes") ?: ""
                val offId = doc.getString("officerId") ?: ""
                val offName = doc.getString("officerName") ?: "Officer"
                val ts = doc.get("timestamp")

                CaseReferral(
                    id = doc.id,
                    reference = ref,
                    referralType = type,
                    targetDepartment = dept,
                    notes = notes,
                    officerId = offId,
                    officerName = offName,
                    timestamp = ts
                )
            }.sortedByDescending { ref ->
                (ref.timestamp as? Timestamp)?.seconds ?: 0L
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            emptyList()
        }
    }

    suspend fun getCaseUpdates(reference: String): List<CaseUpdateRecord> {
        return try {
            val snapshot = FirebaseManager.firestore.collection("caseUpdates")
                .whereEqualTo("reference", reference)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val ref = doc.getString("reference") ?: reference
                val type = doc.getString("type") ?: "status"
                val status = doc.getString("status") ?: ""
                val assignedOfficerId = doc.getString("assignedOfficerId")
                val updatedBy = doc.getString("updatedBy") ?: ""
                val ts = doc.get("timestamp")

                CaseUpdateRecord(
                    id = doc.id,
                    reference = ref,
                    type = type,
                    status = status,
                    assignedOfficerId = assignedOfficerId,
                    updatedBy = updatedBy,
                    timestamp = ts
                )
            }.sortedBy { update ->
                (update.timestamp as? Timestamp)?.seconds ?: 0L
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            emptyList()
        }
    }

    suspend fun logAudit(
        action: String,
        category: String,
        performedBy: String,
        targetId: String,
        details: String
    ) {
        try {
            val auditData = hashMapOf(
                "action" to action,
                "category" to category,
                "performedBy" to performedBy,
                "targetId" to targetId,
                "details" to details,
                "timestamp" to FieldValue.serverTimestamp()
            )

            FirebaseManager.firestore.collection("auditLogs")
                .add(auditData)
                .await()
        } catch (_: Exception) {}
    }

    suspend fun getAuditLogs(): List<AuditLogRecord> {
        return try {
            val snapshot = FirebaseManager.firestore.collection("auditLogs")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val action = doc.getString("action") ?: "SYSTEM_ACTION"
                val category = doc.getString("category") ?: "General"
                val performedBy = doc.getString("performedBy") ?: "System"
                val targetId = doc.getString("targetId") ?: ""
                val details = doc.getString("details") ?: ""
                val ts = doc.get("timestamp")

                AuditLogRecord(
                    id = doc.id,
                    action = action,
                    category = category,
                    performedBy = performedBy,
                    targetId = targetId,
                    details = details,
                    timestamp = ts
                )
            }.sortedByDescending { log ->
                (log.timestamp as? Timestamp)?.seconds ?: 0L
            }.take(50)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            emptyList()
        }
    }
}
