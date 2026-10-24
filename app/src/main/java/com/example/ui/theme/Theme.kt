package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class ReadingSanctuaryTheme(val label: String, val subtitle: String) {
    PARCHMENT("Sacred Parchment", "Warm ivory vellum & gold leaf"),
    NOCTURNE("Cathedral Nocturne", "Deep obsidian & stained glass"),
    SEPIA("Monastic Codex", "Aged parchment & bronze ink")
}

private val ParchmentColorScheme = lightColorScheme(
    primary = ByzantineGold,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF9E8C2),
    onPrimaryContainer = Color(0xFF3D2800),
    secondary = CanaRuby,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFAD9DF),
    onSecondaryContainer = Color(0xFF3E0511),
    tertiary = LapisSapphire,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD8E6FA),
    onTertiaryContainer = Color(0xFF072346),
    background = ParchmentBackground,
    onBackground = ParchmentInk,
    surface = ParchmentSurface,
    onSurface = ParchmentInk,
    surfaceVariant = ParchmentSurfaceVariant,
    onSurfaceVariant = ParchmentMutedInk,
    outline = Color(0xFFCBBBA0)
)

private val NocturneColorScheme = darkColorScheme(
    primary = NocturneGold,
    onPrimary = Color(0xFF281B00),
    primaryContainer = Color(0xFF473309),
    onPrimaryContainer = Color(0xFFFFE29E),
    secondary = NocturneRuby,
    onSecondary = Color(0xFF3D0614),
    secondaryContainer = Color(0xFF5C1526),
    onSecondaryContainer = Color(0xFFFFD9DF),
    tertiary = NocturneSapphire,
    onTertiary = Color(0xFF042748),
    tertiaryContainer = Color(0xFF123B66),
    onTertiaryContainer = Color(0xFFD6E7FF),
    background = NocturneBackground,
    onBackground = NocturneInk,
    surface = NocturneSurface,
    onSurface = NocturneInk,
    surfaceVariant = NocturneSurfaceVariant,
    onSurfaceVariant = NocturneMutedInk,
    outline = Color(0xFF3B3554)
)

private val SepiaColorScheme = lightColorScheme(
    primary = SepiaBronze,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDD6B4),
    onPrimaryContainer = Color(0xFF331B00),
    secondary = CanaRuby,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF0CBD1),
    onSecondaryContainer = Color(0xFF3B0611),
    tertiary = GalileeEmerald,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC9E5DC),
    onTertiaryContainer = Color(0xFF072E21),
    background = SepiaBackground,
    onBackground = SepiaInk,
    surface = SepiaSurface,
    onSurface = SepiaInk,
    surfaceVariant = SepiaSurfaceVariant,
    onSurfaceVariant = SepiaMutedInk,
    outline = Color(0xFFBFA989)
)

@Composable
fun IlluminatedNTTheme(
    sanctuaryTheme: ReadingSanctuaryTheme = ReadingSanctuaryTheme.NOCTURNE,
    content: @Composable () -> Unit
) {
    val colorScheme = when (sanctuaryTheme) {
        ReadingSanctuaryTheme.PARCHMENT -> ParchmentColorScheme
        ReadingSanctuaryTheme.NOCTURNE -> NocturneColorScheme
        ReadingSanctuaryTheme.SEPIA -> SepiaColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
