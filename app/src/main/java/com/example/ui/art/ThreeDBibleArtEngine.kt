package com.example.ui.art

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import com.example.data.BiblicalCharacter
import com.example.data.BiblicalCharactersCatalog
import com.example.data.CharacterVisualStyle
import com.example.model.ArtPalette
import com.example.model.ArtStyleMode
import com.example.model.ChapterArtMetadata
import com.example.model.VerseItem
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * 12 distinct 3D environmental depth stages so every picture has a clear, recognizable
 * 3D biblical world behind its characters.
 */
enum class ThreeDEnvironmentStage(val label: String) {
    GALILEE_SEA_AND_BOAT_3D("3D Sea of Galilee & Wooden Sailboat"),
    JERUSALEM_TEMPLE_COLONNADE_3D("3D Temple Courts & Marble Columns"),
    MOUNT_OF_OLIVES_GROVE_3D("3D Mount of Beatitudes & Olive Trees"),
    BETHLEHEM_STAR_GROTTO_3D("3D Bethlehem Nativity Arch & Blazing Star"),
    JORDAN_RIVER_WATERFALL_3D("3D Jordan River Waters & Descending Dove"),
    UPPER_ROOM_CHALICE_TABLE_3D("3D Upper Room Banquet & Golden Chalice"),
    EMPTY_TOMB_RESURRECTION_DAWN_3D("3D Garden Tomb, Rolled Stone & Calvary"),
    DAMASCUS_DESERT_SUNBURST_3D("3D Damascus Road & Blinding Heaven Light"),
    AREOPAGUS_MARBLE_COURT_3D("3D Classical Colonnade & Altar"),
    PATMOS_RAINBOW_THRONE_3D("3D Emerald Rainbow Throne & Seven Lamps"),
    SYCHAR_STONE_WELL_3D("3D Jacob's Stone Well & Terracotta Amphora"),
    NEW_JERUSALEM_CRYSTAL_GATES_3D("3D New Jerusalem Jasper Gates & River of Life"),
    PSALM_GREEN_PASTURES_HARP_3D("3D Green Pastures, Still Waters & Golden Harp of Zion"),
    PROVERBS_SEVEN_PILLARS_PALACE_3D("3D Seven Pillars of Solomon's Wisdom & Royal Court")
}

/**
 * 24 distinct 3D Bible story vignette types matched to individual verses and variations.
 */
enum class Verse3DPictureType(
    val title: String,
    val primaryCharacterId: String,
    val secondaryCharacterId: String?,
    val badgeLabel: String,
    val defaultStage: ThreeDEnvironmentStage
) {
    JESUS_TEACHING_3D(
        title = "Jesus Teaching on the Mount",
        primaryCharacterId = "JESUS",
        secondaryCharacterId = "MATTHEW",
        badgeLabel = "Teaching of Christ",
        defaultStage = ThreeDEnvironmentStage.MOUNT_OF_OLIVES_GROVE_3D
    ),
    JESUS_HEALING_COMPASSION_3D(
        title = "Jesus Extending Healing & Mercy",
        primaryCharacterId = "JESUS",
        secondaryCharacterId = "LUKE",
        badgeLabel = "Miracle of Mercy",
        defaultStage = ThreeDEnvironmentStage.JERUSALEM_TEMPLE_COLONNADE_3D
    ),
    JESUS_KING_OF_GLORY_3D(
        title = "Christ the Messianic King in Glory",
        primaryCharacterId = "JESUS",
        secondaryCharacterId = "MOSES_AND_ELIJAH",
        badgeLabel = "Kingdom Glory",
        defaultStage = ThreeDEnvironmentStage.PATMOS_RAINBOW_THRONE_3D
    ),
    JESUS_GOOD_SHEPHERD_3D(
        title = "Jesus the Good Shepherd & Flock",
        primaryCharacterId = "JESUS",
        secondaryCharacterId = "JOHN",
        badgeLabel = "Good Shepherd",
        defaultStage = ThreeDEnvironmentStage.MOUNT_OF_OLIVES_GROVE_3D
    ),
    PETER_AND_FISHERMEN_3D(
        title = "Simon Peter & Andrew on the Sea",
        primaryCharacterId = "PETER",
        secondaryCharacterId = "ANDREW",
        badgeLabel = "Fishers of Men",
        defaultStage = ThreeDEnvironmentStage.GALILEE_SEA_AND_BOAT_3D
    ),
    JOHN_AND_BELOVED_DISCIPLES_3D(
        title = "John & James — Sons of Zebedee",
        primaryCharacterId = "JOHN",
        secondaryCharacterId = "JAMES_GREATER",
        badgeLabel = "Beloved Disciples",
        defaultStage = ThreeDEnvironmentStage.UPPER_ROOM_CHALICE_TABLE_3D
    ),
    PAUL_APOSTOLIC_HERALD_3D(
        title = "Paul of Tarsus Proclaiming Grace",
        primaryCharacterId = "PAUL",
        secondaryCharacterId = "TIMOTHY_BARNABAS",
        badgeLabel = "Apostolic Herald",
        defaultStage = ThreeDEnvironmentStage.AREOPAGUS_MARBLE_COURT_3D
    ),
    MARY_AND_HOLY_WOMEN_3D(
        title = "Mary & Joseph at Bethlehem",
        primaryCharacterId = "MARY_MOTHER",
        secondaryCharacterId = "JOSEPH_NAZARETH",
        badgeLabel = "Holy Nativity",
        defaultStage = ThreeDEnvironmentStage.BETHLEHEM_STAR_GROTTO_3D
    ),
    JOHN_BAPTIST_PROPHET_3D(
        title = "John the Baptist by the Jordan",
        primaryCharacterId = "JOHN_BAPTIST",
        secondaryCharacterId = "ANDREW",
        badgeLabel = "Voice in the Desert",
        defaultStage = ThreeDEnvironmentStage.JORDAN_RIVER_WATERFALL_3D
    ),
    ARCHANGEL_HEAVENLY_HOST_3D(
        title = "Archangel Gabriel's Heavenly Annunciation",
        primaryCharacterId = "GABRIEL_ARCHANGEL",
        secondaryCharacterId = "MARY_MOTHER",
        badgeLabel = "Angelic Herald",
        defaultStage = ThreeDEnvironmentStage.BETHLEHEM_STAR_GROTTO_3D
    ),
    ROMAN_CENTURION_AND_SOLDIERS_3D(
        title = "Cornelius & the Centurion's Faith",
        primaryCharacterId = "CORNELIUS_CENTURION",
        secondaryCharacterId = "PETER",
        badgeLabel = "Centurion's Faith",
        defaultStage = ThreeDEnvironmentStage.AREOPAGUS_MARBLE_COURT_3D
    ),
    SCRIBES_AND_TEMPLE_ELDERS_3D(
        title = "Nicodemus & Dialogue in the Temple",
        primaryCharacterId = "NICODEMUS_PHARISEE",
        secondaryCharacterId = "JAMES_AND_JUDE",
        badgeLabel = "Temple Scroll",
        defaultStage = ThreeDEnvironmentStage.JERUSALEM_TEMPLE_COLONNADE_3D
    ),
    MIRACLE_LOAVES_AND_CHALICE_3D(
        title = "Philip & the Miracle of the Loaves",
        primaryCharacterId = "PHILIP",
        secondaryCharacterId = "ANDREW",
        badgeLabel = "Sacred Banquet",
        defaultStage = ThreeDEnvironmentStage.UPPER_ROOM_CHALICE_TABLE_3D
    ),
    PRAYER_AND_LAMP_OF_FAITH_3D(
        title = "Thomas & Nathanael in Watchful Prayer",
        primaryCharacterId = "THOMAS",
        secondaryCharacterId = "NATHANAEL",
        badgeLabel = "Lamp of Prayer",
        defaultStage = ThreeDEnvironmentStage.MOUNT_OF_OLIVES_GROVE_3D
    ),
    CROSS_AND_RESURRECTION_TOMB_3D(
        title = "Mary Magdalene at the Risen Tomb",
        primaryCharacterId = "MARY_MAGDALENE",
        secondaryCharacterId = "JESUS",
        badgeLabel = "Risen Victory",
        defaultStage = ThreeDEnvironmentStage.EMPTY_TOMB_RESURRECTION_DAWN_3D
    ),
    APOCALYPTIC_LAMB_AND_NEW_JERUSALEM_3D(
        title = "Michael & John Before the Heavenly City",
        primaryCharacterId = "MICHAEL_ARCHANGEL",
        secondaryCharacterId = "JOHN",
        badgeLabel = "Heavenly Vision",
        defaultStage = ThreeDEnvironmentStage.NEW_JERUSALEM_CRYSTAL_GATES_3D
    ),
    WISE_MAGI_STAR_GIFTS_3D(
        title = "The Wise Magi Bearing Royal Gifts",
        primaryCharacterId = "WISE_MAGI",
        secondaryCharacterId = "JOSEPH_NAZARETH",
        badgeLabel = "Gifts of the Magi",
        defaultStage = ThreeDEnvironmentStage.BETHLEHEM_STAR_GROTTO_3D
    ),
    SAMARITAN_WOMAN_AT_WELL_3D(
        title = "The Woman of Samaria at Jacob's Well",
        primaryCharacterId = "SAMARITAN_WOMAN",
        secondaryCharacterId = "JESUS",
        badgeLabel = "Living Water",
        defaultStage = ThreeDEnvironmentStage.SYCHAR_STONE_WELL_3D
    ),
    LAZARUS_RAISED_FROM_TOMB_3D(
        title = "Lazarus, Mary & Martha of Bethany",
        primaryCharacterId = "LAZARUS_BETHANY",
        secondaryCharacterId = "MARTHA_AND_MARY_BETHANY",
        badgeLabel = "Bethany Miracle",
        defaultStage = ThreeDEnvironmentStage.EMPTY_TOMB_RESURRECTION_DAWN_3D
    ),
    ZACCHAEUS_SYCAMORE_TREE_3D(
        title = "Zacchaeus of Jericho & Restitution",
        primaryCharacterId = "ZACCHAEUS_JERICHO",
        secondaryCharacterId = "MATTHEW",
        badgeLabel = "Jericho Encounter",
        defaultStage = ThreeDEnvironmentStage.MOUNT_OF_OLIVES_GROVE_3D
    ),
    LYDIA_AND_PRISCILLA_CHURCH_3D(
        title = "Lydia & Priscilla Teaching the Way",
        primaryCharacterId = "LYDIA_PRISCILLA",
        secondaryCharacterId = "PAUL",
        badgeLabel = "House Church",
        defaultStage = ThreeDEnvironmentStage.AREOPAGUS_MARBLE_COURT_3D
    ),
    STEPHEN_HEAVEN_OPENED_3D(
        title = "Stephen Beholding the Heavens Opened",
        primaryCharacterId = "STEPHEN",
        secondaryCharacterId = "GABRIEL_ARCHANGEL",
        badgeLabel = "Crown of Glory",
        defaultStage = ThreeDEnvironmentStage.DAMASCUS_DESERT_SUNBURST_3D
    ),
    TRANSFIGURATION_MOSES_ELIJAH_3D(
        title = "Moses & Elijah in Mountain Glory",
        primaryCharacterId = "MOSES_AND_ELIJAH",
        secondaryCharacterId = "PETER",
        badgeLabel = "Law & Prophets",
        defaultStage = ThreeDEnvironmentStage.DAMASCUS_DESERT_SUNBURST_3D
    ),
    EVANGELISTS_SCROLL_AND_LION_3D(
        title = "Luke & Mark Recording the Gospel",
        primaryCharacterId = "MARK",
        secondaryCharacterId = "LUKE",
        badgeLabel = "Sacred Codices",
        defaultStage = ThreeDEnvironmentStage.JERUSALEM_TEMPLE_COLONNADE_3D
    ),
    PSALM_DAVID_HARP_WORSHIP_3D(
        title = "King David & Asaph Singing Upon the Golden Harp",
        primaryCharacterId = "KING_DAVID",
        secondaryCharacterId = "ASAPH_MUSICIAN",
        badgeLabel = "Psalms of Zion",
        defaultStage = ThreeDEnvironmentStage.PSALM_GREEN_PASTURES_HARP_3D
    ),
    PSALM_SHEPHERD_STILL_WATERS_3D(
        title = "David & The Good Shepherd Beside Still Waters",
        primaryCharacterId = "KING_DAVID",
        secondaryCharacterId = "JESUS",
        badgeLabel = "Green Pastures",
        defaultStage = ThreeDEnvironmentStage.PSALM_GREEN_PASTURES_HARP_3D
    ),
    PROVERBS_SOLOMON_ROYAL_WISDOM_3D(
        title = "King Solomon & Lady Wisdom in the Seven-Pillared Court",
        primaryCharacterId = "KING_SOLOMON",
        secondaryCharacterId = "LADY_WISDOM",
        badgeLabel = "Royal Proverbs",
        defaultStage = ThreeDEnvironmentStage.PROVERBS_SEVEN_PILLARS_PALACE_3D
    ),
    PROVERBS_SEVEN_PILLARS_TREASURE_3D(
        title = "Lady Wisdom Bearing Rubies & The Lamp of Understanding",
        primaryCharacterId = "LADY_WISDOM",
        secondaryCharacterId = "KING_SOLOMON",
        badgeLabel = "Wisdom & Rubies",
        defaultStage = ThreeDEnvironmentStage.PROVERBS_SEVEN_PILLARS_PALACE_3D
    )
}

@Immutable
data class Verse3DPictureSpec(
    val bookId: String,
    val chapterNumber: Int,
    val verseNumber: Int,
    val variationIndex: Int, // 0, 1, or 2 (each verse has 3 distinct 3D pictures with completely different characters!)
    val variationLabel: String,
    val customSceneTitle: String,
    val pictureType: Verse3DPictureType,
    val environmentStage: ThreeDEnvironmentStage,
    val primaryCharacter: BiblicalCharacter,
    val secondaryCharacter: BiblicalCharacter?,
    val primaryPoseIndex: Int,
    val secondaryPoseIndex: Int,
    val skyTop: Color,
    val skyBottom: Color,
    val accentGold: Color,
    val jewelRim: Color,
    val lightAngleFraction: Float,
    val cameraZoom: Float
)

@Immutable
data class Chapter3DSceneSlide(
    val slideIndex: Int,
    val badgeTitle: String,
    val sceneTitle: String,
    val sceneDescription: String,
    val leadCharacter: BiblicalCharacter,
    val companionCharacter: BiblicalCharacter,
    val thirdCharacter: BiblicalCharacter?,
    val environmentStage: ThreeDEnvironmentStage,
    val pictureType: Verse3DPictureType
)

object ThreeDBibleArtEngine {

    // Rich distinct 3D jewel-and-gold color themes so every verse & variation looks visually unique
    private val distinct3DPalettes: List<Pair<Color, Color>> = listOf(
        Color(0xFF0E1B3D) to Color(0xFF285C8F), // Royal Lapis & Sapphire
        Color(0xFF2B102F) to Color(0xFF7D244F), // Imperial Tyrian Crimson & Plum
        Color(0xFF0B2926) to Color(0xFF1E6E5C), // Galilean Emerald & Jade
        Color(0xFF2E1B0E) to Color(0xFF8C4F1F), // Judean Amber & Bronze
        Color(0xFF1A1238) to Color(0xFF523485), // Celestial Amethyst & Indigo
        Color(0xFF0D2638) to Color(0xFF1F6E8C), // Jordan Turquoise & Cerulean
        Color(0xFF33141E) to Color(0xFF94293E), // Calvary Rose-Garnet & Ruby
        Color(0xFF192918) to Color(0xFF3D6E38), // Olive Grove & Cypress Green
        Color(0xFF292010) to Color(0xFF806324), // Temple Gold & Byzantine Ochre
        Color(0xFF121C36) to Color(0xFF3B4F8A)  // Starlight Midnight & Cobalt
    )

    private val goldTones: List<Color> = listOf(
        Color(0xFFFFD86B),
        Color(0xFFFFE57F),
        Color(0xFFF5C451),
        Color(0xFFFFCA61),
        Color(0xFFFFDF78),
        Color(0xFFF7B948)
    )

    private val chapterSlidesCache = java.util.concurrent.ConcurrentHashMap<String, List<Chapter3DSceneSlide>>(64)
    private val verseVariationsCache = java.util.concurrent.ConcurrentHashMap<String, List<Verse3DPictureSpec>>(512)

    /**
     * Generates 6 completely different 3D character & story pictures for every chapter,
     * with ENTIRELY DIFFERENT lead & companion characters in every single picture!
     */
    fun getChapter3DSlides(metadata: ChapterArtMetadata): List<Chapter3DSceneSlide> {
        val cacheKey = "${metadata.bookId}_${metadata.chapterNumber}"
        chapterSlidesCache[cacheKey]?.let { return it }

        val allChars = BiblicalCharactersCatalog.allCharacters
        val chapterCast = BiblicalCharactersCatalog.getCharactersForChapter(
            metadata.bookId,
            metadata.chapterNumber
        )

        // Build a 12-character chapter pool starting with the chapter's specific cast,
        // supplemented deterministically so we have 12 completely distinct characters for 6 slides!
        val pool = ArrayList<BiblicalCharacter>(12)
        pool.addAll(chapterCast)
        val startOffset = abs(metadata.bookId.hashCode() + metadata.chapterNumber * 7) % allChars.size
        for (i in allChars.indices) {
            if (pool.size >= 12) break
            val candidate = allChars[(startOffset + i) % allChars.size]
            if (pool.none { it.id == candidate.id }) {
                pool.add(candidate)
            }
        }

        val stages = ThreeDEnvironmentStage.entries
        val baseStageIdx = abs(metadata.patternSeed + metadata.chapterNumber * 3) % stages.size
        val primaryChapterStage = when (metadata.bookId) {
            "PSA" -> ThreeDEnvironmentStage.PSALM_GREEN_PASTURES_HARP_3D
            "PRO" -> ThreeDEnvironmentStage.PROVERBS_SEVEN_PILLARS_PALACE_3D
            else -> stages[baseStageIdx % stages.size]
        }
        val primaryChapterPictureType = when (metadata.bookId) {
            "PSA" -> Verse3DPictureType.PSALM_DAVID_HARP_WORSHIP_3D
            "PRO" -> Verse3DPictureType.PROVERBS_SOLOMON_ROYAL_WISDOM_3D
            else -> Verse3DPictureType.JESUS_TEACHING_3D
        }

        return listOf(
            Chapter3DSceneSlide(
                slideIndex = 0,
                badgeTitle = "3D Picture I • Chapter Panorama",
                sceneTitle = metadata.artTitle,
                sceneDescription = "${pool[0].name} & ${pool[1].name} in ${metadata.sceneArchetype.displayName}",
                leadCharacter = pool[0],
                companionCharacter = pool[1],
                thirdCharacter = pool[2],
                environmentStage = primaryChapterStage,
                pictureType = primaryChapterPictureType
            ),
            Chapter3DSceneSlide(
                slideIndex = 1,
                badgeTitle = "3D Picture II • Apostolic Witness",
                sceneTitle = "${pool[2].name} & ${pool[3].name}",
                sceneDescription = "${pool[2].sacredAttribute} • ${pool[3].title}",
                leadCharacter = pool[2],
                companionCharacter = pool[3],
                thirdCharacter = null,
                environmentStage = stages[(baseStageIdx + 2) % stages.size],
                pictureType = Verse3DPictureType.PETER_AND_FISHERMEN_3D
            ),
            Chapter3DSceneSlide(
                slideIndex = 2,
                badgeTitle = "3D Picture III • Sacred Fellowship",
                sceneTitle = "${pool[4].name} & ${pool[5].name}",
                sceneDescription = "Witnesses of faith in ${metadata.bookName} ${metadata.chapterNumber} (${pool[4].sacredAttribute})",
                leadCharacter = pool[4],
                companionCharacter = pool[5],
                thirdCharacter = pool[6],
                environmentStage = stages[(baseStageIdx + 4) % stages.size],
                pictureType = Verse3DPictureType.JOHN_AND_BELOVED_DISCIPLES_3D
            ),
            Chapter3DSceneSlide(
                slideIndex = 3,
                badgeTitle = "3D Picture IV • Miracle & Covenant",
                sceneTitle = "${pool[6].name} & ${pool[7].name}",
                sceneDescription = "${pool[6].title} • ${metadata.keyThemeTag}",
                leadCharacter = pool[6],
                companionCharacter = pool[7],
                thirdCharacter = null,
                environmentStage = stages[(baseStageIdx + 6) % stages.size],
                pictureType = Verse3DPictureType.MIRACLE_LOAVES_AND_CHALICE_3D
            ),
            Chapter3DSceneSlide(
                slideIndex = 4,
                badgeTitle = "3D Picture V • Prophetic & Angelic Vision",
                sceneTitle = "${pool[8].name} & ${pool[9].name}",
                sceneDescription = "${pool[8].sacredAttribute} & ${pool[9].sacredAttribute}",
                leadCharacter = pool[8],
                companionCharacter = pool[9],
                thirdCharacter = pool[10],
                environmentStage = stages[(baseStageIdx + 8) % stages.size],
                pictureType = Verse3DPictureType.ARCHANGEL_HEAVENLY_HOST_3D
            ),
            Chapter3DSceneSlide(
                slideIndex = 5,
                badgeTitle = "3D Picture VI • Kingdom Glory",
                sceneTitle = "${pool[10].name} & ${pool[11].name}",
                sceneDescription = metadata.visualMeditation,
                leadCharacter = pool[10],
                companionCharacter = pool[11],
                thirdCharacter = pool[0],
                environmentStage = stages[(baseStageIdx + 10) % stages.size],
                pictureType = Verse3DPictureType.APOCALYPTIC_LAMB_AND_NEW_JERUSALEM_3D
            )
        ).also { slides ->
            if (chapterSlidesCache.size > 128) chapterSlidesCache.clear()
            chapterSlidesCache[cacheKey] = slides
        }
    }

    /**
     * Returns 3 COMPLETELY DIFFERENT 3D character & story pictures for EVERY SINGLE VERSE,
     * where:
     * 1) Each of the 3 variations for a verse uses ENTIRELY DIFFERENT characters (6 distinct characters across the 3 variations!).
     * 2) Every verse number (v1, v2, v3, v4...) rotates characters, 3D stages, poses, and palettes so no two verses in a chapter ever look the same!
     */
    fun getVerse3DPictureVariations(
        verse: VerseItem,
        metadata: ChapterArtMetadata
    ): List<Verse3DPictureSpec> {
        val cacheKey = "${verse.bookId}_${verse.chapterNumber}_${verse.verseNumber}"
        verseVariationsCache[cacheKey]?.let { return it }

        val allChars = BiblicalCharactersCatalog.allCharacters
        val totalChars = allChars.size // 34 distinct characters
        val allTypes = Verse3DPictureType.entries
        val allStages = ThreeDEnvironmentStage.entries

        // 1. Detect primary story archetype from the verse's specific biblical text
        val baseType = matchPrimaryVerseStoryType(verse)
        val matchedPrimaryChar = BiblicalCharactersCatalog.getCharacterById(baseType.primaryCharacterId)
        val matchedSecondaryChar = baseType.secondaryCharacterId?.let {
            BiblicalCharactersCatalog.getCharacterById(it)
        }

        // 2. Compute a unique per-verse step so consecutive verses (v1, v2, v3...) NEVER collide
        // even if their text doesn't mention a named character!
        val bookHash = abs(verse.bookId.hashCode())
        val verseUniqueStride = (bookHash + verse.chapterNumber * 13 + verse.verseNumber * 5) % totalChars

        val prevVerseStride = (bookHash + verse.chapterNumber * 13 + (verse.verseNumber - 1).coerceAtLeast(0) * 5) % totalChars
        val nextVerseStride = (bookHash + verse.chapterNumber * 13 + (verse.verseNumber + 1) * 5) % totalChars

        // Determine Variation 0's characters:
        // Honor matchedPrimaryChar on odd verses when it doesn't collide with adjacent even verses' stride characters
        val hasExplicitKeyword = hasExplicitStoryKeyword(verse.text)
        val canUseMatchedPrimary = hasExplicitKeyword &&
            verse.verseNumber % 2 == 1 &&
            matchedPrimaryChar.id != allChars[prevVerseStride].id &&
            matchedPrimaryChar.id != allChars[nextVerseStride].id

        val char1 = if (canUseMatchedPrimary) {
            matchedPrimaryChar
        } else {
            allChars[verseUniqueStride % totalChars]
        }

        val char2Candidate = if (canUseMatchedPrimary && matchedSecondaryChar != null && matchedSecondaryChar.id != char1.id) {
            matchedSecondaryChar
        } else {
            allChars[(verseUniqueStride + 7 + (verse.verseNumber % 5)) % totalChars]
        }
        val char2 = if (char2Candidate.id != char1.id) char2Candidate else allChars[(verseUniqueStride + 1) % totalChars]

        // Pick 4 more COMPLETELY DISJOINT characters for Variation 1 (char3, char4) and Variation 2 (char5, char6)
        // so that all 3 pictures on this verse have 6 entirely different characters!
        val usedIds = HashSet<String>(6)
        usedIds.add(char1.id)
        usedIds.add(char2.id)

        fun pickNextDistinctChar(seedOffset: Int): BiblicalCharacter {
            for (step in 0 until totalChars) {
                val candidate = allChars[(verseUniqueStride + seedOffset + step) % totalChars]
                if (usedIds.add(candidate.id)) {
                    return candidate
                }
            }
            return allChars[seedOffset % totalChars]
        }

        val char3 = pickNextDistinctChar(3)
        val char4 = pickNextDistinctChar(9)
        val char5 = pickNextDistinctChar(14)
        val char6 = pickNextDistinctChar(21)

        // 3 distinct 3D environment stages for the 3 variations of this verse
        val stage0 = if (hasExplicitKeyword) {
            allStages[(baseType.defaultStage.ordinal + (verse.verseNumber % 3)) % allStages.size]
        } else {
            allStages[(verse.chapterNumber * 3 + verse.verseNumber) % allStages.size]
        }
        val stage1 = allStages[(stage0.ordinal + 4) % allStages.size]
        val stage2 = allStages[(stage0.ordinal + 8) % allStages.size]

        // 3 distinct Verse3DPictureTypes
        val type0 = if (hasExplicitKeyword) baseType else allTypes[(verse.chapterNumber + verse.verseNumber) % allTypes.size]
        val type1 = allTypes[(type0.ordinal + 5 + (verse.verseNumber % 7)) % allTypes.size]
        val type2 = allTypes[(type0.ordinal + 11 + (verse.verseNumber % 5)) % allTypes.size]

        // 3 distinct 3D lighting palettes
        val palIdx0 = (verse.chapterNumber * 3 + verse.verseNumber) % distinct3DPalettes.size
        val palIdx1 = (palIdx0 + 3) % distinct3DPalettes.size
        val palIdx2 = (palIdx0 + 6) % distinct3DPalettes.size

        val pal0 = distinct3DPalettes[palIdx0]
        val pal1 = distinct3DPalettes[palIdx1]
        val pal2 = distinct3DPalettes[palIdx2]

        return listOf(
            Verse3DPictureSpec(
                bookId = verse.bookId,
                chapterNumber = verse.chapterNumber,
                verseNumber = verse.verseNumber,
                variationIndex = 0,
                variationLabel = "3D Scene A",
                customSceneTitle = "${char1.name.substringBefore(" (")} & ${char2.name.substringBefore(" (")} • ${stage0.label.removePrefix("3D ")}",
                pictureType = type0,
                environmentStage = stage0,
                primaryCharacter = char1,
                secondaryCharacter = char2,
                primaryPoseIndex = (verse.verseNumber) % 6,
                secondaryPoseIndex = (verse.verseNumber + 2) % 6,
                skyTop = pal0.first,
                skyBottom = pal0.second,
                accentGold = goldTones[verse.verseNumber % goldTones.size],
                jewelRim = lerp(pal0.second, metadata.palette.primaryJewel, 0.4f),
                lightAngleFraction = ((verse.verseNumber * 19) % 100) / 100f,
                cameraZoom = 0.96f + ((verse.verseNumber % 4) * 0.04f)
            ),
            Verse3DPictureSpec(
                bookId = verse.bookId,
                chapterNumber = verse.chapterNumber,
                verseNumber = verse.verseNumber,
                variationIndex = 1,
                variationLabel = "3D Scene B",
                customSceneTitle = "${char3.name.substringBefore(" (")} & ${char4.name.substringBefore(" (")} • ${stage1.label.removePrefix("3D ")}",
                pictureType = type1,
                environmentStage = stage1,
                primaryCharacter = char3,
                secondaryCharacter = char4,
                primaryPoseIndex = (verse.verseNumber + 1) % 6,
                secondaryPoseIndex = (verse.verseNumber + 4) % 6,
                skyTop = pal1.first,
                skyBottom = pal1.second,
                accentGold = goldTones[(verse.verseNumber + 2) % goldTones.size],
                jewelRim = lerp(pal1.second, metadata.palette.secondaryJewel, 0.4f),
                lightAngleFraction = ((verse.verseNumber * 37 + 25) % 100) / 100f,
                cameraZoom = 1.02f
            ),
            Verse3DPictureSpec(
                bookId = verse.bookId,
                chapterNumber = verse.chapterNumber,
                verseNumber = verse.verseNumber,
                variationIndex = 2,
                variationLabel = "3D Scene C",
                customSceneTitle = "${char5.name.substringBefore(" (")} & ${char6.name.substringBefore(" (")} • ${stage2.label.removePrefix("3D ")}",
                pictureType = type2,
                environmentStage = stage2,
                primaryCharacter = char5,
                secondaryCharacter = char6,
                primaryPoseIndex = (verse.verseNumber + 3) % 6,
                secondaryPoseIndex = (verse.verseNumber + 5) % 6,
                skyTop = pal2.first,
                skyBottom = pal2.second,
                accentGold = goldTones[(verse.verseNumber + 4) % goldTones.size],
                jewelRim = lerp(pal2.second, metadata.palette.goldLeaf, 0.35f),
                lightAngleFraction = ((verse.verseNumber * 53 + 50) % 100) / 100f,
                cameraZoom = 0.94f
            )
        ).also { specs ->
            if (verseVariationsCache.size > 1024) verseVariationsCache.clear()
            verseVariationsCache[cacheKey] = specs
        }
    }

    /**
     * Convenience helper returning the primary 3D picture spec for a verse (Variation 0).
     */
    fun resolveVerse3DPicture(
        verse: VerseItem,
        metadata: ChapterArtMetadata
    ): Verse3DPictureSpec {
        return getVerse3DPictureVariations(verse, metadata).first()
    }

    private fun hasExplicitStoryKeyword(text: String): Boolean {
        val t = text.lowercase()
        val keywords = listOf(
            "cross", "crucif", "risen", "resurrection", "tomb", "bapti", "jordan", "mary",
            "joseph", "magi", "star", "frankincense", "angel", "gabriel", "michael", "bread",
            "loaves", "cup", "peter", "simon", "boat", "sea", "fish", "heal", "blind",
            "leper", "shepherd", "sheep", "lamb", "lazarus", "martha", "bethany", "zacchaeus",
            "sycamore", "well", "water", "samaria", "nicodemus", "born", "centurion", "soldier",
            "cornelius", "lydia", "purple", "stephen", "moses", "elijah", "transfigur", "paul",
            "damascus", "throne", "new jerusalem", "trumpet", "harp", "psalm", "psaltery",
            "cymbal", "zion", "refuge", "pasture", "david", "solomon", "proverb", "wisdom",
            "understanding", "rubies", "pillars", "honeycomb", "virtuous"
        )
        return keywords.any { t.contains(it) }
    }

    private fun matchPrimaryVerseStoryType(verse: VerseItem): Verse3DPictureType {
        val t = verse.text.lowercase()
        if (verse.bookId == "PSA") {
            return when {
                t.contains("shepherd") || t.contains("pasture") || t.contains("still waters") || t.contains("deer") || t.contains("hart") ->
                    Verse3DPictureType.PSALM_SHEPHERD_STILL_WATERS_3D
                t.contains("harp") || t.contains("psaltery") || t.contains("sing") || t.contains("praise") || t.contains("cymbal") || verse.verseNumber % 2 == 1 ->
                    Verse3DPictureType.PSALM_DAVID_HARP_WORSHIP_3D
                else -> Verse3DPictureType.PSALM_SHEPHERD_STILL_WATERS_3D
            }
        }
        if (verse.bookId == "PRO") {
            return when {
                t.contains("rubies") || t.contains("virtuous") || t.contains("pillars") || t.contains("treasure") || t.contains("silver") ->
                    Verse3DPictureType.PROVERBS_SEVEN_PILLARS_TREASURE_3D
                else -> Verse3DPictureType.PROVERBS_SOLOMON_ROYAL_WISDOM_3D
            }
        }
        return when {
            t.contains("harp") || t.contains("psaltery") || t.contains("david") ->
                Verse3DPictureType.PSALM_DAVID_HARP_WORSHIP_3D
            t.contains("solomon") || t.contains("rubies") || t.contains("seven pillars") || t.contains("proverb") ->
                Verse3DPictureType.PROVERBS_SOLOMON_ROYAL_WISDOM_3D
            t.contains("magi") || t.contains("frankincense") || t.contains("myrrh") || t.contains("east") || t.contains("herod") ->
                Verse3DPictureType.WISE_MAGI_STAR_GIFTS_3D
            t.contains("lazarus") || t.contains("martha") || t.contains("bethany") ->
                Verse3DPictureType.LAZARUS_RAISED_FROM_TOMB_3D
            t.contains("zacchaeus") || t.contains("sycamore") || t.contains("jericho") || t.contains("tax") ->
                Verse3DPictureType.ZACCHAEUS_SYCAMORE_TREE_3D
            t.contains("well") || t.contains("samaria") || t.contains("thirst") || t.contains("living water") ->
                Verse3DPictureType.SAMARITAN_WOMAN_AT_WELL_3D
            t.contains("nicodemus") || t.contains("born again") || t.contains("born anew") || t.contains("pharisee") || t.contains("ruler") ->
                Verse3DPictureType.SCRIBES_AND_TEMPLE_ELDERS_3D
            t.contains("moses") || t.contains("elijah") || t.contains("shone") || t.contains("transfigur") || t.contains("law and the prophets") ->
                Verse3DPictureType.TRANSFIGURATION_MOSES_ELIJAH_3D
            t.contains("lydia") || t.contains("priscilla") || t.contains("purple") || t.contains("philippi") ->
                Verse3DPictureType.LYDIA_AND_PRISCILLA_CHURCH_3D
            t.contains("stephen") || t.contains("martyr") || t.contains("right hand of god") || t.contains("stoned") ->
                Verse3DPictureType.STEPHEN_HEAVEN_OPENED_3D
            t.contains("cross") || t.contains("crucif") || t.contains("risen") || t.contains("resurrection") || t.contains("tomb") ->
                Verse3DPictureType.CROSS_AND_RESURRECTION_TOMB_3D
            t.contains("bapti") || t.contains("jordan") || t.contains("wilderness") || t.contains("repent") ->
                Verse3DPictureType.JOHN_BAPTIST_PROPHET_3D
            t.contains("mary") || t.contains("joseph") || t.contains("virgin") || t.contains("manger") || t.contains("bethlehem") ->
                Verse3DPictureType.MARY_AND_HOLY_WOMEN_3D
            t.contains("angel") || t.contains("gabriel") || t.contains("heavenly host") || t.contains("dream") ->
                Verse3DPictureType.ARCHANGEL_HEAVENLY_HOST_3D
            t.contains("bread") || t.contains("loaves") || t.contains("cup") || t.contains("wine") || t.contains("supper") ->
                Verse3DPictureType.MIRACLE_LOAVES_AND_CHALICE_3D
            t.contains("peter") || t.contains("simon") || t.contains("sea") || t.contains("boat") || t.contains("ship") || t.contains("fish") || t.contains("waves") ->
                Verse3DPictureType.PETER_AND_FISHERMEN_3D
            t.contains("heal") || t.contains("sick") || t.contains("blind") || t.contains("mercy") || t.contains("compassion") || t.contains("leper") ->
                Verse3DPictureType.JESUS_HEALING_COMPASSION_3D
            t.contains("sheep") || t.contains("shepherd") || t.contains("flock") || t.contains("pasture") ->
                Verse3DPictureType.JESUS_GOOD_SHEPHERD_3D
            t.contains("pray") || t.contains("ask") || t.contains("seek") || t.contains("knock") || t.contains("lamp") ->
                Verse3DPictureType.PRAYER_AND_LAMP_OF_FAITH_3D
            t.contains("centurion") || t.contains("cornelius") || t.contains("soldier") || t.contains("armor") || t.contains("shield") ->
                Verse3DPictureType.ROMAN_CENTURION_AND_SOLDIERS_3D
            t.contains("dragon") || t.contains("michael") || t.contains("new jerusalem") || t.contains("throne") || t.contains("lamb") ->
                Verse3DPictureType.APOCALYPTIC_LAMB_AND_NEW_JERUSALEM_3D
            else -> {
                val all = Verse3DPictureType.entries
                all[(abs(verse.bookId.hashCode()) + verse.chapterNumber * 7 + verse.verseNumber * 3) % all.size]
            }
        }
    }
}

/**
 * Hardware-accelerated, zero-lag Illustrated Bible Verse Character & Story Vignette.
 * Uses [Modifier.drawWithCache] so all royal-blue & beveled-gold storybook frames,
 * cascading green corner vines, 4-pointed golden star sparkles, luminous sky-blue stage backdrops,
 * and multi-character Left & Right biblical ensembles are cached on the GPU for 60fps scrolling.
 */
@Composable
fun ThreeDVerseIllustration(
    spec: Verse3DPictureSpec,
    modifier: Modifier = Modifier
) {
    val cacheKey = remember(
        spec.bookId,
        spec.chapterNumber,
        spec.verseNumber,
        spec.variationIndex
    ) {
        "${spec.bookId}_${spec.chapterNumber}_${spec.verseNumber}_${spec.variationIndex}"
    }

    Box(
        modifier = modifier
            .drawWithCache {
                val _k = cacheKey
                val w = size.width
                val h = size.height

                onDrawBehind {
                    drawStorybookIllustratedBibleScene(
                        w = w,
                        h = h,
                        leadCharacter = spec.primaryCharacter,
                        companionCharacter = spec.secondaryCharacter ?: BiblicalCharactersCatalog.allCharacters.first(),
                        thirdCharacter = null,
                        environmentStage = spec.environmentStage,
                        pictureType = spec.pictureType,
                        sceneVariantSeed = spec.chapterNumber * 31 + spec.verseNumber * 7 + spec.variationIndex * 13,
                        variationIndex = spec.variationIndex,
                        accentGold = spec.accentGold
                    )
                }
            }
    )
}

/**
 * Hardware-accelerated Storybook Illustrated Chapter Carousel Scene for the 6 distinct chapter slides.
 */
@Composable
fun ThreeDChapterCarouselScene(
    slide: Chapter3DSceneSlide,
    metadata: ChapterArtMetadata,
    artStyleMode: ArtStyleMode,
    modifier: Modifier = Modifier
) {
    val rememberSlideKey = remember(metadata.bookId, metadata.chapterNumber, slide.slideIndex, artStyleMode) {
        "${metadata.bookId}_${metadata.chapterNumber}_${slide.slideIndex}_${artStyleMode.name}"
    }

    Box(
        modifier = modifier
            .drawWithCache {
                val _key = rememberSlideKey
                val w = size.width
                val h = size.height

                onDrawBehind {
                    drawStorybookIllustratedBibleScene(
                        w = w,
                        h = h,
                        leadCharacter = slide.leadCharacter,
                        companionCharacter = slide.companionCharacter,
                        thirdCharacter = slide.thirdCharacter,
                        environmentStage = slide.environmentStage,
                        pictureType = slide.pictureType,
                        sceneVariantSeed = metadata.patternSeed + metadata.chapterNumber * 19 + slide.slideIndex * 11,
                        variationIndex = slide.slideIndex,
                        accentGold = metadata.palette.goldLeaf
                    )
                }
            }
    )
}

/**
 * Complete Storybook Illustrated Bible Scene renderer matching the classic illustrated Bible reference style:
 * 1. Royal Azure/Cerulean Blue Outer Sanctuary Frame with thin gold border lines and 4-point golden star sparkles.
 * 2. Rounded-Rectangle Luminous Sky-Blue Inner Panel (#D4EFFC to #F2FAFF) with subtle stage architecture & soft horizon.
 * 3. Multi-Character Left Biblical Group (Lead Teacher/Apostle/King + supporting disciple/woman/child, open scroll or teaching pose)
 *    and Multi-Character Right Biblical Group (Temple High Priest in crimson/gold & Pharisee Elder in purple, OR multi-generational
 *    listening crowd with praying hands, woman in veil, and child in sky-blue tunic) facing each other across the luminous stage.
 * 4. Beveled 3D Metallic Gold Inner Frame with lush green-and-teal botanical corner leaf vines cascading down the top-left and top-right corners.
 */
fun DrawScope.drawStorybookIllustratedBibleScene(
    w: Float,
    h: Float,
    leadCharacter: BiblicalCharacter,
    companionCharacter: BiblicalCharacter,
    thirdCharacter: BiblicalCharacter?,
    environmentStage: ThreeDEnvironmentStage,
    pictureType: Verse3DPictureType,
    sceneVariantSeed: Int,
    variationIndex: Int,
    accentGold: Color
) {
    val minDim = min(w, h)
    val framePadX = (w * 0.048f).coerceAtLeast(6f)
    val framePadY = (h * 0.068f).coerceAtLeast(6f)
    val innerLeft = framePadX
    val innerTop = framePadY
    val innerW = (w - framePadX * 2f).coerceAtLeast(10f)
    val innerH = (h - framePadY * 2f).coerceAtLeast(10f)
    val innerBottom = innerTop + innerH
    val cornerR = (minDim * 0.068f).coerceIn(8f, 26f)

    // 1. Royal Azure / Cerulean Blue Outer Sanctuary Border (matches attached reference frame)
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1976D2),
                Color(0xFF1E88E5),
                Color(0xFF1565C0)
            )
        ),
        size = Size(w, h)
    )

    // Thin vertical & horizontal gold pinstripe lines along the outer blue border
    val outerPinInset = (framePadX * 0.34f).coerceAtLeast(2.5f)
    drawRect(
        color = Color(0xFFE6B84A).copy(alpha = 0.72f),
        topLeft = Offset(outerPinInset, outerPinInset),
        size = Size((w - outerPinInset * 2f).coerceAtLeast(4f), (h - outerPinInset * 2f).coerceAtLeast(4f)),
        style = Stroke(width = (minDim * 0.005f).coerceIn(1f, 2.2f))
    )

    // 2. Luminous Sky-Blue / Ice-Blue Rounded Inner Stage Panel
    val skyTintShift = (abs(sceneVariantSeed) % 5) * 0.03f
    val skyTopColor = lerp(Color(0xFFC9EAFC), Color(0xFFD6F1FF), skyTintShift)
    val skyMidColor = Color(0xFFE6F6FF)
    val skyCenterGlow = Color(0xFFF5FCFF)

    drawRoundRect(
        brush = Brush.verticalGradient(
            0.0f to skyTopColor,
            0.52f to skyMidColor,
            0.84f to skyCenterGlow,
            1.0f to Color(0xFFD8EEFA),
            startY = innerTop,
            endY = innerBottom
        ),
        topLeft = Offset(innerLeft, innerTop),
        size = Size(innerW, innerH),
        cornerRadius = CornerRadius(cornerR, cornerR)
    )

    // Soft radial sunlight bloom in upper-center of the sky-blue panel
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.75f),
                Color(0xFFFFF9C4).copy(alpha = 0.32f),
                Color.Transparent
            ),
            center = Offset(w * 0.50f, innerTop + innerH * 0.34f),
            radius = innerW * 0.48f
        ),
        radius = innerW * 0.48f,
        center = Offset(w * 0.50f, innerTop + innerH * 0.34f)
    )

    // 3. Subtle Airy Sky-Blue Biblical Background Stage Details (never dark or muddy)
    drawStorybookAiryStageDetails(
        innerLeft = innerLeft,
        innerTop = innerTop,
        innerW = innerW,
        innerH = innerH,
        stage = environmentStage,
        gold = accentGold
    )

    // 4. Multi-Character Left & Right Biblical Ensembles Facing Each Other
    val ensembleMode = abs(sceneVariantSeed + variationIndex) % 3
    drawStorybookMultiCharacterEnsembles(
        innerLeft = innerLeft,
        innerTop = innerTop,
        innerW = innerW,
        innerH = innerH,
        leadCharacter = leadCharacter,
        companionCharacter = companionCharacter,
        thirdCharacter = thirdCharacter,
        pictureType = pictureType,
        ensembleMode = ensembleMode,
        sceneVariantSeed = sceneVariantSeed,
        variationIndex = variationIndex,
        accentGold = accentGold
    )

    // 5. Mask any character torso overflow below the inner rounded panel with the royal-blue bottom border
    if (innerBottom < h) {
        drawRect(
            brush = Brush.verticalGradient(
                listOf(Color(0xFF1976D2), Color(0xFF1565C0)),
                startY = innerBottom,
                endY = h
            ),
            topLeft = Offset(0f, innerBottom),
            size = Size(w, h - innerBottom)
        )
        drawLine(
            color = Color(0xFFE6B84A).copy(alpha = 0.72f),
            start = Offset(outerPinInset, h - outerPinInset),
            end = Offset(w - outerPinInset, h - outerPinInset),
            strokeWidth = (minDim * 0.005f).coerceIn(1f, 2.2f)
        )
    }

    // 6. Beveled 3D Metallic Gold Inner Frame with Crisp Sepia Edges
    val goldStrokeW = (minDim * 0.022f).coerceIn(2.6f, 7.5f)
    // Outer dark sepia contour
    drawRoundRect(
        color = Color(0xFF3E2723).copy(alpha = 0.80f),
        topLeft = Offset(innerLeft - goldStrokeW * 0.45f, innerTop - goldStrokeW * 0.45f),
        size = Size(innerW + goldStrokeW * 0.90f, innerH + goldStrokeW * 0.90f),
        cornerRadius = CornerRadius(cornerR + 2f, cornerR + 2f),
        style = Stroke(width = (goldStrokeW * 0.38f).coerceAtLeast(1.2f))
    )
    // Main 3D metallic gold bevel bar
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFF3B0),
                Color(0xFFE5B53B),
                Color(0xFFB88018),
                Color(0xFFFFE57F),
                Color(0xFF9E6B12)
            ),
            start = Offset(innerLeft, innerTop),
            end = Offset(innerLeft + innerW, innerBottom)
        ),
        topLeft = Offset(innerLeft, innerTop),
        size = Size(innerW, innerH),
        cornerRadius = CornerRadius(cornerR, cornerR),
        style = Stroke(width = goldStrokeW)
    )
    // Specular inner gold highlight rim
    drawRoundRect(
        color = Color(0xFFFFFDE7).copy(alpha = 0.85f),
        topLeft = Offset(innerLeft + goldStrokeW * 0.35f, innerTop + goldStrokeW * 0.35f),
        size = Size(innerW - goldStrokeW * 0.70f, innerH - goldStrokeW * 0.70f),
        cornerRadius = CornerRadius((cornerR - 1.5f).coerceAtLeast(4f), (cornerR - 1.5f).coerceAtLeast(4f)),
        style = Stroke(width = (goldStrokeW * 0.25f).coerceAtLeast(0.9f))
    )

    // 7. Lush Green & Teal Botanical Corner Leaf Vines + 4-Pointed Radiant Golden Stars
    drawStorybookCornerVinesAndStars(
        w = w,
        h = h,
        innerLeft = innerLeft,
        innerTop = innerTop,
        innerW = innerW,
        innerH = innerH
    )
}

/**
 * Renders airy, soft sky-blue architectural & landscape accents inside the luminous stage
 * so each of the 14 biblical stages has recognizable environment context while keeping the
 * center sky-blue backdrop clean and bright like the attached reference artwork.
 */
private fun DrawScope.drawStorybookAiryStageDetails(
    innerLeft: Float,
    innerTop: Float,
    innerW: Float,
    innerH: Float,
    stage: ThreeDEnvironmentStage,
    gold: Color
) {
    val innerRight = innerLeft + innerW
    val innerBottom = innerTop + innerH
    val cx = innerLeft + innerW * 0.50f

    // Soft airy cloud wisps in upper sky-blue panel
    val cloudColor = Color.White.copy(alpha = 0.58f)
    for (c in listOf(0.24f to 0.18f, 0.52f to 0.14f, 0.76f to 0.19f)) {
        val cloudX = innerLeft + innerW * c.first
        val cloudY = innerTop + innerH * c.second
        drawOval(
            color = cloudColor,
            topLeft = Offset(cloudX - innerW * 0.09f, cloudY - innerH * 0.035f),
            size = Size(innerW * 0.18f, innerH * 0.07f)
        )
    }

    when (stage) {
        ThreeDEnvironmentStage.JERUSALEM_TEMPLE_COLONNADE_3D,
        ThreeDEnvironmentStage.AREOPAGUS_MARBLE_COURT_3D,
        ThreeDEnvironmentStage.PROVERBS_SEVEN_PILLARS_PALACE_3D -> {
            // Soft alabaster-and-sky-blue classical columns & arch in background
            for (colFrac in listOf(0.14f, 0.36f, 0.64f, 0.86f)) {
                val colX = innerLeft + innerW * colFrac
                val colW = innerW * 0.042f
                drawRect(
                    color = Color(0xFFB8DCF0).copy(alpha = 0.45f),
                    topLeft = Offset(colX - colW * 0.5f, innerTop + innerH * 0.14f),
                    size = Size(colW, innerH * 0.72f)
                )
                drawRoundRect(
                    color = gold.copy(alpha = 0.42f),
                    topLeft = Offset(colX - colW * 0.7f, innerTop + innerH * 0.12f),
                    size = Size(colW * 1.4f, innerH * 0.03f),
                    cornerRadius = CornerRadius(3f, 3f)
                )
            }
        }

        ThreeDEnvironmentStage.GALILEE_SEA_AND_BOAT_3D,
        ThreeDEnvironmentStage.JORDAN_RIVER_WATERFALL_3D,
        ThreeDEnvironmentStage.PSALM_GREEN_PASTURES_HARP_3D -> {
            // Gentle cerulean water horizon & soft pastel hills in lower background
            val hillPath = Path().apply {
                moveTo(innerLeft, innerTop + innerH * 0.72f)
                quadraticTo(innerLeft + innerW * 0.30f, innerTop + innerH * 0.62f, cx, innerTop + innerH * 0.70f)
                quadraticTo(innerLeft + innerW * 0.75f, innerTop + innerH * 0.63f, innerRight, innerTop + innerH * 0.71f)
                lineTo(innerRight, innerBottom)
                lineTo(innerLeft, innerBottom)
                close()
            }
            drawPath(hillPath, color = Color(0xFFB3E0F7).copy(alpha = 0.52f))
        }

        ThreeDEnvironmentStage.BETHLEHEM_STAR_GROTTO_3D,
        ThreeDEnvironmentStage.DAMASCUS_DESERT_SUNBURST_3D,
        ThreeDEnvironmentStage.PATMOS_RAINBOW_THRONE_3D,
        ThreeDEnvironmentStage.NEW_JERUSALEM_CRYSTAL_GATES_3D -> {
            // Subtle golden star/rainbow arch in upper center sky
            drawArc(
                color = gold.copy(alpha = 0.34f),
                startAngle = 195f,
                sweepAngle = 150f,
                useCenter = false,
                topLeft = Offset(innerLeft + innerW * 0.22f, innerTop + innerH * 0.10f),
                size = Size(innerW * 0.56f, innerH * 0.45f),
                style = Stroke(width = (innerH * 0.015f).coerceAtLeast(2f), cap = StrokeCap.Round)
            )
        }

        else -> {
            // Soft distant Judean horizon line
            val hillPath = Path().apply {
                moveTo(innerLeft, innerTop + innerH * 0.76f)
                quadraticTo(cx, innerTop + innerH * 0.68f, innerRight, innerTop + innerH * 0.76f)
                lineTo(innerRight, innerBottom)
                lineTo(innerLeft, innerBottom)
                close()
            }
            drawPath(hillPath, color = Color(0xFFC2E7FA).copy(alpha = 0.45f))
        }
    }
}

/**
 * Draws the lush green-and-teal botanical corner leaf vines at the top-left and top-right corners
 * of the golden frame, plus the 4-pointed radiant golden star sparkles along the frame edges,
 * matching the user's attached reference picture.
 */
fun DrawScope.drawStorybookCornerVinesAndStars(
    w: Float,
    h: Float,
    innerLeft: Float,
    innerTop: Float,
    innerW: Float,
    innerH: Float
) {
    val minDim = min(w, h)
    val innerRight = innerLeft + innerW
    val vineScale = (minDim * 0.16f).coerceIn(14f, 64f)

    // Top-Left & Top-Right Cascading Botanical Leaf Vines
    for (dir in listOf(1f, -1f)) {
        val cornerX = if (dir > 0f) innerLeft else innerRight
        val cornerY = innerTop

        // Main curved vine stem along top corner
        val stemPath = Path().apply {
            moveTo(cornerX + dir * vineScale * 1.15f, cornerY - vineScale * 0.12f)
            quadraticTo(
                cornerX + dir * vineScale * 0.12f,
                cornerY - vineScale * 0.08f,
                cornerX - dir * vineScale * 0.08f,
                cornerY + vineScale * 1.25f
            )
        }
        drawPath(
            path = stemPath,
            color = Color(0xFF1B5E20),
            style = Stroke(width = (vineScale * 0.07f).coerceAtLeast(1.4f), cap = StrokeCap.Round)
        )

        // 7 layered botanical leaves along the corner vine (alternating emerald, bright leaf green, and deep teal-green)
        val leafSpecs = listOf(
            // (offsetXFactor, offsetYFactor, angleDirX, angleDirY, sizeFactor, color)
            floatArrayOf(0.88f, -0.06f, 0.36f, -0.28f, 0.42f),
            floatArrayOf(0.62f, 0.04f, 0.44f, 0.26f, 0.48f),
            floatArrayOf(0.36f, -0.05f, 0.18f, -0.42f, 0.50f),
            floatArrayOf(0.14f, 0.14f, 0.48f, 0.34f, 0.56f),
            floatArrayOf(-0.04f, 0.36f, -0.32f, 0.28f, 0.52f),
            floatArrayOf(0.06f, 0.64f, 0.42f, 0.32f, 0.48f),
            floatArrayOf(-0.06f, 0.92f, 0.24f, 0.44f, 0.42f)
        )
        val leafColors = listOf(
            Color(0xFF43A047),
            Color(0xFF2E7D32),
            Color(0xFF66BB6A),
            Color(0xFF1E6E5C),
            Color(0xFF388E3C),
            Color(0xFF66BB6A),
            Color(0xFF26A69A)
        )

        for (i in leafSpecs.indices) {
            val spec = leafSpecs[i]
            val baseX = cornerX + dir * vineScale * spec[0]
            val baseY = cornerY + vineScale * spec[1]
            val tipX = baseX + dir * vineScale * spec[2]
            val tipY = baseY + vineScale * spec[3]
            val leafW = vineScale * spec[4] * 0.45f
            val midX = (baseX + tipX) * 0.5f
            val midY = (baseY + tipY) * 0.5f
            val perpX = -(tipY - baseY) * 0.38f
            val perpY = (tipX - baseX) * 0.38f

            val leafPath = Path().apply {
                moveTo(baseX, baseY)
                quadraticTo(midX + perpX, midY + perpY, tipX, tipY)
                quadraticTo(midX - perpX, midY - perpY, baseX, baseY)
                close()
            }
            val leafCol = leafColors[i % leafColors.size]
            drawPath(
                path = leafPath,
                brush = Brush.linearGradient(
                    colors = listOf(lerp(leafCol, Color(0xFFB9F6CA), 0.32f), leafCol, lerp(leafCol, Color.Black, 0.28f)),
                    start = Offset(baseX, baseY),
                    end = Offset(tipX, tipY)
                )
            )
            drawPath(
                path = leafPath,
                color = Color(0xFF143D18),
                style = Stroke(width = (leafW * 0.14f).coerceAtLeast(0.9f))
            )
            // Delicate central leaf vein
            drawLine(
                color = Color(0xFFC8E6C9).copy(alpha = 0.72f),
                start = Offset(baseX, baseY),
                end = Offset(baseX + (tipX - baseX) * 0.80f, baseY + (tipY - baseY) * 0.80f),
                strokeWidth = (leafW * 0.10f).coerceAtLeast(0.7f),
                cap = StrokeCap.Round
            )
        }
    }

    // 4-Pointed Radiant Golden Star Sparkles on the Left & Right Frame Borders (matching reference image)
    val starRadius = (minDim * 0.042f).coerceIn(5f, 16f)
    drawFourPointGoldenStar(
        cx = innerLeft * 0.52f,
        cy = innerTop + innerH * 0.50f,
        radius = starRadius
    )
    drawFourPointGoldenStar(
        cx = innerRight + (w - innerRight) * 0.48f,
        cy = innerTop + innerH * 0.38f,
        radius = starRadius
    )
    drawFourPointGoldenStar(
        cx = innerLeft + innerW * 0.50f,
        cy = innerTop + innerH * 0.12f,
        radius = starRadius * 0.78f
    )
}

private fun DrawScope.drawFourPointGoldenStar(
    cx: Float,
    cy: Float,
    radius: Float
) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFFDE7).copy(alpha = 0.85f),
                Color(0xFFFFD54F).copy(alpha = 0.38f),
                Color.Transparent
            ),
            center = Offset(cx, cy),
            radius = radius * 1.45f
        ),
        radius = radius * 1.45f,
        center = Offset(cx, cy)
    )
    val rInner = radius * 0.22f
    val starPath = Path().apply {
        moveTo(cx, cy - radius)
        lineTo(cx + rInner, cy - rInner)
        lineTo(cx + radius, cy)
        lineTo(cx + rInner, cy + rInner)
        lineTo(cx, cy + radius)
        lineTo(cx - rInner, cy + rInner)
        lineTo(cx - radius, cy)
        lineTo(cx - rInner, cy - rInner)
        close()
    }
    drawPath(starPath, color = Color(0xFFFFF9C4))
    drawPath(starPath, color = Color(0xFFFBC02D), style = Stroke(width = 1.0f))
}

/**
 * Pose & Action modes for the Storybook Illustrated Biblical Figures.
 */
enum class StorybookPoseMode {
    TEACHING_OPEN_ARMS,       // Like Top-Left Jesus in reference: open teaching hands, diagonal blue mantle
    READING_OPEN_SCROLL,      // Like Bottom-Left Paul in reference: holding wide open 2-roller parchment scroll with both hands
    HIGH_PRIEST_MITER_STAFF,  // Like Top-Right High Priest in reference: crimson/gold miter & ephod trim, holding staff/open hand
    PHARISEE_ELDER_SHAWL,     // Like Top-Right Elder in reference: white head-shawl, purple robe, white tallit, hand on chest
    PRAYING_CLASPED_HANDS,    // Like Bottom-Right Young Men in reference: both hands folded together in prayer at chest
    MOTHER_WITH_VEIL,         // Like Top-Left / Bottom-Right Biblical Woman in draped veil & tunic
    CHILD_FOREGROUND,         // Like Top-Left / Bottom-Right Young Child in foreground tunic
    SACRED_ATTRIBUTE_POSE     // Character-specific sacred prop (Golden Harp for David, Scepter for Solomon, Keys for Peter, etc.)
}

/**
 * Renders the multi-character Left & Right Biblical Ensembles inside the rounded golden frame,
 * reproducing the rich multi-figure storytelling compositions of the attached reference picture:
 * - Left Group: Lead character in foreground (e.g. Jesus teaching, OR Paul reading open two-roller scroll,
 *   OR David with golden harp, OR Solomon in crown) flanked by 1–3 supporting companions (bearded disciple,
 *   woman in olive headscarf, child in orange tunic).
 * - Right Group: Either the Temple Elders & High Priest ensemble (High Priest in crimson/gold miter & ephod +
 *   Elder in purple robe & white prayer shawl + companion) OR the Multi-Generational Listening Crowd ensemble
 *   (young men in blue & ochre tunics with clasped praying hands, bearded listener, mother in white veil, and
 *   young boy in sky-blue tunic), with the verse/chapter's specific [companionCharacter] featured prominently!
 */
private fun DrawScope.drawStorybookMultiCharacterEnsembles(
    innerLeft: Float,
    innerTop: Float,
    innerW: Float,
    innerH: Float,
    leadCharacter: BiblicalCharacter,
    companionCharacter: BiblicalCharacter,
    thirdCharacter: BiblicalCharacter?,
    pictureType: Verse3DPictureType,
    ensembleMode: Int,
    sceneVariantSeed: Int,
    variationIndex: Int,
    accentGold: Color
) {
    val minDim = min(innerW, innerH)
    val baseScale = minDim * 0.225f

    // Determine whether the Lead Character on the left uses the Open Scroll pose, Teaching pose, or Sacred Attribute pose
    val leadPose = when {
        leadCharacter.visualStyle == CharacterVisualStyle.JESUS_PANTOCRATOR ->
            StorybookPoseMode.TEACHING_OPEN_ARMS
        leadCharacter.visualStyle in setOf(
            CharacterVisualStyle.KING_DAVID_PSALMIST,
            CharacterVisualStyle.ASAPH_AND_KORAH,
            CharacterVisualStyle.KING_SOLOMON_WISDOM,
            CharacterVisualStyle.LADY_WISDOM_VIRTUOUS,
            CharacterVisualStyle.SIMON_PETER
        ) && variationIndex == 0 ->
            StorybookPoseMode.SACRED_ATTRIBUTE_POSE
        ensembleMode == 1 || leadCharacter.visualStyle in setOf(
            CharacterVisualStyle.PAUL_OF_TARSUS,
            CharacterVisualStyle.MATTHEW_EVANGELIST,
            CharacterVisualStyle.MARK_EVANGELIST,
            CharacterVisualStyle.LUKE_PHYSICIAN,
            CharacterVisualStyle.TIMOTHY_AND_BARNABAS
        ) ->
            StorybookPoseMode.READING_OPEN_SCROLL
        ensembleMode == 0 ->
            StorybookPoseMode.TEACHING_OPEN_ARMS
        else ->
            StorybookPoseMode.SACRED_ATTRIBUTE_POSE
    }

    // ==================== LEFT BIBLICAL GROUP (Facing Right: +1f) ====================
    if (leadPose == StorybookPoseMode.TEACHING_OPEN_ARMS || ensembleMode == 0) {
        // Top-Panel Reference Composition on Left:
        // 1) Back-left bearded disciple in slate-blue tunic
        drawStorybookBiblicalFigure(
            cx = innerLeft + innerW * 0.11f,
            headCy = innerTop + innerH * 0.42f,
            scale = baseScale * 0.76f,
            style = thirdCharacter?.visualStyle ?: CharacterVisualStyle.ANDREW_APOSTLE,
            primaryRobe = Color(0xFF607D8B),
            mantle = Color(0xFF455A64),
            gold = accentGold,
            facingDirection = 1f,
            poseMode = StorybookPoseMode.PRAYING_CLASPED_HANDS,
            showHalo = false
        )
        // 2) Back-right biblical woman in olive-green headscarf (behind lead figure's shoulder)
        drawStorybookBiblicalFigure(
            cx = innerLeft + innerW * 0.37f,
            headCy = innerTop + innerH * 0.45f,
            scale = baseScale * 0.76f,
            style = CharacterVisualStyle.MARTHA_AND_MARY_BETHANY,
            primaryRobe = Color(0xFFF5F0E6),
            mantle = Color(0xFF7E8C54),
            gold = accentGold,
            facingDirection = 1f,
            poseMode = StorybookPoseMode.MOTHER_WITH_VEIL,
            showHalo = false
        )
        // 3) Foreground Lead Figure (e.g. Jesus in white tunic & royal-blue diagonal sash, teaching)
        val leadTunic = if (leadCharacter.visualStyle == CharacterVisualStyle.JESUS_PANTOCRATOR) {
            Color(0xFFF7F5F0)
        } else {
            leadCharacter.primaryRobeColor
        }
        val leadMantle = if (leadCharacter.visualStyle == CharacterVisualStyle.JESUS_PANTOCRATOR) {
            Color(0xFF1E68B8)
        } else {
            leadCharacter.mantleColor
        }
        drawStorybookBiblicalFigure(
            cx = innerLeft + innerW * 0.25f,
            headCy = innerTop + innerH * 0.38f,
            scale = baseScale * 0.96f,
            style = leadCharacter.visualStyle,
            primaryRobe = leadTunic,
            mantle = leadMantle,
            gold = accentGold,
            facingDirection = 1f,
            poseMode = leadPose,
            showHalo = true,
            useSoftGlowHalo = (leadCharacter.visualStyle == CharacterVisualStyle.JESUS_PANTOCRATOR)
        )
        // 4) Lower-left foreground child in warm orange tunic (like the blind/healed boy in top-left of reference)
        drawStorybookBiblicalFigure(
            cx = innerLeft + innerW * 0.12f,
            headCy = innerTop + innerH * 0.67f,
            scale = baseScale * 0.62f,
            style = CharacterVisualStyle.JOHN_BELOVED,
            primaryRobe = Color(0xFFE67E22),
            mantle = Color(0xFFD35400),
            gold = accentGold,
            facingDirection = 1f,
            poseMode = StorybookPoseMode.CHILD_FOREGROUND,
            showHalo = false,
            hasHeadband = (pictureType == Verse3DPictureType.JESUS_HEALING_COMPASSION_3D || ensembleMode == 0)
        )
    } else {
        // Bottom-Panel Reference Composition on Left:
        // Optional subtle companion behind left shoulder if thirdCharacter is present
        if (thirdCharacter != null) {
            drawStorybookBiblicalFigure(
                cx = innerLeft + innerW * 0.11f,
                headCy = innerTop + innerH * 0.44f,
                scale = baseScale * 0.74f,
                style = thirdCharacter.visualStyle,
                primaryRobe = thirdCharacter.primaryRobeColor,
                mantle = thirdCharacter.mantleColor,
                gold = accentGold,
                facingDirection = 1f,
                poseMode = StorybookPoseMode.PRAYING_CLASPED_HANDS,
                showHalo = false
            )
        }
        // Prominent Apostle / King / Evangelist on Left with Luminous Golden Disc Halo & Open Two-Roller Scroll (or Sacred Prop)
        val apostleTunic = if (leadPose == StorybookPoseMode.READING_OPEN_SCROLL) {
            Color(0xFFF5F3EE)
        } else {
            leadCharacter.primaryRobeColor
        }
        val apostleMantle = if (leadPose == StorybookPoseMode.READING_OPEN_SCROLL) {
            Color(0xFF8D1D2C)
        } else {
            leadCharacter.mantleColor
        }
        drawStorybookBiblicalFigure(
            cx = innerLeft + innerW * 0.25f,
            headCy = innerTop + innerH * 0.39f,
            scale = baseScale * 1.00f,
            style = leadCharacter.visualStyle,
            primaryRobe = apostleTunic,
            mantle = apostleMantle,
            gold = accentGold,
            facingDirection = 1f,
            poseMode = leadPose,
            showHalo = true,
            useSoftGlowHalo = false
        )
    }

    // ==================== RIGHT BIBLICAL GROUP (Facing Left: -1f) ====================
    if (ensembleMode == 0 || pictureType == Verse3DPictureType.SCRIBES_AND_TEMPLE_ELDERS_3D) {
        // Top-Panel Reference Composition on Right:
        // Temple High Priest in Crimson & Gold Miter/Ephod + Pharisee/Elder in Royal Purple & White Prayer Shawl
        // 1) Background scholar/elder between them
        drawStorybookBiblicalFigure(
            cx = innerLeft + innerW * 0.77f,
            headCy = innerTop + innerH * 0.37f,
            scale = baseScale * 0.72f,
            style = companionCharacter.visualStyle,
            primaryRobe = companionCharacter.primaryRobeColor,
            mantle = companionCharacter.mantleColor,
            gold = accentGold,
            facingDirection = -1f,
            poseMode = StorybookPoseMode.PRAYING_CLASPED_HANDS,
            showHalo = false
        )
        // 2) Left of Right Group: High Priest / Chief Elder in Crimson-and-Gold Crowned Miter & Gold-Trimmed Robe
        drawStorybookBiblicalFigure(
            cx = innerLeft + innerW * 0.65f,
            headCy = innerTop + innerH * 0.40f,
            scale = baseScale * 0.92f,
            style = CharacterVisualStyle.NICODEMUS_TEACHER,
            primaryRobe = Color(0xFFF7F5F0),
            mantle = Color(0xFF8E1B1B),
            gold = Color(0xFFF5C542),
            facingDirection = -1f,
            poseMode = StorybookPoseMode.HIGH_PRIEST_MITER_STAFF,
            showHalo = false
        )
        // 3) Right of Right Group: Venerable Elder / Pharisee in White Head-Shawl, Royal Purple Robe & White Tallit
        drawStorybookBiblicalFigure(
            cx = innerLeft + innerW * 0.86f,
            headCy = innerTop + innerH * 0.41f,
            scale = baseScale * 0.92f,
            style = CharacterVisualStyle.MOSES_AND_ELIJAH_PROPHETS,
            primaryRobe = Color(0xFF6A359C),
            mantle = Color(0xFFF2F5F8),
            gold = Color(0xFFE5B53B),
            facingDirection = -1f,
            poseMode = StorybookPoseMode.PHARISEE_ELDER_SHAWL,
            showHalo = false
        )
    } else {
        // Bottom-Panel Reference Composition on Right:
        // 5-Figure Multi-Generational Listening Crowd & Family with Clasped Praying Hands!
        // 1) Back-Center Bearded Man in Olive-Brown Tunic
        drawStorybookBiblicalFigure(
            cx = innerLeft + innerW * 0.77f,
            headCy = innerTop + innerH * 0.37f,
            scale = baseScale * 0.76f,
            style = CharacterVisualStyle.JAMES_GREATER,
            primaryRobe = Color(0xFF6E5B3E),
            mantle = Color(0xFF4E3F2A),
            gold = accentGold,
            facingDirection = -1f,
            poseMode = StorybookPoseMode.PRAYING_CLASPED_HANDS,
            showHalo = false
        )
        // 2) Back-Right Mother / Woman in Crisp White Veil, Rose Tunic & Blue Mantle
        drawStorybookBiblicalFigure(
            cx = innerLeft + innerW * 0.89f,
            headCy = innerTop + innerH * 0.40f,
            scale = baseScale * 0.80f,
            style = CharacterVisualStyle.MARY_MOTHER_OF_JESUS,
            primaryRobe = Color(0xFFC0392B),
            mantle = Color(0xFFF5F7FA),
            gold = accentGold,
            facingDirection = -1f,
            poseMode = StorybookPoseMode.MOTHER_WITH_VEIL,
            showHalo = false
        )
        // 3) Front-Center Young Man in Golden-Ochre Tunic with Hands Clasped in Prayer
        drawStorybookBiblicalFigure(
            cx = innerLeft + innerW * 0.75f,
            headCy = innerTop + innerH * 0.45f,
            scale = baseScale * 0.84f,
            style = CharacterVisualStyle.TIMOTHY_AND_BARNABAS,
            primaryRobe = Color(0xFFD68910),
            mantle = Color(0xFFB9770E),
            gold = accentGold,
            facingDirection = -1f,
            poseMode = StorybookPoseMode.PRAYING_CLASPED_HANDS,
            showHalo = false
        )
        // 4) Front-Left Primary Listener (uses the verse/chapter's specific companionCharacter in Royal Blue or character robe, hands clasped in prayer)
        val frontListenerRobe = if (ensembleMode == 1) Color(0xFF2471A3) else companionCharacter.primaryRobeColor
        val frontListenerMantle = if (ensembleMode == 1) Color(0xFF1B4F72) else companionCharacter.mantleColor
        drawStorybookBiblicalFigure(
            cx = innerLeft + innerW * 0.61f,
            headCy = innerTop + innerH * 0.44f,
            scale = baseScale * 0.88f,
            style = companionCharacter.visualStyle,
            primaryRobe = frontListenerRobe,
            mantle = frontListenerMantle,
            gold = accentGold,
            facingDirection = -1f,
            poseMode = if (ensembleMode == 2) StorybookPoseMode.SACRED_ATTRIBUTE_POSE else StorybookPoseMode.PRAYING_CLASPED_HANDS,
            showHalo = (ensembleMode == 2)
        )
        // 5) Front-Right Foreground Young Boy in Bright Sky-Blue Tunic (standing in front of his mother)
        drawStorybookBiblicalFigure(
            cx = innerLeft + innerW * 0.88f,
            headCy = innerTop + innerH * 0.67f,
            scale = baseScale * 0.62f,
            style = CharacterVisualStyle.STEPHEN_PROTOMARTYR,
            primaryRobe = Color(0xFF4FC3F7),
            mantle = Color(0xFF039BE5),
            gold = accentGold,
            facingDirection = -1f,
            poseMode = StorybookPoseMode.CHILD_FOREGROUND,
            showHalo = false
        )
    }
}

/**
 * High-Detail Storybook Illustrated Biblical Character Renderer matching the attached reference image:
 * - Crisp dark-sepia artist ink outlines (#2B1810) around all anatomical features, drapery folds, hands, and props.
 * - 3/4-turned interactive face orientation ([facingDirection]: +1f facing right, -1f facing left).
 * - Multi-layered robes with deep drapery crease lines, diagonal royal-blue sash (Jesus), burgundy mantle (Paul),
 *   crimson-and-gold High Priest ephod & miter, or purple Pharisee robe with white tallit shawl.
 * - Expressive hands: open two-roller parchment scroll with scripture lines, open teaching palms with 5 fingers,
 *   clasped praying hands at the chest, or character-specific 3D sacred attributes.
 */
fun DrawScope.drawStorybookBiblicalFigure(
    cx: Float,
    headCy: Float,
    scale: Float,
    style: CharacterVisualStyle,
    primaryRobe: Color,
    mantle: Color,
    gold: Color,
    facingDirection: Float = 1f,
    poseMode: StorybookPoseMode = StorybookPoseMode.TEACHING_OPEN_ARMS,
    showHalo: Boolean = true,
    useSoftGlowHalo: Boolean = false,
    hasHeadband: Boolean = false
) {
    val ink = Color(0xFF2B1810)
    val inkStroke = (scale * 0.042f).coerceIn(1.0f, 2.6f)
    val thinInkStroke = (scale * 0.026f).coerceIn(0.8f, 1.7f)

    val isJesus = style == CharacterVisualStyle.JESUS_PANTOCRATOR
    val isVeiledWoman = poseMode == StorybookPoseMode.MOTHER_WITH_VEIL || style in setOf(
        CharacterVisualStyle.MARY_MOTHER_OF_JESUS,
        CharacterVisualStyle.MARY_MAGDALENE,
        CharacterVisualStyle.SAMARITAN_WOMAN_WELL,
        CharacterVisualStyle.MARTHA_AND_MARY_BETHANY,
        CharacterVisualStyle.LYDIA_OF_THYATIRA,
        CharacterVisualStyle.LADY_WISDOM_VIRTUOUS
    )
    val isChild = poseMode == StorybookPoseMode.CHILD_FOREGROUND
    val isHighPriest = poseMode == StorybookPoseMode.HIGH_PRIEST_MITER_STAFF
    val isPhariseeElder = poseMode == StorybookPoseMode.PHARISEE_ELDER_SHAWL
    val isArchangel = style == CharacterVisualStyle.GABRIEL_ARCHANGEL ||
        style == CharacterVisualStyle.MICHAEL_ARCHANGEL

    // 0. Optional Feathered Archangel Wings
    if (isArchangel && !isChild) {
        for (wDir in listOf(-1f, 1f)) {
            val wingPath = Path().apply {
                moveTo(cx + wDir * scale * 0.20f, headCy + scale * 0.45f)
                cubicTo(
                    cx + wDir * scale * 1.35f, headCy - scale * 0.65f,
                    cx + wDir * scale * 1.60f, headCy + scale * 0.25f,
                    cx + wDir * scale * 1.15f, headCy + scale * 1.55f
                )
                quadraticTo(
                    cx + wDir * scale * 0.65f, headCy + scale * 1.15f,
                    cx + wDir * scale * 0.22f, headCy + scale * 0.90f
                )
                close()
            }
            drawPath(
                path = wingPath,
                brush = Brush.verticalGradient(
                    listOf(Color.White, Color(0xFFFFF8E1), gold),
                    startY = headCy - scale * 0.60f,
                    endY = headCy + scale * 1.55f
                )
            )
            drawPath(wingPath, color = ink, style = Stroke(width = thinInkStroke))
        }
    }

    // 1. Halo (Either Soft Golden Aura Glow like Top-Left Jesus, OR Crisp Golden Disc Halo with Gold Rim like Bottom-Left Paul)
    if (showHalo && !isChild) {
        val haloR = scale * 0.92f
        if (useSoftGlowHalo) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFFDE7).copy(alpha = 0.92f),
                        Color(0xFFFFE082).copy(alpha = 0.62f),
                        Color(0xFFFFD54F).copy(alpha = 0.22f),
                        Color.Transparent
                    ),
                    center = Offset(cx, headCy - scale * 0.04f),
                    radius = haloR * 1.35f
                ),
                radius = haloR * 1.35f,
                center = Offset(cx, headCy - scale * 0.04f)
            )
        } else {
            // Crisp Storybook Golden Disc Halo (matches Bottom-Left Apostle Paul in reference image)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFFDE7),
                        Color(0xFFFFF59D),
                        Color(0xFFFFE082)
                    ),
                    center = Offset(cx, headCy - scale * 0.04f),
                    radius = haloR
                ),
                radius = haloR,
                center = Offset(cx, headCy - scale * 0.04f)
            )
            drawCircle(
                color = Color(0xFFD49A2A),
                radius = haloR,
                center = Offset(cx, headCy - scale * 0.04f),
                style = Stroke(width = (scale * 0.065f).coerceAtLeast(1.6f))
            )
            drawCircle(
                color = ink.copy(alpha = 0.65f),
                radius = haloR + scale * 0.03f,
                center = Offset(cx, headCy - scale * 0.04f),
                style = Stroke(width = thinInkStroke)
            )
        }
    }

    // Determine Hair Color
    val hairColor = when {
        isHighPriest || isPhariseeElder || style in setOf(
            CharacterVisualStyle.SIMON_PETER,
            CharacterVisualStyle.ANDREW_APOSTLE,
            CharacterVisualStyle.MOSES_AND_ELIJAH_PROPHETS,
            CharacterVisualStyle.NICODEMUS_TEACHER
        ) -> Color(0xFFE6ECF2) // Flowing silver-white
        style in setOf(
            CharacterVisualStyle.GABRIEL_ARCHANGEL,
            CharacterVisualStyle.MICHAEL_ARCHANGEL
        ) -> Color(0xFFD9A74A)
        style in setOf(
            CharacterVisualStyle.JESUS_PANTOCRATOR,
            CharacterVisualStyle.JOHN_BELOVED,
            CharacterVisualStyle.MARY_MAGDALENE
        ) -> Color(0xFF5C2E18) // Rich warm chestnut-brown
        else -> Color(0xFF3E2214) // Deep dark brown
    }
    val hairHighlight = lerp(hairColor, Color.White, 0.28f)
    val hairShadow = lerp(hairColor, Color.Black, 0.38f)

    val shoulderTopY = headCy + scale * 0.44f
    val torsoBottomY = shoulderTopY + scale * 2.05f

    // 2. Back Hair / Flowing Veil / Head-Shawl behind Head & Shoulders
    if (isVeiledWoman || isPhariseeElder) {
        val veilCol = if (isPhariseeElder) Color(0xFFF0F4F8) else mantle
        val veilBack = Path().apply {
            moveTo(cx - scale * 0.40f, headCy - scale * 0.30f)
            quadraticTo(cx, headCy - scale * 0.64f, cx + scale * 0.40f, headCy - scale * 0.30f)
            quadraticTo(cx + scale * 0.72f, shoulderTopY + scale * 0.25f, cx + scale * 0.62f, shoulderTopY + scale * 0.95f)
            lineTo(cx - scale * 0.62f, shoulderTopY + scale * 0.95f)
            quadraticTo(cx - scale * 0.72f, shoulderTopY + scale * 0.25f, cx - scale * 0.40f, headCy - scale * 0.30f)
            close()
        }
        drawPath(
            path = veilBack,
            brush = Brush.horizontalGradient(
                listOf(lerp(veilCol, Color.White, 0.25f), veilCol, lerp(veilCol, Color.Black, 0.28f)),
                startX = cx - scale * 0.68f,
                endX = cx + scale * 0.68f
            )
        )
        drawPath(veilBack, color = ink, style = Stroke(width = inkStroke))
    } else if (!isChild) {
        val backHair = Path().apply {
            moveTo(cx - scale * 0.38f, headCy - scale * 0.22f)
            quadraticTo(cx, headCy - scale * 0.54f, cx + scale * 0.38f, headCy - scale * 0.22f)
            quadraticTo(cx + scale * 0.56f, headCy + scale * 0.30f, cx + scale * 0.46f, shoulderTopY + scale * 0.26f)
            lineTo(cx - scale * 0.46f, shoulderTopY + scale * 0.26f)
            quadraticTo(cx - scale * 0.56f, headCy + scale * 0.30f, cx - scale * 0.38f, headCy - scale * 0.22f)
            close()
        }
        drawPath(
            path = backHair,
            brush = Brush.horizontalGradient(
                listOf(hairHighlight, hairColor, hairShadow),
                startX = cx - scale * 0.50f,
                endX = cx + scale * 0.50f
            )
        )
        drawPath(backHair, color = ink, style = Stroke(width = thinInkStroke))
    }

    // 3. Base Pleated Tunic (Chiton) with Painterly Vertical Drapery Folds & Ink Contour
    val tunicPath = Path().apply {
        moveTo(cx - scale * 0.36f, shoulderTopY)
        quadraticTo(cx - scale * 1.00f, shoulderTopY + scale * 0.14f, cx - scale * 1.12f, torsoBottomY)
        lineTo(cx + scale * 1.12f, torsoBottomY)
        quadraticTo(cx + scale * 1.00f, shoulderTopY + scale * 0.14f, cx + scale * 0.36f, shoulderTopY)
        close()
    }
    val tunicHighlight = lerp(primaryRobe, Color.White, 0.25f)
    val tunicShadow = lerp(primaryRobe, Color(0xFF607D8B), 0.32f)
    drawPath(
        path = tunicPath,
        brush = Brush.horizontalGradient(
            listOf(tunicHighlight, primaryRobe, tunicShadow),
            startX = cx - scale * 1.10f,
            endX = cx + scale * 1.10f
        )
    )
    drawPath(tunicPath, color = ink, style = Stroke(width = inkStroke))

    // Vertical drapery pleat lines on Tunic
    for (p in -2..2) {
        val px = cx + p * scale * 0.22f
        drawLine(
            color = lerp(primaryRobe, Color.Black, 0.28f),
            start = Offset(px * 0.94f + cx * 0.06f, shoulderTopY + scale * 0.16f),
            end = Offset(px, torsoBottomY),
            strokeWidth = thinInkStroke,
            cap = StrokeCap.Round
        )
    }

    // 4. Costume-Specific Draped Mantle / Diagonal Sash / High Priest Ephod
    if (isJesus || (poseMode == StorybookPoseMode.TEACHING_OPEN_ARMS && !isHighPriest && !isPhariseeElder)) {
        // Jesus's Iconic Diagonal Royal-Blue Sash/Mantle from Left Shoulder across Chest to Hip (matches Top-Left reference!)
        val sashPath = Path().apply {
            moveTo(cx - facingDirection * scale * 0.42f, shoulderTopY - scale * 0.02f)
            lineTo(cx + facingDirection * scale * 0.12f, shoulderTopY + scale * 0.04f)
            quadraticTo(
                cx + facingDirection * scale * 0.65f,
                shoulderTopY + scale * 0.85f,
                cx + facingDirection * scale * 0.82f,
                torsoBottomY
            )
            lineTo(cx + facingDirection * scale * 0.10f, torsoBottomY)
            quadraticTo(
                cx - facingDirection * scale * 0.25f,
                shoulderTopY + scale * 0.90f,
                cx - facingDirection * scale * 0.62f,
                shoulderTopY + scale * 0.18f
            )
            close()
        }
        drawPath(
            path = sashPath,
            brush = Brush.linearGradient(
                listOf(lerp(mantle, Color.White, 0.26f), mantle, lerp(mantle, Color.Black, 0.38f)),
                start = Offset(cx - scale * 0.5f, shoulderTopY),
                end = Offset(cx + scale * 0.7f, torsoBottomY)
            )
        )
        drawPath(sashPath, color = ink, style = Stroke(width = inkStroke))
        // Diagonal sash fold creases
        for (f in 1..2) {
            val offsetF = f * scale * 0.12f
            drawLine(
                color = lerp(mantle, Color.Black, 0.42f),
                start = Offset(cx - facingDirection * (scale * 0.42f - offsetF), shoulderTopY + scale * 0.08f),
                end = Offset(cx + facingDirection * (scale * 0.28f + offsetF), torsoBottomY - scale * 0.15f),
                strokeWidth = thinInkStroke,
                cap = StrokeCap.Round
            )
        }
    } else if (isHighPriest) {
        // High Priest Crimson Robe with Wide Golden Embroidered Ephod Borders (matches Top-Right reference!)
        val ephodPath = Path().apply {
            moveTo(cx - scale * 0.42f, shoulderTopY)
            quadraticTo(cx - scale * 0.98f, shoulderTopY + scale * 0.18f, cx - scale * 1.05f, torsoBottomY)
            lineTo(cx - scale * 0.22f, torsoBottomY)
            lineTo(cx - scale * 0.18f, shoulderTopY + scale * 0.15f)
            close()
            moveTo(cx + scale * 0.42f, shoulderTopY)
            quadraticTo(cx + scale * 0.98f, shoulderTopY + scale * 0.18f, cx + scale * 1.05f, torsoBottomY)
            lineTo(cx + scale * 0.22f, torsoBottomY)
            lineTo(cx + scale * 0.18f, shoulderTopY + scale * 0.15f)
            close()
        }
        drawPath(
            path = ephodPath,
            brush = Brush.verticalGradient(
                listOf(Color(0xFFA61C1C), Color(0xFF7A1212)),
                startY = shoulderTopY,
                endY = torsoBottomY
            )
        )
        drawPath(ephodPath, color = ink, style = Stroke(width = inkStroke))
        // Wide Golden Trim Stoles along the inner edges of the crimson robe
        for (side in listOf(-1f, 1f)) {
            drawLine(
                color = Color(0xFFF5C542),
                start = Offset(cx + side * scale * 0.22f, shoulderTopY + scale * 0.05f),
                end = Offset(cx + side * scale * 0.26f, torsoBottomY),
                strokeWidth = scale * 0.13f
            )
            drawLine(
                color = ink,
                start = Offset(cx + side * scale * 0.22f, shoulderTopY + scale * 0.05f),
                end = Offset(cx + side * scale * 0.26f, torsoBottomY),
                strokeWidth = thinInkStroke
            )
        }
    } else if (!isChild) {
        // Draped Outer Mantle (Himation) over Shoulders (like Bottom-Left Apostle Paul in Burgundy Mantle or Elder in White Shawl)
        val mantlePath = Path().apply {
            moveTo(cx - scale * 0.40f, shoulderTopY)
            quadraticTo(cx - scale * 1.04f, shoulderTopY + scale * 0.16f, cx - scale * 1.12f, torsoBottomY)
            lineTo(cx - scale * 0.32f, torsoBottomY)
            quadraticTo(cx, shoulderTopY + scale * 0.85f, cx + scale * 0.32f, torsoBottomY)
            lineTo(cx + scale * 1.12f, torsoBottomY)
            quadraticTo(cx + scale * 1.04f, shoulderTopY + scale * 0.16f, cx + scale * 0.40f, shoulderTopY)
            quadraticTo(cx, shoulderTopY + scale * 0.42f, cx - scale * 0.40f, shoulderTopY)
            close()
        }
        drawPath(
            path = mantlePath,
            brush = Brush.linearGradient(
                listOf(lerp(mantle, Color.White, 0.24f), mantle, lerp(mantle, Color.Black, 0.36f)),
                start = Offset(cx - scale * 0.90f, shoulderTopY),
                end = Offset(cx + scale * 0.90f, torsoBottomY)
            )
        )
        drawPath(mantlePath, color = ink, style = Stroke(width = inkStroke))

        // Curved drapery fold lines on mantle
        for (f in 1..3) {
            val fy = shoulderTopY + scale * (0.24f * f)
            val fold = Path().apply {
                moveTo(cx - scale * 0.78f, fy)
                quadraticTo(cx - scale * 0.35f, fy + scale * 0.22f, cx - scale * 0.15f, fy + scale * 0.12f)
                moveTo(cx + scale * 0.78f, fy)
                quadraticTo(cx + scale * 0.35f, fy + scale * 0.22f, cx + scale * 0.15f, fy + scale * 0.12f)
            }
            drawPath(fold, color = lerp(mantle, Color.Black, 0.38f), style = Stroke(width = thinInkStroke, cap = StrokeCap.Round))
        }
    }

    // 5. Sculpted Neck & V-Collar
    val skinLight = Color(0xFFFDEBD0)
    val skinMid = Color(0xFFF5CBA7)
    val skinShadow = Color(0xFFD99B72)

    drawRoundRect(
        brush = Brush.horizontalGradient(
            listOf(skinMid, skinShadow),
            startX = cx - scale * 0.16f,
            endX = cx + scale * 0.16f
        ),
        topLeft = Offset(cx - scale * 0.16f, headCy + scale * 0.26f),
        size = Size(scale * 0.32f, scale * 0.32f),
        cornerRadius = CornerRadius(scale * 0.08f, scale * 0.08f)
    )
    // V-neck collar line
    val collarPath = Path().apply {
        moveTo(cx - scale * 0.24f, shoulderTopY - scale * 0.02f)
        lineTo(cx, shoulderTopY + scale * 0.18f)
        lineTo(cx + scale * 0.24f, shoulderTopY - scale * 0.02f)
    }
    drawPath(collarPath, color = ink, style = Stroke(width = thinInkStroke, cap = StrokeCap.Round))

    // 6. Sculpted Head, Ear & Warm Facial Shading
    // Ear on the back-facing side of the 3/4 head
    val earX = cx - facingDirection * scale * 0.33f
    val earY = headCy - scale * 0.02f
    drawOval(
        color = skinShadow,
        topLeft = Offset(earX - scale * 0.07f, earY - scale * 0.10f),
        size = Size(scale * 0.14f, scale * 0.22f)
    )
    drawOval(
        color = ink,
        topLeft = Offset(earX - scale * 0.07f, earY - scale * 0.10f),
        size = Size(scale * 0.14f, scale * 0.22f),
        style = Stroke(width = thinInkStroke)
    )

    // Main Face Oval
    val faceRectTopLeft = Offset(cx - scale * 0.34f, headCy - scale * 0.42f)
    val faceRectSize = Size(scale * 0.68f, scale * 0.84f)
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(skinLight, skinMid, skinShadow),
            center = Offset(cx + facingDirection * scale * 0.06f, headCy - scale * 0.08f),
            radius = scale * 0.52f
        ),
        topLeft = faceRectTopLeft,
        size = faceRectSize
    )
    drawOval(
        color = ink,
        topLeft = faceRectTopLeft,
        size = faceRectSize,
        style = Stroke(width = inkStroke)
    )

    // 7. Crown Hair, High Priest Miter, Pharisee Head-Shawl, Royal Crowns & Veils
    if (isHighPriest) {
        // Ornate Crimson & Gold High Priest Crowned Miter (matches Top-Right Elder in reference image!)
        val miterPath = Path().apply {
            moveTo(cx - scale * 0.36f, headCy - scale * 0.18f)
            quadraticTo(cx - scale * 0.44f, headCy - scale * 0.72f, cx, headCy - scale * 0.78f)
            quadraticTo(cx + scale * 0.44f, headCy - scale * 0.72f, cx + scale * 0.36f, headCy - scale * 0.18f)
            close()
        }
        drawPath(
            path = miterPath,
            brush = Brush.verticalGradient(
                listOf(Color(0xFFD32F2F), Color(0xFF8E1B1B)),
                startY = headCy - scale * 0.78f,
                endY = headCy - scale * 0.18f
            )
        )
        drawPath(miterPath, color = ink, style = Stroke(width = inkStroke))
        // Golden Crown Rim & Vertical Gold Ribbons on Miter
        drawRoundRect(
            color = Color(0xFFF5C542),
            topLeft = Offset(cx - scale * 0.38f, headCy - scale * 0.28f),
            size = Size(scale * 0.76f, scale * 0.13f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = ink,
            topLeft = Offset(cx - scale * 0.38f, headCy - scale * 0.28f),
            size = Size(scale * 0.76f, scale * 0.13f),
            cornerRadius = CornerRadius(4f, 4f),
            style = Stroke(width = thinInkStroke)
        )
        drawLine(
            color = Color(0xFFF5C542),
            start = Offset(cx, headCy - scale * 0.78f),
            end = Offset(cx, headCy - scale * 0.28f),
            strokeWidth = scale * 0.09f
        )
    } else if (isPhariseeElder) {
        // White Draped Pharisee Head-Shawl with Fillet Band (matches Top-Right right-most Elder in reference!)
        val headShawl = Path().apply {
            moveTo(cx - scale * 0.38f, headCy - scale * 0.14f)
            quadraticTo(cx, headCy - scale * 0.62f, cx + scale * 0.38f, headCy - scale * 0.14f)
            quadraticTo(cx, headCy - scale * 0.32f, cx - scale * 0.38f, headCy - scale * 0.14f)
            close()
        }
        drawPath(headShawl, color = Color(0xFFF2F5F8))
        drawPath(headShawl, color = ink, style = Stroke(width = inkStroke))
        drawLine(
            color = Color(0xFF4A235A),
            start = Offset(cx - scale * 0.36f, headCy - scale * 0.26f),
            end = Offset(cx + scale * 0.36f, headCy - scale * 0.26f),
            strokeWidth = scale * 0.07f,
            cap = StrokeCap.Round
        )
    } else if (isVeiledWoman) {
        // Draped Woman's Headscarf / Veil (matches olive-green & white veils in reference image!)
        val hoodCol = if (poseMode == StorybookPoseMode.MOTHER_WITH_VEIL) mantle else primaryRobe
        val hood = Path().apply {
            moveTo(cx - scale * 0.36f, headCy - scale * 0.08f)
            quadraticTo(cx, headCy - scale * 0.58f, cx + scale * 0.36f, headCy - scale * 0.08f)
            quadraticTo(cx, headCy - scale * 0.34f, cx - scale * 0.36f, headCy - scale * 0.08f)
            close()
        }
        drawPath(hood, color = hoodCol)
        drawPath(hood, color = ink, style = Stroke(width = inkStroke))
    } else if (
        style == CharacterVisualStyle.WISE_MAGI_KING ||
        style == CharacterVisualStyle.KING_DAVID_PSALMIST ||
        style == CharacterVisualStyle.KING_SOLOMON_WISDOM
    ) {
        // Royal Golden Crown for David, Solomon, and Magi
        val crownPath = Path().apply {
            moveTo(cx - scale * 0.35f, headCy - scale * 0.22f)
            lineTo(cx - scale * 0.39f, headCy - scale * 0.58f)
            lineTo(cx - scale * 0.17f, headCy - scale * 0.40f)
            lineTo(cx, headCy - scale * 0.68f)
            lineTo(cx + scale * 0.17f, headCy - scale * 0.40f)
            lineTo(cx + scale * 0.39f, headCy - scale * 0.58f)
            lineTo(cx + scale * 0.35f, headCy - scale * 0.22f)
            close()
        }
        drawPath(
            path = crownPath,
            brush = Brush.linearGradient(
                listOf(Color(0xFFFFF59D), Color(0xFFFBC02D), Color(0xFFB27814)),
                start = Offset(cx - scale * 0.35f, headCy - scale * 0.65f),
                end = Offset(cx + scale * 0.35f, headCy - scale * 0.22f)
            )
        )
        drawPath(crownPath, color = ink, style = Stroke(width = inkStroke))
        drawCircle(color = Color(0xFFD91E48), radius = scale * 0.05f, center = Offset(cx, headCy - scale * 0.35f))
    } else if (style == CharacterVisualStyle.PAUL_OF_TARSUS && !isChild) {
        // Apostle Paul's Signature Receding Forehead with Full Brown Side Hair Locks (matches Bottom-Left reference!)
        for (side in listOf(-1f, 1f)) {
            val lock = Path().apply {
                moveTo(cx + side * scale * 0.24f, headCy - scale * 0.36f)
                quadraticTo(
                    cx + side * scale * 0.44f,
                    headCy - scale * 0.18f,
                    cx + side * scale * 0.36f,
                    headCy + scale * 0.08f
                )
                quadraticTo(
                    cx + side * scale * 0.26f,
                    headCy - scale * 0.10f,
                    cx + side * scale * 0.24f,
                    headCy - scale * 0.36f
                )
                close()
            }
            drawPath(lock, color = hairColor)
            drawPath(lock, color = ink, style = Stroke(width = thinInkStroke))
        }
    } else {
        // Wavy Parted or Curly Storybook Hair Crown
        val topHair = Path().apply {
            moveTo(cx - scale * 0.36f, headCy - scale * 0.06f)
            quadraticTo(cx - scale * 0.38f, headCy - scale * 0.52f, cx, headCy - scale * 0.46f)
            quadraticTo(cx + scale * 0.38f, headCy - scale * 0.52f, cx + scale * 0.36f, headCy - scale * 0.06f)
            quadraticTo(cx + scale * 0.18f, headCy - scale * 0.34f, cx, headCy - scale * 0.28f)
            quadraticTo(cx - scale * 0.18f, headCy - scale * 0.34f, cx - scale * 0.36f, headCy - scale * 0.06f)
            close()
        }
        drawPath(
            path = topHair,
            brush = Brush.horizontalGradient(
                listOf(hairHighlight, hairColor, hairShadow),
                startX = cx - scale * 0.36f,
                endX = cx + scale * 0.36f
            )
        )
        drawPath(topHair, color = ink, style = Stroke(width = thinInkStroke))
    }

    // 8. Expressive 3/4-Oriented Eyebrows, Eyelids, Sclera, Irises, Pupils & Catchlights
    val gazeShift = facingDirection * scale * 0.032f
    val eyeY = headCy - scale * 0.06f
    val eyeOffsetX = scale * 0.13f

    if (hasHeadband) {
        // White Cloth Bandage / Headband across Brow/Eyes (like the blind child in Top-Left reference!)
        drawRoundRect(
            color = Color(0xFFF5F7FA),
            topLeft = Offset(cx - scale * 0.35f, eyeY - scale * 0.09f),
            size = Size(scale * 0.70f, scale * 0.16f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = ink,
            topLeft = Offset(cx - scale * 0.35f, eyeY - scale * 0.09f),
            size = Size(scale * 0.70f, scale * 0.16f),
            cornerRadius = CornerRadius(4f, 4f),
            style = Stroke(width = thinInkStroke)
        )
    } else {
        for (dir in listOf(-1f, 1f)) {
            val ex = cx + dir * eyeOffsetX + gazeShift * 0.5f
            // Arched Eyebrow
            drawArc(
                color = ink,
                startAngle = 200f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(ex - scale * 0.09f, eyeY - scale * 0.10f),
                size = Size(scale * 0.18f, scale * 0.09f),
                style = Stroke(width = (scale * 0.038f).coerceAtLeast(1.2f), cap = StrokeCap.Round)
            )
            // Sclera
            drawOval(
                color = Color(0xFFFFFAF4),
                topLeft = Offset(ex - scale * 0.065f, eyeY - scale * 0.036f),
                size = Size(scale * 0.13f, scale * 0.072f)
            )
            // Upper Eyelid Ink Line
            drawArc(
                color = ink,
                startAngle = 190f,
                sweepAngle = 160f,
                useCenter = false,
                topLeft = Offset(ex - scale * 0.068f, eyeY - scale * 0.038f),
                size = Size(scale * 0.136f, scale * 0.072f),
                style = Stroke(width = thinInkStroke, cap = StrokeCap.Round)
            )
            // Iris & Pupil shifted in facingDirection so characters look at each other!
            val irisX = ex + facingDirection * scale * 0.014f
            drawCircle(
                color = Color(0xFF4A2B18),
                radius = scale * 0.032f,
                center = Offset(irisX, eyeY)
            )
            drawCircle(
                color = Color(0xFF140A05),
                radius = scale * 0.017f,
                center = Offset(irisX, eyeY)
            )
            drawCircle(
                color = Color.White,
                radius = scale * 0.011f,
                center = Offset(irisX - scale * 0.008f, eyeY - scale * 0.009f)
            )
        }
    }

    // 9. Sculpted 3/4 Nose Bridge & Nostril Tip
    val noseTipX = cx + facingDirection * scale * 0.045f
    val nosePath = Path().apply {
        moveTo(cx + facingDirection * scale * 0.015f, eyeY - scale * 0.01f)
        lineTo(noseTipX, headCy + scale * 0.10f)
        lineTo(cx - facingDirection * scale * 0.02f, headCy + scale * 0.12f)
    }
    drawPath(nosePath, color = ink, style = Stroke(width = thinInkStroke, cap = StrokeCap.Round))

    // 10. Full Contoured Beard, Mustache & Strand Lines (or Serene Lips for Women/Youths/Children)
    val hasBeard = !isVeiledWoman && !isChild && !isArchangel &&
        poseMode != StorybookPoseMode.PRAYING_CLASPED_HANDS &&
        style !in setOf(
            CharacterVisualStyle.JOHN_BELOVED,
            CharacterVisualStyle.STEPHEN_PROTOMARTYR,
            CharacterVisualStyle.TIMOTHY_AND_BARNABAS,
            CharacterVisualStyle.CORNELIUS_CENTURION
        ) || isHighPriest || isPhariseeElder || isJesus || style == CharacterVisualStyle.PAUL_OF_TARSUS

    if (hasBeard && !isVeiledWoman && !isChild) {
        val beardLen = when {
            isHighPriest || isPhariseeElder -> scale * 0.68f // Long flowing silver-white beard!
            style == CharacterVisualStyle.PAUL_OF_TARSUS ||
                style == CharacterVisualStyle.MOSES_AND_ELIJAH_PROPHETS ||
                style == CharacterVisualStyle.JOHN_THE_BAPTIST -> scale * 0.56f
            else -> scale * 0.46f
        }
        val beardPath = Path().apply {
            moveTo(cx - scale * 0.32f, headCy + scale * 0.02f)
            quadraticTo(cx - scale * 0.32f, headCy + beardLen * 0.86f, cx + facingDirection * scale * 0.03f, headCy + beardLen)
            quadraticTo(cx + scale * 0.32f, headCy + beardLen * 0.86f, cx + scale * 0.32f, headCy + scale * 0.02f)
            quadraticTo(cx + scale * 0.15f, headCy + scale * 0.22f, cx, headCy + scale * 0.20f)
            quadraticTo(cx - scale * 0.15f, headCy + scale * 0.22f, cx - scale * 0.32f, headCy + scale * 0.02f)
            close()
        }
        drawPath(
            path = beardPath,
            brush = Brush.verticalGradient(
                listOf(hairColor, hairShadow),
                startY = headCy + scale * 0.02f,
                endY = headCy + beardLen
            )
        )
        drawPath(beardPath, color = ink, style = Stroke(width = inkStroke))

        // Wavy beard texture lines
        for (b in -1..1) {
            val bx = cx + b * scale * 0.11f
            drawLine(
                color = ink.copy(alpha = 0.45f),
                start = Offset(bx, headCy + scale * 0.25f),
                end = Offset(bx + facingDirection * scale * 0.02f, headCy + beardLen * 0.86f),
                strokeWidth = thinInkStroke,
                cap = StrokeCap.Round
            )
        }
        // Full Mustache Arch
        val mustache = Path().apply {
            moveTo(cx + facingDirection * scale * 0.01f, headCy + scale * 0.15f)
            quadraticTo(cx - scale * 0.14f, headCy + scale * 0.15f, cx - scale * 0.21f, headCy + scale * 0.24f)
            moveTo(cx + facingDirection * scale * 0.01f, headCy + scale * 0.15f)
            quadraticTo(cx + scale * 0.14f, headCy + scale * 0.15f, cx + scale * 0.21f, headCy + scale * 0.24f)
        }
        drawPath(mustache, color = ink, style = Stroke(width = (scale * 0.045f).coerceAtLeast(1.4f), cap = StrokeCap.Round))
    }

    // Serene Mouth / Parted Speaking Lips
    drawLine(
        color = Color(0xFFA84A42),
        start = Offset(cx - scale * 0.065f + gazeShift * 0.3f, headCy + scale * 0.20f),
        end = Offset(cx + scale * 0.065f + gazeShift * 0.3f, headCy + scale * 0.20f),
        strokeWidth = (scale * 0.042f).coerceAtLeast(1.3f),
        cap = StrokeCap.Round
    )

    // 11. Pose-Specific Articulated Sleeves, Hands, Fingers & Scrolls/Props
    when (poseMode) {
        StorybookPoseMode.READING_OPEN_SCROLL -> {
            // Wide Open Two-Roller Parchment Scroll held across chest by both hands (matches Bottom-Left Paul in reference!)
            val scrollCx = cx + facingDirection * scale * 0.18f
            val scrollCy = shoulderTopY + scale * 0.66f
            val scrollW = scale * 1.05f
            val scrollH = scale * 0.68f
            val scrollLeft = scrollCx - scrollW * 0.5f
            val scrollRight = scrollCx + scrollW * 0.5f
            val scrollTop = scrollCy - scrollH * 0.5f
            val scrollBot = scrollCy + scrollH * 0.5f

            // Parchment body
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFFFDF2D9), Color(0xFFF3DCA6), Color(0xFFE0BE7E)),
                    startX = scrollLeft,
                    endX = scrollRight
                ),
                topLeft = Offset(scrollLeft, scrollTop),
                size = Size(scrollW, scrollH),
                cornerRadius = CornerRadius(4f, 4f)
            )
            drawRoundRect(
                color = ink,
                topLeft = Offset(scrollLeft, scrollTop),
                size = Size(scrollW, scrollH),
                cornerRadius = CornerRadius(4f, 4f),
                style = Stroke(width = inkStroke)
            )

            // Top & Bottom Wooden Scroll Rollers + Finial Knobs
            val rollerH = scale * 0.11f
            for (ry in listOf(scrollTop - rollerH * 0.4f, scrollBot - rollerH * 0.6f)) {
                // Wooden handles extending on left & right
                drawRoundRect(
                    color = Color(0xFF6E3B16),
                    topLeft = Offset(scrollLeft - scale * 0.16f, ry + rollerH * 0.15f),
                    size = Size(scrollW + scale * 0.32f, rollerH * 0.70f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
                drawRoundRect(
                    color = ink,
                    topLeft = Offset(scrollLeft - scale * 0.16f, ry + rollerH * 0.15f),
                    size = Size(scrollW + scale * 0.32f, rollerH * 0.70f),
                    cornerRadius = CornerRadius(4f, 4f),
                    style = Stroke(width = thinInkStroke)
                )
                // Rolled parchment cylinder
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFFF7E4B5), Color(0xFFD6B06A), Color(0xFFA67B38)),
                        startY = ry,
                        endY = ry + rollerH
                    ),
                    topLeft = Offset(scrollLeft - scale * 0.04f, ry),
                    size = Size(scrollW + scale * 0.08f, rollerH),
                    cornerRadius = CornerRadius(rollerH * 0.5f, rollerH * 0.5f)
                )
                drawRoundRect(
                    color = ink,
                    topLeft = Offset(scrollLeft - scale * 0.04f, ry),
                    size = Size(scrollW + scale * 0.08f, rollerH),
                    cornerRadius = CornerRadius(rollerH * 0.5f, rollerH * 0.5f),
                    style = Stroke(width = thinInkStroke)
                )
            }

            // Scripture ink lines inside the unrolled scroll
            for (lineIdx in 1..4) {
                val ly = scrollTop + scrollH * (0.18f + lineIdx * 0.15f)
                drawLine(
                    color = ink.copy(alpha = 0.55f),
                    start = Offset(scrollLeft + scrollW * 0.14f, ly),
                    end = Offset(scrollRight - scrollW * 0.14f, ly),
                    strokeWidth = thinInkStroke,
                    cap = StrokeCap.Round
                )
            }

            // Both Hands & Curled Fingers holding the left and right edges of the open scroll
            for (hSide in listOf(-1f, 1f)) {
                val hx = if (hSide < 0f) scrollLeft + scale * 0.02f else scrollRight - scale * 0.02f
                val hy = scrollCy + hSide * scale * 0.04f
                drawStorybookHandWithFingers(
                    hx = hx,
                    hy = hy,
                    scale = scale,
                    skinMid = skinMid,
                    ink = ink,
                    thinInkStroke = thinInkStroke,
                    fingerDirX = -hSide
                )
            }
        }

        StorybookPoseMode.PRAYING_CLASPED_HANDS -> {
            // Clasped Praying Hands at Chest Level (matches Bottom-Right Young Men in blue & ochre tunics!)
            val prayX = cx + facingDirection * scale * 0.14f
            val prayY = shoulderTopY + scale * 0.48f
            val prayPath = Path().apply {
                moveTo(prayX - scale * 0.12f, prayY + scale * 0.18f)
                lineTo(prayX - scale * 0.03f, prayY - scale * 0.16f)
                quadraticTo(prayX, prayY - scale * 0.22f, prayX + scale * 0.04f, prayY - scale * 0.16f)
                lineTo(prayX + scale * 0.12f, prayY + scale * 0.18f)
                close()
            }
            drawPath(prayPath, color = skinMid)
            drawPath(prayPath, color = ink, style = Stroke(width = thinInkStroke))
            // Finger separation lines on praying hands
            drawLine(
                color = ink,
                start = Offset(prayX, prayY - scale * 0.18f),
                end = Offset(prayX, prayY + scale * 0.12f),
                strokeWidth = thinInkStroke
            )
        }

        StorybookPoseMode.HIGH_PRIEST_MITER_STAFF -> {
            // Golden High Priest Staff in outer hand + Open Conversational Palm toward Jesus
            val staffX = cx - facingDirection * scale * 0.56f
            drawLine(
                color = Color(0xFFF5C542),
                start = Offset(staffX, headCy - scale * 0.42f),
                end = Offset(staffX, torsoBottomY),
                strokeWidth = (scale * 0.08f).coerceAtLeast(2.2f),
                cap = StrokeCap.Round
            )
            drawLine(
                color = ink,
                start = Offset(staffX, headCy - scale * 0.42f),
                end = Offset(staffX, torsoBottomY),
                strokeWidth = thinInkStroke
            )
            drawCircle(
                color = Color(0xFFF5C542),
                radius = scale * 0.10f,
                center = Offset(staffX, headCy - scale * 0.46f)
            )
            drawCircle(
                color = ink,
                radius = scale * 0.10f,
                center = Offset(staffX, headCy - scale * 0.46f),
                style = Stroke(width = thinInkStroke)
            )
            // Hand grasping staff
            drawStorybookHandWithFingers(
                hx = staffX,
                hy = shoulderTopY + scale * 0.48f,
                scale = scale,
                skinMid = skinMid,
                ink = ink,
                thinInkStroke = thinInkStroke,
                fingerDirX = facingDirection
            )
            // Inner hand raised in dialogue
            drawStorybookHandWithFingers(
                hx = cx + facingDirection * scale * 0.52f,
                hy = shoulderTopY + scale * 0.42f,
                scale = scale,
                skinMid = skinMid,
                ink = ink,
                thinInkStroke = thinInkStroke,
                fingerDirX = facingDirection
            )
        }

        StorybookPoseMode.PHARISEE_ELDER_SHAWL -> {
            // One hand over chest, one hand extended in scholarly dialogue (matches Top-Right Elder in purple!)
            drawStorybookHandWithFingers(
                hx = cx - facingDirection * scale * 0.10f,
                hy = shoulderTopY + scale * 0.44f,
                scale = scale,
                skinMid = skinMid,
                ink = ink,
                thinInkStroke = thinInkStroke,
                fingerDirX = facingDirection
            )
            drawStorybookHandWithFingers(
                hx = cx + facingDirection * scale * 0.50f,
                hy = shoulderTopY + scale * 0.52f,
                scale = scale,
                skinMid = skinMid,
                ink = ink,
                thinInkStroke = thinInkStroke,
                fingerDirX = facingDirection
            )
        }

        StorybookPoseMode.SACRED_ATTRIBUTE_POSE -> {
            // Teaching/Blessing hand on one side + Character-Specific Sacred Attribute on the other
            drawStorybookHandWithFingers(
                hx = cx - facingDirection * scale * 0.48f,
                hy = shoulderTopY + scale * 0.38f,
                scale = scale,
                skinMid = skinMid,
                ink = ink,
                thinInkStroke = thinInkStroke,
                fingerDirX = facingDirection
            )
            val attrX = cx + facingDirection * scale * 0.48f
            val attrY = shoulderTopY + scale * 0.52f
            drawStorybookSacredAttribute(
                style = style,
                attrX = attrX,
                attrY = attrY,
                headCy = headCy,
                torsoBottomY = torsoBottomY,
                scale = scale,
                gold = gold,
                ink = ink,
                thinInkStroke = thinInkStroke
            )
            drawStorybookHandWithFingers(
                hx = attrX - facingDirection * scale * 0.08f,
                hy = attrY + scale * 0.08f,
                scale = scale,
                skinMid = skinMid,
                ink = ink,
                thinInkStroke = thinInkStroke,
                fingerDirX = facingDirection
            )
        }

        else -> {
            // TEACHING_OPEN_ARMS / MOTHER_WITH_VEIL / CHILD_FOREGROUND:
            // Expressive open-palm teaching & compassion hands (matches Top-Left Jesus in reference!)
            val leftHandX = cx - scale * 0.54f
            val leftHandY = shoulderTopY + scale * 0.34f
            val rightHandX = cx + scale * 0.54f
            val rightHandY = shoulderTopY + scale * 0.48f

            drawStorybookHandWithFingers(
                hx = leftHandX,
                hy = leftHandY,
                scale = scale,
                skinMid = skinMid,
                ink = ink,
                thinInkStroke = thinInkStroke,
                fingerDirX = -0.6f
            )
            drawStorybookHandWithFingers(
                hx = rightHandX,
                hy = rightHandY,
                scale = scale,
                skinMid = skinMid,
                ink = ink,
                thinInkStroke = thinInkStroke,
                fingerDirX = 0.8f
            )
        }
    }
}

private fun DrawScope.drawStorybookHandWithFingers(
    hx: Float,
    hy: Float,
    scale: Float,
    skinMid: Color,
    ink: Color,
    thinInkStroke: Float,
    fingerDirX: Float
) {
    val palmR = scale * 0.115f
    drawCircle(
        color = skinMid,
        radius = palmR,
        center = Offset(hx, hy)
    )
    drawCircle(
        color = ink,
        radius = palmR,
        center = Offset(hx, hy),
        style = Stroke(width = thinInkStroke)
    )
    // Articulated fingers
    for (f in -1..1) {
        val fx = hx + fingerDirX * palmR * 0.85f
        val fy = hy + f * palmR * 0.45f - palmR * 0.35f
        drawLine(
            color = skinMid,
            start = Offset(hx, hy + f * palmR * 0.25f),
            end = Offset(fx, fy),
            strokeWidth = (scale * 0.045f).coerceAtLeast(1.3f),
            cap = StrokeCap.Round
        )
        drawLine(
            color = ink,
            start = Offset(hx, hy + f * palmR * 0.25f),
            end = Offset(fx, fy),
            strokeWidth = thinInkStroke * 0.75f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawStorybookSacredAttribute(
    style: CharacterVisualStyle,
    attrX: Float,
    attrY: Float,
    headCy: Float,
    torsoBottomY: Float,
    scale: Float,
    gold: Color,
    ink: Color,
    thinInkStroke: Float
) {
    when (style) {
        CharacterVisualStyle.KING_DAVID_PSALMIST,
        CharacterVisualStyle.ASAPH_AND_KORAH -> {
            // 10-Stringed Golden Kinnor Harp of Psalms
            val harpFrame = Path().apply {
                moveTo(attrX - scale * 0.22f, attrY + scale * 0.26f)
                lineTo(attrX + scale * 0.22f, attrY + scale * 0.26f)
                quadraticTo(attrX + scale * 0.30f, attrY - scale * 0.24f, attrX + scale * 0.16f, attrY - scale * 0.40f)
                quadraticTo(attrX, attrY - scale * 0.30f, attrX - scale * 0.20f, attrY - scale * 0.36f)
                close()
            }
            drawPath(harpFrame, color = Color(0xFFF5C542), style = Stroke(width = (scale * 0.09f).coerceAtLeast(2.4f), cap = StrokeCap.Round))
            drawPath(harpFrame, color = ink, style = Stroke(width = thinInkStroke))
            for (s in -2..2) {
                val sx = attrX + s * (scale * 0.07f)
                drawLine(
                    color = Color.White,
                    start = Offset(sx, attrY - scale * 0.30f),
                    end = Offset(sx, attrY + scale * 0.24f),
                    strokeWidth = thinInkStroke
                )
            }
        }

        CharacterVisualStyle.KING_SOLOMON_WISDOM,
        CharacterVisualStyle.LADY_WISDOM_VIRTUOUS -> {
            // Royal Scepter of Solomon's Wisdom
            drawLine(
                color = Color(0xFFF5C542),
                start = Offset(attrX, headCy - scale * 0.36f),
                end = Offset(attrX, torsoBottomY),
                strokeWidth = (scale * 0.08f).coerceAtLeast(2.2f),
                cap = StrokeCap.Round
            )
            drawCircle(
                color = Color(0xFFF5C542),
                radius = scale * 0.12f,
                center = Offset(attrX, headCy - scale * 0.40f)
            )
            drawCircle(
                color = ink,
                radius = scale * 0.12f,
                center = Offset(attrX, headCy - scale * 0.40f),
                style = Stroke(width = thinInkStroke)
            )
            drawCircle(
                color = Color(0xFFE01E48),
                radius = scale * 0.055f,
                center = Offset(attrX, headCy - scale * 0.40f)
            )
        }

        CharacterVisualStyle.SIMON_PETER -> {
            // Crossed Keys of the Kingdom
            drawLine(
                color = Color(0xFFE2E8F0),
                start = Offset(attrX - scale * 0.20f, attrY + scale * 0.25f),
                end = Offset(attrX + scale * 0.20f, attrY - scale * 0.25f),
                strokeWidth = (scale * 0.08f).coerceAtLeast(2.2f),
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color(0xFFF5C542),
                start = Offset(attrX + scale * 0.20f, attrY + scale * 0.25f),
                end = Offset(attrX - scale * 0.20f, attrY - scale * 0.25f),
                strokeWidth = (scale * 0.08f).coerceAtLeast(2.2f),
                cap = StrokeCap.Round
            )
        }

        else -> {
            // Rolled Parchment Scroll with Wooden Rollers
            drawRoundRect(
                color = Color(0xFFF5DEB3),
                topLeft = Offset(attrX - scale * 0.22f, attrY - scale * 0.16f),
                size = Size(scale * 0.44f, scale * 0.34f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            drawRoundRect(
                color = ink,
                topLeft = Offset(attrX - scale * 0.22f, attrY - scale * 0.16f),
                size = Size(scale * 0.44f, scale * 0.34f),
                cornerRadius = CornerRadius(4f, 4f),
                style = Stroke(width = thinInkStroke)
            )
        }
    }
}

/**
 * Compatibility delegate so any external caller invoking [drawSculpted3DCharacterFigure]
 * automatically renders with the new high-detail Storybook Illustrated Biblical Character renderer.
 */
fun DrawScope.drawSculpted3DCharacterFigure(
    cx: Float,
    headCy: Float,
    scale: Float,
    style: CharacterVisualStyle,
    primaryRobe: Color,
    mantle: Color,
    gold: Color,
    poseIndex: Int = 0
) {
    val poseMode = when (poseIndex % 3) {
        0 -> StorybookPoseMode.TEACHING_OPEN_ARMS
        1 -> StorybookPoseMode.READING_OPEN_SCROLL
        else -> StorybookPoseMode.SACRED_ATTRIBUTE_POSE
    }
    drawStorybookBiblicalFigure(
        cx = cx,
        headCy = headCy,
        scale = scale,
        style = style,
        primaryRobe = primaryRobe,
        mantle = mantle,
        gold = gold,
        facingDirection = 1f,
        poseMode = poseMode,
        showHalo = true,
        useSoftGlowHalo = (style == CharacterVisualStyle.JESUS_PANTOCRATOR)
    )
}


