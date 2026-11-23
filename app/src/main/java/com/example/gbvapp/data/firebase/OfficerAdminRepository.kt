package com.example.gbvapp.data.firebase

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

object OfficerAdminRepository {

    suspend fun listByStatus(status: OfficerStatus): List<OfficerProfile>? {
        return try {
            val snapshot = FirebaseManager.firestore
                .collection("officers")
                .whereEqualTo("status", status.value)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val uid = doc.id
                val data = doc.data ?: return@mapNotNull null
                val name = data["name"] as? String ?: ""
                val email = data["email"] as? String ?: ""
                val statusStr = data["status"] as? String
                val roleStr = data["role"] as? String
                val createdAt = data["createdAt"]

                OfficerProfile(
                    uid = uid,
                    name = name,
                    email = email,
                    status = OfficerStatus.fromString(statusStr),
                    role = OfficerRole.fromString(roleStr),
                    createdAt = createdAt
                )
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            null
        }
    }

    suspend fun approve(uid: String): Boolean {
        val currentUid = FirebaseManager.auth.currentUser?.uid ?: return false
        return try {
            FirebaseManager.firestore
                .collection("officers")
                .document(uid)
                .update("status", OfficerStatus.APPROVED.value)
                .await()

            CaseManagementRepository.logAudit(
                action = "OFFICER_APPROVED",
                category = "User Management",
                performedBy = currentUid,
                targetId = uid,
                details = "Approved officer account $uid."
            )
            true
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            false
        }
    }

    suspend fun disable(uid: String): Boolean {
        val currentUid = FirebaseManager.auth.currentUser?.uid ?: return false
        return try {
            FirebaseManager.firestore
                .collection("officers")
                .document(uid)
                .update("status", OfficerStatus.DISABLED.value)
                .await()

            CaseManagementRepository.logAudit(
                action = "OFFICER_DISABLED",
                category = "User Management",
                performedBy = currentUid,
                targetId = uid,
                details = "Disabled officer account $uid."
            )
            true
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            false
        }
    }

    suspend fun reject(uid: String): Boolean {
        return disable(uid)
    }
}
