package com.example.gbvapp.data.firebase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class ReportDetailModel(
    val reference: String,
    val category: String,
    val description: String,
    val incidentDate: String? = null,
    val incidentTime: String? = null,
    val location: String? = null,
    val reportingFor: String = "Myself",
    val evidencePaths: List<String> = emptyList(),
    val status: String = "Pending",
    val assignedOfficerId: String? = null,
    val assignedOfficerName: String? = null,
    val assignedAt: Any? = null,
    val closureDate: Any? = null,
    val submissionTimestamp: Any? = null,
    val lastUpdatedAt: Any? = null
)

sealed class SubmitResult {
    data class Success(val reference: String) : SubmitResult()
    object NoConnection : SubmitResult()
    data class EvidenceFailed(val failedCount: Int) : SubmitResult()
    data class Failure(val message: String) : SubmitResult()
}

sealed class ReportsResult {
    data class Success(val reports: List<ReportDetailModel>) : ReportsResult()
    data class Error(val message: String) : ReportsResult()
}

sealed class SingleReportResult {
    data class Success(val report: ReportDetailModel) : SingleReportResult()
    object NotFound : SingleReportResult()
    data class Error(val message: String) : SingleReportResult()
}

object ReportRepository {

    fun formatTimestamp(timestamp: Any?): String {
        if (timestamp == null) return "Not provided"
        return try {
            val date: Date = when (timestamp) {
                is Timestamp -> timestamp.toDate()
                is Long -> Date(timestamp)
                is Date -> timestamp
                else -> return "Not provided"
            }
            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            sdf.format(date)
        } catch (_: Exception) {
            "Not provided"
        }
    }

    suspend fun submitReport(
        context: Context,
        category: String,
        description: String,
        incidentDate: String? = null,
        incidentTime: String? = null,
        location: String? = null,
        reportingFor: String = "Myself",
        evidenceUris: List<Uri> = emptyList(),
        skipFailedEvidence: Boolean = false
    ): SubmitResult {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            return SubmitResult.NoConnection
        }

        try {
            if (FirebaseManager.auth.currentUser == null) {
                FirebaseManager.auth.signInAnonymously().await()
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            val msg = e.message ?: ""
            val detailedMsg = if (msg.contains("disabled", ignoreCase = true) || msg.contains("not allowed", ignoreCase = true) || msg.contains("OPERATION_NOT_ALLOWED", ignoreCase = true)) {
                "Anonymous sign-in is disabled in Firebase Console. Please enable Anonymous authentication under Firebase Console > Authentication > Sign-in method."
            } else if (msg.isNotBlank()) {
                "Authentication failed: $msg"
            } else {
                "Authentication failed. Please try again."
            }
            return SubmitResult.Failure(detailedMsg)
        }

        val evidencePaths = mutableListOf<String>()
        var failedCount = 0

        for (uri in evidenceUris) {
            val jpegBytes = ImageProcessingUtils.processImageToJpegBytes(context, uri)
            if (jpegBytes == null) {
                failedCount++
                continue
            }

            val uuid = UUID.randomUUID().toString()
            val path = "evidence/$uuid"
            val ref = FirebaseManager.storage.reference.child(path)
            val metadata = StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build()

            try {
                ref.putBytes(jpegBytes, metadata).await()
                evidencePaths.add(path)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                failedCount++
            }
        }

        if (failedCount > 0 && !skipFailedEvidence) {
            return SubmitResult.EvidenceFailed(failedCount)
        }

        var reference = ReferenceGenerator.generateReference()

        fun buildReportData(ref: String): HashMap<String, Any?> {
            val data = hashMapOf<String, Any?>(
                "reference" to ref,
                "category" to category.trim(),
                "description" to description.trim(),
                "reportingFor" to reportingFor.trim(),
                "status" to "Pending",
                "assignedOfficerId" to null,
                "assignedOfficerName" to null,
                "submissionTimestamp" to FieldValue.serverTimestamp(),
                "lastUpdatedAt" to FieldValue.serverTimestamp()
            )
            if (!incidentDate.isNullOrBlank()) data["incidentDate"] = incidentDate.trim()
            if (!incidentTime.isNullOrBlank()) data["incidentTime"] = incidentTime.trim()
            if (!location.isNullOrBlank()) data["location"] = location.trim()
            if (evidencePaths.isNotEmpty()) data["evidencePaths"] = evidencePaths
            return data
        }

        var attempts = 0
        var lastErrorMsg = "Your report could not be saved. Please try again."
        while (attempts < 2) {
            attempts++
            try {
                val docRef = FirebaseManager.firestore.collection("reports").document(reference)
                val reportData = buildReportData(reference)

                withTimeout(20_000) {
                    FirebaseManager.firestore.runTransaction { transaction ->
                        transaction.set(docRef, reportData)
                    }.await()
                }
                return SubmitResult.Success(reference)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                lastErrorMsg = e.message ?: "Your report could not be saved. Please try again."
                if (attempts >= 2) {
                    return SubmitResult.Failure(lastErrorMsg)
                }
                reference = ReferenceGenerator.generateReference()
            }
        }

        return SubmitResult.Failure(lastErrorMsg)
    }

    suspend fun getAllReports(): ReportsResult {
        return try {
            val snapshot = FirebaseManager.firestore.collection("reports")
                .get()
                .await()
            val list = snapshot.documents.mapNotNull { parseReportDoc(it) }
                .sortedByDescending { doc ->
                    (doc.submissionTimestamp as? Timestamp)?.seconds ?: 0L
                }
            ReportsResult.Success(list)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            ReportsResult.Error("Could not load reports. Check your connection and try again.")
        }
    }

    suspend fun getAssignedReportsForOfficer(officerId: String): ReportsResult {
        return try {
            val snapshot = FirebaseManager.firestore.collection("reports")
                .whereEqualTo("assignedOfficerId", officerId)
                .get()
                .await()
            val list = snapshot.documents.mapNotNull { parseReportDoc(it) }
                .sortedByDescending { doc ->
                    (doc.submissionTimestamp as? Timestamp)?.seconds ?: 0L
                }
            ReportsResult.Success(list)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            ReportsResult.Error("Could not load assigned cases. Check your connection and try again.")
        }
    }

    suspend fun getReport(reference: String): SingleReportResult {
        return try {
            val doc = FirebaseManager.firestore.collection("reports")
                .document(reference)
                .get()
                .await()
            if (!doc.exists()) {
                SingleReportResult.NotFound
            } else {
                val report = parseReportDoc(doc)
                if (report != null) {
                    SingleReportResult.Success(report)
                } else {
                    SingleReportResult.Error("Could not parse case details.")
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            SingleReportResult.Error("Could not load case details.")
        }
    }

    suspend fun loadEvidenceBitmap(path: String): Bitmap? {
        return try {
            val tenMB: Long = 10 * 1024 * 1024
            val bytes = FirebaseManager.storage.reference.child(path).getBytes(tenMB).await()
            val options = BitmapFactory.Options().apply {
                inSampleSize = 2
            }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        } catch (_: Exception) {
            null
        }
    }

    private fun parseReportDoc(doc: DocumentSnapshot): ReportDetailModel? {
        val ref = doc.getString("reference") ?: doc.id
        val cat = doc.getString("category") ?: return null
        val desc = doc.getString("description") ?: ""
        val incDate = doc.getString("incidentDate")
        val incTime = doc.getString("incidentTime")
        val loc = doc.getString("location")
        val repFor = doc.getString("reportingFor") ?: "Myself"
        @Suppress("UNCHECKED_CAST")
        val evPaths = doc.get("evidencePaths") as? List<String> ?: emptyList()
        val status = doc.getString("status") ?: "Pending"
        val assignedOfficerId = doc.getString("assignedOfficerId")
        val assignedOfficerName = doc.getString("assignedOfficerName")
        val assignedAt = doc.get("assignedAt")
        val closureDate = doc.get("closureDate")
        val subTimestamp = doc.get("submissionTimestamp")
        val lastUpdate = doc.get("lastUpdatedAt")

        return ReportDetailModel(
            reference = ref,
            category = cat,
            description = desc,
            incidentDate = incDate,
            incidentTime = incTime,
            location = loc,
            reportingFor = repFor,
            evidencePaths = evPaths,
            status = status,
            assignedOfficerId = assignedOfficerId,
            assignedOfficerName = assignedOfficerName,
            assignedAt = assignedAt,
            closureDate = closureDate,
            submissionTimestamp = subTimestamp,
            lastUpdatedAt = lastUpdate
        )
    }
}
