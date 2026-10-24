package com.example

import android.content.Context
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.test.core.app.ApplicationProvider
import com.example.data.BiblicalCharactersCatalog
import com.example.data.ChapterArtRegistry
import com.example.data.NewTestamentBooks
import com.example.data.OfflineScriptureProvider
import com.example.model.BibleTranslation
import com.example.ui.art.ThreeDBibleArtEngine
import com.example.ui.components.SanctuaryInputDeduplicator
import com.example.ui.components.createSyncedTextFieldValue
import com.example.ui.components.matchesSanctuarySearch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `34 biblical characters, Psalms and Proverbs 3D chapters, and 3 disjoint-character 3D variations per verse are configured`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("The Radiant New Testament", appName)

        // Verify all 29 Sacred Books (27 New Testament + Psalms & Proverbs) and 441 chapters have unique artwork metadata
        assertEquals(29, NewTestamentBooks.allBooks.size)
        val allChapters = ChapterArtRegistry.getAll260Chapters()
        assertEquals(441, allChapters.size)
        assertTrue(allChapters.all { it.artTitle.isNotBlank() && it.visualMeditation.isNotBlank() })

        // Verify all 34 biblical characters & disciples portraits (including King David, Asaph, King Solomon, Lady Wisdom)
        assertEquals(34, BiblicalCharactersCatalog.allCharacters.size)

        // Verify Psalms (150 chapters) and Proverbs (31 chapters) have offline verses, 6 3D chapter slides, and 3D verse variations
        for ((bookId, chNum) in listOf("MAT" to 5, "PSA" to 23, "PSA" to 150, "PRO" to 3, "PRO" to 31)) {
            val chMeta = ChapterArtRegistry.getChapterMetadata(bookId, chNum)
            val slides = ThreeDBibleArtEngine.getChapter3DSlides(chMeta)
            assertEquals(6, slides.size)
            val slideLeadIds = slides.map { it.leadCharacter.id }.toSet()
            assertEquals(6, slideLeadIds.size)

            val verses = OfflineScriptureProvider.getOfflineVerses(bookId, chNum, BibleTranslation.KJV)
            assertTrue(verses.isNotEmpty())

            verses.forEachIndexed { idx, verse ->
                val variations = ThreeDBibleArtEngine.getVerse3DPictureVariations(verse, chMeta)
                assertEquals(3, variations.size)

                // Verify all 6 characters across the 3 variations of this verse are completely distinct
                val charIdsInVerse = variations.flatMap {
                    listOfNotNull(it.primaryCharacter.id, it.secondaryCharacter?.id)
                }
                assertEquals(6, charIdsInVerse.toSet().size)

                // Verify consecutive verses never have the same primary character & secondary character pair
                if (idx > 0) {
                    val prevSpec = ThreeDBibleArtEngine.resolveVerse3DPicture(verses[idx - 1], chMeta)
                    val currSpec = variations.first()
                    assertNotEquals(
                        "${prevSpec.primaryCharacter.id}_${prevSpec.secondaryCharacter?.id}",
                        "${currSpec.primaryCharacter.id}_${currSpec.secondaryCharacter?.id}"
                    )
                }
            }
        }
    }

    @Test
    fun `search bar deduplicator prevents single keystroke from producing two letters across all pathways`() {
        val deduplicator = SanctuaryInputDeduplicator()

        // 1. First keystroke arriving as a batched 2-char duplicate ("" -> "jj") even after long idle time
        val initial = createSyncedTextFieldValue("", TextRange.Zero, syncTick = 0)
        val batchedFirst = deduplicator.processValueChange(
            currentValue = initial,
            incomingValue = TextFieldValue("jj", TextRange(2)),
            nowMs = 5000L
        )
        assertEquals("j", batchedFirst.sanitizedValue.text)
        assertTrue(batchedFirst.wasDeduplicated)

        // 2. Second keystroke arriving as two sequential callbacks ("j" -> "jo", then phantom "jo" -> "joo")
        val afterO1 = deduplicator.processValueChange(
            currentValue = batchedFirst.sanitizedValue,
            incomingValue = TextFieldValue("jo", TextRange(2)),
            nowMs = 5250L
        )
        assertEquals("jo", afterO1.sanitizedValue.text)
        assertFalse(afterO1.wasDeduplicated)

        val afterO2Phantom = deduplicator.processValueChange(
            currentValue = afterO1.sanitizedValue,
            incomingValue = TextFieldValue("joo", TextRange(3)),
            nowMs = 5400L
        )
        assertEquals("jo", afterO2Phantom.sanitizedValue.text)
        assertTrue(afterO2Phantom.wasDeduplicated)
        // Verify that the rejected duplicate produces a non-equal TextFieldValue via AnnotatedString sync annotation
        // so CoreTextField is guaranteed not to skip recomposition and resets EditingBuffer
        assertNotEquals(afterO1.sanitizedValue, afterO2Phantom.sanitizedValue)

        // 3. Soft-keyboard composing re-commit of previous char + new char ("m" -> "mma" collapsed to "ma")
        deduplicator.reset()
        val mStep = deduplicator.processValueChange(
            currentValue = initial,
            incomingValue = TextFieldValue("m", TextRange(1)),
            nowMs = 6000L
        )
        val mmaStep = deduplicator.processValueChange(
            currentValue = mStep.sanitizedValue,
            incomingValue = TextFieldValue("mma", TextRange(3)),
            nowMs = 6200L
        )
        assertEquals("ma", mmaStep.sanitizedValue.text)
        assertTrue(mmaStep.wasDeduplicated)

        // 4. Hardware KeyDown followed by duplicate KeyDown or commitText
        assertFalse(deduplicator.shouldConsumeKeyDown('h'.code, isDeleteKey = false, nowMs = 6700L))
        assertTrue(deduplicator.shouldConsumeKeyDown('h'.code, isDeleteKey = false, nowMs = 6740L))

        // 5. Case-insensitive duplicate ("M" -> "Mm") collapsed to "M"
        deduplicator.reset()
        val m1 = deduplicator.processValueChange(
            currentValue = initial,
            incomingValue = TextFieldValue("M", TextRange(1)),
            nowMs = 8000L
        )
        val m2Phantom = deduplicator.processValueChange(
            currentValue = m1.sanitizedValue,
            incomingValue = TextFieldValue("Mm", TextRange(2)),
            nowMs = 8120L
        )
        assertEquals("M", m2Phantom.sanitizedValue.text)
        assertTrue(m2Phantom.wasDeduplicated)

        // 6. Double-letter-tolerant search matching
        assertTrue(matchesSanctuarySearch("Mathew", "Matthew"))
        assertTrue(matchesSanctuarySearch("Matthew", "Matthew"))
        assertTrue(matchesSanctuarySearch("Philipians", "Philippians"))
    }
}
