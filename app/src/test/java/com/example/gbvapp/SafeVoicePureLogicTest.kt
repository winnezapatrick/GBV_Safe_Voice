package com.example.gbvapp

import com.example.gbvapp.data.firebase.ReferenceGenerator
import com.example.gbvapp.data.firebase.ReportDetailModel
import com.example.gbvapp.data.firebase.canTransition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

fun validateReportInput(category: String, description: String): String? {
    if (category.isBlank()) return "Please select a category."
    if (description.isBlank()) return "Please describe what happened."
    return null
}

fun countReportsByStatus(reports: List<ReportDetailModel>): Map<String, Int> {
    val counts = mutableMapOf(
        "Pending" to 0,
        "Assigned" to 0,
        "Open" to 0,
        "Resolved" to 0,
        "Closed" to 0
    )
    for (report in reports) {
        val current = counts[report.status] ?: 0
        counts[report.status] = current + 1
    }
    return counts
}

fun filterReports(reports: List<ReportDetailModel>, filter: String): List<ReportDetailModel> {
    return when (filter) {
        "All" -> reports
        "Unassigned" -> reports.filter { it.status == "Pending" }
        else -> reports.filter { it.status.equals(filter, ignoreCase = true) }
    }
}

class SafeVoicePureLogicTest {

    @Test
    fun testReferenceGenerator_formatRegex() {
        val regex = "^SV-[A-HJ-NP-Z2-9]{4}-[A-HJ-NP-Z2-9]{4}$".toRegex()
        repeat(100) {
            val ref = ReferenceGenerator.generateReference()
            assertTrue("Generated ref $ref should match regex", ref.matches(regex))
        }
    }

    @Test
    fun testReferenceGenerator_noDuplicatesIn10000Samples() {
        val set = HashSet<String>(10000)
        repeat(10000) {
            val ref = ReferenceGenerator.generateReference()
            set.add(ref)
        }
        assertEquals(10000, set.size)
    }

    @Test
    fun testCanTransition_fullTruthTable() {
        // Valid transitions
        assertTrue(canTransition("Assigned", "Open"))
        assertTrue(canTransition("Open", "Resolved"))
        assertTrue(canTransition("Open", "Closed"))
        assertTrue(canTransition("Resolved", "Closed"))

        // Invalid transitions
        assertFalse(canTransition("Pending", "Assigned"))
        assertFalse(canTransition("Pending", "Open"))
        assertFalse(canTransition("Pending", "Resolved"))
        assertFalse(canTransition("Pending", "Closed"))
        assertFalse(canTransition("Assigned", "Resolved"))
        assertFalse(canTransition("Assigned", "Closed"))
        assertFalse(canTransition("Resolved", "Open"))
        assertFalse(canTransition("Closed", "Open"))
        assertFalse(canTransition("Closed", "Resolved"))
        assertFalse(canTransition("Closed", "Closed"))
        assertFalse(canTransition("Unknown", "Open"))
    }

    @Test
    fun testStatusCountsAndFilters_mutuallyExclusiveAndMatching() {
        val reports = listOf(
            ReportDetailModel(reference = "SV-2026-0001", category = "Harassment", description = "Test 1", status = "Pending"),
            ReportDetailModel(reference = "SV-2026-0002", category = "Harassment", description = "Test 2", status = "Pending"),
            ReportDetailModel(reference = "SV-2026-0003", category = "Physical", description = "Test 3", status = "Assigned"),
            ReportDetailModel(reference = "SV-2026-0004", category = "Domestic", description = "Test 4", status = "Open"),
            ReportDetailModel(reference = "SV-2026-0005", category = "Cyber", description = "Test 5", status = "Resolved"),
            ReportDetailModel(reference = "SV-2026-0006", category = "Economic", description = "Test 6", status = "Closed")
        )

        val counts = countReportsByStatus(reports)
        assertEquals(2, counts["Pending"])
        assertEquals(1, counts["Assigned"])
        assertEquals(1, counts["Open"])
        assertEquals(1, counts["Resolved"])
        assertEquals(1, counts["Closed"])

        val totalSum = counts.values.sum()
        assertEquals(reports.size, totalSum)

        // Test filtering
        assertEquals(6, filterReports(reports, "All").size)
        assertEquals(2, filterReports(reports, "Unassigned").size)
        assertEquals(1, filterReports(reports, "Assigned").size)
        assertEquals(1, filterReports(reports, "Open").size)
        assertEquals(1, filterReports(reports, "Resolved").size)
        assertEquals(1, filterReports(reports, "Closed").size)
    }

    @Test
    fun testReportInputValidationMessages() {
        assertEquals("Please select a category.", validateReportInput("", "Description here"))
        assertEquals("Please select a category.", validateReportInput("   ", "Description here"))
        assertEquals("Please describe what happened.", validateReportInput("Sexual Harassment", ""))
        assertEquals("Please describe what happened.", validateReportInput("Sexual Harassment", "   "))
        assertNull(validateReportInput("Sexual Harassment", "A valid description of the incident."))
    }
}
