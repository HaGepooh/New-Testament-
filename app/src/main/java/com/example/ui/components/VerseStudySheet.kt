package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.ChapterArtRegistry
import com.example.data.local.SavedVerseEntity
import com.example.model.HighlightColor
import com.example.model.VerseItem
import com.example.ui.art.ThreeDBibleArtEngine
import com.example.ui.art.ThreeDVerseIllustration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerseStudySheet(
    bookName: String,
    chapterArtTitle: String,
    verse: VerseItem,
    existingSavedVerse: SavedVerseEntity?,
    onSaveHighlight: (HighlightColor, String) -> Unit,
    onRemoveHighlight: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val verseVariations = remember(verse.bookId, verse.chapterNumber, verse.verseNumber, verse.text) {
        val metadata = ChapterArtRegistry.getChapterMetadata(verse.bookId, verse.chapterNumber)
        ThreeDBibleArtEngine.getVerse3DPictureVariations(verse, metadata)
    }
    var selectedVarIndex by remember(verse) { mutableStateOf(0) }
    val verse3DSpec = verseVariations.getOrElse(selectedVarIndex) { verseVariations.first() }

    val initialColor = existingSavedVerse?.highlightColorId?.let { id ->
        HighlightColor.entries.firstOrNull { it.id == id }
    } ?: HighlightColor.GOLD

    var selectedColor by remember(verse) { mutableStateOf(initialColor) }
    var reflectionNote by remember(verse) { mutableStateOf(existingSavedVerse?.reflectionNote.orEmpty()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$bookName ${verse.chapterNumber}:${verse.verseNumber}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = chapterArtTitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                OutlinedButton(
                    onClick = {
                        val shareText = "“${verse.text}” — $bookName ${verse.chapterNumber}:${verse.verseNumber} ($chapterArtTitle • The Radiant New Testament)"
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Verse"))
                    },
                    modifier = Modifier.testTag("share_verse_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share verse",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("Share")
                }
            }

            // 3D Volumetric Verse Illustration Showcase Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .graphicsLayer {
                        shadowElevation = 14f
                        rotationX = 3f
                    }
                    .clip(RoundedCornerShape(18.dp))
                    .border(
                        width = 2.dp,
                        color = verse3DSpec.accentGold,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .testTag("study_sheet_3d_verse_art")
            ) {
                ThreeDVerseIllustration(
                    spec = verse3DSpec,
                    modifier = Modifier.fillMaxSize()
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.88f))
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = verse3DSpec.accentGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${verse3DSpec.variationLabel.uppercase()} • ${verse3DSpec.customSceneTitle.uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = verse3DSpec.accentGold,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = if (verse3DSpec.secondaryCharacter != null) {
                            "Characters: ${verse3DSpec.primaryCharacter.name} & ${verse3DSpec.secondaryCharacter.name} (${verse3DSpec.environmentStage.label})"
                        } else {
                            "Character: ${verse3DSpec.primaryCharacter.name} (${verse3DSpec.environmentStage.label})"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.92f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // 3-Picture 3D Character Variation Selector inside Study Sheet
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                verseVariations.forEachIndexed { idx, specVar ->
                    val isSelected = idx == selectedVarIndex
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) specVar.accentGold else specVar.accentGold.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedVarIndex = idx }
                            .testTag("study_sheet_var_$idx")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                ThreeDVerseIllustration(
                                    spec = specVar,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${specVar.primaryCharacter.name.substringBefore(" ")} & ${specVar.secondaryCharacter?.name?.substringBefore(" ") ?: ""}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF141122),
                border = BorderStroke(1.dp, selectedColor.swatchColor.copy(alpha = 0.65f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "“${verse.text}”",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Text(
                text = "Sacred Jewel Highlight Color",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HighlightColor.entries.forEach { colorOption ->
                    val isSelected = colorOption == selectedColor
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedColor = colorOption }
                            .testTag("highlight_swatch_${colorOption.id.lowercase()}")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(colorOption.swatchColor)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    shape = CircleShape
                                )
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected color",
                                    tint = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = colorOption.label.substringAfter(" "),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            SanctuaryNoteField(
                value = reflectionNote,
                onValueChange = { reflectionNote = it },
                labelText = "Personal Meditation or Study Note (optional)",
                placeholderText = "Write a reflection on this verse…",
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("verse_reflection_input"),
                minLines = 2,
                maxLines = 4
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (existingSavedVerse != null) {
                    OutlinedButton(
                        onClick = {
                            onRemoveHighlight()
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("remove_highlight_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Remove")
                    }
                }

                Button(
                    onClick = {
                        onSaveHighlight(selectedColor, reflectionNote)
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1.4f)
                        .testTag("save_verse_highlight_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkAdded,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Save to Treasures")
                }
            }
        }
    }
}
