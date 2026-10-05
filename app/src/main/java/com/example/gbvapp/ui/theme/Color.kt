package com.example.gbvapp.ui.theme

import androidx.compose.ui.graphics.Color

// Named Central Theme Tokens
val NavyPrimary = Color(0xFF15324F)       // Deep Navy for headers, CTAs, icons
val BlueAccent = Color(0xFF4F8FC0)        // Accent blue for active states & indicators
val LightBlueTint = Color(0xFFF0F7FF)     // Soft light blue background tint for badges & callouts
val CardBorderBlue = Color(0xFFBAE6FD)    // Soft border tint for blue cards

// Soft Off-White Background (Gentle for stressed users / low light)
val AppBackground = Color(0xFFF3F6F9)     // Soft pale blue-grey screen background
val SurfaceCard = Color(0xFFFFFFFF)       // Clean white for foreground card surfaces
val SurfaceBorder = Color(0xFFE2E8F0)     // Border for cards

// Text Tokens (Audited to meet WCAG AA > 4.5:1 Contrast Ratio)
val TextPrimary = Color(0xFF0F2537)       // High contrast dark navy text (> 12:1 ratio)
val TextSecondary = Color(0xFF4A5568)     // Soft warm neutral grey text (> 5.5:1 ratio against #F3F6F9 and #FFFFFF)

// Quick Exit Tokens (The ONLY place red is used in the app)
val QuickExitRed = Color(0xFFDC2626)      // Vivid red for Quick Exit
val QuickExitBg = Color(0xFFFEF2F2)       // Soft pink/red background
val QuickExitBorder = Color(0xFFFCA5A5)   // Light red border
