package com.example.gbvapp.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf

enum class CaseStatus {
    Assigned, Open, Resolved, Closed
}

data class CaseItem(
    val reference: String,
    val category: String,
    val location: String,
    val dateReported: String,
    val description: String,
    val evidenceAttachments: List<String>,
    var status: CaseStatus,
    val submissionDate: String,
    var closureDate: String? = null
)

object CaseRepository {
    val cases = mutableStateListOf<CaseItem>()

    fun init(context: Context) {
        // SQLite persistence removed per Section 8 spec
    }

    fun addCase(caseItem: CaseItem) {
        cases.add(0, caseItem)
    }

    fun getCase(reference: String): CaseItem? {
        return cases.find { it.reference == reference }
    }

    fun closeCase(reference: String, closureDate: String): Boolean {
        val caseItem = getCase(reference) ?: return false
        if (caseItem.status == CaseStatus.Closed) return false
        caseItem.status = CaseStatus.Closed
        caseItem.closureDate = closureDate
        return true
    }

    fun getAssignedCount(): Int = cases.count { it.status == CaseStatus.Assigned }
    fun getOpenCount(): Int = cases.count { it.status == CaseStatus.Open }
    fun getResolvedCount(): Int = cases.count { it.status == CaseStatus.Resolved }
    fun getClosedCount(): Int = cases.count { it.status == CaseStatus.Closed }
}
