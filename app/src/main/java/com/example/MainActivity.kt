package com.example

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.ArtStyleMode
import com.example.model.VerseItem
import com.example.ui.art.FullScreenArtLightbox
import com.example.ui.components.ReaderSettingsSheet
import com.example.ui.components.VerseStudySheet
import com.example.ui.screens.BookCanonBrowserScreen
import com.example.ui.screens.ImmersiveReaderScreen
import com.example.ui.screens.SacredArtGalleryScreen
import com.example.ui.screens.TreasuresAndProgressScreen
import com.example.ui.theme.IlluminatedNTTheme
import com.example.viewmodel.AppDestination
import com.example.viewmodel.BibleViewModel
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val bibleViewModel: BibleViewModel = viewModel(
                factory = BibleViewModel.provideFactory(context)
            )
            val uiState by bibleViewModel.uiState.collectAsStateWithLifecycle()

            IlluminatedNTTheme(sanctuaryTheme = uiState.sanctuaryTheme) {
                IlluminatedBibleApp(viewModel = bibleViewModel)
            }
        }
    }
}

private data class NavTabInfo(
    val destination: AppDestination,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val navTabs = listOf(
    NavTabInfo(
        destination = AppDestination.SANCTUARY_READER,
        label = "Sanctuary",
        selectedIcon = Icons.AutoMirrored.Filled.MenuBook,
        unselectedIcon = Icons.Outlined.MenuBook,
        testTag = "nav_tab_sanctuary"
    ),
    NavTabInfo(
        destination = AppDestination.CANON_BOOKS,
        label = "Canon",
        selectedIcon = Icons.Filled.AutoAwesome,
        unselectedIcon = Icons.Outlined.AutoAwesome,
        testTag = "nav_tab_canon"
    ),
    NavTabInfo(
        destination = AppDestination.SACRED_ART_GALLERY,
        label = "Sacred Art",
        selectedIcon = Icons.Filled.Collections,
        unselectedIcon = Icons.Outlined.Collections,
        testTag = "nav_tab_gallery"
    ),
    NavTabInfo(
        destination = AppDestination.TREASURES_SAVED,
        label = "Treasures",
        selectedIcon = Icons.Filled.Bookmarks,
        unselectedIcon = Icons.Outlined.Bookmarks,
        testTag = "nav_tab_treasures"
    )
)

@Composable
fun IlluminatedBibleApp(viewModel: BibleViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val savedVerses by viewModel.savedVerses.collectAsStateWithLifecycle()
    val completedChapterKeys by viewModel.completedChapterKeys.collectAsStateWithLifecycle()

    var showSettingsSheet by remember { mutableStateOf(false) }
    var activeStudyVerse by remember { mutableStateOf<VerseItem?>(null) }

    // Android TextToSpeech Integration for Verse-by-Verse Chapter Narration
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        var engine: TextToSpeech? = null
        engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                engine?.language = Locale.US
                isTtsReady = true
            }
        }
        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                val verseNum = utteranceId?.removePrefix("verse_")?.toIntOrNull()
                viewModel.updateSpeechState(isSpeaking = true, verseNumber = verseNum)
            }

            override fun onDone(utteranceId: String?) {
                val verses = viewModel.uiState.value.currentChapterContent.verses
                val lastVerseNum = verses.lastOrNull()?.verseNumber
                val finishedNum = utteranceId?.removePrefix("verse_")?.toIntOrNull()
                if (finishedNum != null && finishedNum == lastVerseNum) {
                    viewModel.updateSpeechState(isSpeaking = false, verseNumber = null)
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                viewModel.updateSpeechState(isSpeaking = false, verseNumber = null)
            }
        })
        ttsEngine = engine

        onDispose {
            engine.stop()
            engine.shutdown()
        }
    }

    val toggleAudioNarration: () -> Unit = {
        val engine = ttsEngine
        if (uiState.isSpeaking) {
            engine?.stop()
            viewModel.updateSpeechState(isSpeaking = false, verseNumber = null)
        } else if (engine != null && isTtsReady) {
            engine.setSpeechRate(uiState.speechRate)
            val verses = uiState.currentChapterContent.verses
            val headerIntro = "${uiState.currentChapterContent.bookInfo.name} Chapter ${uiState.selectedChapterNumber}. ${uiState.currentChapterContent.metadata.artTitle}."
            engine.speak(headerIntro, TextToSpeech.QUEUE_FLUSH, null, "intro_header")
            verses.forEach { v ->
                engine.speak(
                    v.text,
                    TextToSpeech.QUEUE_ADD,
                    null,
                    "verse_${v.verseNumber}"
                )
            }
            viewModel.updateSpeechState(
                isSpeaking = true,
                verseNumber = verses.firstOrNull()?.verseNumber
            )
        }
    }

    // BackHandler for non-home tabs when using state-based screen switching
    if (uiState.activeDestination != AppDestination.SANCTUARY_READER && uiState.inspectedArtMetadata == null) {
        BackHandler {
            viewModel.selectDestination(AppDestination.SANCTUARY_READER)
        }
    }

    val savedVersesMapForCurrentChapter = remember(
        savedVerses,
        uiState.selectedBookId,
        uiState.selectedChapterNumber
    ) {
        savedVerses
            .filter {
                it.bookId == uiState.selectedBookId &&
                    it.chapterNumber == uiState.selectedChapterNumber
            }
            .associateBy { it.verseNumber }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 680.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                if (!isWideScreen) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.testTag("bottom_navigation_bar")
                    ) {
                        navTabs.forEach { tab ->
                            val selected = uiState.activeDestination == tab.destination
                            NavigationBarItem(
                                selected = selected,
                                onClick = { viewModel.selectDestination(tab.destination) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = { Text(tab.label) },
                                modifier = Modifier.testTag(tab.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isWideScreen) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.testTag("side_navigation_rail")
                    ) {
                        navTabs.forEach { tab ->
                            val selected = uiState.activeDestination == tab.destination
                            NavigationRailItem(
                                selected = selected,
                                onClick = { viewModel.selectDestination(tab.destination) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = { Text(tab.label) },
                                modifier = Modifier.testTag(tab.testTag)
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    when (uiState.activeDestination) {
                        AppDestination.SANCTUARY_READER -> {
                            ImmersiveReaderScreen(
                                uiState = uiState,
                                savedVersesForChapter = savedVersesMapForCurrentChapter,
                                completedChapterKeys = completedChapterKeys,
                                onSelectChapter = { chNum ->
                                    ttsEngine?.stop()
                                    viewModel.selectBookAndChapter(uiState.selectedBookId, chNum)
                                },
                                onPreviousChapter = {
                                    ttsEngine?.stop()
                                    viewModel.navigatePreviousChapter()
                                },
                                onNextChapter = {
                                    ttsEngine?.stop()
                                    viewModel.navigateNextChapter()
                                },
                                onOpenBookPicker = {
                                    viewModel.selectDestination(AppDestination.CANON_BOOKS)
                                },
                                onInspectArt = { metadata ->
                                    viewModel.openArtLightbox(metadata)
                                },
                                onInspectCharacter = { character ->
                                    viewModel.openCharacterModal(character)
                                },
                                onCycleArtStyle = {
                                    val modes = ArtStyleMode.entries
                                    val nextIdx = (modes.indexOf(uiState.artStyleMode) + 1) % modes.size
                                    viewModel.setArtStyleMode(modes[nextIdx])
                                },
                                onOpenSettings = { showSettingsSheet = true },
                                onToggleAudioNarration = toggleAudioNarration,
                                onVerseClick = { verse -> activeStudyVerse = verse },
                                onToggleChapterComplete = {
                                    viewModel.toggleChapterCompleted(
                                        uiState.selectedBookId,
                                        uiState.selectedChapterNumber
                                    )
                                }
                            )
                        }

                        AppDestination.CANON_BOOKS -> {
                            BookCanonBrowserScreen(
                                selectedBookId = uiState.selectedBookId,
                                selectedCategory = uiState.selectedCategoryFilter,
                                searchQuery = uiState.searchQuery,
                                artStyleMode = uiState.artStyleMode,
                                completedChapterKeys = completedChapterKeys,
                                onCategorySelected = viewModel::setCategoryFilter,
                                onSearchQueryChange = viewModel::setSearchQuery,
                                onSelectBookAndChapter = { bookId, chNum ->
                                    ttsEngine?.stop()
                                    viewModel.selectBookAndChapter(bookId, chNum, switchToReader = true)
                                }
                            )
                        }

                        AppDestination.SACRED_ART_GALLERY -> {
                            SacredArtGalleryScreen(
                                allChapterArtworks = viewModel.all260ChapterArtworks,
                                selectedThemeFilter = uiState.selectedGalleryThemeFilter,
                                searchQuery = uiState.searchQuery,
                                artStyleMode = uiState.artStyleMode,
                                onThemeFilterSelected = viewModel::setGalleryThemeFilter,
                                onSearchQueryChange = viewModel::setSearchQuery,
                                onArtStyleModeChange = viewModel::setArtStyleMode,
                                onInspectArt = viewModel::openArtLightbox,
                                onInspectCharacter = viewModel::openCharacterModal,
                                onOpenChapterInReader = { bookId, chNum ->
                                    ttsEngine?.stop()
                                    viewModel.selectBookAndChapter(bookId, chNum, switchToReader = true)
                                }
                            )
                        }

                        AppDestination.TREASURES_SAVED -> {
                            TreasuresAndProgressScreen(
                                savedVerses = savedVerses,
                                completedChapterKeys = completedChapterKeys,
                                artStyleMode = uiState.artStyleMode,
                                onOpenChapter = { bookId, chNum ->
                                    ttsEngine?.stop()
                                    viewModel.selectBookAndChapter(bookId, chNum, switchToReader = true)
                                },
                                onRemoveSavedVerse = viewModel::removeVerseHighlight,
                                onSeedSampleHighlight = viewModel::saveVerseHighlight
                            )
                        }
                    }
                }
            }
        }

        // Full-Screen Sacred Art Lightbox Modal Overlay
        val inspectedArt = uiState.inspectedArtMetadata
        if (inspectedArt != null) {
            FullScreenArtLightbox(
                metadata = inspectedArt,
                currentStyleMode = uiState.artStyleMode,
                onStyleModeChange = viewModel::setArtStyleMode,
                onDismiss = viewModel::closeArtLightbox,
                onOpenChapterInReader = {
                    ttsEngine?.stop()
                    viewModel.selectBookAndChapter(
                        inspectedArt.bookId,
                        inspectedArt.chapterNumber,
                        switchToReader = true
                    )
                }
            )
        }

        // Full-Screen Biblical Character Portrait & Biography Modal Overlay
        val inspectedChar = uiState.inspectedCharacter
        if (inspectedChar != null) {
            com.example.ui.art.CharacterDetailModal(
                character = inspectedChar,
                onDismiss = viewModel::closeCharacterModal,
                onOpenKeyChapter = { bookId, chNum ->
                    ttsEngine?.stop()
                    viewModel.selectBookAndChapter(bookId, chNum, switchToReader = true)
                }
            )
        }

        // Sanctuary & Typography Settings Sheet
        if (showSettingsSheet) {
            ReaderSettingsSheet(
                sanctuaryTheme = uiState.sanctuaryTheme,
                artStyleMode = uiState.artStyleMode,
                translation = uiState.translation,
                fontSizeSp = uiState.fontSizeSp,
                lineSpacingMultiplier = uiState.lineSpacingMultiplier,
                isFlowingCodexMode = uiState.isFlowingCodexMode,
                isRedLetterEnabled = uiState.isRedLetterEnabled,
                speechRate = uiState.speechRate,
                onSanctuaryThemeChange = viewModel::setSanctuaryTheme,
                onArtStyleModeChange = viewModel::setArtStyleMode,
                onTranslationChange = viewModel::setTranslation,
                onFontSizeChange = viewModel::setFontSizeSp,
                onLineSpacingChange = viewModel::setLineSpacingMultiplier,
                onFlowingCodexModeChange = viewModel::setFlowingCodexMode,
                onRedLetterChange = viewModel::setRedLetterEnabled,
                onSpeechRateChange = viewModel::setSpeechRate,
                onDismiss = { showSettingsSheet = false }
            )
        }

        // Verse Highlight & Reflection Study Sheet
        val studyVerse = activeStudyVerse
        if (studyVerse != null) {
            VerseStudySheet(
                bookName = uiState.currentChapterContent.bookInfo.name,
                chapterArtTitle = uiState.currentChapterContent.metadata.artTitle,
                verse = studyVerse,
                existingSavedVerse = savedVersesMapForCurrentChapter[studyVerse.verseNumber],
                onSaveHighlight = { color, note ->
                    viewModel.saveVerseHighlight(studyVerse, color, note)
                },
                onRemoveHighlight = {
                    viewModel.removeVerseHighlight(
                        studyVerse.bookId,
                        studyVerse.chapterNumber,
                        studyVerse.verseNumber
                    )
                },
                onDismiss = { activeStudyVerse = null }
            )
        }
    }
}
