package com.example.gbvapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Https
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
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
fun WelcomeScreen(
    onProceedToReport: () -> Unit = {},
    onQuickExit: () -> Unit = {},
    onAdminTabSelected: () -> Unit = {},
    onOfficerTabSelected: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var activeInfoPillar by remember { mutableStateOf<Pair<String, String>?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // Top Header Bar
        TopHeaderBar(
            onQuickExit = onQuickExit
        )

        // Main Scrollable Area
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Notice Banner
            DiscreetBanner()

            Spacer(modifier = Modifier.height(8.dp))

            // Main Hero Badge & Title Section
            HeroSection()

            Spacer(modifier = Modifier.height(8.dp))

            // 100% Anonymous Banner Card
            AnonymousBannerCard()

            Spacer(modifier = Modifier.height(8.dp))

            // Protection Items Section
            ProtectionPillarsSection(
                onPillarClicked = { title, description ->
                    activeInfoPillar = title to description
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action Button & Footer Link
            ActionButtonSection(
                onProceedToReport = onProceedToReport
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Bottom Navigation Bar
        BottomNavigationBar(
            selectedTab = selectedTab,
            onTabSelected = { tab ->
                selectedTab = tab
                when (tab) {
                    1 -> onAdminTabSelected()
                    2 -> onOfficerTabSelected()
                }
            }
        )
    }

    // Modal Dialog for Pillar Details
    if (activeInfoPillar != null) {
        AlertDialog(
            onDismissRequest = { activeInfoPillar = null },
            title = {
                Text(
                    text = activeInfoPillar!!.first,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = activeInfoPillar!!.second,
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { activeInfoPillar = null }) {
                    Text(
                        text = "Got it",
                        color = NavyPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            containerColor = SurfaceCard,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun TopHeaderBar(
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
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // App Brand Name & Logo
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceCard,
                    border = BorderStroke(1.dp, SurfaceBorder)
                ) {
                    Box(
                        modifier = Modifier.padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        SafeVoiceLogoIcon(modifier = Modifier.fillMaxSize())
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "SAFE VOICE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Report (Public)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            // Right Actions: Quick Exit & Profile
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = onQuickExit,
                    colors = ButtonDefaults.buttonColors(containerColor = QuickExitBg),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, QuickExitBorder),
                    elevation = null,
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = QuickExitRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "QUICK EXIT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuickExitRed
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(NavyPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DiscreetBanner() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = LightBlueTint,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, CardBorderBlue)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(SurfaceCard),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(14.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = buildAnnotatedString {
                    append("Tap ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Quick Exit")
                    }
                    append(" anytime to leave instantly")
                },
                fontSize = 12.sp,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun HeroSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Large Safe Voice Logo Container
        Surface(
            modifier = Modifier.size(92.dp),
            shape = RoundedCornerShape(20.dp),
            color = SurfaceCard,
            border = BorderStroke(1.dp, SurfaceBorder),
            shadowElevation = 2.dp
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(14.dp)
            ) {
                SafeVoiceLogoIcon(modifier = Modifier.fillMaxSize())
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Safe Voice Pill Badge
        Surface(
            color = Color(0xFFE0F2FE),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, CardBorderBlue)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = BlueAccent,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SAFE VOICE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Safe Voice Reporting",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "A confidential, secure space to report GBV safely and get support.",
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 28.dp)
        )
    }
}

@Composable
private fun AnonymousBannerCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = LightBlueTint,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorderBlue)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    color = Color(0x224F8FC0),
                    radius = size.height * 1.2f,
                    center = Offset(size.width * 0.95f, size.height * 0.5f)
                )
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "100% Anonymous by Default",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(BlueAccent)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "No login or tracking required unless you choose to share.",
                    fontSize = 13.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun ProtectionPillarsSection(
    onPillarClicked: (title: String, description: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        ProtectionPillarCard(
            icon = Icons.Default.Lock,
            title = "100% Confidential",
            onClick = {
                onPillarClicked(
                    "100% Confidential",
                    "Isolated storage with zero telemetry ensures your report remains strictly confidential."
                )
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        ProtectionPillarCard(
            icon = Icons.Default.Https,
            title = "Encrypted Evidence",
            onClick = {
                onPillarClicked(
                    "Encrypted Evidence",
                    "Client-side cryptographic protection keeps all uploaded media and files fully encrypted."
                )
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        ProtectionPillarCard(
            icon = Icons.Default.PowerSettingsNew,
            title = "Immediate Action & Tracking",
            onClick = {
                onPillarClicked(
                    "Immediate Action & Tracking",
                    "A unique secure token is generated so you can track case updates anonymously."
                )
            }
        )
    }
}

@Composable
private fun ProtectionPillarCard(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = SurfaceCard,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SurfaceBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Verified Protection",
                tint = BlueAccent,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ActionButtonSection(
    onProceedToReport: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onProceedToReport,
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
                    text = "PROCEED TO REPORT",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = Color.White
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

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Authorized Personnel? ",
                fontSize = 12.sp,
                color = TextSecondary
            )
            Text(
                text = "Switch to Admin or Officer tab below.",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceCard,
        border = BorderStroke(1.dp, SurfaceBorder)
    ) {
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxWidth()
                .height(64.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.Shield,
                label = "Report",
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) }
            )

            BottomNavItem(
                icon = Icons.Default.GridView,
                label = "Admin",
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) }
            )

            BottomNavItem(
                icon = Icons.Default.Work,
                label = "Officer",
                isSelected = selectedTab == 2,
                onClick = { onTabSelected(2) }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val contentColor = if (isSelected) NavyPrimary else Color(0xFF94A3B8)

    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor
        )
    }
}

@Composable
fun SafeVoiceLogoIcon(
    modifier: Modifier = Modifier,
    shieldColor: Color = NavyPrimary,
    waveColor: Color = BlueAccent
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Outer Shield Path
        val shieldPath = Path().apply {
            moveTo(w * 0.5f, h * 0.05f)
            cubicTo(w * 0.8f, h * 0.05f, w * 0.95f, h * 0.15f, w * 0.95f, h * 0.35f)
            cubicTo(w * 0.95f, h * 0.65f, w * 0.65f, h * 0.88f, w * 0.5f, h * 0.95f)
            cubicTo(w * 0.35f, h * 0.88f, w * 0.05f, h * 0.65f, w * 0.05f, h * 0.35f)
            cubicTo(w * 0.05f, h * 0.15f, w * 0.2f, h * 0.05f, w * 0.5f, h * 0.05f)
            close()
        }

        // Draw Shield Outline
        drawPath(
            path = shieldPath,
            color = shieldColor,
            style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Draw Sound-Wave / Voice Mark inside shield (5 vertical bars)
        val barWidth = w * 0.06f
        val centerY = h * 0.45f

        val barHeights = listOf(
            h * 0.16f,
            h * 0.28f,
            h * 0.38f,
            h * 0.28f,
            h * 0.16f
        )

        val spacing = w * 0.11f
        val startX = w * 0.5f - (spacing * 2)

        barHeights.forEachIndexed { index, barHeight ->
            val x = startX + (index * spacing)
            val topY = centerY - (barHeight / 2f)
            val bottomY = centerY + (barHeight / 2f)

            drawLine(
                color = waveColor,
                start = Offset(x, topY),
                end = Offset(x, bottomY),
                strokeWidth = barWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen()
}
