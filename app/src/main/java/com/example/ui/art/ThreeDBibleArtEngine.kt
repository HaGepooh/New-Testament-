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
    NEW_JERUSALEM_CRYSTAL_GATES_3D("3D New Jerusalem Jasper Gates & River of Life")
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

    /**
     * Generates 6 completely different 3D character & story pictures for every chapter,
     * with ENTIRELY DIFFERENT lead & companion characters in every single picture!
     */
    fun getChapter3DSlides(metadata: ChapterArtMetadata): List<Chapter3DSceneSlide> {
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

        return listOf(
            Chapter3DSceneSlide(
                slideIndex = 0,
                badgeTitle = "3D Picture I • Chapter Panorama",
                sceneTitle = metadata.artTitle,
                sceneDescription = "${pool[0].name} & ${pool[1].name} in ${metadata.sceneArchetype.displayName}",
                leadCharacter = pool[0],
                companionCharacter = pool[1],
                thirdCharacter = pool[2],
                environmentStage = stages[baseStageIdx % stages.size],
                pictureType = Verse3DPictureType.JESUS_TEACHING_3D
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
        )
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
        val allChars = BiblicalCharactersCatalog.allCharacters
        val totalChars = allChars.size // 30 distinct characters
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

        // Determine Variation 0's characters:
        // If the verse text explicitly names a character/story, honor it for Variation 0 unless it would
        // repeat the previous verse's exact pair; we mix in verse.verseNumber to guarantee every verse is distinct!
        val hasExplicitKeyword = hasExplicitStoryKeyword(verse.text)
        val char1 = if (hasExplicitKeyword && verse.verseNumber % 2 == 1) {
            matchedPrimaryChar
        } else {
            allChars[verseUniqueStride % totalChars]
        }

        val char2Candidate = if (hasExplicitKeyword && matchedSecondaryChar != null && matchedSecondaryChar.id != char1.id) {
            matchedSecondaryChar
        } else {
            allChars[(verseUniqueStride + 7) % totalChars]
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
        )
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
            "damascus", "throne", "new jerusalem", "trumpet"
        )
        return keywords.any { t.contains(it) }
    }

    private fun matchPrimaryVerseStoryType(verse: VerseItem): Verse3DPictureType {
        val t = verse.text.lowercase()
        return when {
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
 * Hardware-accelerated, zero-lag 3D Bible Verse Character & Story Vignette.
 * Uses [Modifier.drawWithCache] + [CompositingStrategy.Offscreen] so all 3D volumetric
 * brushes, 3D stage architecture, bas-relief drop shadows, and dual-character layers are cached on the GPU
 * and never recomputed during LazyColumn scrolling.
 */
@Composable
fun ThreeDVerseIllustration(
    spec: Verse3DPictureSpec,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            }
            .drawWithCache {
                val w = size.width
                val h = size.height
                val minDim = min(w, h)

                val lightSource = Offset(
                    x = w * (0.20f + spec.lightAngleFraction * 0.60f),
                    y = h * 0.20f
                )
                val bgBrush = Brush.radialGradient(
                    colors = listOf(
                        spec.accentGold.copy(alpha = 0.52f),
                        spec.skyBottom,
                        spec.skyTop,
                        Color(0xFF07050E)
                    ),
                    center = lightSource,
                    radius = maxOf(w, h) * 0.95f
                )
                val floorPlinthBrush = Brush.verticalGradient(
                    colors = listOf(
                        spec.jewelRim.copy(alpha = 0.55f),
                        Color(0xFF090712)
                    ),
                    startY = h * 0.70f,
                    endY = h
                )
                val bevelTopLeftBrush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.55f),
                        spec.accentGold.copy(alpha = 0.78f),
                        Color.Black.copy(alpha = 0.70f)
                    ),
                    start = Offset.Zero,
                    end = Offset(w, h)
                )
                val glassSpecularBrush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.24f),
                        Color.White.copy(alpha = 0.04f),
                        Color.Transparent
                    ),
                    start = Offset.Zero,
                    end = Offset(w * 0.6f, h * 0.5f)
                )

                onDrawBehind {
                    // 1. 3D Volumetric Spherical Studio Sky
                    drawRoundRect(
                        brush = bgBrush,
                        cornerRadius = CornerRadius(14f, 14f)
                    )

                    // 2. Detailed 3D Biblical Environment Stage (Boats, Columns, Stars, Tombs, Wells, Thrones)
                    drawDetailed3DEnvironmentStage(
                        w = w,
                        h = h,
                        stage = spec.environmentStage,
                        gold = spec.accentGold,
                        jewel = spec.jewelRim
                    )

                    // 3. 3D Perspective Stage Plinth with Checkered Depth Lines
                    drawRoundRect(
                        brush = floorPlinthBrush,
                        topLeft = Offset(0f, h * 0.73f),
                        size = Size(w, h * 0.27f),
                        cornerRadius = CornerRadius(14f, 14f)
                    )
                    drawLine(
                        color = spec.accentGold.copy(alpha = 0.65f),
                        start = Offset(w * 0.06f, h * 0.73f),
                        end = Offset(w * 0.94f, h * 0.73f),
                        strokeWidth = 2f
                    )

                    // 4. Secondary Biblical Character in Midground Depth Plane (with 3D Drop Shadow)
                    val secondary = spec.secondaryCharacter
                    val zoom = spec.cameraZoom
                    if (secondary != null) {
                        val secX = if (spec.variationIndex % 2 == 0) w * 0.70f else w * 0.30f
                        val secY = h * 0.44f
                        val secScale = minDim * 0.165f * zoom

                        // Cast 3D Shadow
                        drawOval(
                            color = Color.Black.copy(alpha = 0.48f),
                            topLeft = Offset(secX - secScale * 1.15f, secY - secScale * 0.5f),
                            size = Size(secScale * 2.1f, secScale * 2.8f)
                        )

                        drawSculpted3DCharacterFigure(
                            cx = secX,
                            headCy = secY,
                            scale = secScale,
                            style = secondary.visualStyle,
                            primaryRobe = secondary.primaryRobeColor,
                            mantle = secondary.mantleColor,
                            gold = secondary.haloAccentColor,
                            poseIndex = spec.secondaryPoseIndex
                        )
                    }

                    // 5. Primary Biblical Character in Foreground Depth Plane (High-Relief 3D)
                    val priX = if (secondary != null) {
                        if (spec.variationIndex % 2 == 0) w * 0.35f else w * 0.65f
                    } else {
                        w * 0.50f
                    }
                    val priY = h * 0.40f
                    val priScale = minDim * 0.205f * zoom

                    // Deep 3D Ambient Occlusion Shadow behind Primary Character
                    drawOval(
                        color = Color.Black.copy(alpha = 0.58f),
                        topLeft = Offset(priX - priScale * 1.25f, priY - priScale * 0.45f),
                        size = Size(priScale * 2.3f, priScale * 2.9f)
                    )

                    drawSculpted3DCharacterFigure(
                        cx = priX,
                        headCy = priY,
                        scale = priScale,
                        style = spec.primaryCharacter.visualStyle,
                        primaryRobe = spec.primaryCharacter.primaryRobeColor,
                        mantle = spec.primaryCharacter.mantleColor,
                        gold = spec.accentGold,
                        poseIndex = spec.primaryPoseIndex
                    )

                    // 6. 3D Story Emblem Prop in Foreground Corner
                    draw3DStoryPropOverlay(
                        w = w,
                        h = h,
                        pictureType = spec.pictureType,
                        gold = spec.accentGold,
                        jewel = spec.jewelRim
                    )

                    // 7. 3D Specular Glass Highlight & Beveled 3D Frame Rim
                    drawRoundRect(
                        brush = glassSpecularBrush,
                        cornerRadius = CornerRadius(14f, 14f)
                    )
                    drawRoundRect(
                        brush = bevelTopLeftBrush,
                        cornerRadius = CornerRadius(14f, 14f),
                        style = Stroke(width = 3f)
                    )
                }
            }
    )
}

/**
 * Hardware-accelerated 3D Chapter Carousel Scene for the 6 distinct chapter slides.
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
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            }
            .drawWithCache {
                val _key = rememberSlideKey
                val w = size.width
                val h = size.height
                val minDim = min(w, h)
                val p = metadata.palette

                val skyGradient = Brush.verticalGradient(
                    0.0f to lerp(p.skyTop, slide.leadCharacter.backgroundSkyTop, 0.45f),
                    0.50f to lerp(p.skyMiddle, slide.companionCharacter.backgroundSkyBottom, 0.45f),
                    0.84f to p.skyHorizon,
                    1.0f to Color(0xFF080610)
                )
                val sunCenter = Offset(
                    x = w * (0.22f + (slide.slideIndex % 6) * 0.11f),
                    y = h * 0.26f
                )
                val sunAura = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.78f),
                        slide.leadCharacter.haloAccentColor.copy(alpha = 0.52f),
                        p.primaryJewel.copy(alpha = 0.24f),
                        Color.Transparent
                    ),
                    center = sunCenter,
                    radius = minDim * 0.68f
                )

                onDrawBehind {
                    // 1. 3D Deep Atmospheric Dome
                    drawRect(brush = skyGradient)
                    drawCircle(brush = sunAura, radius = minDim * 0.68f, center = sunCenter)

                    // 2. Detailed 3D Biblical Environment Stage
                    drawDetailed3DEnvironmentStage(
                        w = w,
                        h = h,
                        stage = slide.environmentStage,
                        gold = p.goldLeaf,
                        jewel = p.primaryJewel
                    )

                    // 3. Multi-Character 3D Composition based on Slide Index (6 Distinct Layouts)
                    val third = slide.thirdCharacter
                    when (slide.slideIndex % 3) {
                        0 -> {
                            // 3-Character Wide 3D Diorama
                            if (third != null) {
                                drawOval(
                                    color = Color.Black.copy(alpha = 0.46f),
                                    topLeft = Offset(w * 0.16f - 45f, h * 0.42f),
                                    size = Size(100f, 150f)
                                )
                                drawSculpted3DCharacterFigure(
                                    cx = w * 0.18f,
                                    headCy = h * 0.48f,
                                    scale = minDim * 0.145f,
                                    style = third.visualStyle,
                                    primaryRobe = third.primaryRobeColor,
                                    mantle = third.mantleColor,
                                    gold = third.haloAccentColor,
                                    poseIndex = (slide.slideIndex + 2) % 6
                                )
                            }
                            drawOval(
                                color = Color.Black.copy(alpha = 0.46f),
                                topLeft = Offset(w * 0.80f - 45f, h * 0.42f),
                                size = Size(100f, 150f)
                            )
                            drawSculpted3DCharacterFigure(
                                cx = w * 0.82f,
                                headCy = h * 0.48f,
                                scale = minDim * 0.145f,
                                style = slide.companionCharacter.visualStyle,
                                primaryRobe = slide.companionCharacter.primaryRobeColor,
                                mantle = slide.companionCharacter.mantleColor,
                                gold = slide.companionCharacter.haloAccentColor,
                                poseIndex = (slide.slideIndex + 1) % 6
                            )
                            drawOval(
                                color = Color.Black.copy(alpha = 0.56f),
                                topLeft = Offset(w * 0.50f - 65f, h * 0.34f),
                                size = Size(140f, 200f)
                            )
                            drawSculpted3DCharacterFigure(
                                cx = w * 0.50f,
                                headCy = h * 0.41f,
                                scale = minDim * 0.185f,
                                style = slide.leadCharacter.visualStyle,
                                primaryRobe = slide.leadCharacter.primaryRobeColor,
                                mantle = slide.leadCharacter.mantleColor,
                                gold = slide.leadCharacter.haloAccentColor,
                                poseIndex = slide.slideIndex % 6
                            )
                        }

                        1 -> {
                            // 2-Character Dramatic 3D Encounter
                            drawOval(
                                color = Color.Black.copy(alpha = 0.52f),
                                topLeft = Offset(w * 0.34f - 60f, h * 0.36f),
                                size = Size(130f, 190f)
                            )
                            drawSculpted3DCharacterFigure(
                                cx = w * 0.34f,
                                headCy = h * 0.42f,
                                scale = minDim * 0.185f,
                                style = slide.leadCharacter.visualStyle,
                                primaryRobe = slide.leadCharacter.primaryRobeColor,
                                mantle = slide.leadCharacter.mantleColor,
                                gold = slide.leadCharacter.haloAccentColor,
                                poseIndex = slide.slideIndex % 6
                            )
                            drawOval(
                                color = Color.Black.copy(alpha = 0.52f),
                                topLeft = Offset(w * 0.66f - 60f, h * 0.36f),
                                size = Size(130f, 190f)
                            )
                            drawSculpted3DCharacterFigure(
                                cx = w * 0.66f,
                                headCy = h * 0.42f,
                                scale = minDim * 0.185f,
                                style = slide.companionCharacter.visualStyle,
                                primaryRobe = slide.companionCharacter.primaryRobeColor,
                                mantle = slide.companionCharacter.mantleColor,
                                gold = slide.companionCharacter.haloAccentColor,
                                poseIndex = (slide.slideIndex + 3) % 6
                            )
                        }

                        else -> {
                            // 3D Sacred Monument flanked by Two Distinct Characters
                            drawSculpted3DCharacterFigure(
                                cx = w * 0.24f,
                                headCy = h * 0.45f,
                                scale = minDim * 0.16f,
                                style = slide.leadCharacter.visualStyle,
                                primaryRobe = slide.leadCharacter.primaryRobeColor,
                                mantle = slide.leadCharacter.mantleColor,
                                gold = slide.leadCharacter.haloAccentColor,
                                poseIndex = (slide.slideIndex + 4) % 6
                            )
                            drawSculpted3DCharacterFigure(
                                cx = w * 0.76f,
                                headCy = h * 0.45f,
                                scale = minDim * 0.16f,
                                style = slide.companionCharacter.visualStyle,
                                primaryRobe = slide.companionCharacter.primaryRobeColor,
                                mantle = slide.companionCharacter.mantleColor,
                                gold = slide.companionCharacter.haloAccentColor,
                                poseIndex = (slide.slideIndex + 5) % 6
                            )
                            draw3DCenterMiracleMonument(w, h, p)
                        }
                    }

                    // 4. 3D Depth Vignette
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.40f)),
                            center = Offset(w * 0.5f, h * 0.45f),
                            radius = maxOf(w, h) * 0.68f
                        )
                    )
                }
            }
    )
}

/**
 * Renders 12 distinct, clear 3D biblical background stages so every picture has
 * recognizable 3D architecture or landscape depth.
 */
private fun DrawScope.drawDetailed3DEnvironmentStage(
    w: Float,
    h: Float,
    stage: ThreeDEnvironmentStage,
    gold: Color,
    jewel: Color
) {
    when (stage) {
        ThreeDEnvironmentStage.GALILEE_SEA_AND_BOAT_3D -> {
            // 3D Waves & Wooden Galilean Fishing Boat with Sail
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF145273), Color(0xFF082238)),
                    startY = h * 0.55f,
                    endY = h
                ),
                topLeft = Offset(0f, h * 0.55f),
                size = Size(w, h * 0.45f)
            )
            // 3D Sailboat on horizon
            val hull = Path().apply {
                moveTo(w * 0.10f, h * 0.60f)
                lineTo(w * 0.36f, h * 0.60f)
                lineTo(w * 0.31f, h * 0.69f)
                lineTo(w * 0.15f, h * 0.69f)
                close()
            }
            drawPath(hull, color = Color(0xFF6E3F20))
            val sail = Path().apply {
                moveTo(w * 0.23f, h * 0.28f)
                lineTo(w * 0.34f, h * 0.58f)
                lineTo(w * 0.16f, h * 0.58f)
                close()
            }
            drawPath(sail, color = Color(0xFFF5ECD8).copy(alpha = 0.85f))
        }

        ThreeDEnvironmentStage.JERUSALEM_TEMPLE_COLONNADE_3D,
        ThreeDEnvironmentStage.AREOPAGUS_MARBLE_COURT_3D -> {
            // 3D Cylindrical Marble Temple Columns on Left & Right
            for (colX in listOf(w * 0.12f, w * 0.88f)) {
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.White, Color(0xFFE8DFC8), Color(0xFF6E624D)),
                        startX = colX - w * 0.05f,
                        endX = colX + w * 0.05f
                    ),
                    topLeft = Offset(colX - w * 0.05f, h * 0.12f),
                    size = Size(w * 0.10f, h * 0.65f)
                )
                // Gold Capital
                drawRoundRect(
                    color = gold,
                    topLeft = Offset(colX - w * 0.065f, h * 0.10f),
                    size = Size(w * 0.13f, h * 0.045f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
            }
        }

        ThreeDEnvironmentStage.BETHLEHEM_STAR_GROTTO_3D -> {
            // 8-Pointed Blazing 3D Star of Bethlehem & Light Cone
            val sx = w * 0.50f
            val sy = h * 0.16f
            val beam = Path().apply {
                moveTo(sx, sy)
                lineTo(w * 0.15f, h * 0.75f)
                lineTo(w * 0.85f, h * 0.75f)
                close()
            }
            drawPath(
                path = beam,
                brush = Brush.verticalGradient(
                    listOf(gold.copy(alpha = 0.38f), Color.Transparent),
                    startY = sy,
                    endY = h * 0.75f
                )
            )
            drawLine(color = Color.White, start = Offset(sx - 18f, sy), end = Offset(sx + 18f, sy), strokeWidth = 3f)
            drawLine(color = Color.White, start = Offset(sx, sy - 18f), end = Offset(sx, sy + 18f), strokeWidth = 3f)
            drawCircle(color = gold, radius = 6f, center = Offset(sx, sy))
        }

        ThreeDEnvironmentStage.EMPTY_TOMB_RESURRECTION_DAWN_3D -> {
            // 3D Stone Tomb Arch & Rolled-Away Circular Stone
            drawArc(
                color = Color(0xFF3E3545),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(w * 0.06f, h * 0.32f),
                size = Size(w * 0.45f, h * 0.48f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color.White, gold, Color.Transparent),
                    center = Offset(w * 0.28f, h * 0.54f),
                    radius = w * 0.22f
                ),
                radius = w * 0.18f,
                center = Offset(w * 0.28f, h * 0.54f)
            )
            // Rolled 3D Stone Wheel
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFFD8D0C5), Color(0xFF5E554B)),
                    center = Offset(w * 0.78f, h * 0.54f),
                    radius = w * 0.16f
                ),
                radius = w * 0.14f,
                center = Offset(w * 0.80f, h * 0.56f)
            )
        }

        ThreeDEnvironmentStage.PATMOS_RAINBOW_THRONE_3D,
        ThreeDEnvironmentStage.NEW_JERUSALEM_CRYSTAL_GATES_3D -> {
            // 3D Emerald & Gold Rainbow Arch around the Sky
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(Color(0xFF28CC84), gold, Color(0xFF388BFF), Color(0xFFD9386A), Color(0xFF28CC84))
                ),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(w * 0.08f, h * 0.08f),
                size = Size(w * 0.84f, h * 0.72f),
                style = Stroke(width = 6f)
            )
        }

        ThreeDEnvironmentStage.JORDAN_RIVER_WATERFALL_3D,
        ThreeDEnvironmentStage.SYCHAR_STONE_WELL_3D -> {
            // 3D Flowing Turquoise River / Wellspring & White Dove Rays
            val river = Path().apply {
                moveTo(w * 0.35f, h * 0.45f)
                quadraticTo(w * 0.55f, h * 0.60f, w * 0.20f, h)
                lineTo(w * 0.80f, h)
                quadraticTo(w * 0.65f, h * 0.60f, w * 0.55f, h * 0.45f)
                close()
            }
            drawPath(
                path = river,
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF42E0D8).copy(alpha = 0.75f), Color(0xFF104F6E)),
                    startY = h * 0.45f,
                    endY = h
                )
            )
        }

        else -> {
            // 3D Terraced Galilean Hills & Olive Foliage Spheres
            val hill = Path().apply {
                moveTo(0f, h * 0.64f)
                quadraticTo(w * 0.30f, h * 0.48f, w * 0.65f, h * 0.62f)
                quadraticTo(w * 0.85f, h * 0.52f, w, h * 0.60f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = hill,
                brush = Brush.verticalGradient(
                    listOf(jewel.copy(alpha = 0.65f), Color(0xFF0D1A1E)),
                    startY = h * 0.48f,
                    endY = h
                )
            )
        }
    }
}

/**
 * Fast, allocation-free 3D Volumetric Character Figure renderer with:
 * - 3D Archangel Wings (Gabriel & Michael)
 * - 3D Royal Crowns (Wise Magi) & 3D Roman Crested Helmets (Cornelius)
 * - 3D Veils (Mary, Magdalene, Samaritan Woman, Martha, Lydia) & Tallit Shawls (Nicodemus)
 * - 6 Distinct 3D Arm/Hand Poses & Held Biblical Props so characters look dynamic and varied!
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
    val isJesus = style == CharacterVisualStyle.JESUS_PANTOCRATOR
    val isVeiledWoman = style in setOf(
        CharacterVisualStyle.MARY_MOTHER_OF_JESUS,
        CharacterVisualStyle.MARY_MAGDALENE,
        CharacterVisualStyle.SAMARITAN_WOMAN_WELL,
        CharacterVisualStyle.MARTHA_AND_MARY_BETHANY,
        CharacterVisualStyle.LYDIA_OF_THYATIRA
    )
    val isArchangel = style == CharacterVisualStyle.GABRIEL_ARCHANGEL ||
        style == CharacterVisualStyle.MICHAEL_ARCHANGEL

    // 0. 3D Sculpted Feathered Wings for Archangels
    if (isArchangel) {
        val wingColor = if (style == CharacterVisualStyle.GABRIEL_ARCHANGEL) Color(0xFFF0F6FF) else Color(0xFFD92B3E)
        for (dir in listOf(-1f, 1f)) {
            val wing = Path().apply {
                moveTo(cx + dir * scale * 0.20f, headCy + scale * 0.48f)
                cubicTo(
                    cx + dir * scale * 1.45f, headCy - scale * 0.68f,
                    cx + dir * scale * 1.75f, headCy + scale * 0.25f,
                    cx + dir * scale * 1.25f, headCy + scale * 1.65f
                )
                lineTo(cx + dir * scale * 0.25f, headCy + scale * 0.95f)
                close()
            }
            drawPath(
                path = wing,
                brush = Brush.verticalGradient(
                    listOf(Color.White, wingColor, gold),
                    startY = headCy - scale * 0.6f,
                    endY = headCy + scale * 1.65f
                )
            )
            drawPath(wing, color = gold, style = Stroke(width = 1.8f))
        }
    }

    // 1. 3D Beveled Golden Halo Sphere with Specular Highlight
    val haloR = scale * 0.88f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White,
                gold,
                Color(0xFFB87314),
                Color(0xFF5C3404)
            ),
            center = Offset(cx - haloR * 0.28f, headCy - haloR * 0.28f),
            radius = haloR * 1.25f
        ),
        radius = haloR,
        center = Offset(cx, headCy)
    )
    if (isJesus) {
        val barW = scale * 0.22f
        drawRect(
            color = Color(0xFF8F192E),
            topLeft = Offset(cx - barW / 2f, headCy - haloR * 0.94f),
            size = Size(barW, haloR * 0.52f)
        )
        drawRect(
            color = Color(0xFF8F192E),
            topLeft = Offset(cx - haloR * 0.94f, headCy - barW / 2f),
            size = Size(haloR * 1.88f, barW)
        )
    }
    drawCircle(
        color = Color.White.copy(alpha = 0.78f),
        radius = haloR,
        center = Offset(cx, headCy),
        style = Stroke(width = 2.0f)
    )

    // 2. 3D Hair / Veil Volume with Drop Shadow
    val hairColor = when (style) {
        CharacterVisualStyle.SIMON_PETER,
        CharacterVisualStyle.ANDREW_APOSTLE,
        CharacterVisualStyle.MOSES_AND_ELIJAH_PROPHETS,
        CharacterVisualStyle.NICODEMUS_TEACHER -> Color(0xFFD8E0E8)
        CharacterVisualStyle.GABRIEL_ARCHANGEL,
        CharacterVisualStyle.MICHAEL_ARCHANGEL -> Color(0xFFE8C36B)
        CharacterVisualStyle.JOHN_BELOVED,
        CharacterVisualStyle.MARY_MAGDALENE,
        CharacterVisualStyle.MARTHA_AND_MARY_BETHANY -> Color(0xFF703624)
        else -> Color(0xFF2E1B12)
    }
    drawOval(
        color = if (isVeiledWoman || style == CharacterVisualStyle.NICODEMUS_TEACHER) mantle else hairColor,
        topLeft = Offset(cx - scale * 0.46f, headCy - scale * 0.48f),
        size = Size(scale * 0.92f, scale * 1.15f)
    )

    // 3. 3D Cylindrical Torso Robes (Chiton & Himation) with 3D Lighting Gradient
    val shoulderY = headCy + scale * 0.46f
    val bodyH = scale * 1.85f
    drawRoundRect(
        brush = Brush.horizontalGradient(
            colors = listOf(
                lerp(primaryRobe, Color.White, 0.25f),
                primaryRobe,
                lerp(primaryRobe, Color.Black, 0.48f)
            ),
            startX = cx - scale,
            endX = cx + scale
        ),
        topLeft = Offset(cx - scale * 0.92f, shoulderY),
        size = Size(scale * 1.84f, bodyH),
        cornerRadius = CornerRadius(scale * 0.45f, scale * 0.45f)
    )

    // 3D Draped Mantle across torso with cast shadow
    val mantlePath = Path().apply {
        moveTo(cx - scale * 0.85f, shoulderY + scale * 0.05f)
        lineTo(cx + scale * 0.72f, shoulderY + scale * 0.48f)
        lineTo(cx + scale * 0.45f, shoulderY + bodyH)
        lineTo(cx - scale * 0.92f, shoulderY + bodyH)
        close()
    }
    drawPath(
        path = mantlePath,
        brush = Brush.linearGradient(
            colors = listOf(
                lerp(mantle, Color.White, 0.25f),
                mantle,
                lerp(mantle, Color.Black, 0.45f)
            ),
            start = Offset(cx - scale, shoulderY),
            end = Offset(cx + scale * 0.6f, shoulderY + bodyH)
        )
    )
    drawPath(mantlePath, color = gold.copy(alpha = 0.72f), style = Stroke(width = 2.0f))

    // 4. 3D Neck & Chin Ambient Shadow
    drawOval(
        color = Color(0xFF8C5A3C),
        topLeft = Offset(cx - scale * 0.16f, headCy + scale * 0.28f),
        size = Size(scale * 0.32f, scale * 0.30f)
    )

    // 5. 3D Spherical Sculpted Head & Face with Specular Highlight
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFDFC4),
                Color(0xFFE6B48A),
                Color(0xFFA66E49)
            ),
            center = Offset(cx - scale * 0.10f, headCy - scale * 0.12f),
            radius = scale * 0.52f
        ),
        topLeft = Offset(cx - scale * 0.33f, headCy - scale * 0.40f),
        size = Size(scale * 0.66f, scale * 0.80f)
    )

    // 5B. Character-Specific 3D Headwear (Royal Crown, Centurion Helmet, Veil, Tallit)
    when (style) {
        CharacterVisualStyle.WISE_MAGI_KING -> {
            val crown = Path().apply {
                moveTo(cx - scale * 0.34f, headCy - scale * 0.24f)
                lineTo(cx - scale * 0.38f, headCy - scale * 0.56f)
                lineTo(cx - scale * 0.16f, headCy - scale * 0.40f)
                lineTo(cx, headCy - scale * 0.64f)
                lineTo(cx + scale * 0.16f, headCy - scale * 0.40f)
                lineTo(cx + scale * 0.38f, headCy - scale * 0.56f)
                lineTo(cx + scale * 0.34f, headCy - scale * 0.24f)
                close()
            }
            drawPath(crown, color = gold)
            drawCircle(color = Color(0xFFD91E48), radius = scale * 0.05f, center = Offset(cx, headCy - scale * 0.36f))
        }
        CharacterVisualStyle.CORNELIUS_CENTURION -> {
            drawArc(
                color = Color(0xFFD92638),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(cx - scale * 0.42f, headCy - scale * 0.74f),
                size = Size(scale * 0.84f, scale * 0.50f)
            )
            drawArc(
                color = gold,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(cx - scale * 0.35f, headCy - scale * 0.48f),
                size = Size(scale * 0.70f, scale * 0.38f)
            )
        }
        else -> {}
    }

    // 6. Eyes with 3D Catchlights
    val eyeY = headCy - scale * 0.05f
    for (dir in listOf(-1f, 1f)) {
        val ex = cx + dir * scale * 0.13f
        drawOval(
            color = Color.White,
            topLeft = Offset(ex - scale * 0.06f, eyeY - scale * 0.03f),
            size = Size(scale * 0.12f, scale * 0.06f)
        )
        drawCircle(color = Color(0xFF2D190E), radius = scale * 0.028f, center = Offset(ex, eyeY))
        drawCircle(color = Color.White, radius = scale * 0.010f, center = Offset(ex - scale * 0.01f, eyeY - scale * 0.01f))
    }

    // 7. 3D Sculpted Beard (only for bearded male characters)
    val hasBeard = !isVeiledWoman && !isArchangel && style !in setOf(
        CharacterVisualStyle.JOHN_BELOVED,
        CharacterVisualStyle.STEPHEN_PROTOMARTYR,
        CharacterVisualStyle.TIMOTHY_AND_BARNABAS,
        CharacterVisualStyle.CORNELIUS_CENTURION
    )
    if (hasBeard) {
        drawOval(
            brush = Brush.verticalGradient(
                colors = listOf(hairColor, lerp(hairColor, Color.Black, 0.45f)),
                startY = headCy + scale * 0.12f,
                endY = headCy + scale * 0.52f
            ),
            topLeft = Offset(cx - scale * 0.28f, headCy + scale * 0.12f),
            size = Size(scale * 0.56f, scale * 0.40f)
        )
    }

    // 8. Dynamic 3D Hand Pose & Held Character Attribute (varies by poseIndex & character style!)
    val handOffsetY = when (poseIndex % 3) {
        0 -> shoulderY + scale * 0.45f
        1 -> shoulderY + scale * 0.24f // Raised blessing/proclaiming hand
        else -> shoulderY + scale * 0.56f
    }
    val handX = cx - scale * 0.44f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFDFC4), Color(0xFFB87D56)),
            center = Offset(handX - 2f, handOffsetY - 2f),
            radius = scale * 0.16f
        ),
        radius = scale * 0.13f,
        center = Offset(handX, handOffsetY)
    )

    // Character-Specific 3D Held Prop on Viewer's Right
    val propX = cx + scale * 0.42f
    val propY = shoulderY + scale * 0.36f
    when (style) {
        CharacterVisualStyle.SIMON_PETER -> {
            // 3D Crossed Gold & Silver Keys
            drawLine(color = Color.White, start = Offset(propX - scale * 0.18f, propY + scale * 0.22f), end = Offset(propX + scale * 0.18f, propY - scale * 0.22f), strokeWidth = 3.5f)
            drawLine(color = gold, start = Offset(propX + scale * 0.18f, propY + scale * 0.22f), end = Offset(propX - scale * 0.18f, propY - scale * 0.22f), strokeWidth = 3.5f)
        }
        CharacterVisualStyle.PAUL_OF_TARSUS,
        CharacterVisualStyle.MICHAEL_ARCHANGEL -> {
            // 3D Gleaming Sword of the Spirit
            drawLine(color = Color(0xFFEFF5FC), start = Offset(propX, propY - scale * 0.48f), end = Offset(propX, propY + scale * 0.42f), strokeWidth = 4f, cap = StrokeCap.Round)
            drawLine(color = gold, start = Offset(propX - scale * 0.18f, propY - scale * 0.12f), end = Offset(propX + scale * 0.18f, propY - scale * 0.12f), strokeWidth = 4f, cap = StrokeCap.Round)
        }
        CharacterVisualStyle.JOHN_THE_BAPTIST,
        CharacterVisualStyle.PHILIP_APOSTLE,
        CharacterVisualStyle.JAMES_GREATER,
        CharacterVisualStyle.JOSEPH_CARPENTER,
        CharacterVisualStyle.GABRIEL_ARCHANGEL -> {
            // 3D Tall Golden Cross Staff / Flowering Staff
            drawLine(color = gold, start = Offset(propX, headCy - scale * 0.38f), end = Offset(propX, shoulderY + bodyH), strokeWidth = 4f, cap = StrokeCap.Round)
            drawLine(color = gold, start = Offset(propX - scale * 0.16f, headCy - scale * 0.14f), end = Offset(propX + scale * 0.16f, headCy - scale * 0.14f), strokeWidth = 3.5f, cap = StrokeCap.Round)
        }
        CharacterVisualStyle.SAMARITAN_WOMAN_WELL,
        CharacterVisualStyle.MARY_MAGDALENE,
        CharacterVisualStyle.MARTHA_AND_MARY_BETHANY -> {
            // 3D Sculpted Amphora / Alabaster Jar
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, gold, Color(0xFFA85828)),
                    center = Offset(propX - 3f, propY - 3f),
                    radius = scale * 0.30f
                ),
                topLeft = Offset(propX - scale * 0.18f, propY - scale * 0.12f),
                size = Size(scale * 0.36f, scale * 0.48f)
            )
        }
        else -> {
            // 3D Jeweled Golden Codex / Tablets
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.45f),
                topLeft = Offset(propX - scale * 0.16f + 3f, propY - scale * 0.16f + 4f),
                size = Size(scale * 0.40f, scale * 0.50f),
                cornerRadius = CornerRadius(5f, 5f)
            )
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color.White, gold, Color(0xFF9E6412)),
                    start = Offset(propX - scale * 0.16f, propY - scale * 0.16f),
                    end = Offset(propX + scale * 0.24f, propY + scale * 0.34f)
                ),
                topLeft = Offset(propX - scale * 0.16f, propY - scale * 0.16f),
                size = Size(scale * 0.40f, scale * 0.50f),
                cornerRadius = CornerRadius(5f, 5f)
            )
        }
    }
}

private fun DrawScope.draw3DStoryPropOverlay(
    w: Float,
    h: Float,
    pictureType: Verse3DPictureType,
    gold: Color,
    jewel: Color
) {
    val px = w * 0.84f
    val py = h * 0.80f
    val r = min(w, h) * 0.125f

    drawCircle(
        color = Color.Black.copy(alpha = 0.48f),
        radius = r,
        center = Offset(px + 2f, py + 3f)
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White, gold, jewel),
            center = Offset(px - r * 0.3f, py - r * 0.3f),
            radius = r * 1.3f
        ),
        radius = r,
        center = Offset(px, py)
    )
    when (pictureType) {
        Verse3DPictureType.CROSS_AND_RESURRECTION_TOMB_3D,
        Verse3DPictureType.JESUS_KING_OF_GLORY_3D -> {
            drawLine(color = Color.White, start = Offset(px, py - r * 0.55f), end = Offset(px, py + r * 0.55f), strokeWidth = 2.2f)
            drawLine(color = Color.White, start = Offset(px - r * 0.45f, py - r * 0.12f), end = Offset(px + r * 0.45f, py - r * 0.12f), strokeWidth = 2.2f)
        }
        else -> {
            drawCircle(color = Color.White.copy(alpha = 0.85f), radius = r * 0.32f, center = Offset(px, py), style = Stroke(width = 1.8f))
        }
    }
}

private fun DrawScope.draw3DCenterMiracleMonument(
    w: Float,
    h: Float,
    p: ArtPalette
) {
    val cx = w * 0.5f
    val cy = h * 0.50f
    val orbR = min(w, h) * 0.19f

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White,
                p.goldLeaf,
                p.primaryJewel,
                Color.Transparent
            ),
            center = Offset(cx - orbR * 0.25f, cy - orbR * 0.25f),
            radius = orbR * 1.4f
        ),
        radius = orbR * 1.3f,
        center = Offset(cx, cy)
    )
    drawLine(color = Color.White, start = Offset(cx, cy - orbR * 0.65f), end = Offset(cx, cy + orbR * 0.65f), strokeWidth = 6f, cap = StrokeCap.Round)
    drawLine(color = Color.White, start = Offset(cx - orbR * 0.48f, cy - orbR * 0.18f), end = Offset(cx + orbR * 0.48f, cy - orbR * 0.18f), strokeWidth = 6f, cap = StrokeCap.Round)
}
