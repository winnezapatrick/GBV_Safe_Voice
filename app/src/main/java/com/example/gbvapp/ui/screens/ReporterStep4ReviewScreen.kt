package com.example.gbvapp.ui.screens

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gbvapp.data.firebase.ReportRepository
import com.example.gbvapp.data.firebase.SubmitResult
import com.example.gbvapp.ui.components.ReporterHeader
import com.example.gbvapp.ui.theme.AppBackground
import com.example.gbvapp.ui.theme.NavyPrimary
import com.example.gbvapp.ui.theme.SurfaceBorder
import com.example.gbvapp.ui.theme.SurfaceCard
import com.example.gbvapp.ui.theme.TextPrimary
import com.example.gbvapp.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun ReporterStep4ReviewScreen(
    reportingFor: String,
    category: String,
    description: String,
    evidenceUris: List<Uri>,
    incidentDate: String,
    incidentTime: String,
    location: String,
    onBack: () -> Unit,
    onSuccess: (reference: String) -> Unit,
    onQuickExit: () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showEvidenceFailedDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    fun performSubmission(skipFailedEvidence: Boolean = false) {
        isLoading = true
        errorMessage = null
        showEvidenceFailedDialog = false

        scope.launch {
            try {
                val result = ReportRepository.submitReport(
                    context = context,
                    category = category,
                    description = description,
                    incidentDate = incidentDate,
                    incidentTime = incidentTime,
                    location = location,
                    reportingFor = reportingFor,
                    evidenceUris = evidenceUris,
                    skipFailedEvidence = skipFailedEvidence
                )

                when (result) {
                    is SubmitResult.Success -> {
                        onSuccess(result.reference)
                    }
                    is SubmitResult.NoConnection -> {
                        errorMessage = "No internet connection. Your report has not been sent."
                    }
                    is SubmitResult.EvidenceFailed -> {
                        showEvidenceFailedDialog = true
                    }
                    is SubmitResult.Failure -> {
                        errorMessage = result.message
                    }
                }
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        ReporterHeader(
            stepNumber = 4,
            title = "Review & Submit",
            onBack = onBack,
            onQuickExit = onQuickExit
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Please review your report details before submitting.",
                fontSize = 14.sp,
                color = TextSecondary
            )

            // Read-only Summary Card
            Surface(
                color = SurfaceCard,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, SurfaceBorder)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SummaryRow(label = "Reporting For", value = reportingFor.ifBlank { "Myself" })
                    SummaryRow(label = "Category", value = category)
                    SummaryRow(label = "Description", value = description)
                    SummaryRow(label = "Incident Date", value = incidentDate.ifBlank { "Not specified" })
                    SummaryRow(label = "Incident Time", value = incidentTime.ifBlank { "Not specified" })
                    SummaryRow(label = "Location", value = location.ifBlank { "Not specified" })
                    SummaryRow(label = "Attached Photos", value = if (evidenceUris.isNotEmpty()) "${evidenceUris.size} photo(s)" else "None")
                }
            }

            // Anonymity Warning Box
            Surface(
                color = Color(0xFFFEF3C7),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFFCD34D))
            ) {
                Text(
                    text = "Absolute anonymity cannot be guaranteed if your description or photos contain identifying details.",
                    fontSize = 13.sp,
                    color = Color(0xFF92400E),
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(14.dp)
                )
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color(0xFFDC2626),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { performSubmission() },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isLoading) "Submitting..." else "Submit report",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }

    if (showEvidenceFailedDialog) {
        AlertDialog(
            onDismissRequest = { showEvidenceFailedDialog = false },
            title = {
                Text(text = "Some photos could not be uploaded", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Text(text = "One or more attached photos failed to upload. Would you like to try uploading again, or submit the report without photos?", fontSize = 14.sp)
            },
            confirmButton = {
                Button(
                    onClick = { performSubmission(skipFailedEvidence = false) },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text("Try again", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { performSubmission(skipFailedEvidence = true) }
                ) {
                    Text("Submit without photos", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}
