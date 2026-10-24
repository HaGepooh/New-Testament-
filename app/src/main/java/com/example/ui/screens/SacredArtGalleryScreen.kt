package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.BiblicalCharacter
import com.example.data.BiblicalCharactersCatalog
import com.example.model.ArtStyleMode
import com.example.model.ChapterArtMetadata
import com.example.ui.art.CharacterPortraitCanvas
import com.example.ui.art.SacredArtCanvas
import com.example.ui.components.SanctuarySearchField

private val galleryThemeFilters = listOf(
    "All Themes",
    "Psalms & Proverbs",
    "Incarnation & Light",
    "Teachings & Parables",
    "Miracles & Wonders",
    "Passion & Glory",
    "Apostolic Mission",
    "Wisdom & Epistles",
    "Apocalyptic Vision"
)

private enum class GallerySectionMode(val label: String) {
    CHARACTERS_AND_DISCIPLES("Biblical Figures (34 Portraits)"),
    CHAPTER_SCENES("441 Chapter Illustrations")
}

@Composable
fun SacredArtGalleryScreen(
    allChapterArtworks: List<ChapterArtMetadata>,
    selectedThemeFilter: String,
    searchQuery: String,
    artStyleMode: ArtStyleMode,
    onThemeFilterSelected: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onArtStyleModeChange: (ArtStyleMode) -> Unit,
    onInspectArt: (ChapterArtMetadata) -> Unit,
    onInspectCharacter: (BiblicalCharacter) -> Unit,
    onOpenChapterInReader: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var gallerySection by remember { mutableStateOf(GallerySectionMode.CHARACTERS_AND_DISCIPLES) }

    val filteredCharacters = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            BiblicalCharactersCatalog.allCharacters
        } else {
            BiblicalCharactersCatalog.allCharacters.filter { ch ->
                com.example.ui.components.matchesSanctuarySearch(
                    searchQuery,
                    ch.name,
                    ch.title,
                    ch.groupLabel,
                    ch.sacredAttribute,
                    ch.biography
                )
            }
        }
    }

    val filteredArtworks = remember(allChapterArtworks, selectedThemeFilter, searchQuery) {
        allChapterArtworks.filter { art ->
            val matchesTheme = selectedThemeFilter == "All Themes" ||
                art.sceneArchetype.themeCategory.equals(selectedThemeFilter, ignoreCase = true)
            val matchesQuery = com.example.ui.components.matchesSanctuarySearch(
                searchQuery,
                art.artTitle,
                art.bookName,
                art.sceneArchetype.displayName,
                art.keyThemeTag,
                "${art.bookName} ${art.chapterNumber}"
            )
            matchesTheme && matchesQuery
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val columns = when {
            maxWidth >= 840.dp -> 3
            maxWidth >= 520.dp -> 2
            else -> 2
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // Pinned Header, Section Toggle, Deduplicated Search Bar & Filters outside LazyVerticalGrid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Sacred Art & Disciples Gallery",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Detailed 3D portraits of Jesus, the Twelve Disciples, King David, King Solomon & 34 Bible figures alongside all 441 New Testament, Psalms & Proverbs chapter paintings",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Primary Section Switcher: Portraits of Jesus & Disciples vs 260 Chapter Illustrations
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GallerySectionMode.entries.forEach { mode ->
                        val selected = gallerySection == mode
                        FilterChip(
                            selected = selected,
                            onClick = { gallerySection = mode },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (mode == GallerySectionMode.CHARACTERS_AND_DISCIPLES) {
                                        Icons.Default.Person
                                    } else {
                                        Icons.Default.AutoAwesome
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = mode.label,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier.testTag("gallery_section_${mode.name.lowercase()}")
                        )
                    }
                }

                // Deduplicated Search Bar (fixes double-letter typing issue across all Android devices)
                SanctuarySearchField(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    placeholderText = if (gallerySection == GallerySectionMode.CHARACTERS_AND_DISCIPLES) {
                        "Search Jesus, Peter, John, Paul, Mary, Thomas…"
                    } else {
                        "Search chapter paintings (e.g., Galilee, Pentecost, Revelation 21)…"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gallery_search_input")
                )

                if (gallerySection == GallerySectionMode.CHAPTER_SCENES) {
                    // Art Rendering Style Switcher
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ArtStyleMode.entries.forEach { mode ->
                            FilterChip(
                                selected = mode == artStyleMode,
                                onClick = { onArtStyleModeChange(mode) },
                                label = { Text(mode.label) },
                                modifier = Modifier.testTag("gallery_style_chip_${mode.name.lowercase()}")
                            )
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(galleryThemeFilters) { theme ->
                            FilterChip(
                                selected = theme == selectedThemeFilter,
                                onClick = { onThemeFilterSelected(theme) },
                                label = { Text(theme) }
                            )
                        }
                    }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("sacred_art_gallery_grid"),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                val showCharacters =
                    gallerySection == GallerySectionMode.CHARACTERS_AND_DISCIPLES &&
                        (searchQuery.isBlank() || filteredCharacters.isNotEmpty() || filteredArtworks.isEmpty())

                if (showCharacters) {
                    items(
                        items = filteredCharacters,
                        key = { "char_${it.id}" }
                    ) { character ->
                        GalleryCharacterPortraitCard(
                            character = character,
                            onInspect = { onInspectCharacter(character) },
                            onOpenKeyChapter = {
                                onOpenChapterInReader(character.keyBookId, character.keyChapterNumber)
                            }
                        )
                    }
                } else {
                    items(
                        items = filteredArtworks,
                        key = { "${it.bookId}_${it.chapterNumber}" }
                    ) { art ->
                        GalleryArtworkCard(
                            metadata = art,
                            artStyleMode = artStyleMode,
                            onInspect = { onInspectArt(art) },
                            onReadChapter = { onOpenChapterInReader(art.bookId, art.chapterNumber) }
                        )
                    }
                }

                item(span = { GridItemSpan(maxLineSpan) }, key = "gallery_hagezpooh_signature") {
                    com.example.ui.components.HagezPoohSignatureCard(
                        modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GalleryCharacterPortraitCard(
    character: BiblicalCharacter,
    onInspect: () -> Unit,
    onOpenKeyChapter: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, character.haloAccentColor.copy(alpha = 0.65f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onInspect() }
            .testTag("gallery_character_card_${character.id}")
    ) {
        Column {
            // Detailed Figurative Portrait Painting
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                CharacterPortraitCanvas(
                    character = character,
                    modifier = Modifier.fillMaxSize(),
                    animateHalo = false,
                    showArchFrame = true
                )

                // Top-left Group Badge
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.Black.copy(alpha = 0.68f),
                    border = BorderStroke(1.dp, character.haloAccentColor.copy(alpha = 0.8f)),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = character.groupLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = character.haloAccentColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }

                // Top-right Fullscreen Inspect Icon
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.Black.copy(alpha = 0.58f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Fullscreen,
                        contentDescription = "Inspect portrait",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(5.dp)
                            .size(16.dp)
                    )
                }

                // Bottom Scrim
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.82f))
                            )
                        )
                )

                Text(
                    text = character.ancientName,
                    style = MaterialTheme.typography.labelSmall,
                    color = character.haloAccentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            // Character Details Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = character.title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = character.sacredAttribute,
                    style = MaterialTheme.typography.labelSmall,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenKeyChapter() }
                        .testTag("gallery_char_story_button_${character.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = "Read Story",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GalleryArtworkCard(
    metadata: ChapterArtMetadata,
    artStyleMode: ArtStyleMode,
    onInspect: () -> Unit,
    onReadChapter: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, metadata.palette.goldLeaf.copy(alpha = 0.45f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onInspect() }
            .testTag("gallery_art_card_${metadata.bookId}_${metadata.chapterNumber}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(165.dp)
            ) {
                SacredArtCanvas(
                    metadata = metadata,
                    artStyleMode = artStyleMode,
                    modifier = Modifier.fillMaxSize(),
                    animateShimmer = false,
                    showOrnamentalFrame = true
                )

                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.Black.copy(alpha = 0.68f),
                    border = BorderStroke(1.dp, metadata.palette.goldLeaf.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "${metadata.bookName} ${metadata.chapterNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        color = metadata.palette.goldLeaf,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.Black.copy(alpha = 0.58f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Fullscreen,
                        contentDescription = "Inspect artwork",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(5.dp)
                            .size(16.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(44.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                            )
                        )
                )

                Text(
                    text = metadata.keyThemeTag,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = metadata.artTitle,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    minLines = 2
                )

                Text(
                    text = "“${metadata.keyVersePreview}”",
                    style = MaterialTheme.typography.labelSmall,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onReadChapter() }
                        .testTag("gallery_read_button_${metadata.bookId}_${metadata.chapterNumber}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = "Read Chapter",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
