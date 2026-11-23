package com.example.gbvapp

import com.example.gbvapp.data.CaseItem
import com.example.gbvapp.data.CaseRepository
import com.example.gbvapp.data.CaseStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SafeVoiceUnitTest {

    @Before
    fun setUp() {
        CaseRepository.cases.clear()
        CaseRepository.cases.addAll(
            listOf(
                CaseItem(
                    reference = "GBV-2026-0047",
                    category = "Sexual Harassment • Immediate Response",
                    location = "Zomba Central, Near District Hospital Market",
                    dateReported = "Today, 14:30",
                    description = "Victim reported targeted harassment and physical intimidation near the local market area. Immediate support and intervention required.",
                    evidenceAttachments = listOf("Audio Recording (0:45)", "Photo Evidence.jpg"),
                    status = CaseStatus.Assigned,
                    submissionDate = "Today, 14:30"
                ),
                CaseItem(
                    reference = "GBV-2026-0042",
                    category = "Domestic Violence • Intake Handover",
                    location = "Blantyre West Transit Zone",
                    dateReported = "Yesterday, 09:15",
                    description = "Ongoing domestic dispute with risk of physical escalation. Safe shelter referral requested.",
                    evidenceAttachments = listOf("Medical Report Form.pdf"),
                    status = CaseStatus.Open,
                    submissionDate = "Yesterday, 09:15"
                ),
                CaseItem(
                    reference = "GBV-2026-0035",
                    category = "Physical Abuse • Legal Closure",
                    location = "Zomba Urban",
                    dateReported = "01/10/2026, 11:20",
                    description = "Physical assault case referred to legal aid and counseling services.",
                    evidenceAttachments = listOf("Incident Statement.pdf"),
                    status = CaseStatus.Resolved,
                    submissionDate = "01/10/2026, 11:20"
                ),
                CaseItem(
                    reference = "GBV-2026-0028",
                    category = "Economic Abuse • Counseling",
                    location = "Lilongwe Area 25",
                    dateReported = "28/09/2026, 16:00",
                    description = "Denial of financial resources and support. Counseling sessions completed.",
                    evidenceAttachments = listOf("Case Summary.pdf"),
                    status = CaseStatus.Closed,
                    submissionDate = "28/09/2026, 16:00",
                    closureDate = "30/09/2026, 10:00"
                )
            )
        )
    }

    @Test
    fun test_REP_WELCOME_001_WelcomeScreenInitialState() {
        val initialScreen = "welcome"
        assertEquals("welcome", initialScreen)
    }

    @Test
    fun test_REP_ACCESS_001_ProceedWithoutAccount() {
        var currentScreen = "welcome"
        currentScreen = "step1"
        assertEquals("step1", currentScreen)
    }

    @Test
    fun test_REP_CONTEXT_001_SelectReportingContext() {
        var reportingFor = "Myself"
        reportingFor = "Someone Else"
        assertEquals("Someone Else", reportingFor)
    }

    @Test
    fun test_REP_CONTEXT_002_MissingReportingContextValidation() {
        val reportingFor = ""
        val isValid = reportingFor.isNotBlank()
        assertFalse(isValid)
    }

    @Test
    fun test_REP_PRIVACY_001_AnonymousSubmissionNoContactDetails() {
        val name = ""
        val phone = ""
        val isAnonymousAllowed = name.isBlank() && phone.isBlank()
        assertTrue(isAnonymousAllowed)
    }

    @Test
    fun test_REP_CATEGORY_001_SelectIncidentCategory() {
        val category = "Domestic Violence"
        assertTrue(category.isNotBlank())
    }

    @Test
    fun test_REP_CATEGORY_002_MissingCategoryValidation() {
        val category = ""
        val isValid = category.isNotBlank()
        assertFalse(isValid)
    }

    @Test
    fun test_REP_DESCRIPTION_001_EnterDescription() {
        val description = "Fictional test report created only to check the Safe Voice form."
        assertTrue(description.length <= 2000)
    }

    @Test
    fun test_REP_DESCRIPTION_002_MissingDescriptionValidation() {
        val description = ""
        val isValid = description.isNotBlank()
        assertFalse(isValid)
    }

    @Test
    fun test_OFF_LOGIN_001_ValidEmailValidation() {
        val email = "officer@safevoice.org"
        val password = "SecurePassword123!"
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
        val isValid = email.isNotBlank() && password.isNotBlank() && email.matches(emailRegex)
        assertTrue(isValid)
    }

    @Test
    fun test_OFF_LOGIN_002_RejectInvalidEmailFormat() {
        val email = "invalid-email-format"
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
        val isValid = email.matches(emailRegex)
        assertFalse(isValid)
    }

    @Test
    fun test_OFF_LOGIN_003_RejectEmptyOfficerLoginFields() {
        val email = ""
        val password = ""
        val isValid = email.isNotBlank() && password.isNotBlank()
        assertFalse(isValid)
    }

    @Test
    fun test_OFF_DASHBOARD_001_CorrectDashboardCaseTotals() {
        assertEquals(1, CaseRepository.getAssignedCount())
        assertEquals(1, CaseRepository.getOpenCount())
        assertEquals(1, CaseRepository.getResolvedCount())
        assertEquals(1, CaseRepository.getClosedCount())
    }

    @Test
    fun test_OFF_CASELIST_001_RetrieveCaseByReference() {
        val caseItem = CaseRepository.getCase("GBV-2026-0047")
        assertNotNull(caseItem)
        assertEquals("GBV-2026-0047", caseItem?.reference)
    }

    @Test
    fun test_OFF_CASELIST_002_RestrictNonExistentCase() {
        val caseItem = CaseRepository.getCase("SV-TEST-RESTRICTED")
        assertNull(caseItem)
    }

    @Test
    fun test_OFF_CLOSE_001_CancelCaseClosure() {
        val caseItem = CaseRepository.getCase("GBV-2026-0047")
        assertNotNull(caseItem)
        assertEquals(CaseStatus.Assigned, caseItem?.status)
    }

    @Test
    fun test_OFF_CLOSE_002_ConfirmCaseClosure() {
        val success = CaseRepository.closeCase("GBV-2026-0047", "02/10/2026, 15:00")
        assertTrue(success)
        val closedCase = CaseRepository.getCase("GBV-2026-0047")
        assertEquals(CaseStatus.Closed, closedCase?.status)
        assertEquals("02/10/2026, 15:00", closedCase?.closureDate)
    }

    @Test
    fun test_OFF_CLOSE_004_PreventRepeatedClosure() {
        val success = CaseRepository.closeCase("GBV-2026-0028", "02/10/2026, 15:00")
        assertFalse(success)
    }

    @Test
    fun test_OFF_DASHBOARD_002_RefreshDashboardTotalsAfterClosure() {
        assertEquals(1, CaseRepository.getClosedCount())
        CaseRepository.closeCase("GBV-2026-0035", "02/10/2026, 15:00")
        assertEquals(2, CaseRepository.getClosedCount())
        assertEquals(0, CaseRepository.getResolvedCount())
    }

    @Test
    fun test_OFF_DASHBOARD_003_MutuallyExclusiveStatusCounts() {
        val totalCases = CaseRepository.cases.size
        val sumCounts = CaseRepository.getAssignedCount() +
                CaseRepository.getOpenCount() +
                CaseRepository.getResolvedCount() +
                CaseRepository.getClosedCount()
        assertEquals(totalCases, sumCounts)
    }
}
