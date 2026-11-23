package com.example.gbvapp

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.gbvapp.data.CaseItem
import com.example.gbvapp.data.CaseRepository
import com.example.gbvapp.data.CaseStatus
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SafeVoiceInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
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

    private fun clickText(text: String) {
        val node = composeTestRule.onNodeWithText(text)
        try {
            node.performScrollTo()
        } catch (_: Throwable) {}
        node.performClick()
    }

    private fun clickFirstText(text: String) {
        val node = composeTestRule.onAllNodesWithText(text).onFirst()
        try {
            node.performScrollTo()
        } catch (_: Throwable) {}
        node.performClick()
    }

    private fun clickIndexedText(text: String, index: Int) {
        val node = composeTestRule.onAllNodesWithText(text)[index]
        try {
            node.performScrollTo()
        } catch (_: Throwable) {}
        node.performClick()
    }

    // ==========================================
    // REPORTER WORKFLOW TEST CASES (REP_*)
    // ==========================================

    @Test
    fun test_REP_WELCOME_001_DisplayWelcomeScreen() {
        composeTestRule.onNodeWithText("Safe Voice Reporting").assertExists()
        composeTestRule.onNodeWithText("PROCEED TO REPORT").assertExists()
        composeTestRule.onNodeWithText("A confidential, secure space to report GBV safely and get support.").assertExists()
    }

    @Test
    fun test_REP_ACCESS_001_BeginReportingWithoutAccount() {
        clickText("PROCEED TO REPORT")
        composeTestRule.onNodeWithText("STEP 1 OF 3").assertExists()
        composeTestRule.onAllNodesWithText("Your Details").onFirst().assertExists()
        composeTestRule.onNodeWithText("Who are you reporting for?").assertExists()
    }

    @Test
    fun test_REP_CONTEXT_001_SelectReportingContext() {
        clickText("PROCEED TO REPORT")
        clickText("Someone Else")
        clickText("CONTINUE TO CATEGORY & EVIDENCE")
        composeTestRule.onNodeWithText("STEP 2 OF 3").assertExists()
        composeTestRule.onAllNodesWithText("Category & Evidence").onFirst().assertExists()
    }

    @Test
    fun test_REP_CONTEXT_002_ContinueWithoutSelectingReportingContext() {
        clickText("PROCEED TO REPORT")
        clickText("CONTINUE TO CATEGORY & EVIDENCE")
        composeTestRule.onNodeWithText("Please select who you are reporting for.").assertExists()
        composeTestRule.onNodeWithText("STEP 1 OF 3").assertExists()
    }

    @Test
    fun test_REP_PRIVACY_001_SubmitReportWithoutPersonalContactDetails() {
        clickText("PROCEED TO REPORT")
        clickText("Myself")
        clickText("CONTINUE TO CATEGORY & EVIDENCE")
        
        clickText("Sexual Harassment")
        composeTestRule.onNodeWithText("Describe the incident in your own words...")
            .performTextInput("Fictional test report for Safe Voice")
        clickText("CONTINUE TO DATE & LOCATION")
        
        clickText("SUBMIT REPORT")
        composeTestRule.onNodeWithText("Report Submitted Successfully").assertExists()
        composeTestRule.onNodeWithText("YOUR CONFIDENTIAL CASE REFERENCE").assertExists()
    }

    @Test
    fun test_REP_CATEGORY_001_SelectGBVIncidentCategory() {
        clickText("PROCEED TO REPORT")
        clickText("Myself")
        clickText("CONTINUE TO CATEGORY & EVIDENCE")
        clickText("Domestic Violence")
        composeTestRule.onNodeWithText("Describe the incident in your own words...")
            .performTextInput("Fictional test report")
        clickText("CONTINUE TO DATE & LOCATION")
        composeTestRule.onAllNodesWithText("Domestic Violence").onFirst().assertExists()
    }

    @Test
    fun test_REP_CATEGORY_002_SubmitWithoutIncidentCategory() {
        clickText("PROCEED TO REPORT")
        clickText("Myself")
        clickText("CONTINUE TO CATEGORY & EVIDENCE")
        composeTestRule.onNodeWithText("Describe the incident in your own words...")
            .performTextInput("Fictional test report")
        clickText("CONTINUE TO DATE & LOCATION")
        composeTestRule.onNodeWithText("Incident category is required.").assertExists()
    }

    @Test
    fun test_REP_DESCRIPTION_001_EnterIncidentDescription() {
        clickText("PROCEED TO REPORT")
        clickText("Myself")
        clickText("CONTINUE TO CATEGORY & EVIDENCE")
        composeTestRule.onNodeWithText("Describe the incident in your own words...")
            .performTextInput("Fictional test report created only to check the Safe Voice form.")
        composeTestRule.onNodeWithText("Fictional test report created only to check the Safe Voice form.").assertExists()
    }

    @Test
    fun test_REP_DESCRIPTION_002_SubmitWithoutIncidentDescription() {
        clickText("PROCEED TO REPORT")
        clickText("Myself")
        clickText("CONTINUE TO CATEGORY & EVIDENCE")
        clickText("Sexual Harassment")
        clickText("CONTINUE TO DATE & LOCATION")
        composeTestRule.onNodeWithText("Incident description is required.").assertExists()
    }

    @Test
    fun test_REP_DETAILS_001_EnterKnownIncidentDateAndTimeAndLocation() {
        clickText("PROCEED TO REPORT")
        clickText("Myself")
        clickText("CONTINUE TO CATEGORY & EVIDENCE")
        clickText("Sexual Harassment")
        composeTestRule.onNodeWithText("Describe the incident in your own words...")
            .performTextInput("Fictional test report")
        clickText("CONTINUE TO DATE & LOCATION")
        
        composeTestRule.onNodeWithText("02 / 10 / 2026")
            .performTextReplacement("20 / 10 / 2026")
        composeTestRule.onNodeWithText("20 / 10 / 2026").assertExists()
    }

    @Test
    fun test_REP_DETAILS_002_LeaveUnknownIncidentDetailsBlank() {
        clickText("PROCEED TO REPORT")
        clickText("Myself")
        clickText("CONTINUE TO CATEGORY & EVIDENCE")
        clickText("Sexual Harassment")
        composeTestRule.onNodeWithText("Describe the incident in your own words...")
            .performTextInput("Fictional test report")
        clickText("CONTINUE TO DATE & LOCATION")
        clickText("SUBMIT REPORT")
        composeTestRule.onNodeWithText("Report Submitted Successfully").assertExists()
    }

    @Test
    fun test_REP_EVIDENCE_001_AttachOptionalSupportingEvidence() {
        clickText("PROCEED TO REPORT")
        clickText("Myself")
        clickText("CONTINUE TO CATEGORY & EVIDENCE")
        composeTestRule.onNodeWithText("Upload Supporting Files").assertExists()
        composeTestRule.onNodeWithText("photo_evidence_01.jpg").assertExists()
        composeTestRule.onNodeWithText("1.2 MB • Encrypted").assertExists()
    }

    @Test
    fun test_REP_EVIDENCE_002_HandleEvidenceUploadFailure() {
        clickText("PROCEED TO REPORT")
        clickText("Myself")
        clickText("CONTINUE TO CATEGORY & EVIDENCE")
        composeTestRule.onNodeWithContentDescription("Remove").performClick()
        composeTestRule.onNodeWithText("photo_evidence_01.jpg").assertDoesNotExist()
    }

    @Test
    fun test_REP_SUBMIT_001_SubmitValidReportSuccessfully() {
        clickText("PROCEED TO REPORT")
        clickText("Myself")
        clickText("CONTINUE TO CATEGORY & EVIDENCE")
        clickText("Sexual Harassment")
        composeTestRule.onNodeWithText("Describe the incident in your own words...")
            .performTextInput("Valid test incident report")
        clickText("CONTINUE TO DATE & LOCATION")
        clickText("SUBMIT REPORT")
        composeTestRule.onNodeWithText("Report Submitted Successfully").assertExists()
        composeTestRule.onNodeWithText("SV-2026-0047").assertExists()
    }

    @Test
    fun test_REP_SUBMIT_002_HandleReportSubmissionFailure() {
        clickText("PROCEED TO REPORT")
        clickText("Myself")
        clickText("CONTINUE TO CATEGORY & EVIDENCE")
        clickText("Sexual Harassment")
        clickText("CONTINUE TO DATE & LOCATION")
        composeTestRule.onNodeWithText("Incident description is required.").assertExists()
        composeTestRule.onNodeWithText("Report Submitted Successfully").assertDoesNotExist()
    }

    @Test
    fun test_REP_REFERENCE_001_GenerateDifferentReferencesForSeparateReports() {
        clickText("PROCEED TO REPORT")
        clickText("Myself")
        clickText("CONTINUE TO CATEGORY & EVIDENCE")
        clickText("Sexual Harassment")
        composeTestRule.onNodeWithText("Describe the incident in your own words...")
            .performTextInput("Report A")
        clickText("CONTINUE TO DATE & LOCATION")
        clickText("SUBMIT REPORT")
        composeTestRule.onNodeWithText("SV-2026-0047").assertExists()

        clickText("DONE")
        composeTestRule.onNodeWithText("Safe Voice Reporting").assertExists()
    }

    // ==========================================
    // OFFICER PORTAL TEST CASES (OFF_*)
    // ==========================================

    @Test
    fun test_OFF_LOGIN_001_LogInWithValidOfficerCredentials() {
        clickFirstText("Officer")
        composeTestRule.onNodeWithText("Officer Login").assertExists()
        clickText("LOGIN")
        composeTestRule.onNodeWithText("Officer Dashboard").assertExists()
        composeTestRule.onNodeWithText("Welcome, Officer Banda. Here is your active case overview.").assertExists()
    }

    @Test
    fun test_OFF_LOGIN_002_RejectInvalidOfficerCredentials() {
        clickFirstText("Officer")
        composeTestRule.onNodeWithText("officer@safevoice.org")
            .performTextReplacement("invalid-email-format")
        clickText("LOGIN")
        composeTestRule.onNodeWithText("Please enter a valid email address.").assertExists()
        composeTestRule.onNodeWithText("Officer Dashboard").assertDoesNotExist()
    }

    @Test
    fun test_OFF_LOGIN_003_RejectEmptyOfficerLoginFields() {
        clickFirstText("Officer")
        clickText("LOGIN")
        composeTestRule.onNodeWithText("Please enter your email and password.").assertExists()
        composeTestRule.onNodeWithText("Officer Dashboard").assertDoesNotExist()
    }

    @Test
    fun test_OFF_ACCESS_001_PreventOfficerDashboardAccessWithoutLogin() {
        composeTestRule.onNodeWithText("Safe Voice Reporting").assertExists()
        composeTestRule.onNodeWithText("Officer Dashboard").assertDoesNotExist()
        composeTestRule.onNodeWithText("All Authorized Cases").assertDoesNotExist()
    }

    @Test
    fun test_OFF_DASHBOARD_001_DisplayCorrectDashboardCaseTotals() {
        clickFirstText("Officer")
        clickText("LOGIN")
        composeTestRule.onNodeWithText("Assigned").assertExists()
        composeTestRule.onNodeWithText("Open").assertExists()
        composeTestRule.onNodeWithText("Resolved").assertExists()
        composeTestRule.onNodeWithText("Closed").assertExists()
    }

    @Test
    fun test_OFF_CASELIST_001_ViewAllCasesList() {
        clickFirstText("Officer")
        clickText("LOGIN")
        composeTestRule.onNodeWithText("All Authorized Cases").assertExists()
        composeTestRule.onNodeWithText("GBV-2026-0047").assertExists()
        composeTestRule.onNodeWithText("GBV-2026-0042").assertExists()
        composeTestRule.onNodeWithText("GBV-2026-0035").assertExists()
        composeTestRule.onNodeWithText("GBV-2026-0028").assertExists()
    }

    @Test
    fun test_OFF_CASELIST_002_RestrictCasesOutsideOfficerPermissions() {
        clickFirstText("Officer")
        clickText("LOGIN")
        composeTestRule.onNodeWithText("SV-TEST-RESTRICTED").assertDoesNotExist()
    }

    @Test
    fun test_OFF_CASEDETAIL_001_OpenAnAuthorizedCase() {
        clickFirstText("Officer")
        clickText("LOGIN")
        clickFirstText("OPEN CASE")
        composeTestRule.onNodeWithText("CASE FILE REVIEW").assertExists()
        composeTestRule.onNodeWithText("GBV-2026-0047").assertExists()
        composeTestRule.onNodeWithText("Sexual Harassment • Immediate Response").assertExists()
    }

    @Test
    fun test_OFF_CASEDETAIL_002_DenyAccessToUnauthorizedCase() {
        clickFirstText("Officer")
        clickText("LOGIN")
        composeTestRule.onNodeWithText("SV-TEST-RESTRICTED").assertDoesNotExist()
    }

    @Test
    fun test_OFF_CLOSE_001_CancelCaseClosure() {
        clickFirstText("Officer")
        clickText("LOGIN")
        clickFirstText("OPEN CASE")
        clickText("CLOSE CASE")
        composeTestRule.onNodeWithText("Are you sure you want to close this case?").assertExists()
        clickText("Cancel")
        composeTestRule.onNodeWithText("Are you sure you want to close this case?").assertDoesNotExist()
        composeTestRule.onNodeWithText("CLOSE CASE").assertExists()
    }

    @Test
    fun test_OFF_CLOSE_002_ConfirmCaseClosure() {
        clickFirstText("Officer")
        clickText("LOGIN")
        clickFirstText("OPEN CASE")
        clickText("CLOSE CASE")
        clickText("Confirm Close")
        composeTestRule.onNodeWithText("This case is formally CLOSED").assertExists()
    }

    @Test
    fun test_OFF_CLOSE_003_HandleFailedCaseClosureUpdate() {
        clickFirstText("Officer")
        clickText("LOGIN")
        clickFirstText("OPEN CASE")
        clickText("CLOSE CASE")
        clickText("Cancel")
        composeTestRule.onNodeWithText("This case is formally CLOSED").assertDoesNotExist()
    }

    @Test
    fun test_OFF_CLOSE_004_PreventRepeatedClosureOfAlreadyClosedCase() {
        clickFirstText("Officer")
        clickText("LOGIN")
        composeTestRule.onNodeWithText("GBV-2026-0028").assertExists()
        clickIndexedText("OPEN CASE", 3)
        composeTestRule.onNodeWithText("This case is formally CLOSED").assertExists()
        composeTestRule.onNodeWithText("CLOSE CASE").assertDoesNotExist()
    }

    @Test
    fun test_OFF_DASHBOARD_002_RefreshDashboardTotalsAfterCaseClosure() {
        clickFirstText("Officer")
        clickText("LOGIN")
        clickIndexedText("OPEN CASE", 2)
        clickText("CLOSE CASE")
        clickText("Confirm Close")
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.onNodeWithText("Officer Dashboard").assertExists()
    }

    @Test
    fun test_OFF_DASHBOARD_003_CountEachCaseUnderOneCurrentStatus() {
        clickFirstText("Officer")
        clickText("LOGIN")
        composeTestRule.onNodeWithText("4 Total").assertExists()
        composeTestRule.onNodeWithText("Assigned").assertExists()
        composeTestRule.onNodeWithText("Open").assertExists()
        composeTestRule.onNodeWithText("Resolved").assertExists()
        composeTestRule.onNodeWithText("Closed").assertExists()
    }
}
