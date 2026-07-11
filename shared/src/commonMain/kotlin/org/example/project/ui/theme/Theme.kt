package org.example.project.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryPurple,
    onPrimary = Color.White,
    primaryContainer = LightSurfaceVariant,
    onPrimaryContainer = PrimaryPurpleDark,
    secondary = AccentTeal,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0FEFF),
    onSecondaryContainer = Color(0xFF004A4B),
    tertiary = AccentPink,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFD6E7),
    onTertiaryContainer = Color(0xFF3E0021),
    error = ExpenseRed,
    onError = Color.White,
    errorContainer = ExpenseRedLight,
    onErrorContainer = Color(0xFF690005),
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF4A4A6A),
    outline = LightOutline,
    outlineVariant = Color(0xFFCAC4D0)
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryPurpleLight,
    onPrimary = Color(0xFF1A0080),
    primaryContainer = PrimaryPurpleDark,
    onPrimaryContainer = Color(0xFFE6DEFF),
    secondary = AccentTeal,
    onSecondary = Color(0xFF003738),
    secondaryContainer = Color(0xFF004F50),
    onSecondaryContainer = Color(0xFF9EF2F2),
    tertiary = AccentPink,
    onTertiary = Color(0xFF5C1040),
    tertiaryContainer = Color(0xFF7A2857),
    onTertiaryContainer = Color(0xFFFFD8E8),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFCBC8DB),
    outline = DarkOutline,
    outlineVariant = Color(0xFF49454F)
)

data class ExtendedColors(
    val income: Color,
    val incomeLight: Color,
    val expense: Color,
    val expenseLight: Color,
    val savings: Color,
    val savingsLight: Color,
    val progressGreen: Color,
    val progressYellow: Color,
    val progressOrange: Color,
    val progressRed: Color,
    val glassWhite: Color,
    val glassBorder: Color,
    val cardBackground: Color
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        income = IncomeGreen,
        incomeLight = IncomeGreenLight,
        expense = ExpenseRed,
        expenseLight = ExpenseRedLight,
        savings = SavingsBlue,
        savingsLight = SavingsBlueLight,
        progressGreen = ProgressGreen,
        progressYellow = ProgressYellow,
        progressOrange = ProgressOrange,
        progressRed = ProgressRed,
        glassWhite = GlassWhite,
        glassBorder = GlassBorder,
        cardBackground = LightSurface
    )
}

@Composable
fun TrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val extendedColors = if (darkTheme) {
        ExtendedColors(
            income = IncomeGreen,
            incomeLight = Color(0xFF003D2F),
            expense = Color(0xFFFF8E8E),
            expenseLight = Color(0xFF3D1A1A),
            savings = SavingsBlue,
            savingsLight = Color(0xFF1A2E4D),
            progressGreen = ProgressGreen,
            progressYellow = ProgressYellow,
            progressOrange = ProgressOrange,
            progressRed = ProgressRed,
            glassWhite = GlassBlack,
            glassBorder = Color(0x22FFFFFF),
            cardBackground = DarkSurface
        )
    } else {
        ExtendedColors(
            income = IncomeGreen,
            incomeLight = IncomeGreenLight,
            expense = ExpenseRed,
            expenseLight = ExpenseRedLight,
            savings = SavingsBlue,
            savingsLight = SavingsBlueLight,
            progressGreen = ProgressGreen,
            progressYellow = ProgressYellow,
            progressOrange = ProgressOrange,
            progressRed = ProgressRed,
            glassWhite = GlassWhite,
            glassBorder = GlassBorder,
            cardBackground = LightSurface
        )
    }

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}

object TrackerTheme {
    val extendedColors: ExtendedColors
        @Composable get() = LocalExtendedColors.current
}
