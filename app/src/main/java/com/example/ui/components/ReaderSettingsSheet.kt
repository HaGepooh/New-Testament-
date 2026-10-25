package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.ArtStyleMode
import com.example.model.BibleTranslation
import com.example.ui.theme.ReadingSanctuaryTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettingsSheet(
    sanctuaryTheme: ReadingSanctuaryTheme,
    artStyleMode: ArtStyleMode,
    translation: BibleTranslation,
    fontSizeSp: Int,
    lineSpacingMultiplier: Float,
    isFlowingCodexMode: Boolean,
    isRedLetterEnabled: Boolean,
    speechRate: Float,
    onSanctuaryThemeChange: (ReadingSanctuaryTheme) -> Unit,
    onArtStyleModeChange: (ArtStyleMode) -> Unit,
    onTranslationChange: (BibleTranslation) -> Unit,
    onFontSizeChange: (Int) -> Unit,
    onLineSpacingChange: (Float) -> Unit,
    onFlowingCodexModeChange: (Boolean) -> Unit,
    onRedLetterChange: (Boolean) -> Unit,
    onSpeechRateChange: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Sanctuary & Illumination Settings",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            // 1. Reading Sanctuary Theme Cards
            Text(
                text = "Reading Sanctuary Atmosphere",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ReadingSanctuaryTheme.entries.forEach { themeOption ->
                    val selected = themeOption == sanctuaryTheme
                    val bgColor = when (themeOption) {
                        ReadingSanctuaryTheme.PARCHMENT -> Color(0xFFFBF7EE)
                        ReadingSanctuaryTheme.NOCTURNE -> Color(0xFF141122)
                        ReadingSanctuaryTheme.SEPIA -> Color(0xFFF0E2C8)
                    }
                    val txtColor = when (themeOption) {
                        ReadingSanctuaryTheme.NOCTURNE -> Color(0xFFF4EFE6)
                        else -> Color(0xFF211A14)
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = bgColor,
                        border = BorderStroke(
                            width = if (selected) 2.5.dp else 1.dp,
                            color = if (selected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSanctuaryThemeChange(themeOption) }
                            .testTag("theme_card_${themeOption.name.lowercase()}")
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Aa",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = txtColor,
                                    fontWeight = FontWeight.Bold
                                )
                                if (selected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Text(
                                text = themeOption.label,
                                style = MaterialTheme.typography.labelMedium,
                                color = txtColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // 2. Chapter Art Illustration Style
            Text(
                text = "Chapter Artwork Rendering Style",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ArtStyleMode.entries.forEach { mode ->
                    FilterChip(
                        selected = mode == artStyleMode,
                        onClick = { onArtStyleModeChange(mode) },
                        label = { Text(mode.label) },
                        modifier = Modifier.testTag("settings_art_mode_${mode.name.lowercase()}")
                    )
                }
            }

            // 3. Scripture Translation
            Text(
                text = "Scripture Translation",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BibleTranslation.entries.forEach { trans ->
                    FilterChip(
                        selected = trans == translation,
                        onClick = { onTranslationChange(trans) },
                        label = { Text("${trans.displayName} (${trans.badge})") },
                        modifier = Modifier.testTag("translation_chip_${trans.code}")
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            // 4. Scripture Font Size & Line Spacing
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatSize,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Scripture Font Size",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                Text(
                    text = "${fontSizeSp}sp",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = fontSizeSp.toFloat(),
                onValueChange = { onFontSizeChange(it.toInt()) },
                valueRange = 15f..26f,
                steps = 10,
                modifier = Modifier.testTag("font_size_slider")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Line Spacing",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "${"%.2f".format(lineSpacingMultiplier)}x",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = lineSpacingMultiplier,
                onValueChange = onLineSpacingChange,
                valueRange = 1.35f..1.95f,
                modifier = Modifier.testTag("line_spacing_slider")
            )

            // 5. Codex Layout & Red-Letter Edition Toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Red-Letter Words of Christ",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Illuminate words spoken by Jesus in radiant Pure White",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isRedLetterEnabled,
                    onCheckedChange = onRedLetterChange,
                    modifier = Modifier.testTag("red_letter_switch")
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Flowing Manuscript Paragraph Mode",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Read verses as continuous literary prose",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isFlowingCodexMode,
                    onCheckedChange = onFlowingCodexModeChange,
                    modifier = Modifier.testTag("flowing_codex_switch")
                )
            }

            // 6. Voice Narration Speed
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Audio Narration Pace",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(0.85f to "Reverent", 1.0f to "Natural", 1.2f to "Brisk").forEach { (rate, label) ->
                        FilterChip(
                            selected = kotlin.math.abs(speechRate - rate) < 0.05f,
                            onClick = { onSpeechRateChange(rate) },
                            label = { Text(label) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
