package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.ViewCompact
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BiblicalCharacter
import com.example.data.BiblicalCharactersCatalog
import com.example.data.local.SavedVerseEntity
import com.example.model.ChapterArtMetadata
import com.example.model.HighlightColor
import com.example.model.VerseItem
import com.example.ui.art.CharacterPortraitCanvas
import com.example.ui.art.SacredArtCanvas
import com.example.ui.art.ThreeDBibleArtEngine
import com.example.ui.art.ThreeDChapterCarouselScene
import com.example.ui.art.ThreeDVerseIllustration
import com.example.ui.theme.LoraFontFamily
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.ReadingSanctuaryTheme
import com.example.ui.theme.WordsOfChristDark
import com.example.ui.theme.WordsOfChristLight
import com.example.viewmodel.ReaderUiState

@Composable
fun ImmersiveReaderScreen(
    uiState: ReaderUiState,
    savedVersesForChapter: Map<Int, SavedVerseEntity>,
    completedChapterKeys: Set<String>,
    onSelectChapter: (Int) -> Unit,
    onSelectBookAndChapter: (String, Int) -> Unit = { _, ch -> onSelectChapter(ch) },
    onPreviousChapter: () -> Unit,
    onNextChapter: () -> Unit,
    onOpenBookPicker: () -> Unit,
    onInspectArt: (ChapterArtMetadata) -> Unit,
    onInspectCharacter: (BiblicalCharacter) -> Unit,
    onCycleArtStyle: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleAudioNarration: () -> Unit,
    onVerseClick: (VerseItem) -> Unit,
    onToggleChapterComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val content = uiState.currentChapterContent
    val metadata = content.metadata
    val book = content.bookInfo
    val verses = content.verses
    val isChapterCompleted = completedChapterKeys.contains("${book.id}_${metadata.chapterNumber}")

    val chapterCharacters = remember(book.id, metadata.chapterNumber) {
        BiblicalCharactersCatalog.getCharactersForChapter(book.id, metadata.chapterNumber)
    }

    val chapter3DSlides = remember(book.id, metadata.chapterNumber) {
        ThreeDBibleArtEngine.getChapter3DSlides(metadata)
    }

    var selectedChapterSlideIndex by remember(book.id, metadata.chapterNumber) {
        mutableIntStateOf(0)
    }
    var expanded3DVerseCards by remember { mutableStateOf(false) }
    var showArtMeditationCard by remember { mutableStateOf(true) }
    val listState = rememberLazyListState()

    LaunchedEffect(book.id, metadata.chapterNumber) {
        listState.scrollToItem(0)
    }

    val christWordsColor = Color.White

    val activeSlide = chapter3DSlides.getOrElse(selectedChapterSlideIndex) { chapter3DSlides.first() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 760.dp)
                .testTag("reader_lazy_column"),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 1. MULTI-SCENE 3D CHAPTER SACRED ART STAGE & 4-PICTURE GALLERY SWITCHER
            item(key = "hero_art_${book.id}_${metadata.chapterNumber}") {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)
                            .clickable { onInspectArt(metadata) }
                            .testTag("chapter_hero_art_banner")
                    ) {
                        if (selectedChapterSlideIndex == 0) {
                            SacredArtCanvas(
                                metadata = metadata,
                                artStyleMode = uiState.artStyleMode,
                                modifier = Modifier.fillMaxSize(),
                                animateShimmer = false,
                                showOrnamentalFrame = true
                            )
                        } else {
                            ThreeDChapterCarouselScene(
                                slide = activeSlide,
                                metadata = metadata,
                                artStyleMode = uiState.artStyleMode,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Top Scrim & Action Bar inside Hero
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Black.copy(alpha = 0.76f),
                                            Color.Transparent
                                        )
                                    )
                                )
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color.Black.copy(alpha = 0.65f),
                                border = BorderStroke(1.dp, metadata.palette.goldLeaf.copy(alpha = 0.8f)),
                                modifier = Modifier
                                    .clickable { onOpenBookPicker() }
                                    .testTag("reader_book_selector_pill")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                        contentDescription = null,
                                        tint = metadata.palette.goldLeaf,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "${book.name} ${metadata.chapterNumber}",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "• ${verses.size}v • ${uiState.translation.badge}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = metadata.palette.goldLeaf
                                    )
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = onCycleArtStyle,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.58f))
                                        .testTag("cycle_art_style_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = "Switch illustration style (${uiState.artStyleMode.label})",
                                        tint = metadata.palette.goldLeaf,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                IconButton(
                                    onClick = onToggleAudioNarration,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (uiState.isSpeaking) metadata.palette.goldLeaf
                                            else Color.Black.copy(alpha = 0.58f)
                                        )
                                        .testTag("audio_narration_button")
                                ) {
                                    Icon(
                                        imageVector = if (uiState.isSpeaking) Icons.Default.StopCircle else Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Narrate chapter aloud",
                                        tint = if (uiState.isSpeaking) Color(0xFF1F1400) else Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                IconButton(
                                    onClick = onOpenSettings,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.58f))
                                        .testTag("reader_settings_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TextFormat,
                                        contentDescription = "Typography and sanctuary settings",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        // Bottom Scrim with Active 3D Chapter Picture Title & "Full Art" Button
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Transparent,
                                            Color(0xC40D0A18),
                                            Color(0xF50D0A18)
                                        )
                                    )
                                )
                                .padding(horizontal = 18.dp, vertical = 14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = metadata.palette.goldLeaf,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "${activeSlide.badgeTitle.uppercase()} • 3D VOLUMETRIC ART (${selectedChapterSlideIndex + 1}/${chapter3DSlides.size})",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = metadata.palette.goldLeaf
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = activeSlide.sceneTitle,
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = activeSlide.sceneDescription,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White.copy(alpha = 0.85f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = metadata.palette.goldLeaf.copy(alpha = 0.22f),
                                    border = BorderStroke(1.dp, metadata.palette.goldLeaf),
                                    modifier = Modifier
                                        .clickable { onInspectArt(metadata) }
                                        .testTag("inspect_fullscreen_art_button")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Fullscreen,
                                            contentDescription = "Inspect full-screen art",
                                            tint = metadata.palette.goldLeaf,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "Full Art",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 4-SCENE 3D CHAPTER PICTURES CAROUSEL STRIP (MULTIPLE PICTURES PER CHAPTER)
                    Surface(
                        color = Color(0xFF120E21),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Collections,
                                        contentDescription = null,
                                        tint = metadata.palette.goldLeaf,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = "3D Chapter Story Illustrations (${chapter3DSlides.size} Scenes)",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = metadata.palette.goldLeaf,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "Tap scene to view in 3D stage",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }

                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("chapter_3d_slides_row")
                            ) {
                                itemsIndexed(
                                    items = chapter3DSlides,
                                    key = { idx, slide -> "slide_${book.id}_${metadata.chapterNumber}_${idx}_${slide.badgeTitle}" }
                                ) { idx, slide ->
                                    val isSelectedSlide = idx == selectedChapterSlideIndex
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (isSelectedSlide) {
                                            Color(0xFF281F42)
                                        } else {
                                            Color(0xFF1A152C)
                                        },
                                        border = BorderStroke(
                                            width = if (isSelectedSlide) 2.dp else 1.dp,
                                            color = if (isSelectedSlide) {
                                                metadata.palette.goldLeaf
                                            } else {
                                                metadata.palette.goldLeaf.copy(alpha = 0.35f)
                                            }
                                        ),
                                        modifier = Modifier
                                            .width(178.dp)
                                            .clickable { selectedChapterSlideIndex = idx }
                                            .testTag("chapter_3d_slide_$idx")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(9.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(54.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .border(
                                                        1.dp,
                                                        metadata.palette.goldLeaf.copy(alpha = 0.8f),
                                                        RoundedCornerShape(10.dp)
                                                    )
                                            ) {
                                                ThreeDChapterCarouselScene(
                                                    slide = slide,
                                                    metadata = metadata,
                                                    artStyleMode = uiState.artStyleMode,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = slide.badgeTitle,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = metadata.palette.goldLeaf,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = slide.leadCharacter.name,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.SemiBold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = slide.pictureType.title,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color.White.copy(alpha = 0.72f),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (uiState.isLoadingChapter) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = metadata.palette.goldLeaf
                        )
                    }
                }
            }

            // 2. QUICK BOOK & CHAPTER SELECTOR RIBBON (INCLUDING PSALMS & PROVERBS)
            item(key = "chapter_strip_${book.id}") {
                val quickBooks = remember {
                    listOf(
                        Triple("PSA", "Psalms (150)", 23),
                        Triple("PRO", "Proverbs (31)", 3),
                        Triple("MAT", "Matthew", 5),
                        Triple("JHN", "John", 1),
                        Triple("ACT", "Acts", 2),
                        Triple("ROM", "Romans", 8),
                        Triple("REV", "Revelation", 21)
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 4.dp)
                            .testTag("quick_book_selector_row"),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(quickBooks, key = { it.first }) { (bId, label, defaultCh) ->
                            val isCurrentBook = bId == book.id
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = if (isCurrentBook) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                },
                                border = BorderStroke(
                                    1.dp,
                                    if (isCurrentBook) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                                ),
                                modifier = Modifier
                                    .clickable {
                                        if (!isCurrentBook) {
                                            onSelectBookAndChapter(bId, defaultCh)
                                        }
                                    }
                                    .testTag("quick_book_pill_$bId")
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isCurrentBook) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    fontWeight = if (isCurrentBook) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    val chapterNumbers = remember(book.chapterCount) {
                        (1..book.chapterCount).toList()
                    }
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .testTag("chapter_selector_row"),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(chapterNumbers, key = { it }) { chNum ->
                        val isSelected = chNum == metadata.chapterNumber
                        val isDone = completedChapterKeys.contains("${book.id}_$chNum")

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                            },
                            border = if (isDone && !isSelected) {
                                BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                            } else null,
                            modifier = Modifier
                                .clickable { onSelectChapter(chNum) }
                                .testTag("chapter_pill_$chNum")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Ch $chNum",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    },
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                                if (isDone) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.onPrimary
                                                else MaterialTheme.colorScheme.primary
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
            }

            // 2B. KEY BIBLICAL CHARACTERS & DISCIPLES IN THIS CHAPTER (3D PORTRAIT STRIP)
            item(key = "chapter_characters_${book.id}_${metadata.chapterNumber}") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "3D Portraits in ${book.name} ${metadata.chapterNumber}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Tap portrait for 3D biography",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("chapter_characters_row")
                    ) {
                        items(chapterCharacters, key = { it.id }) { character ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(
                                    1.2.dp,
                                    character.haloAccentColor.copy(alpha = 0.65f)
                                ),
                                shadowElevation = 4.dp,
                                modifier = Modifier
                                    .width(202.dp)
                                    .clickable { onInspectCharacter(character) }
                                    .testTag("reader_character_card_${character.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .shadow(4.dp, RoundedCornerShape(12.dp))
                                            .clip(RoundedCornerShape(12.dp))
                                            .border(
                                                1.5.dp,
                                                character.haloAccentColor,
                                                RoundedCornerShape(12.dp)
                                            )
                                    ) {
                                        CharacterPortraitCanvas(
                                            character = character,
                                            modifier = Modifier.fillMaxSize(),
                                            animateHalo = false,
                                            showArchFrame = false
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = character.name,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = character.groupLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = character.sacredAttribute,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. SACRED ART MEDITATION, AUDIO BANNER & 3D VERSE PICTURE DISPLAY BAR
            item(key = "art_meditation_card") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (uiState.isSpeaking) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = if (uiState.speakingVerseNumber != null) {
                                            "Narrating ${book.name} ${metadata.chapterNumber}:${uiState.speakingVerseNumber}…"
                                        } else {
                                            "Narrating ${book.name} ${metadata.chapterNumber}…"
                                        },
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Text(
                                    text = "Stop",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.clickable { onToggleAudioNarration() }
                                )
                            }
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                        ),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showArtMeditationCard = !showArtMeditationCard }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
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
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Chapter Illumination Commentary",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Icon(
                                    imageVector = if (showArtMeditationCard) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "Toggle commentary",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            AnimatedVisibility(visible = showArtMeditationCard) {
                                Column {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = metadata.visualMeditation,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // 3D Verse Picture Layout Toggle Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${verses.size} Complete Verses • ${verses.size * 3} Unique 3D Scenes",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "All verses 1–${verses.size} of ${book.name} ${metadata.chapterNumber} with 3 distinct 3D Bible character pictures per verse",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
                            modifier = Modifier
                                .clickable { expanded3DVerseCards = !expanded3DVerseCards }
                                .testTag("toggle_3d_verse_card_size_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (expanded3DVerseCards) Icons.Default.ViewAgenda else Icons.Default.ViewCompact,
                                    contentDescription = "Toggle 3D verse illustration layout",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (expanded3DVerseCards) "3D Triptych Cards" else "3D Medallions + 3 Scenes",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // 4. SCRIPTURE BODY (FLOWING CODEX MODE vs VERSE-BY-VERSE 3D MULTI-PICTURE MODE)
            if (uiState.isFlowingCodexMode) {
                item(key = "flowing_codex_paragraph") {
                    FlowingCodexParagraphView(
                        verses = verses,
                        metadata = metadata,
                        fontSizeSp = uiState.fontSizeSp,
                        lineSpacingMultiplier = uiState.lineSpacingMultiplier,
                        isRedLetterEnabled = uiState.isRedLetterEnabled,
                        christWordsColor = christWordsColor,
                        savedVersesForChapter = savedVersesForChapter,
                        onVerseClick = onVerseClick
                    )
                }
            } else {
                items(
                    items = verses,
                    key = { v -> "${v.bookId}_${v.chapterNumber}_${v.verseNumber}" },
                    contentType = { "illustrated_3d_verse_row" }
                ) { verse ->
                    val savedEntity = savedVersesForChapter[verse.verseNumber]
                    val isCurrentlySpoken = uiState.isSpeaking && uiState.speakingVerseNumber == verse.verseNumber
                    val isFirstVerse = verse.verseNumber == verses.firstOrNull()?.verseNumber

                    VerseRowItem(
                        verse = verse,
                        isFirstVerse = isFirstVerse,
                        metadata = metadata,
                        savedEntity = savedEntity,
                        isCurrentlySpoken = isCurrentlySpoken,
                        expanded3DCardMode = expanded3DVerseCards,
                        fontSizeSp = uiState.fontSizeSp,
                        lineSpacingMultiplier = uiState.lineSpacingMultiplier,
                        isRedLetterEnabled = uiState.isRedLetterEnabled,
                        christWordsColor = christWordsColor,
                        onClick = { onVerseClick(verse) },
                        onCharacterClick = onInspectCharacter
                    )
                }
            }

            // 5. CHAPTER COMPLETION & NEXT/PREVIOUS CHAPTER PAGER FOOTER
            item(key = "chapter_footer") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isChapterCompleted) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        border = BorderStroke(
                            1.5.dp,
                            if (isChapterCompleted) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleChapterComplete() }
                            .testTag("toggle_chapter_completed_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = if (isChapterCompleted) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = if (isChapterCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(24.dp)
                                )
                                Column {
                                    Text(
                                        text = if (isChapterCompleted) {
                                            "${book.name} ${metadata.chapterNumber} Completed"
                                        } else {
                                            "Mark ${book.name} ${metadata.chapterNumber} as Read"
                                        },
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Track your journey through all 441 Sacred Chapters (New Testament, Psalms & Proverbs)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onPreviousChapter,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("previous_chapter_button"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Previous")
                        }

                        Button(
                            onClick = onNextChapter,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("next_chapter_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("Next Chapter", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    com.example.ui.components.HagezPoohSignatureCard(
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun VerseRowItem(
    verse: VerseItem,
    isFirstVerse: Boolean,
    metadata: ChapterArtMetadata,
    savedEntity: SavedVerseEntity?,
    isCurrentlySpoken: Boolean,
    expanded3DCardMode: Boolean,
    fontSizeSp: Int,
    lineSpacingMultiplier: Float,
    isRedLetterEnabled: Boolean,
    christWordsColor: Color,
    onClick: () -> Unit,
    onCharacterClick: (BiblicalCharacter) -> Unit
) {
    // 3 distinct 3D character & story picture variations per verse (6 distinct characters per verse!)
    val verseVariations = remember(verse.bookId, verse.chapterNumber, verse.verseNumber, verse.text) {
        ThreeDBibleArtEngine.getVerse3DPictureVariations(verse, metadata)
    }
    var selectedVariationIdx by remember(verse.bookId, verse.chapterNumber, verse.verseNumber) {
        mutableIntStateOf(0)
    }
    val activeSpec = verseVariations.getOrElse(selectedVariationIdx) { verseVariations.first() }

    val highlightColor = savedEntity?.highlightColorId?.let { id ->
        HighlightColor.entries.firstOrNull { it.id == id }
    }

    val backgroundColor = when {
        isCurrentlySpoken -> Color(0xFF281F42)
        highlightColor != null -> Color(0xFF1D1830)
        else -> Color(0xFF141122)
    }

    val textColor = if (isRedLetterEnabled && verse.isWordsOfChrist) {
        christWordsColor
    } else {
        Color.White
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(
                width = if (isCurrentlySpoken) 1.5.dp else 1.dp,
                color = if (isCurrentlySpoken) {
                    MaterialTheme.colorScheme.primary
                } else {
                    activeSpec.accentGold.copy(alpha = 0.32f)
                },
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("verse_row_${verse.verseNumber}")
    ) {
        if (expanded3DCardMode) {
            // EXPANDED 3-PICTURE 3D CHARACTER TRIPTYCH ABOVE EACH VERSE
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(148.dp)
                        .graphicsLayer {
                            shadowElevation = 10f
                            rotationX = 2.5f
                        }
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            1.5.dp,
                            activeSpec.accentGold.copy(alpha = 0.88f),
                            RoundedCornerShape(14.dp)
                        )
                        .testTag("verse_3d_illustration_${verse.verseNumber}")
                ) {
                    ThreeDVerseIllustration(
                        spec = activeSpec,
                        modifier = Modifier.fillMaxSize()
                    )

                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                )
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "v${verse.verseNumber} (${activeSpec.variationLabel}) • ${activeSpec.customSceneTitle}",
                            style = MaterialTheme.typography.labelSmall,
                            color = activeSpec.accentGold,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color.Black.copy(alpha = 0.65f),
                            border = BorderStroke(1.dp, activeSpec.accentGold.copy(alpha = 0.75f)),
                            modifier = Modifier.clickable { onCharacterClick(activeSpec.primaryCharacter) }
                        ) {
                            Text(
                                text = if (activeSpec.secondaryCharacter != null) {
                                    "${activeSpec.primaryCharacter.name.substringBefore(" ")} & ${activeSpec.secondaryCharacter.name.substringBefore(" ")}"
                                } else {
                                    activeSpec.primaryCharacter.name
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // 3-Picture Side-by-Side 3D Triptych Strip for this Verse
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    verseVariations.forEachIndexed { vIdx, varSpec ->
                        val isSelectedVar = vIdx == selectedVariationIdx
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelectedVar) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                            },
                            border = BorderStroke(
                                width = if (isSelectedVar) 1.5.dp else 0.8.dp,
                                color = if (isSelectedVar) varSpec.accentGold else varSpec.accentGold.copy(alpha = 0.35f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedVariationIdx = vIdx }
                                .testTag("verse_${verse.verseNumber}_variation_$vIdx")
                        ) {
                            Row(
                                modifier = Modifier.padding(5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(7.dp))
                                        .border(1.dp, varSpec.accentGold, RoundedCornerShape(7.dp))
                                ) {
                                    ThreeDVerseIllustration(
                                        spec = varSpec,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = varSpec.variationLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${varSpec.primaryCharacter.name.substringBefore(" ")} & ${varSpec.secondaryCharacter?.name?.substringBefore(" ") ?: ""}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 9.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // VERSE CONTENT + COMPACT 3D DIORAMA COLUMN WITH 3 VARIATION THUMBNAILS
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!expanded3DCardMode) {
                // Main 3D Picture + 3 Mini 3D Variation Thumbnails (6 Distinct Characters per Verse!)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(112.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 108.dp, height = 102.dp)
                            .graphicsLayer {
                                shadowElevation = 8f
                                rotationY = if (verse.verseNumber % 2 == 0) -3.5f else 3.5f
                                rotationX = 2.5f
                            }
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                width = 1.5.dp,
                                color = activeSpec.accentGold.copy(alpha = 0.88f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .testTag("verse_3d_illustration_${verse.verseNumber}")
                    ) {
                        ThreeDVerseIllustration(
                            spec = activeSpec,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Verse Number + Active Variation Pill in Top-Left
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.75f))
                                .border(
                                    0.8.dp,
                                    activeSpec.accentGold,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "v${verse.verseNumber}•${selectedVariationIdx + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                color = activeSpec.accentGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    // 3 Mini 3D Character Picture Thumbnails right beneath the main 3D picture
                    // so every verse visibly displays 3 different 3D character pictures!
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        verseVariations.forEachIndexed { vIdx, varSpec ->
                            val isSelectedVar = vIdx == selectedVariationIdx
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(32.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(
                                        width = if (isSelectedVar) 1.5.dp else 0.7.dp,
                                        color = if (isSelectedVar) varSpec.accentGold else varSpec.accentGold.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .clickable { selectedVariationIdx = vIdx }
                                    .testTag("verse_${verse.verseNumber}_mini_var_$vIdx")
                            ) {
                                ThreeDVerseIllustration(
                                    spec = varSpec,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = if (activeSpec.secondaryCharacter != null) {
                            "${activeSpec.primaryCharacter.name.substringBefore(" ")} & ${activeSpec.secondaryCharacter.name.substringBefore(" ")}"
                        } else {
                            activeSpec.primaryCharacter.name.substringBefore(" (")
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 9.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clickable { onCharacterClick(activeSpec.primaryCharacter) }
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                // Header showing the active 3D scene title & characters + quick 1/2/3 variation pills
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                ) {
                    Text(
                        text = activeSpec.customSceneTitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (isFirstVerse && verse.text.isNotEmpty()) {
                    val cleanText = verse.text.trimStart('\'', '"')
                    val initialChar = cleanText.firstOrNull()?.uppercaseChar() ?: 'I'
                    val remainderText = if (cleanText.length > 1) cleanText.substring(1) else ""

                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            metadata.palette.primaryJewel,
                                            metadata.palette.skyTop
                                        )
                                    )
                                )
                                .border(
                                    1.8.dp,
                                    metadata.palette.goldLeaf,
                                    RoundedCornerShape(10.dp)
                                )
                        ) {
                            Text(
                                text = initialChar.toString(),
                                fontFamily = PlayfairDisplayFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                color = metadata.palette.goldLeaf
                            )
                        }

                        Text(
                            text = remainderText,
                            fontFamily = LoraFontFamily,
                            fontSize = fontSizeSp.sp,
                            lineHeight = (fontSizeSp * lineSpacingMultiplier).sp,
                            color = textColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    val annotated = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = (fontSizeSp * 0.68f).sp,
                                baselineShift = BaselineShift.Superscript
                            )
                        ) {
                            append("${verse.verseNumber} ")
                        }
                        withStyle(
                            SpanStyle(
                                color = textColor,
                                fontFamily = LoraFontFamily,
                                fontSize = fontSizeSp.sp
                            )
                        ) {
                            append(verse.text)
                        }
                    }

                    Text(
                        text = annotated,
                        lineHeight = (fontSizeSp * lineSpacingMultiplier).sp
                    )
                }

                if (savedEntity != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Saved verse",
                            tint = highlightColor?.swatchColor ?: MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (savedEntity.reflectionNote.isNotBlank()) {
                                savedEntity.reflectionNote
                            } else {
                                "Highlighted in ${highlightColor?.label ?: "Gold"}"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            fontStyle = FontStyle.Italic,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FlowingCodexParagraphView(
    verses: List<VerseItem>,
    metadata: ChapterArtMetadata,
    fontSizeSp: Int,
    lineSpacingMultiplier: Float,
    isRedLetterEnabled: Boolean,
    christWordsColor: Color,
    savedVersesForChapter: Map<Int, SavedVerseEntity>,
    onVerseClick: (VerseItem) -> Unit
) {
    val defaultInk = Color.White
    val numColor = metadata.palette.goldLeaf

    val paragraphText = buildAnnotatedString {
        verses.forEachIndexed { idx, v ->
            val saved = savedVersesForChapter[v.verseNumber]
            val highlightBg = saved?.highlightColorId?.let { id ->
                HighlightColor.entries.firstOrNull { it.id == id }?.tintBackground
            } ?: Color.Transparent

            withStyle(
                SpanStyle(
                    color = numColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = (fontSizeSp * 0.65f).sp,
                    baselineShift = BaselineShift.Superscript
                )
            ) {
                append("${v.verseNumber}")
            }
            append(" ")
            withStyle(
                SpanStyle(
                    color = if (isRedLetterEnabled && v.isWordsOfChrist) christWordsColor else defaultInk,
                    background = highlightBg,
                    fontFamily = LoraFontFamily,
                    fontSize = fontSizeSp.sp
                )
            ) {
                append(v.text)
            }
            if (idx < verses.lastIndex) {
                append("  ")
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Horizontal 3D Verse Vignette Strip in Flowing Codex Mode
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(
                items = verses,
                key = { v -> "codex_3d_${v.bookId}_${v.chapterNumber}_${v.verseNumber}" }
            ) { v ->
                val spec = remember(v.bookId, v.chapterNumber, v.verseNumber, v.text) {
                    ThreeDBibleArtEngine.resolveVerse3DPicture(v, metadata)
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(96.dp)
                        .clickable { onVerseClick(v) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(84.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.5.dp, spec.accentGold, RoundedCornerShape(14.dp))
                    ) {
                        ThreeDVerseIllustration(
                            spec = spec,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "v${v.verseNumber} • ${spec.primaryCharacter.name.substringBefore(" ")}",
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF141122)
            ),
            border = BorderStroke(1.dp, metadata.palette.goldLeaf.copy(alpha = 0.45f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clickable { verses.firstOrNull()?.let(onVerseClick) }
        ) {
            Text(
                text = paragraphText,
                color = Color.White,
                lineHeight = (fontSizeSp * lineSpacingMultiplier).sp,
                modifier = Modifier.padding(18.dp)
            )
        }
    }
}
