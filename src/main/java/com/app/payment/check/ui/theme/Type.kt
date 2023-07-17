package com.app.payment.check.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.app.payment.check.R

// Set of Material typography styles to start with
val proxima = FontFamily(
    Font(R.font.proxima_regular, weight = FontWeight.Normal),
    Font(R.font.proxima_medium, weight = FontWeight.Medium),
    Font(R.font.proxima_semibold, weight = FontWeight.SemiBold),
    Font(R.font.proxima_bold, weight = FontWeight.Bold)
)
val Typography = Typography(
    defaultFontFamily = proxima,
    body1 = TextStyle(
        fontFamily = proxima,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    h1 = TextStyle(
        fontFamily = proxima,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp

    )
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)