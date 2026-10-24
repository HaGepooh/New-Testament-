package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.BibleRepository
import com.example.data.BiblicalCharacter
import com.example.data.ChapterArtRegistry
import com.example.data.NewTestamentBooks
import com.example.data.OfflineScriptureProvider
import com.example.data.local.BibleDatabase
import com.example.data.local.SavedVerseEntity
import com.example.data.remote.BibleApiService
import com.example.model.ArtStyleMode
import com.example.model.BibleTranslation
import com.example.model.BookInfo
import com.example.model.ChapterArtMetadata
import com.example.model.ChapterContent
import com.example.model.HighlightColor
import com.example.model.TestamentCategory
import com.example.model.VerseItem
import com.example.ui.theme.ReadingSanctuaryTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppDestination {
    SANCTUARY_READER,
    CANON_BOOKS,
    SACRED_ART_GALLERY,
    TREASURES_SAVED
}

data class ReaderUiState(
    val activeDestination: AppDestination = AppDestination.SANCTUARY_READER,
    val selectedBookId: String = "MAT",
    val selectedChapterNumber: Int = 5, // Open on Sermon on the Mount (Matthew 5) for an inspiring first launch
    val translation: BibleTranslation = BibleTranslation.WEB,
    val sanctuaryTheme: ReadingSanctuaryTheme = ReadingSanctuaryTheme.NOCTURNE,
    val artStyleMode: ArtStyleMode = ArtStyleMode.STAINED_GLASS,
    val fontSizeSp: Int = 19,
    val lineSpacingMultiplier: Float = 1.65f,
    val isFlowingCodexMode: Boolean = false,
    val isRedLetterEnabled: Boolean = true,
    val isLoadingChapter: Boolean = false,
    val currentChapterContent: ChapterContent = ChapterContent(
        metadata = ChapterArtRegistry.getChapterMetadata("MAT", 5),
        bookInfo = NewTestamentBooks.getBookById("MAT"),
        verses = OfflineScriptureProvider.getOfflineVerses("MAT", 5, BibleTranslation.WEB),
        translation = BibleTranslation.WEB,
        isFromLiveNetwork = false
    ),
    val inspectedArtMetadata: ChapterArtMetadata? = null,
    val inspectedCharacter: BiblicalCharacter? = null,
    val selectedCategoryFilter: TestamentCategory? = null,
    val selectedGalleryThemeFilter: String = "All Themes",
    val searchQuery: String = "",
    val speakingVerseNumber: Int? = null,
    val isSpeaking: Boolean = false,
    val speechRate: Float = 1.0f
)

class BibleViewModel(
    private val repository: BibleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    val savedVerses: StateFlow<List<SavedVerseEntity>> = repository.allSavedVerses
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val completedChapterKeys: StateFlow<Set<String>> = repository.completedChapters
        .map { list -> list.map { it.chapterKey }.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    val all260ChapterArtworks: List<ChapterArtMetadata> = ChapterArtRegistry.getAll260Chapters()

    init {
        loadChapter("MAT", 5, BibleTranslation.WEB)
    }

    fun selectDestination(destination: AppDestination) {
        _uiState.update { it.copy(activeDestination = destination) }
    }

    fun selectBookAndChapter(bookId: String, chapterNumber: Int, switchToReader: Boolean = true) {
        val book = NewTestamentBooks.getBookById(bookId)
        val validChapter = chapterNumber.coerceIn(1, book.chapterCount)
        _uiState.update {
            it.copy(
                selectedBookId = book.id,
                selectedChapterNumber = validChapter,
                activeDestination = if (switchToReader) AppDestination.SANCTUARY_READER else it.activeDestination,
                inspectedArtMetadata = null,
                inspectedCharacter = null,
                isSpeaking = false,
                speakingVerseNumber = null
            )
        }
        loadChapter(book.id, validChapter, _uiState.value.translation)
    }

    fun navigateNextChapter() {
        val current = _uiState.value
        val currentBook = NewTestamentBooks.getBookById(current.selectedBookId)
        if (current.selectedChapterNumber < currentBook.chapterCount) {
            selectBookAndChapter(currentBook.id, current.selectedChapterNumber + 1)
        } else {
            val allBooks = NewTestamentBooks.allBooks
            val bookIndex = allBooks.indexOfFirst { it.id == currentBook.id }
            val nextBook = allBooks.getOrNull((bookIndex + 1) % allBooks.size) ?: allBooks.first()
            selectBookAndChapter(nextBook.id, 1)
        }
    }

    fun navigatePreviousChapter() {
        val current = _uiState.value
        val currentBook = NewTestamentBooks.getBookById(current.selectedBookId)
        if (current.selectedChapterNumber > 1) {
            selectBookAndChapter(currentBook.id, current.selectedChapterNumber - 1)
        } else {
            val allBooks = NewTestamentBooks.allBooks
            val bookIndex = allBooks.indexOfFirst { it.id == currentBook.id }
            val prevIndex = if (bookIndex > 0) bookIndex - 1 else allBooks.lastIndex
            val prevBook = allBooks[prevIndex]
            selectBookAndChapter(prevBook.id, prevBook.chapterCount)
        }
    }

    fun setTranslation(translation: BibleTranslation) {
        if (_uiState.value.translation == translation) return
        _uiState.update { it.copy(translation = translation) }
        loadChapter(_uiState.value.selectedBookId, _uiState.value.selectedChapterNumber, translation)
    }

    fun setSanctuaryTheme(theme: ReadingSanctuaryTheme) {
        _uiState.update { it.copy(sanctuaryTheme = theme) }
    }

    fun setArtStyleMode(mode: ArtStyleMode) {
        _uiState.update { it.copy(artStyleMode = mode) }
    }

    fun setFontSizeSp(sizeSp: Int) {
        _uiState.update { it.copy(fontSizeSp = sizeSp.coerceIn(15, 28)) }
    }

    fun setLineSpacingMultiplier(multiplier: Float) {
        _uiState.update { it.copy(lineSpacingMultiplier = multiplier.coerceIn(1.35f, 2.0f)) }
    }

    fun setFlowingCodexMode(enabled: Boolean) {
        _uiState.update { it.copy(isFlowingCodexMode = enabled) }
    }

    fun setRedLetterEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isRedLetterEnabled = enabled) }
    }

    fun openArtLightbox(metadata: ChapterArtMetadata) {
        _uiState.update { it.copy(inspectedArtMetadata = metadata) }
    }

    fun closeArtLightbox() {
        _uiState.update { it.copy(inspectedArtMetadata = null) }
    }

    fun openCharacterModal(character: BiblicalCharacter) {
        _uiState.update { it.copy(inspectedCharacter = character) }
    }

    fun closeCharacterModal() {
        _uiState.update { it.copy(inspectedCharacter = null) }
    }

    fun setCategoryFilter(category: TestamentCategory?) {
        _uiState.update { it.copy(selectedCategoryFilter = category) }
    }

    fun setGalleryThemeFilter(themeFilter: String) {
        _uiState.update { it.copy(selectedGalleryThemeFilter = themeFilter) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun updateSpeechState(isSpeaking: Boolean, verseNumber: Int?) {
        _uiState.update { it.copy(isSpeaking = isSpeaking, speakingVerseNumber = verseNumber) }
    }

    fun setSpeechRate(rate: Float) {
        _uiState.update { it.copy(speechRate = rate) }
    }

    fun saveVerseHighlight(
        verse: VerseItem,
        color: HighlightColor,
        reflectionNote: String
    ) {
        viewModelScope.launch {
            repository.saveVerseHighlight(verse, color, reflectionNote)
        }
    }

    fun removeVerseHighlight(bookId: String, chapterNumber: Int, verseNumber: Int) {
        viewModelScope.launch {
            repository.removeVerseHighlight(bookId, chapterNumber, verseNumber)
        }
    }

    fun toggleChapterCompleted(bookId: String, chapterNumber: Int) {
        val key = "${bookId}_$chapterNumber"
        val currentlyDone = completedChapterKeys.value.contains(key)
        viewModelScope.launch {
            repository.toggleChapterCompleted(bookId, chapterNumber, !currentlyDone)
        }
    }

    private fun loadChapter(
        bookId: String,
        chapterNumber: Int,
        translation: BibleTranslation
    ) {
        // Immediately show offline metadata & verses so there is zero UI flash or blank state
        val immediateBook: BookInfo = NewTestamentBooks.getBookById(bookId)
        val immediateMetadata = ChapterArtRegistry.getChapterMetadata(bookId, chapterNumber)
        val immediateVerses = OfflineScriptureProvider.getOfflineVerses(bookId, chapterNumber, translation)

        _uiState.update {
            it.copy(
                isLoadingChapter = true,
                currentChapterContent = ChapterContent(
                    metadata = immediateMetadata,
                    bookInfo = immediateBook,
                    verses = immediateVerses,
                    translation = translation,
                    isFromLiveNetwork = false
                )
            )
        }

        viewModelScope.launch {
            val loaded = repository.loadChapterContent(bookId, chapterNumber, translation)
            if (_uiState.value.selectedBookId == bookId && _uiState.value.selectedChapterNumber == chapterNumber) {
                _uiState.update {
                    it.copy(
                        isLoadingChapter = false,
                        currentChapterContent = loaded
                    )
                }
            }
        }
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val database = BibleDatabase.getInstance(context)
                    val apiService = BibleApiService.create()
                    val repository = BibleRepository(database.bibleDao(), apiService)
                    return BibleViewModel(repository) as T
                }
            }
        }
    }
}
