package com.example.gbvapp.data.firebase

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

enum class OfficerStatus(val value: String) {
    PENDING("pending"),
    APPROVED("approved"),
    DISABLED("disabled");

    companion object {
        fun fromString(value: String?): OfficerStatus {
            return entries.find { it.value == value } ?: PENDING
        }
    }
}

enum class OfficerRole(val value: String) {
    OFFICER("officer"),
    ADMIN("admin");

    companion object {
        fun fromString(value: String?): OfficerRole {
            return entries.find { it.value == value } ?: OFFICER
        }
    }
}

data class OfficerProfile(
    val uid: String,
    val name: String,
    val email: String,
    val status: OfficerStatus,
    val role: OfficerRole,
    val department: String = "Student Support & Safety",
    val createdAt: Any? = null
)

sealed class LoginResult {
    data class Success(val profile: OfficerProfile) : LoginResult()
    object InvalidCredentials : LoginResult()
    object PendingApproval : LoginResult()
    object AccountDisabled : LoginResult()
    object AccessDenied : LoginResult()
    data class Failure(val message: String) : LoginResult()
}

sealed class RegisterResult {
    object Submitted : RegisterResult()
    object EmailAlreadyUsed : RegisterResult()
    object WeakPassword : RegisterResult()
    data class Failure(val message: String) : RegisterResult()
}

object CurrentOfficer {
    var profile: OfficerProfile? = null
}

fun validateRegistrationInput(name: String, email: String, pass: String, confirmPass: String): String? {
    if (name.isBlank()) return "Please enter your full name."
    val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
    if (email.isBlank() || !email.trim().matches(emailRegex)) return "Please enter a valid email address."
    if (pass.length < 8) return "Password must be at least 8 characters."
    if (pass != confirmPass) return "Passwords do not match."
    return null
}

fun mapStatus(statusStr: String?): OfficerStatus = OfficerStatus.fromString(statusStr)
fun mapRole(roleStr: String?): OfficerRole = OfficerRole.fromString(roleStr)

object AuthRepository {

    suspend fun loginOfficer(email: String, pass: String): LoginResult {
        val cleanEmail = email.trim().lowercase()
        return try {
            val authResult: AuthResult = FirebaseManager.auth
                .signInWithEmailAndPassword(cleanEmail, pass)
                .await()
            val firebaseUser = authResult.user ?: return LoginResult.InvalidCredentials
            val uid = firebaseUser.uid

            val firestore = FirebaseManager.firestore
            val docRef = firestore.collection("officers").document(uid)
            val docSnapshot = docRef.get().await()

            if (!docSnapshot.exists()) {
                logout()
                return LoginResult.AccessDenied
            }

            val data = docSnapshot.data
            val name = data?.get("name") as? String ?: ""
            val statusStr = data?.get("status") as? String
            val roleStr = data?.get("role") as? String
            val profileEmail = data?.get("email") as? String ?: cleanEmail

            val status = OfficerStatus.fromString(statusStr)
            val role = OfficerRole.fromString(roleStr)
            val profile = OfficerProfile(
                uid = docSnapshot.id,
                name = name,
                email = profileEmail,
                status = status,
                role = role
            )

            when (status) {
                OfficerStatus.APPROVED -> {
                    CurrentOfficer.profile = profile
                    LoginResult.Success(profile)
                }
                OfficerStatus.PENDING -> {
                    logout()
                    LoginResult.PendingApproval
                }
                OfficerStatus.DISABLED -> {
                    logout()
                    LoginResult.AccountDisabled
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            when (e) {
                is FirebaseAuthInvalidUserException,
                is FirebaseAuthInvalidCredentialsException -> {
                    LoginResult.InvalidCredentials
                }
                is FirebaseNetworkException -> {
                    LoginResult.Failure("No internet connection. Please try again.")
                }
                else -> {
                    LoginResult.Failure("Something went wrong. Please try again.")
                }
            }
        }
    }

    suspend fun registerOfficer(name: String, email: String, pass: String): RegisterResult {
        val cleanEmail = email.trim().lowercase()
        return try {
            val authResult = FirebaseManager.auth
                .createUserWithEmailAndPassword(cleanEmail, pass)
                .await()
            val user = authResult.user
                ?: return RegisterResult.Failure("Failed to create user account.")
            val uid = user.uid

            val officerData = hashMapOf(
                "name" to name.trim(),
                "email" to cleanEmail,
                "status" to OfficerStatus.PENDING.value,
                "role" to OfficerRole.OFFICER.value,
                "createdAt" to FieldValue.serverTimestamp()
            )

            try {
                FirebaseManager.firestore
                    .collection("officers")
                    .document(uid)
                    .set(officerData)
                    .await()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                try { user.delete().await() } catch (_: Exception) {}
                return RegisterResult.Failure("Failed to save officer profile. Please try again.")
            }

            logout()
            RegisterResult.Submitted
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            when (e) {
                is FirebaseAuthUserCollisionException -> RegisterResult.EmailAlreadyUsed
                is FirebaseAuthWeakPasswordException -> RegisterResult.WeakPassword
                is FirebaseNetworkException -> RegisterResult.Failure("No internet connection. Please try again.")
                else -> RegisterResult.Failure("Something went wrong. Please try again.")
            }
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Boolean {
        return try {
            FirebaseManager.auth.sendPasswordResetEmail(email.trim().lowercase()).await()
            true
        } catch (_: Exception) {
            false
        }
    }

    fun logout() {
        try {
            FirebaseManager.auth.signOut()
        } catch (_: Exception) {}
        CurrentOfficer.profile = null
    }

    fun getCurrentOfficerId(): String? {
        return try {
            FirebaseManager.auth.currentUser?.uid
        } catch (_: Exception) {
            null
        }
    }
}
