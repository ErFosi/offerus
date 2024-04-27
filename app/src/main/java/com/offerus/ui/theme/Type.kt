package com.offerus.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.offerus.R


val AppTypography = FontFamily(
    //maven pro font family
    Font(R.font.mavenproregular),
    Font(R.font.mavenprobold, FontWeight.Bold),
    Font(R.font.mavenprosemibold, FontWeight.SemiBold),
    Font(R.font.mavenproblack, FontWeight.Black),
    Font(R.font.mavenproextrabold, FontWeight.ExtraBold),
    Font(R.font.mavenprosemibold, FontWeight.SemiBold)
)


// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = AppTypography,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)



