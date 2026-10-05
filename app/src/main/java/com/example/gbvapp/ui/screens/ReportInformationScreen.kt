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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
fun ReportInformationScreen(
    onBack: () -> Unit = {},
    onProceedToDetails: () -> Unit = {},
    onQuickExit: () -> Unit = {}
) {
    var reportingFor by remember { mutableStateOf("Myself") }
    var reporterName by remember { mutableStateOf("") }
    var reporterPhone by remember { mutableStateOf("") }

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

        // Scrollable Content Form
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Step Progress Bar Indicator
            StepProgressHeader()

            Spacer(modifier = Modifier.height(16.dp))

            // Who are you reporting for?
            ReportingForSection(
                selectedOption = reportingFor,
                onOptionSelected = { reportingFor = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Reporter Contact Card
            ReporterContactSection(
                name = reporterName,
                onNameChange = { reporterName = it },
                phone = reporterPhone,
                onPhoneChange = { reporterPhone = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Action Button
            Button(
                onClick = onProceedToDetails,
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
                    Text(
                        text = "CONTINUE TO CATEGORY & EVIDENCE",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
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
                        text = "Your Details",
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
                    text = "STEP 1 OF 3",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BlueAccent,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Your Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Text(
                text = "Next: Category & Evidence",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { 0.33f },
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
private fun ReportingForSection(
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Who are you reporting for?",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "REQUIRED",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        ReportingForOptionCard(
            title = "Myself",
            subtitle = "Directly experiencing or experienced the incident.",
            icon = Icons.Outlined.Person,
            isSelected = selectedOption == "Myself",
            onClick = { onOptionSelected("Myself") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        ReportingForOptionCard(
            title = "Someone Else",
            subtitle = "Reporting on behalf of a survivor, family member, or friend.",
            icon = Icons.Outlined.Group,
            isSelected = selectedOption == "Someone Else",
            onClick = { onOptionSelected("Someone Else") }
        )
    }
}

@Composable
private fun ReportingForOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = if (isSelected) LightBlueTint else SurfaceCard,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) BlueAccent else SurfaceBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) NavyPrimary else AppBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else NavyPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) NavyPrimary else Color.Transparent)
                    .border(
                        width = if (isSelected) 0.dp else 1.5.dp,
                        color = if (isSelected) Color.Transparent else Color(0xFFCBD5E1),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ReporterContactSection(
    name: String,
    onNameChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceCard,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reporter Contact",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Surface(
                    color = Color(0xFFE0F2FE),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, CardBorderBlue)
                ) {
                    Text(
                        text = "Optional / Anonymous",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Leave blank to submit completely anonymously.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Name (Optional)",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                placeholder = { Text("e.g., Anonymous or Preferred Name", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Badge,
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
                    unfocusedContainerColor = AppBackground
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Phone Number (Optional)",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = onPhoneChange,
                placeholder = { Text("+265 999 000 000", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
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
                    unfocusedContainerColor = AppBackground
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Only authorized caseworkers can view contact channels.",
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReportInformationScreenPreview() {
    ReportInformationScreen()
}
