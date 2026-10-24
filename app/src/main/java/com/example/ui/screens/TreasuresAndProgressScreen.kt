package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.ChapterArtRegistry
import com.example.data.NewTestamentBooks
import com.example.data.local.SavedVerseEntity
import com.example.model.ArtStyleMode
import com.example.model.HighlightColor
import com.example.model.TestamentCategory
import com.example.model.VerseItem
import com.example.ui.art.SacredArtCanvas

@Composable
fun TreasuresAndProgressScreen(
    savedVerses: List<SavedVerseEntity>,
    completedChapterKeys: Set<String>,
    artStyleMode: ArtStyleMode,
    onOpenChapter: (String, Int) -> Unit,
    onRemoveSavedVerse: (String, Int, Int) -> Unit,
    onSeedSampleHighlight: (VerseItem, HighlightColor, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedColorFilter by remember { mutableStateOf<HighlightColor?>(null) }

    val filteredVerses = remember(savedVerses, selectedColorFilter) {
        if (selectedColorFilter == null) {
            savedVerses
        } else {
            savedVerses.filter { it.highlightColorId == selectedColorFilter?.id }
        }
    }

    val totalCompleted = completedChapterKeys.size
    val totalChapters = NewTestamentBooks.totalNewTestamentChapters // 260

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 760.dp)
                .testTag("treasures_lazy_column"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Header
            item(key = "treasures_header") {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Sacred Treasures & Journey",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Your New Testament reading progress and jewel-highlighted verses",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 2. 260-Chapter New Testament Illumination Progress Card
            item(key = "progress_summary_card") {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
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
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "New Testament Illumination",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "$totalCompleted / $totalChapters Chapters",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = { (totalCompleted.toFloat() / totalChapters.toFloat()).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )

                        // Breakdown by Testament Section
                        TestamentCategory.entries.forEach { cat ->
                            val booksInCat = NewTestamentBooks.allBooks.filter { it.category == cat }
                            val catTotal = booksInCat.sumOf { it.chapterCount }
                            val catDone = booksInCat.sumOf { b ->
                                (1..b.chapterCount).count { ch ->
                                    completedChapterKeys.contains("${b.id}_$ch")
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = cat.title,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "$catDone / $catTotal",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(cat.accentHex)
                                )
                            }
                        }
                    }
                }
            }

            // 3. Jewel Color Filter Row for Saved Verses
            item(key = "color_filter_row") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Saved Verses & Meditations (${savedVerses.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = selectedColorFilter == null,
                                onClick = { selectedColorFilter = null },
                                label = { Text("All Colors") }
                            )
                        }
                        items(HighlightColor.entries) { colorOpt ->
                            FilterChip(
                                selected = selectedColorFilter == colorOpt,
                                onClick = { selectedColorFilter = colorOpt },
                                leadingIcon = {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(colorOpt.swatchColor)
                                    )
                                },
                                label = { Text(colorOpt.label) }
                            )
                        }
                    }
                }
            }

            // 4. Empty State or Saved Verse Cards
            if (filteredVerses.isEmpty()) {
                item(key = "empty_treasures_state") {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .testTag("empty_treasures_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(44.dp)
                            )
                            Text(
                                text = "Your Sacred Treasury Is Ready",
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Tap any verse while reading to highlight it in Byzantine Gold, Cana Ruby, Galilee Emerald, or Lapis Sapphire and record personal reflections.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = {
                                    onSeedSampleHighlight(
                                        VerseItem(
                                            bookId = "MAT",
                                            chapterNumber = 5,
                                            verseNumber = 8,
                                            text = "Blessed are the pure in heart, for they shall see God.",
                                            isWordsOfChrist = true
                                        ),
                                        HighlightColor.GOLD,
                                        "The sixth Beatitude on the Mount: purity of heart opens spiritual eyes to behold divine glory."
                                    )
                                },
                                modifier = Modifier.testTag("seed_sample_treasure_button")
                            ) {
                                Text("Save Beatitude Sample (Matthew 5:8)")
                            }
                        }
                    }
                }
            } else {
                items(
                    items = filteredVerses,
                    key = { it.verseKey }
                ) { saved ->
                    val colorEnum = HighlightColor.entries.firstOrNull { it.id == saved.highlightColorId }
                        ?: HighlightColor.GOLD
                    val chapterArt = remember(saved.bookId, saved.chapterNumber) {
                        ChapterArtRegistry.getChapterMetadata(saved.bookId, saved.chapterNumber)
                    }

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.5.dp, colorEnum.swatchColor.copy(alpha = 0.65f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenChapter(saved.bookId, saved.chapterNumber) }
                            .testTag("saved_verse_card_${saved.verseKey}")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Mini Chapter Art Preview
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                    ) {
                                        SacredArtCanvas(
                                            metadata = chapterArt,
                                            artStyleMode = artStyleMode,
                                            modifier = Modifier.fillMaxSize(),
                                            animateShimmer = false,
                                            showOrnamentalFrame = false
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = "${saved.bookName} ${saved.chapterNumber}:${saved.verseNumber}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = chapterArt.artTitle,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = colorEnum.swatchColor
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            onRemoveSavedVerse(saved.bookId, saved.chapterNumber, saved.verseNumber)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Remove saved verse",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Open chapter",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = colorEnum.tintBackground,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "“${saved.verseText}”",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontStyle = FontStyle.Italic,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }

                            if (saved.reflectionNote.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .height(24.dp)
                                            .background(colorEnum.swatchColor, RoundedCornerShape(2.dp))
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = saved.reflectionNote,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Creator Signature Card on Last Page ("This app made by HagezPooh")
            item(key = "hagezpooh_signature_footer") {
                com.example.ui.components.HagezPoohSignatureCard(
                    modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                )
            }
        }
    }
}
