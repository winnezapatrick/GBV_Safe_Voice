package com.example.gbvapp

import com.example.gbvapp.data.firebase.OfficerRole
import com.example.gbvapp.data.firebase.OfficerStatus
import com.example.gbvapp.data.firebase.validateRegistrationInput
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OfficerRegistrationAuthTest {

    @Test
    fun testValidateRegistrationInput_emptyName() {
        val result = validateRegistrationInput("", "officer@example.com", "Password123", "Password123")
        assertEquals("Please enter your full name.", result)
    }

    @Test
    fun testValidateRegistrationInput_blankName() {
        val result = validateRegistrationInput("   ", "officer@example.com", "Password123", "Password123")
        assertEquals("Please enter your full name.", result)
    }

    @Test
    fun testValidateRegistrationInput_invalidEmail() {
        val result1 = validateRegistrationInput("John Doe", "", "Password123", "Password123")
        assertEquals("Please enter a valid email address.", result1)

        val result2 = validateRegistrationInput("John Doe", "invalid-email", "Password123", "Password123")
        assertEquals("Please enter a valid email address.", result2)
    }

    @Test
    fun testValidateRegistrationInput_shortPassword() {
        val result = validateRegistrationInput("John Doe", "officer@example.com", "short", "short")
        assertEquals("Password must be at least 8 characters.", result)
    }

    @Test
    fun testValidateRegistrationInput_passwordMismatch() {
        val result = validateRegistrationInput("John Doe", "officer@example.com", "Password123", "Password456")
        assertEquals("Passwords do not match.", result)
    }

    @Test
    fun testValidateRegistrationInput_validInput() {
        val result = validateRegistrationInput("John Doe", "officer@example.com", "Password123", "Password123")
        assertNull(result)
    }

    @Test
    fun testOfficerStatusFromString_exactValues() {
        assertEquals(OfficerStatus.PENDING, OfficerStatus.fromString("pending"))
        assertEquals(OfficerStatus.APPROVED, OfficerStatus.fromString("approved"))
        assertEquals(OfficerStatus.DISABLED, OfficerStatus.fromString("disabled"))
    }

    @Test
    fun testOfficerStatusFromString_failClosedOnWrongCaseNullAndUnknown() {
        assertEquals(OfficerStatus.PENDING, OfficerStatus.fromString("Approved"))
        assertEquals(OfficerStatus.PENDING, OfficerStatus.fromString("APPROVED"))
        assertEquals(OfficerStatus.PENDING, OfficerStatus.fromString("Disabled"))
        assertEquals(OfficerStatus.PENDING, OfficerStatus.fromString("Pending"))
        assertEquals(OfficerStatus.PENDING, OfficerStatus.fromString(null))
        assertEquals(OfficerStatus.PENDING, OfficerStatus.fromString("unknown_status"))
        assertEquals(OfficerStatus.PENDING, OfficerStatus.fromString("admin"))
    }

    @Test
    fun testOfficerRoleFromString_exactValues() {
        assertEquals(OfficerRole.OFFICER, OfficerRole.fromString("officer"))
        assertEquals(OfficerRole.ADMIN, OfficerRole.fromString("admin"))
    }

    @Test
    fun testOfficerRoleFromString_failClosedOnWrongCaseNullAndUnknown() {
        assertEquals(OfficerRole.OFFICER, OfficerRole.fromString("Officer"))
        assertEquals(OfficerRole.OFFICER, OfficerRole.fromString("ADMIN"))
        assertEquals(OfficerRole.OFFICER, OfficerRole.fromString("Admin"))
        assertEquals(OfficerRole.OFFICER, OfficerRole.fromString(null))
        assertEquals(OfficerRole.OFFICER, OfficerRole.fromString("unknown_role"))
        assertEquals(OfficerRole.OFFICER, OfficerRole.fromString("pending"))
    }
}
