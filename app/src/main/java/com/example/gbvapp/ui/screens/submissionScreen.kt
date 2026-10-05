package com.example.gbvapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gbvapp.ui.theme.AppBackground
import com.example.gbvapp.ui.theme.BlueAccent
import com.example.gbvapp.ui.theme.CardBorderBlue
import com.example.gbvapp.ui.theme.LightBlueTint
import com.example.gbvapp.ui.theme.NavyPrimary
import com.example.gbvapp.ui.theme.QuickExitBg
import com.example.gbvapp.ui.theme.QuickExitBorder
import com.example.gbvapp.ui.theme.QuickExitRed
import com.example.gbvapp.ui.theme.SurfaceBorder
import com.example.gbvapp.ui.theme.SurfaceCard
import com.example.gbvapp.ui.theme.TextPrimary
import com.example.gbvapp.ui.theme.TextSecondary

@Composable
fun SubmissionScreen(
    onBack: () -> Unit = {},
    onSubmitFinalReport: () -> Unit = {},
    onQuickExit: () -> Unit = {}
) {
    var incidentDate by remember { mutableStateOf("02 / 10 / 2026") }
    var incidentTime by remember { mutableStateOf("14:30 (Afternoon)") }
    var incidentLocation by remember { mutableStateOf("Zomba Central, Near District Hospital Market") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // Top Header Bar
        TopHeaderBar(
            onBack = onBack,
            onQuickExit = onQuickExit
        )

        // Scrollable Content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Step Progress Bar Indicator
            StepProgressHeader()

            Spacer(modifier = Modifier.height(12.dp))

            // Confidential Safe Intake Badge
            Surface(
                color = LightBlueTint,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, CardBorderBlue)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = NavyPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Confidential Safe Intake",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Incident Details",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Provide approximate timing and location details to help officers investigate and dispatch assistance.",
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date Input Field
            InputFieldSection(
                label = "Date of Incident",
                tag = "Required",
                value = incidentDate,
                onValueChange = { incidentDate = it },
                leadingIcon = Icons.Default.CalendarToday,
                trailingIcon = Icons.Default.Edit,
                subtext = "Select the date when the incident occurred."
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Time Input Field
            InputFieldSection(
                label = "Time of Incident",
                tag = "Approximate",
                value = incidentTime,
                onValueChange = { incidentTime = it },
                leadingIcon = Icons.Default.AccessTime,
                trailingIcon = Icons.Default.Edit,
                subtext = "Estimate within a few hours if exact time is uncertain."
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Location Input Field
            InputFieldSection(
                label = "Location of Incident",
                tag = "Secure Map Reference",
                value = incidentLocation,
                onValueChange = { incidentLocation = it },
                leadingIcon = Icons.Default.LocationOn,
                trailingIcon = Icons.Default.MyLocation,
                subtext = "Include landmarks, town, or neighborhood if known."
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Intake Summary Card
            IntakeSummaryCard()

            Spacer(modifier = Modifier.height(16.dp))

            // End-to-End Encrypted Handover Card
            EncryptedHandoverCard()

            Spacer(modifier = Modifier.height(20.dp))

            // Submit Final Report Button
            Button(
                onClick = onSubmitFinalReport,
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
                        text = "SUBMIT REPORT",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Back to Edit Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onBack() }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Back to Edit",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TopHeaderBar(
    onBack: () -> Unit,
    onQuickExit: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceCard,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Surface(
                    modifier = Modifier.size(34.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceCard,
                    border = BorderStroke(1.dp, SurfaceBorder)
                ) {
                    Box(
                        modifier = Modifier.padding(5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        SafeVoiceLogoIcon(modifier = Modifier.fillMaxSize())
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "SAFE VOICE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Incident Details",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = onQuickExit,
                    colors = ButtonDefaults.buttonColors(containerColor = QuickExitBg),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, QuickExitBorder),
                    elevation = null,
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = QuickExitRed,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "QUICK EXIT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuickExitRed
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(NavyPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StepProgressHeader() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "STEP 2 OF 2",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BlueAccent,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Date, Time & Location",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Text(
                text = "100% Ready",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { 1.0f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = NavyPrimary,
            trackColor = SurfaceBorder
        )
    }
}

@Composable
private fun InputFieldSection(
    label: String,
    tag: String,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: ImageVector,
    trailingIcon: ImageVector,
    subtext: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = tag,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BlueAccent,
                unfocusedBorderColor = SurfaceBorder,
                focusedContainerColor = LightBlueTint,
                unfocusedContainerColor = SurfaceCard
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = subtext,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun IntakeSummaryCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = LightBlueTint,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, CardBorderBlue)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(NavyPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Intake Summary",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Surface(
                    color = Color(0xFFE0F2FE),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Encrypted Draft",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 1: Category
            SummaryRow(
                label = "Category",
                value = "Sexual Harassment"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Row 2: Reporting for
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reporting for",
                    fontSize = 13.sp,
                    color = TextSecondary
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Myself",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = Color(0xFFE0F2FE),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Anonymous",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 3: Evidence
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Evidence",
                    fontSize = 13.sp,
                    color = TextSecondary
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = NavyPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "1 photo attached",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = TextSecondary
        )

        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}

@Composable
private fun EncryptedHandoverCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceCard,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SurfaceBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(LightBlueTint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "End-to-End Encrypted Handover",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Your submission will be immediately encrypted and routed to the central response coordinator. You will receive a unique tracking reference code.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SubmissionScreenPreview() {
    SubmissionScreen()
}
