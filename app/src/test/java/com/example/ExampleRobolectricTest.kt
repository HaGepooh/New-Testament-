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

        // Verify all 24 classical European oil painting masterwork plates have distinct drawable resources
        val allOilPlates = com.example.ui.art.BiblicalOilPaintingPlate.entries
        assertEquals(24, allOilPlates.size)
        assertEquals(24, allOilPlates.map { it.drawableResId }.toSet().size)

        // Verify Psalms (150 chapters) and Proverbs (31 chapters) have offline verses, 6 3D chapter slides, and 3D verse variations
        for ((bookId, chNum) in listOf("MAT" to 5, "JHN" to 4, "ACT" to 2, "PSA" to 23, "PSA" to 150, "PRO" to 3, "PRO" to 31)) {
            val chMeta = ChapterArtRegistry.getChapterMetadata(bookId, chNum)
            val slides = ThreeDBibleArtEngine.getChapter3DSlides(chMeta)
            assertEquals(6, slides.size)
            val slideLeadIds = slides.map { it.leadCharacter.id }.toSet()
            assertEquals(6, slideLeadIds.size)

            // Verify all 6 chapter slides use 6 completely different oil paintings!
            val slideOilPlates = (0..5).map { slideIdx ->
                com.example.ui.art.resolveChapterOilPaintingSpec(bookId, chNum, slideIdx).plate
            }.toSet()
            assertEquals(6, slideOilPlates.size)

            val verses = OfflineScriptureProvider.getOfflineVerses(bookId, chNum, BibleTranslation.KJV)
            assertTrue(verses.isNotEmpty())

            verses.forEachIndexed { idx, verse ->
                val variations = ThreeDBibleArtEngine.getVerse3DPictureVariations(verse, chMeta)
                assertEquals(3, variations.size)

                // Verify all 3 variations of this verse use 3 completely different oil paintings!
                val verseOilPlates = variations.map {
                    com.example.ui.art.resolveVerseOilPaintingSpec(it).plate
                }
                assertEquals(3, verseOilPlates.toSet().size)

                // Verify all 6 characters across the 3 variations of this verse are completely distinct
                val charIdsInVerse = variations.flatMap {
                    listOfNotNull(it.primaryCharacter.id, it.secondaryCharacter?.id)
                }
                assertEquals(6, charIdsInVerse.toSet().size)

                // Verify consecutive verses never show the same primary oil painting OR character pair
                if (idx > 0) {
                    val prevSpec = ThreeDBibleArtEngine.resolveVerse3DPicture(verses[idx - 1], chMeta)
                    val currSpec = variations.first()
                    assertNotEquals(
                        "${prevSpec.primaryCharacter.id}_${prevSpec.secondaryCharacter?.id}",
                        "${currSpec.primaryCharacter.id}_${currSpec.secondaryCharacter?.id}"
                    )
                    assertNotEquals(
                        "Consecutive verses ${idx} and ${idx + 1} in $bookId $chNum must not share the same oil painting",
                        com.example.ui.art.resolveVerseOilPaintingSpec(prevSpec).plate,
                        com.example.ui.art.resolveVerseOilPaintingSpec(currSpec).plate
                    )
                }
            }
        }
    }

    @Test
    fun `search bar deduplicator prevents single keystroke from producing two letters and allows typing full text smoothly`() {
        val deduplicator = SanctuaryInputDeduplicator()

        // 1. First keystroke arriving as a batched 2-char duplicate ("" -> "jj") even after long idle time
        val initial = createSyncedTextFieldValue("", TextRange.Zero, syncTick = 0)
        assertTrue(initial.annotatedString.getStringAnnotations(0, 0).isEmpty())

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
            incomingValue = TextFieldValue("jo", TextRange(2), composition = TextRange(0, 2)),
            nowMs = 5250L
        )
        assertEquals("jo", afterO1.sanitizedValue.text)
        assertFalse(afterO1.wasDeduplicated)
        // Normal keystrokes MUST have zero StringAnnotations and preserve IME composition so EditProcessor never calls restartInput()
        assertTrue(afterO1.sanitizedValue.annotatedString.getStringAnnotations(0, 2).isEmpty())
        assertEquals(TextRange(0, 2), afterO1.sanitizedValue.composition)

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

        // 4. Hardware KeyDown followed by duplicate KeyDown within bounce window
        assertFalse(deduplicator.shouldConsumeKeyDown('h'.code, isDeleteKey = false, nowMs = 6700L))
        assertTrue(deduplicator.shouldConsumeKeyDown('h'.code, isDeleteKey = false, nowMs = 6740L))

        // 5. Case-insensitive first-letter duplicate ("M" -> "Mm") collapsed to "M"
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

        // 6. Full word typing with double letters & numbers ("Matthew 22", "Philippians", "Psalm 119") works without dropping any characters
        for (phrase in listOf("Matthew 22", "Philippians", "Psalm 119", "Proverbs 31", "Thessalonians")) {
            deduplicator.reset()
            var current = createSyncedTextFieldValue("", TextRange.Zero, syncTick = 0)
            var clock = 10_000L
            val builder = StringBuilder()
            for (ch in phrase) {
                clock += 190L // Normal human typing speed (~190ms per key)
                builder.append(ch)
                val nextText = builder.toString()
                assertFalse(deduplicator.shouldConsumeKeyDown(ch.code, isDeleteKey = false, nowMs = clock))
                val step = deduplicator.processValueChange(
                    currentValue = current,
                    incomingValue = TextFieldValue(
                        text = nextText,
                        selection = TextRange(nextText.length),
                        composition = TextRange(0, nextText.length)
                    ),
                    nowMs = clock
                )
                assertFalse("Expected normal keystroke for '$ch' in '$phrase'", step.wasDeduplicated)
                assertEquals(nextText, step.sanitizedValue.text)
                assertTrue(step.sanitizedValue.annotatedString.getStringAnnotations(0, nextText.length).isEmpty())
                current = step.sanitizedValue
            }
            assertEquals(phrase, current.text)
        }

        // 7. Double-letter-tolerant and multi-word tokenized search matching
        assertTrue(matchesSanctuarySearch("Mathew", "Matthew"))
        assertTrue(matchesSanctuarySearch("Matthew", "Matthew"))
        assertTrue(matchesSanctuarySearch("Philipians", "Philippians"))
        assertTrue(matchesSanctuarySearch("Psalm 23", "Psalms 23"))
        assertTrue(matchesSanctuarySearch("Matthew 5", "Matthew", "Gospel"))
    }

    @Test
    fun `all 441 chapters across New Testament, Psalms, and Proverbs contain 100 percent of their complete canonical verses`() {
        var totalChaptersChecked = 0
        var totalVersesChecked = 0

        for (book in NewTestamentBooks.allBooks) {
            for (ch in 1..book.chapterCount) {
                totalChaptersChecked++
                val expectedCount = OfflineScriptureProvider.getExpectedVerseCount(book.id, ch)
                assertTrue("Expected > 0 verses for ${book.id} $ch", expectedCount > 0)

                val webVerses = OfflineScriptureProvider.getOfflineVerses(book.id, ch, BibleTranslation.WEB)
                val kjvVerses = OfflineScriptureProvider.getOfflineVerses(book.id, ch, BibleTranslation.KJV)

                assertEquals("Verse count mismatch in WEB for ${book.id} $ch", expectedCount, webVerses.size)
                assertEquals("Verse count mismatch in KJV for ${book.id} $ch", expectedCount, kjvVerses.size)

                // Verify contiguous 1..N verse numbering with zero skipped verses
                val expectedNumbers = (1..expectedCount).toList()
                assertEquals("Skipped verse numbers in ${book.id} $ch", expectedNumbers, webVerses.map { it.verseNumber })
                assertEquals("Skipped verse numbers in KJV ${book.id} $ch", expectedNumbers, kjvVerses.map { it.verseNumber })

                // Verify no verse text is empty
                assertTrue("Blank WEB verse in ${book.id} $ch", webVerses.all { it.text.isNotBlank() })
                assertTrue("Blank KJV verse in ${book.id} $ch", kjvVerses.all { it.text.isNotBlank() })

                totalVersesChecked += webVerses.size
            }
        }

        // Spot-check canonical chapter lengths that were previously incomplete
        assertEquals(25, OfflineScriptureProvider.getOfflineVerses("MAT", 1, BibleTranslation.KJV).size)
        assertEquals(23, OfflineScriptureProvider.getOfflineVerses("MAT", 2, BibleTranslation.KJV).size)
        assertEquals(48, OfflineScriptureProvider.getOfflineVerses("MAT", 5, BibleTranslation.KJV).size)
        assertEquals(75, OfflineScriptureProvider.getOfflineVerses("MAT", 26, BibleTranslation.KJV).size)
        assertEquals(80, OfflineScriptureProvider.getOfflineVerses("LUK", 1, BibleTranslation.KJV).size)
        assertEquals(51, OfflineScriptureProvider.getOfflineVerses("JHN", 1, BibleTranslation.KJV).size)
        assertEquals(176, OfflineScriptureProvider.getOfflineVerses("PSA", 119, BibleTranslation.KJV).size)
        assertEquals(31, OfflineScriptureProvider.getOfflineVerses("PRO", 31, BibleTranslation.KJV).size)

        assertEquals(441, totalChaptersChecked)
        assertEquals(11333, totalVersesChecked)
    }
}

