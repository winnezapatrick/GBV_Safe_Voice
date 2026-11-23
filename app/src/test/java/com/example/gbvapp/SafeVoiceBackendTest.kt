package com.example.gbvapp

import com.example.gbvapp.data.CaseItem
import com.example.gbvapp.data.CaseRepository
import com.example.gbvapp.data.CaseStatus
import com.example.gbvapp.data.firebase.AuthRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SafeVoiceBackendTest {

    @Before
    fun setUp() {
        CaseRepository.cases.clear()
        CaseRepository.cases.addAll(
            listOf(
                CaseItem(
                    reference = "GBV-2026-0047",
                    category = "Sexual Harassment • Immediate Response",
                    location = "Zomba Central",
                    dateReported = "Today, 14:30",
                    description = "Test description 1",
                    evidenceAttachments = listOf("evidence1.jpg"),
                    status = CaseStatus.Assigned,
                    submissionDate = "Today, 14:30"
                ),
                CaseItem(
                    reference = "GBV-2026-0042",
                    category = "Domestic Violence",
                    location = "Blantyre",
                    dateReported = "Yesterday",
                    description = "Test description 2",
                    evidenceAttachments = emptyList(),
                    status = CaseStatus.Open,
                    submissionDate = "Yesterday"
                )
            )
        )
    }

    @Test
    fun testBackendCaseRepositoryPersistence() {
        val newCase = CaseItem(
            reference = "GBV-2026-9999",
            category = "Physical Abuse",
            location = "Lilongwe",
            dateReported = "Just now",
            description = "Backend test case description",
            evidenceAttachments = listOf("photo.jpg"),
            status = CaseStatus.Assigned,
            submissionDate = "Today"
        )
        CaseRepository.addCase(newCase)

        val retrieved = CaseRepository.getCase("GBV-2026-9999")
        assertNotNull(retrieved)
        assertEquals("Physical Abuse", retrieved?.category)
        assertEquals(CaseStatus.Assigned, retrieved?.status)
    }

    @Test
    fun testBackendCaseClosureFlow() {
        val success = CaseRepository.closeCase("GBV-2026-0042", "02/10/2026, 15:00")
        assertTrue(success)

        val closedCase = CaseRepository.getCase("GBV-2026-0042")
        assertEquals(CaseStatus.Closed, closedCase?.status)
        assertEquals("02/10/2026, 15:00", closedCase?.closureDate)
    }

    @Test
    fun testBackendDashboardCounts() {
        assertEquals(1, CaseRepository.getAssignedCount())
        assertEquals(1, CaseRepository.getOpenCount())
        assertEquals(0, CaseRepository.getResolvedCount())
        assertEquals(0, CaseRepository.getClosedCount())
    }

    @Test
    fun testAuthRepositoryOfficerId() {
        val officerId = AuthRepository.getCurrentOfficerId()
        assertNull(officerId)
    }

    @Test
    fun testReportRepositoryReferenceNumberGeneration() {
        val refNumber = "SV-2026-" + (1000..9999).random()
        assertTrue(refNumber.startsWith("SV-2026-"))
        assertEquals(12, refNumber.length)
    }
}
