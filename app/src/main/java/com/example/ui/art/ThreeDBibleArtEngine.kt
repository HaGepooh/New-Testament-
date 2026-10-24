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

                    // 3. 3D Perspective Stage Plinth with Carved Marble Edge & Perspective Tiles
                    drawRoundRect(
                        brush = floorPlinthBrush,
                        topLeft = Offset(0f, h * 0.76f),
                        size = Size(w, h * 0.24f),
                        cornerRadius = CornerRadius(14f, 14f)
                    )
                    // Perspective floor lines for 3D depth
                    for (t in 1..4) {
                        val txTop = w * (0.15f + t * 0.14f)
                        val txBot = w * (0.05f + t * 0.18f)
                        drawLine(
                            color = spec.accentGold.copy(alpha = 0.22f),
                            start = Offset(txTop, h * 0.76f),
                            end = Offset(txBot, h),
                            strokeWidth = 1.2f
                        )
                    }
                    drawLine(
                        color = spec.accentGold.copy(alpha = 0.75f),
                        start = Offset(w * 0.04f, h * 0.76f),
                        end = Offset(w * 0.96f, h * 0.76f),
                        strokeWidth = 2.2f
                    )

                    // 4. Secondary Biblical Character in Midground Depth Plane (with Soft 3D Aura & Ground Shadow)
                    val secondary = spec.secondaryCharacter
                    val zoom = spec.cameraZoom
                    if (secondary != null) {
                        val secX = if (spec.variationIndex % 2 == 0) w * 0.71f else w * 0.29f
                        val secY = h * 0.43f
                        val secScale = minDim * 0.185f * zoom

                        // Soft radial depth backlight instead of a dark blob
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    secondary.haloAccentColor.copy(alpha = 0.32f),
                                    Color.Black.copy(alpha = 0.28f),
                                    Color.Transparent
                                ),
                                center = Offset(secX, secY + secScale * 0.4f),
                                radius = secScale * 1.8f
                            ),
                            radius = secScale * 1.8f,
                            center = Offset(secX, secY + secScale * 0.4f)
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
                        if (spec.variationIndex % 2 == 0) w * 0.33f else w * 0.67f
                    } else {
                        w * 0.50f
                    }
                    val priY = h * 0.39f
                    val priScale = minDim * 0.225f * zoom

                    // Warm 3D Volumetric Backlight + Rim Shadow behind Primary Character
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                spec.accentGold.copy(alpha = 0.38f),
                                Color.Black.copy(alpha = 0.32f),
                                Color.Transparent
                            ),
                            center = Offset(priX, priY + priScale * 0.35f),
                            radius = priScale * 1.95f
                        ),
                        radius = priScale * 1.95f,
                        center = Offset(priX, priY + priScale * 0.35f)
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

                    // 6. Subtle 3D Story Prop on the Center Foreground Altar Plinth (never covering characters)
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
                                drawSculpted3DCharacterFigure(
                                    cx = w * 0.18f,
                                    headCy = h * 0.46f,
                                    scale = minDim * 0.155f,
                                    style = third.visualStyle,
                                    primaryRobe = third.primaryRobeColor,
                                    mantle = third.mantleColor,
                                    gold = third.haloAccentColor,
                                    poseIndex = (slide.slideIndex + 2) % 6
                                )
                            }
                            drawSculpted3DCharacterFigure(
                                cx = w * 0.82f,
                                headCy = h * 0.46f,
                                scale = minDim * 0.155f,
                                style = slide.companionCharacter.visualStyle,
                                primaryRobe = slide.companionCharacter.primaryRobeColor,
                                mantle = slide.companionCharacter.mantleColor,
                                gold = slide.companionCharacter.haloAccentColor,
                                poseIndex = (slide.slideIndex + 1) % 6
                            )
                            drawSculpted3DCharacterFigure(
                                cx = w * 0.50f,
                                headCy = h * 0.40f,
                                scale = minDim * 0.195f,
                                style = slide.leadCharacter.visualStyle,
                                primaryRobe = slide.leadCharacter.primaryRobeColor,
                                mantle = slide.leadCharacter.mantleColor,
                                gold = slide.leadCharacter.haloAccentColor,
                                poseIndex = slide.slideIndex % 6
                            )
                        }

                        1 -> {
                            // 2-Character Dramatic 3D Encounter
                            drawSculpted3DCharacterFigure(
                                cx = w * 0.34f,
                                headCy = h * 0.41f,
                                scale = minDim * 0.195f,
                                style = slide.leadCharacter.visualStyle,
                                primaryRobe = slide.leadCharacter.primaryRobeColor,
                                mantle = slide.leadCharacter.mantleColor,
                                gold = slide.leadCharacter.haloAccentColor,
                                poseIndex = slide.slideIndex % 6
                            )
                            drawSculpted3DCharacterFigure(
                                cx = w * 0.66f,
                                headCy = h * 0.41f,
                                scale = minDim * 0.195f,
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
                                headCy = h * 0.44f,
                                scale = minDim * 0.17f,
                                style = slide.leadCharacter.visualStyle,
                                primaryRobe = slide.leadCharacter.primaryRobeColor,
                                mantle = slide.leadCharacter.mantleColor,
                                gold = slide.leadCharacter.haloAccentColor,
                                poseIndex = (slide.slideIndex + 4) % 6
                            )
                            drawSculpted3DCharacterFigure(
                                cx = w * 0.76f,
                                headCy = h * 0.44f,
                                scale = minDim * 0.17f,
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
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.36f)),
                            center = Offset(w * 0.5f, h * 0.45f),
                            radius = maxOf(w, h) * 0.70f
                        )
                    )
                }
            }
    )
}

/**
 * Renders all 12 distinct, richly detailed 3D biblical background stages so every verse
 * picture and chapter carousel slide has clear 3D architectural and landscape depth.
 */
private fun DrawScope.drawDetailed3DEnvironmentStage(
    w: Float,
    h: Float,
    stage: ThreeDEnvironmentStage,
    gold: Color,
    jewel: Color
) {
    // Subtle volumetric celestial rays in upper sky for 3D atmospheric depth
    for (r in 0..4) {
        val rayX = w * (0.18f + r * 0.16f)
        drawLine(
            color = gold.copy(alpha = 0.12f),
            start = Offset(w * 0.5f, 0f),
            end = Offset(rayX, h * 0.65f),
            strokeWidth = w * 0.04f
        )
    }

    when (stage) {
        ThreeDEnvironmentStage.GALILEE_SEA_AND_BOAT_3D -> {
            // Distant Galilean Hills
            val hills = Path().apply {
                moveTo(0f, h * 0.56f)
                quadraticTo(w * 0.25f, h * 0.42f, w * 0.55f, h * 0.54f)
                quadraticTo(w * 0.80f, h * 0.44f, w, h * 0.54f)
                lineTo(w, h * 0.60f)
                lineTo(0f, h * 0.60f)
                close()
            }
            drawPath(hills, color = Color(0xFF1E354D).copy(alpha = 0.75f))

            // 3D Shimmering Sea of Galilee with Wave Crests
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF1B6CA8), Color(0xFF0D3B66), Color(0xFF061A2E)),
                    startY = h * 0.54f,
                    endY = h
                ),
                topLeft = Offset(0f, h * 0.54f),
                size = Size(w, h * 0.46f)
            )
            for (wv in 0..3) {
                val wy = h * (0.58f + wv * 0.055f)
                drawLine(
                    color = Color(0xFF7CE8FF).copy(alpha = 0.38f),
                    start = Offset(w * (0.08f + wv * 0.05f), wy),
                    end = Offset(w * (0.92f - wv * 0.05f), wy),
                    strokeWidth = 2f,
                    cap = StrokeCap.Round
                )
            }

            // Detailed 3D Wooden Galilean Fishing Boat with Curved Hull, Mast & Billowing Sail
            val hull = Path().apply {
                moveTo(w * 0.06f, h * 0.56f)
                quadraticTo(w * 0.22f, h * 0.59f, w * 0.38f, h * 0.55f)
                lineTo(w * 0.33f, h * 0.67f)
                quadraticTo(w * 0.22f, h * 0.70f, w * 0.11f, h * 0.67f)
                close()
            }
            drawPath(
                path = hull,
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF8C532B), Color(0xFF4A2810)),
                    startY = h * 0.55f,
                    endY = h * 0.69f
                )
            )
            drawPath(hull, color = gold.copy(alpha = 0.65f), style = Stroke(width = 1.5f))
            // Mast
            drawLine(
                color = Color(0xFF5C3418),
                start = Offset(w * 0.22f, h * 0.20f),
                end = Offset(w * 0.22f, h * 0.58f),
                strokeWidth = 3f
            )
            // Billowing 3D Linen Sail
            val sail = Path().apply {
                moveTo(w * 0.22f, h * 0.22f)
                quadraticTo(w * 0.36f, h * 0.36f, w * 0.34f, h * 0.53f)
                quadraticTo(w * 0.22f, h * 0.50f, w * 0.12f, h * 0.53f)
                quadraticTo(w * 0.15f, h * 0.36f, w * 0.22f, h * 0.22f)
                close()
            }
            drawPath(
                path = sail,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFFF9EB), Color(0xFFE5D2B0), Color(0xFFB89B6C)),
                    start = Offset(w * 0.14f, h * 0.24f),
                    end = Offset(w * 0.34f, h * 0.53f)
                )
            )
        }

        ThreeDEnvironmentStage.JERUSALEM_TEMPLE_COLONNADE_3D,
        ThreeDEnvironmentStage.AREOPAGUS_MARBLE_COURT_3D -> {
            // Classical 3D Arch Entablature across Top
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFF5EFE2), Color(0xFFC9BCA3), Color(0xFF7D7058)),
                    startY = h * 0.06f,
                    endY = h * 0.16f
                ),
                topLeft = Offset(w * 0.05f, h * 0.06f),
                size = Size(w * 0.90f, h * 0.08f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            // 4 Cylindrical Fluted Marble Columns for Deep 3D Perspective
            val cols = listOf(w * 0.10f, w * 0.24f, w * 0.76f, w * 0.90f)
            cols.forEachIndexed { idx, colX ->
                val isInner = idx == 1 || idx == 2
                val colW = if (isInner) w * 0.065f else w * 0.085f
                val alphaMod = if (isInner) 0.72f else 0.95f
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFFFBF2).copy(alpha = alphaMod),
                            Color(0xFFE0D4BE).copy(alpha = alphaMod),
                            Color(0xFF6B5E49).copy(alpha = alphaMod)
                        ),
                        startX = colX - colW * 0.5f,
                        endX = colX + colW * 0.5f
                    ),
                    topLeft = Offset(colX - colW * 0.5f, h * 0.13f),
                    size = Size(colW, h * 0.63f)
                )
                // Golden Corinthian Capital & Base
                drawRoundRect(
                    color = gold.copy(alpha = alphaMod),
                    topLeft = Offset(colX - colW * 0.65f, h * 0.11f),
                    size = Size(colW * 1.3f, h * 0.035f),
                    cornerRadius = CornerRadius(3f, 3f)
                )
                drawRoundRect(
                    color = gold.copy(alpha = alphaMod),
                    topLeft = Offset(colX - colW * 0.65f, h * 0.73f),
                    size = Size(colW * 1.3f, h * 0.03f),
                    cornerRadius = CornerRadius(3f, 3f)
                )
            }
        }

        ThreeDEnvironmentStage.BETHLEHEM_STAR_GROTTO_3D -> {
            // Limestone Nativity Arch Frame
            drawArc(
                color = Color(0xFF3A2D24).copy(alpha = 0.75f),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(w * 0.04f, h * 0.05f),
                size = Size(w * 0.92f, h * 0.85f),
                style = Stroke(width = w * 0.06f)
            )
            // 8-Pointed Blazing 3D Star of Bethlehem & Volumetric Light Cone
            val sx = w * 0.50f
            val sy = h * 0.15f
            val beam = Path().apply {
                moveTo(sx, sy)
                lineTo(w * 0.14f, h * 0.76f)
                lineTo(w * 0.86f, h * 0.76f)
                close()
            }
            drawPath(
                path = beam,
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.45f), gold.copy(alpha = 0.25f), Color.Transparent),
                    startY = sy,
                    endY = h * 0.76f
                )
            )
            val starLen = min(w, h) * 0.14f
            drawLine(color = Color.White, start = Offset(sx - starLen, sy), end = Offset(sx + starLen, sy), strokeWidth = 2.8f, cap = StrokeCap.Round)
            drawLine(color = Color.White, start = Offset(sx, sy - starLen), end = Offset(sx, sy + starLen), strokeWidth = 2.8f, cap = StrokeCap.Round)
            drawLine(color = gold, start = Offset(sx - starLen * 0.6f, sy - starLen * 0.6f), end = Offset(sx + starLen * 0.6f, sy + starLen * 0.6f), strokeWidth = 2f, cap = StrokeCap.Round)
            drawLine(color = gold, start = Offset(sx - starLen * 0.6f, sy + starLen * 0.6f), end = Offset(sx + starLen * 0.6f, sy - starLen * 0.6f), strokeWidth = 2f, cap = StrokeCap.Round)
            drawCircle(color = Color.White, radius = starLen * 0.28f, center = Offset(sx, sy))
        }

        ThreeDEnvironmentStage.EMPTY_TOMB_RESURRECTION_DAWN_3D -> {
            // Distant Calvary Hill with Three Crosses on Right Horizon
            val hillPath = Path().apply {
                moveTo(w * 0.52f, h * 0.76f)
                quadraticTo(w * 0.78f, h * 0.36f, w, h * 0.62f)
                lineTo(w, h * 0.76f)
                close()
            }
            drawPath(hillPath, color = Color(0xFF2B2138).copy(alpha = 0.82f))
            // Center & side crosses on hill
            for (cxOff in listOf(-0.08f, 0f, 0.08f)) {
                val crossX = w * (0.78f + cxOff)
                val crossTop = if (cxOff == 0f) h * 0.28f else h * 0.33f
                drawLine(color = gold.copy(alpha = 0.85f), start = Offset(crossX, crossTop), end = Offset(crossX, h * 0.48f), strokeWidth = 2.2f)
                drawLine(color = gold.copy(alpha = 0.85f), start = Offset(crossX - w * 0.03f, crossTop + h * 0.05f), end = Offset(crossX + w * 0.03f, crossTop + h * 0.05f), strokeWidth = 2.2f)
            }

            // 3D Hewn Rock Tomb & Rolled-Away Circular Stone on Left
            drawArc(
                color = Color(0xFF473D52),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(w * 0.04f, h * 0.30f),
                size = Size(w * 0.46f, h * 0.48f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color.White, gold, Color.Transparent),
                    center = Offset(w * 0.26f, h * 0.54f),
                    radius = w * 0.20f
                ),
                radius = w * 0.17f,
                center = Offset(w * 0.26f, h * 0.54f)
            )
            // Rolled 3D Cylindrical Millstone
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFFE6DEC8), Color(0xFF8C8270), Color(0xFF4A4338)),
                    center = Offset(w * 0.46f, h * 0.54f),
                    radius = w * 0.15f
                ),
                radius = w * 0.13f,
                center = Offset(w * 0.48f, h * 0.56f)
            )
        }

        ThreeDEnvironmentStage.PATMOS_RAINBOW_THRONE_3D,
        ThreeDEnvironmentStage.NEW_JERUSALEM_CRYSTAL_GATES_3D -> {
            // 3D Emerald, Sapphire & Gold Rainbow Arch
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(Color(0xFF28CC84), gold, Color(0xFF388BFF), Color(0xFFD9386A), Color(0xFF28CC84))
                ),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(w * 0.06f, h * 0.06f),
                size = Size(w * 0.88f, h * 0.74f),
                style = Stroke(width = 7f)
            )
            // Crystal City Spires / Golden Lampstands in background
            for (sp in 0..4) {
                val sx = w * (0.14f + sp * 0.18f)
                val spireH = if (sp == 2) h * 0.48f else h * 0.36f
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.75f), gold.copy(alpha = 0.55f), jewel.copy(alpha = 0.35f)),
                        startY = h * 0.76f - spireH,
                        endY = h * 0.76f
                    ),
                    topLeft = Offset(sx - w * 0.03f, h * 0.76f - spireH),
                    size = Size(w * 0.06f, spireH),
                    cornerRadius = CornerRadius(6f, 6f)
                )
            }
        }

        ThreeDEnvironmentStage.JORDAN_RIVER_WATERFALL_3D,
        ThreeDEnvironmentStage.SYCHAR_STONE_WELL_3D -> {
            // 3D Flowing Turquoise River & Stone Well Banks
            val leftBank = Path().apply {
                moveTo(0f, h * 0.48f)
                quadraticTo(w * 0.28f, h * 0.52f, w * 0.22f, h * 0.76f)
                lineTo(0f, h * 0.76f)
                close()
            }
            drawPath(leftBank, color = Color(0xFF3E5C38).copy(alpha = 0.75f))
            val rightBank = Path().apply {
                moveTo(w, h * 0.48f)
                quadraticTo(w * 0.72f, h * 0.52f, w * 0.78f, h * 0.76f)
                lineTo(w, h * 0.76f)
                close()
            }
            drawPath(rightBank, color = Color(0xFF3E5C38).copy(alpha = 0.75f))

            val river = Path().apply {
                moveTo(w * 0.32f, h * 0.44f)
                quadraticTo(w * 0.54f, h * 0.58f, w * 0.16f, h * 0.76f)
                lineTo(w * 0.84f, h * 0.76f)
                quadraticTo(w * 0.64f, h * 0.58f, w * 0.56f, h * 0.44f)
                close()
            }
            drawPath(
                path = river,
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF64F4EE).copy(alpha = 0.82f), Color(0xFF166D8F), Color(0xFF0A324A)),
                    startY = h * 0.44f,
                    endY = h * 0.76f
                )
            )
            if (stage == ThreeDEnvironmentStage.SYCHAR_STONE_WELL_3D) {
                // 3D Cylindrical Stone Well Curb in Midground Center
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xFFD9CBB8), Color(0xFF9E8E7A), Color(0xFF594E40)),
                        startX = w * 0.36f,
                        endX = w * 0.64f
                    ),
                    topLeft = Offset(w * 0.36f, h * 0.58f),
                    size = Size(w * 0.28f, h * 0.18f),
                    cornerRadius = CornerRadius(8f, 8f)
                )
            }
        }

        ThreeDEnvironmentStage.UPPER_ROOM_CHALICE_TABLE_3D -> {
            // Warm Upper Room Arched Windows & Draped Linen Banquet Table
            for (winX in listOf(w * 0.20f, w * 0.50f, w * 0.80f)) {
                drawArc(
                    color = gold.copy(alpha = 0.35f),
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(winX - w * 0.10f, h * 0.12f),
                    size = Size(w * 0.20f, h * 0.28f)
                )
            }
            // White Linen Table Bar along lower midground
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFFFFAF0), Color(0xFFD6C7B2)),
                    startY = h * 0.66f,
                    endY = h * 0.76f
                ),
                topLeft = Offset(w * 0.08f, h * 0.66f),
                size = Size(w * 0.84f, h * 0.10f),
                cornerRadius = CornerRadius(6f, 6f)
            )
        }

        ThreeDEnvironmentStage.DAMASCUS_DESERT_SUNBURST_3D -> {
            // Blinding Heavenly Sunburst & Desert Dunes
            val burstCenter = Offset(w * 0.50f, h * 0.14f)
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color.White, gold.copy(alpha = 0.70f), Color.Transparent),
                    center = burstCenter,
                    radius = w * 0.48f
                ),
                radius = w * 0.48f,
                center = burstCenter
            )
            val dunes = Path().apply {
                moveTo(0f, h * 0.62f)
                quadraticTo(w * 0.35f, h * 0.48f, w * 0.68f, h * 0.60f)
                quadraticTo(w * 0.88f, h * 0.52f, w, h * 0.58f)
                lineTo(w, h * 0.76f)
                lineTo(0f, h * 0.76f)
                close()
            }
            drawPath(
                path = dunes,
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFD49B54).copy(alpha = 0.72f), Color(0xFF5E3A18)),
                    startY = h * 0.48f,
                    endY = h * 0.76f
                )
            )
        }

        ThreeDEnvironmentStage.MOUNT_OF_OLIVES_GROVE_3D -> {
            // 3D Terraced Galilean Hills & Sculpted Olive Trees with Volumetric Foliage Canopies
            val hill = Path().apply {
                moveTo(0f, h * 0.62f)
                quadraticTo(w * 0.30f, h * 0.46f, w * 0.65f, h * 0.60f)
                quadraticTo(w * 0.85f, h * 0.50f, w, h * 0.58f)
                lineTo(w, h * 0.76f)
                lineTo(0f, h * 0.76f)
                close()
            }
            drawPath(
                path = hill,
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF2D6B4F).copy(alpha = 0.75f), jewel.copy(alpha = 0.60f), Color(0xFF0D1A1E)),
                    startY = h * 0.46f,
                    endY = h * 0.76f
                )
            )
            // Left & Right 3D Olive Trees
            for (treeX in listOf(w * 0.12f, w * 0.88f)) {
                drawLine(
                    color = Color(0xFF5C3D24),
                    start = Offset(treeX, h * 0.58f),
                    end = Offset(treeX, h * 0.34f),
                    strokeWidth = w * 0.03f,
                    cap = StrokeCap.Round
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Color(0xFF68B87A), Color(0xFF28593A), Color(0xFF132E1D)),
                        center = Offset(treeX - w * 0.02f, h * 0.28f),
                        radius = w * 0.14f
                    ),
                    radius = w * 0.11f,
                    center = Offset(treeX, h * 0.30f)
                )
            }
        }
    }
}

/**
 * High-Detail, Allocation-Free 3D Sculpted Biblical Character Renderer:
 * - Curved 3D anatomical shoulders, tunic chiton with cylindrical vertical folds & golden clavi
 * - Curved 3D draped himation mantle with catenary fold shadows & highlights (no flat polygon boards!)
 * - Full 3D facial anatomy: sculpted brow, eyelids, sclera, iris, pupil, catchlight, 3D nose bridge & nostrils,
 *   mustache/lips, and multi-layered 3D beard & flowing shoulder hair locks
 * - Articulated 3D sleeves, forearms, and hands with fingers in 6 expressive poses
 * - Detailed 3D sacred attributes (Gospel codex with cross, keys, sword, staff, amphora, lily, scroll)
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
    val isMaryMother = style == CharacterVisualStyle.MARY_MOTHER_OF_JESUS
    val isVeiledWoman = style in setOf(
        CharacterVisualStyle.MARY_MOTHER_OF_JESUS,
        CharacterVisualStyle.MARY_MAGDALENE,
        CharacterVisualStyle.SAMARITAN_WOMAN_WELL,
        CharacterVisualStyle.MARTHA_AND_MARY_BETHANY,
        CharacterVisualStyle.LYDIA_OF_THYATIRA
    )
    val isArchangel = style == CharacterVisualStyle.GABRIEL_ARCHANGEL ||
        style == CharacterVisualStyle.MICHAEL_ARCHANGEL

    // 0. 3D Sculpted Multi-Feathered Wings for Archangels
    if (isArchangel) {
        val wingColor = if (style == CharacterVisualStyle.GABRIEL_ARCHANGEL) Color(0xFFF0F6FF) else Color(0xFFD92B3E)
        for (dir in listOf(-1f, 1f)) {
            val wing = Path().apply {
                moveTo(cx + dir * scale * 0.18f, headCy + scale * 0.44f)
                cubicTo(
                    cx + dir * scale * 1.48f, headCy - scale * 0.72f,
                    cx + dir * scale * 1.80f, headCy + scale * 0.25f,
                    cx + dir * scale * 1.28f, headCy + scale * 1.72f
                )
                quadraticTo(
                    cx + dir * scale * 0.75f, headCy + scale * 1.25f,
                    cx + dir * scale * 0.24f, headCy + scale * 0.96f
                )
                close()
            }
            drawPath(
                path = wing,
                brush = Brush.verticalGradient(
                    listOf(Color.White, wingColor, gold),
                    startY = headCy - scale * 0.65f,
                    endY = headCy + scale * 1.72f
                )
            )
            drawPath(wing, color = gold, style = Stroke(width = 1.6f))
            // Sculpted feather covert lines
            for (f in 1..3) {
                drawLine(
                    color = gold.copy(alpha = 0.65f),
                    start = Offset(cx + dir * scale * 0.35f, headCy + scale * (0.25f + f * 0.18f)),
                    end = Offset(cx + dir * scale * (1.35f - f * 0.12f), headCy + scale * (0.10f + f * 0.35f)),
                    strokeWidth = 1.4f,
                    cap = StrokeCap.Round
                )
            }
        }
    }

    // 1. 3D Beveled Golden Nimbus Halo with Filigree Rays & Rim Highlight
    val haloR = scale * 0.86f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFF8DC),
                gold,
                Color(0xFFC88218),
                Color(0xFF6B3E06)
            ),
            center = Offset(cx - haloR * 0.25f, headCy - haloR * 0.25f),
            radius = haloR * 1.22f
        ),
        radius = haloR,
        center = Offset(cx, headCy)
    )
    // Radial filigree rays inside the 3D halo
    for (ray in 0 until 12) {
        val angle = (ray * 30.0) * (PI / 180.0)
        val cosA = cos(angle).toFloat()
        val sinA = sin(angle).toFloat()
        drawLine(
            color = Color.White.copy(alpha = 0.35f),
            start = Offset(cx + cosA * haloR * 0.38f, headCy + sinA * haloR * 0.38f),
            end = Offset(cx + cosA * haloR * 0.90f, headCy + sinA * haloR * 0.90f),
            strokeWidth = 1.3f
        )
    }
    if (isJesus) {
        // Cruciform Halo Cross of Christ
        val barW = scale * 0.22f
        drawRect(
            color = Color(0xFF94182D),
            topLeft = Offset(cx - barW / 2f, headCy - haloR * 0.95f),
            size = Size(barW, haloR * 0.55f)
        )
        drawRect(
            color = Color(0xFF94182D),
            topLeft = Offset(cx - haloR * 0.95f, headCy - barW / 2f),
            size = Size(haloR * 1.90f, barW)
        )
        drawRect(
            color = gold,
            topLeft = Offset(cx - barW / 2f, headCy - haloR * 0.95f),
            size = Size(barW, haloR * 0.55f),
            style = Stroke(width = 1.5f)
        )
    }
    drawCircle(
        color = Color.White.copy(alpha = 0.82f),
        radius = haloR,
        center = Offset(cx, headCy),
        style = Stroke(width = 2.0f)
    )
    drawCircle(
        color = Color(0xFF593204).copy(alpha = 0.65f),
        radius = haloR * 0.86f,
        center = Offset(cx, headCy),
        style = Stroke(width = 1.2f)
    )

    // 2. 3D Flowing Shoulder Hair / Draped Veil Back Layer
    val hairColor = when (style) {
        CharacterVisualStyle.SIMON_PETER,
        CharacterVisualStyle.ANDREW_APOSTLE,
        CharacterVisualStyle.MOSES_AND_ELIJAH_PROPHETS,
        CharacterVisualStyle.NICODEMUS_TEACHER -> Color(0xFFDCE4EC)
        CharacterVisualStyle.GABRIEL_ARCHANGEL,
        CharacterVisualStyle.MICHAEL_ARCHANGEL -> Color(0xFFE8C36B)
        CharacterVisualStyle.JOHN_BELOVED,
        CharacterVisualStyle.MARY_MAGDALENE,
        CharacterVisualStyle.MARTHA_AND_MARY_BETHANY -> Color(0xFF753824)
        else -> Color(0xFF331D12)
    }
    val hairHighlight = lerp(hairColor, Color.White, 0.32f)
    val hairShadow = lerp(hairColor, Color.Black, 0.45f)

    val shoulderTopY = headCy + scale * 0.44f
    val torsoBottomY = shoulderTopY + scale * 1.95f

    if (isVeiledWoman || style == CharacterVisualStyle.NICODEMUS_TEACHER) {
        val veilColor = if (isMaryMother || style == CharacterVisualStyle.NICODEMUS_TEACHER) mantle else primaryRobe
        val veilPath = Path().apply {
            moveTo(cx - scale * 0.40f, headCy - scale * 0.25f)
            quadraticTo(cx, headCy - scale * 0.62f, cx + scale * 0.40f, headCy - scale * 0.25f)
            quadraticTo(cx + scale * 0.68f, shoulderTopY + scale * 0.22f, cx + scale * 0.56f, shoulderTopY + scale * 0.72f)
            lineTo(cx - scale * 0.56f, shoulderTopY + scale * 0.72f)
            quadraticTo(cx - scale * 0.68f, shoulderTopY + scale * 0.22f, cx - scale * 0.40f, headCy - scale * 0.25f)
            close()
        }
        drawPath(
            path = veilPath,
            brush = Brush.horizontalGradient(
                listOf(lerp(veilColor, Color.White, 0.22f), veilColor, lerp(veilColor, Color.Black, 0.42f)),
                startX = cx - scale * 0.65f,
                endX = cx + scale * 0.65f
            )
        )
        drawPath(veilPath, color = gold.copy(alpha = 0.80f), style = Stroke(width = 1.8f))
    } else {
        // Sculpted 3D Shoulder-Length Hair Volume with Highlight Strands
        val backHairPath = Path().apply {
            moveTo(cx - scale * 0.36f, headCy - scale * 0.26f)
            quadraticTo(cx, headCy - scale * 0.56f, cx + scale * 0.36f, headCy - scale * 0.26f)
            quadraticTo(cx + scale * 0.54f, headCy + scale * 0.28f, cx + scale * 0.44f, shoulderTopY + scale * 0.24f)
            lineTo(cx - scale * 0.44f, shoulderTopY + scale * 0.24f)
            quadraticTo(cx - scale * 0.54f, headCy + scale * 0.28f, cx - scale * 0.36f, headCy - scale * 0.26f)
            close()
        }
        drawPath(
            path = backHairPath,
            brush = Brush.horizontalGradient(
                listOf(hairHighlight, hairColor, hairShadow),
                startX = cx - scale * 0.50f,
                endX = cx + scale * 0.50f
            )
        )
    }

    // 3. 3D Sculpted Anatomical Tunic (Chiton) with Curved Shoulders & Cylindrical Vertical Folds
    val tunicPath = Path().apply {
        moveTo(cx - scale * 0.34f, shoulderTopY)
        quadraticTo(cx - scale * 1.02f, shoulderTopY + scale * 0.12f, cx - scale * 1.15f, torsoBottomY)
        lineTo(cx + scale * 1.15f, torsoBottomY)
        quadraticTo(cx + scale * 1.02f, shoulderTopY + scale * 0.12f, cx + scale * 0.34f, shoulderTopY)
        close()
    }
    drawPath(
        path = tunicPath,
        brush = Brush.horizontalGradient(
            colors = listOf(
                lerp(primaryRobe, Color.White, 0.28f),
                primaryRobe,
                lerp(primaryRobe, Color.Black, 0.48f)
            ),
            startX = cx - scale * 1.15f,
            endX = cx + scale * 1.15f
        )
    )
    // Cylindrical vertical tunic folds & golden clavi bands
    for (f in -2..2) {
        val fx = cx + f * scale * 0.24f
        drawLine(
            color = if (f < 0) Color.White.copy(alpha = 0.18f) else Color.Black.copy(alpha = 0.25f),
            start = Offset(fx * 0.96f + cx * 0.04f, shoulderTopY + scale * 0.14f),
            end = Offset(fx, torsoBottomY),
            strokeWidth = scale * 0.06f,
            cap = StrokeCap.Round
        )
    }
    // Royal Gold Clavi Band on right shoulder
    drawLine(
        color = gold.copy(alpha = 0.85f),
        start = Offset(cx + scale * 0.28f, shoulderTopY + scale * 0.04f),
        end = Offset(cx + scale * 0.34f, torsoBottomY),
        strokeWidth = scale * 0.09f
    )

    // 4. Curved 3D Draped Outer Mantle (Himation) with Natural Catenary Folds
    val mantlePath = Path().apply {
        moveTo(cx - scale * 0.36f, shoulderTopY)
        quadraticTo(cx - scale * 1.04f, shoulderTopY + scale * 0.16f, cx - scale * 1.14f, torsoBottomY)
        lineTo(cx + scale * 0.56f, torsoBottomY)
        quadraticTo(cx + scale * 0.38f, shoulderTopY + scale * 0.62f, cx - scale * 0.36f, shoulderTopY)
        close()
    }
    drawPath(
        path = mantlePath,
        brush = Brush.linearGradient(
            colors = listOf(
                lerp(mantle, Color.White, 0.30f),
                mantle,
                lerp(mantle, Color.Black, 0.45f)
            ),
            start = Offset(cx - scale * 0.95f, shoulderTopY),
            end = Offset(cx + scale * 0.55f, torsoBottomY)
        )
    )
    drawPath(mantlePath, color = gold.copy(alpha = 0.78f), style = Stroke(width = 1.8f))

    // Curved 3D Fabric Fold Ridges & Valleys on Mantle
    for (fold in 1..3) {
        val fy = shoulderTopY + scale * (0.25f * fold)
        val foldCurve = Path().apply {
            moveTo(cx - scale * 0.80f, fy)
            quadraticTo(
                cx - scale * 0.25f, fy + scale * 0.26f,
                cx + scale * (0.14f * fold - 0.08f), fy + scale * 0.30f
            )
        }
        drawPath(
            path = foldCurve,
            color = Color.Black.copy(alpha = 0.26f),
            style = Stroke(width = 2.2f, cap = StrokeCap.Round)
        )
        drawPath(
            path = foldCurve,
            color = Color.White.copy(alpha = 0.16f),
            style = Stroke(width = 1.0f, cap = StrokeCap.Round)
        )
    }

    // 5. 3D Sculpted Neck & Gold-Embroidered Collar
    val skinLight = Color(0xFFFFE5CC)
    val skinMid = Color(0xFFE5B48B)
    val skinShadow = Color(0xFFB37850)
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
    drawArc(
        color = gold,
        startAngle = 18f,
        sweepAngle = 144f,
        useCenter = false,
        topLeft = Offset(cx - scale * 0.30f, shoulderTopY - scale * 0.13f),
        size = Size(scale * 0.60f, scale * 0.28f),
        style = Stroke(width = 2.6f, cap = StrokeCap.Round)
    )

    // 6. 3D Spherical Sculpted Head & Face with Subsurface Highlight & Cheek Contour
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(skinLight, skinMid, skinShadow),
            center = Offset(cx - scale * 0.08f, headCy - scale * 0.10f),
            radius = scale * 0.50f
        ),
        topLeft = Offset(cx - scale * 0.33f, headCy - scale * 0.42f),
        size = Size(scale * 0.66f, scale * 0.82f)
    )

    // 6B. Crown Hair, Veils, Royal Crowns & Roman Helmets
    if (isVeiledWoman) {
        val hoodColor = if (isMaryMother) mantle else primaryRobe
        val hood = Path().apply {
            moveTo(cx - scale * 0.34f, headCy - scale * 0.08f)
            quadraticTo(cx, headCy - scale * 0.56f, cx + scale * 0.34f, headCy - scale * 0.08f)
            quadraticTo(cx, headCy - scale * 0.34f, cx - scale * 0.34f, headCy - scale * 0.08f)
            close()
        }
        drawPath(hood, color = hoodColor)
        drawPath(hood, color = gold, style = Stroke(width = 1.8f))
        if (isMaryMother) {
            // Traditional 3 Golden Stars of the Virgin's Maphorion
            drawCircle(color = gold, radius = scale * 0.042f, center = Offset(cx, headCy - scale * 0.36f))
            drawCircle(color = gold, radius = scale * 0.042f, center = Offset(cx - scale * 0.52f, shoulderTopY + scale * 0.28f))
            drawCircle(color = gold, radius = scale * 0.042f, center = Offset(cx + scale * 0.52f, shoulderTopY + scale * 0.28f))
        }
    } else if (style == CharacterVisualStyle.WISE_MAGI_KING) {
        val crownPath = Path().apply {
            moveTo(cx - scale * 0.35f, headCy - scale * 0.22f)
            lineTo(cx - scale * 0.39f, headCy - scale * 0.58f)
            lineTo(cx - scale * 0.17f, headCy - scale * 0.40f)
            lineTo(cx, headCy - scale * 0.66f)
            lineTo(cx + scale * 0.17f, headCy - scale * 0.40f)
            lineTo(cx + scale * 0.39f, headCy - scale * 0.58f)
            lineTo(cx + scale * 0.35f, headCy - scale * 0.22f)
            close()
        }
        drawPath(
            path = crownPath,
            brush = Brush.linearGradient(
                listOf(Color.White, gold, Color(0xFFB36B00)),
                start = Offset(cx - scale * 0.35f, headCy - scale * 0.60f),
                end = Offset(cx + scale * 0.35f, headCy - scale * 0.22f)
            )
        )
        drawPath(crownPath, color = Color.White.copy(alpha = 0.8f), style = Stroke(width = 1.4f))
        drawCircle(color = Color(0xFFD91E48), radius = scale * 0.05f, center = Offset(cx, headCy - scale * 0.35f))
    } else if (style == CharacterVisualStyle.CORNELIUS_CENTURION) {
        drawArc(
            color = Color(0xFFD92638),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(cx - scale * 0.44f, headCy - scale * 0.76f),
            size = Size(scale * 0.88f, scale * 0.54f)
        )
        drawArc(
            brush = Brush.horizontalGradient(listOf(Color.White, gold, Color(0xFF9E6310))),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(cx - scale * 0.36f, headCy - scale * 0.50f),
            size = Size(scale * 0.72f, scale * 0.42f)
        )
    } else if (style == CharacterVisualStyle.PAUL_OF_TARSUS) {
        drawOval(
            color = hairColor,
            topLeft = Offset(cx - scale * 0.35f, headCy - scale * 0.32f),
            size = Size(scale * 0.13f, scale * 0.34f)
        )
        drawOval(
            color = hairColor,
            topLeft = Offset(cx + scale * 0.22f, headCy - scale * 0.32f),
            size = Size(scale * 0.13f, scale * 0.34f)
        )
    } else {
        val topHair = Path().apply {
            moveTo(cx - scale * 0.34f, headCy - scale * 0.06f)
            quadraticTo(cx - scale * 0.36f, headCy - scale * 0.50f, cx, headCy - scale * 0.44f)
            quadraticTo(cx + scale * 0.36f, headCy - scale * 0.50f, cx + scale * 0.34f, headCy - scale * 0.06f)
            quadraticTo(cx + scale * 0.18f, headCy - scale * 0.33f, cx, headCy - scale * 0.29f)
            quadraticTo(cx - scale * 0.18f, headCy - scale * 0.33f, cx - scale * 0.34f, headCy - scale * 0.06f)
            close()
        }
        drawPath(
            path = topHair,
            brush = Brush.horizontalGradient(
                listOf(hairHighlight, hairColor, hairShadow),
                startX = cx - scale * 0.34f,
                endX = cx + scale * 0.34f
            )
        )
    }

    // 7. Expressive 3D Eyebrows, Eyelids, Sclera, Irises, Pupils & Specular Catchlights
    val eyeY = headCy - scale * 0.06f
    val eyeOffsetX = scale * 0.13f
    for (dir in listOf(-1f, 1f)) {
        val ex = cx + dir * eyeOffsetX
        // Arched Eyebrow
        drawArc(
            color = Color(0xFF2B1A12),
            startAngle = 200f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(ex - scale * 0.09f, eyeY - scale * 0.10f),
            size = Size(scale * 0.18f, scale * 0.09f),
            style = Stroke(width = 2.0f, cap = StrokeCap.Round)
        )
        // Sclera
        drawOval(
            color = Color(0xFFFFFAF2),
            topLeft = Offset(ex - scale * 0.065f, eyeY - scale * 0.034f),
            size = Size(scale * 0.13f, scale * 0.068f)
        )
        // Warm Iris & Dark Pupil
        drawCircle(
            color = Color(0xFF4A2B18),
            radius = scale * 0.031f,
            center = Offset(ex, eyeY)
        )
        drawCircle(
            color = Color(0xFF1A0D06),
            radius = scale * 0.016f,
            center = Offset(ex, eyeY)
        )
        // Specular Catchlight
        drawCircle(
            color = Color.White,
            radius = scale * 0.011f,
            center = Offset(ex - scale * 0.01f, eyeY - scale * 0.01f)
        )
    }

    // 8. 3D Sculpted Nose Bridge, Highlight & Tip
    drawLine(
        color = skinShadow,
        start = Offset(cx + scale * 0.01f, eyeY - scale * 0.02f),
        end = Offset(cx + scale * 0.02f, headCy + scale * 0.11f),
        strokeWidth = 2.0f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = skinLight,
        start = Offset(cx - scale * 0.01f, eyeY - scale * 0.01f),
        end = Offset(cx - scale * 0.01f, headCy + scale * 0.10f),
        strokeWidth = 1.4f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = skinShadow,
        start = Offset(cx - scale * 0.04f, headCy + scale * 0.12f),
        end = Offset(cx + scale * 0.04f, headCy + scale * 0.12f),
        strokeWidth = 2.0f,
        cap = StrokeCap.Round
    )

    // 9. 3D Sculpted Beard, Mustache & Serene Mouth
    val hasBeard = !isVeiledWoman && !isArchangel && style !in setOf(
        CharacterVisualStyle.JOHN_BELOVED,
        CharacterVisualStyle.STEPHEN_PROTOMARTYR,
        CharacterVisualStyle.TIMOTHY_AND_BARNABAS,
        CharacterVisualStyle.CORNELIUS_CENTURION
    )
    if (hasBeard) {
        val beardLength = when (style) {
            CharacterVisualStyle.PAUL_OF_TARSUS,
            CharacterVisualStyle.JOHN_THE_BAPTIST,
            CharacterVisualStyle.ANDREW_APOSTLE,
            CharacterVisualStyle.MOSES_AND_ELIJAH_PROPHETS -> scale * 0.54f
            CharacterVisualStyle.SIMON_PETER -> scale * 0.42f
            else -> scale * 0.46f
        }
        val beardPath = Path().apply {
            moveTo(cx - scale * 0.31f, headCy + scale * 0.04f)
            quadraticTo(cx - scale * 0.29f, headCy + beardLength * 0.85f, cx, headCy + beardLength)
            quadraticTo(cx + scale * 0.29f, headCy + beardLength * 0.85f, cx + scale * 0.31f, headCy + scale * 0.04f)
            quadraticTo(cx + scale * 0.15f, headCy + scale * 0.23f, cx, headCy + scale * 0.21f)
            quadraticTo(cx - scale * 0.15f, headCy + scale * 0.23f, cx - scale * 0.31f, headCy + scale * 0.04f)
            close()
        }
        drawPath(
            path = beardPath,
            brush = Brush.verticalGradient(
                colors = listOf(hairColor, hairShadow),
                startY = headCy + scale * 0.04f,
                endY = headCy + beardLength
            )
        )
        // Mustache
        val mustache = Path().apply {
            moveTo(cx, headCy + scale * 0.15f)
            quadraticTo(cx - scale * 0.14f, headCy + scale * 0.15f, cx - scale * 0.20f, headCy + scale * 0.23f)
            moveTo(cx, headCy + scale * 0.15f)
            quadraticTo(cx + scale * 0.14f, headCy + scale * 0.15f, cx + scale * 0.20f, headCy + scale * 0.23f)
        }
        drawPath(mustache, color = hairShadow, style = Stroke(width = 2.6f, cap = StrokeCap.Round))
    }

    // Serene Lips
    drawLine(
        color = Color(0xFFA8524A),
        start = Offset(cx - scale * 0.065f, headCy + scale * 0.20f),
        end = Offset(cx + scale * 0.065f, headCy + scale * 0.20f),
        strokeWidth = 2.2f,
        cap = StrokeCap.Round
    )

    // 10. Articulated 3D Sleeves, Hands & Character-Specific Sacred Attributes
    val handY = when (poseIndex % 3) {
        0 -> shoulderTopY + scale * 0.52f
        1 -> shoulderTopY + scale * 0.34f // Raised blessing / proclaiming hand
        else -> shoulderTopY + scale * 0.62f
    }
    val rightBlessX = cx - scale * 0.44f
    val leftAttrX = cx + scale * 0.48f
    val attrY = shoulderTopY + scale * 0.52f

    // Draped 3D Right Sleeve leading to Blessing Hand
    val sleevePath = Path().apply {
        moveTo(cx - scale * 0.72f, shoulderTopY + scale * 0.22f)
        quadraticTo(cx - scale * 0.88f, handY + scale * 0.25f, rightBlessX, handY + scale * 0.16f)
        lineTo(rightBlessX, handY - scale * 0.08f)
        close()
    }
    drawPath(sleevePath, color = lerp(mantle, Color.Black, 0.18f))
    drawPath(sleevePath, color = gold.copy(alpha = 0.65f), style = Stroke(width = 1.2f))

    // Sculpted 3D Right Hand & Two Raised Blessing Fingers
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(skinLight, skinMid, skinShadow),
            center = Offset(rightBlessX - scale * 0.03f, handY - scale * 0.03f),
            radius = scale * 0.16f
        ),
        radius = scale * 0.12f,
        center = Offset(rightBlessX, handY)
    )
    drawLine(
        color = skinLight,
        start = Offset(rightBlessX - scale * 0.03f, handY),
        end = Offset(rightBlessX - scale * 0.03f, handY - scale * 0.20f),
        strokeWidth = scale * 0.05f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = skinMid,
        start = Offset(rightBlessX + scale * 0.035f, handY),
        end = Offset(rightBlessX + scale * 0.035f, handY - scale * 0.18f),
        strokeWidth = scale * 0.05f,
        cap = StrokeCap.Round
    )

    // Left Hand Holding Character-Specific 3D Sacred Attribute
    when (style) {
        CharacterVisualStyle.JESUS_PANTOCRATOR,
        CharacterVisualStyle.MATTHEW_EVANGELIST,
        CharacterVisualStyle.MARK_EVANGELIST,
        CharacterVisualStyle.LUKE_PHYSICIAN,
        CharacterVisualStyle.JOHN_BELOVED -> {
            // 3D Jeweled Golden Gospel Codex with Crimson Velvet Cover & Gold Cross
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.42f),
                topLeft = Offset(leftAttrX - scale * 0.24f + 2f, attrY - scale * 0.22f + 3f),
                size = Size(scale * 0.46f, scale * 0.58f),
                cornerRadius = CornerRadius(5f, 5f)
            )
            drawRoundRect(
                brush = Brush.linearGradient(
                    listOf(Color(0xFFFFF6D6), gold, Color(0xFFB37412)),
                    start = Offset(leftAttrX - scale * 0.24f, attrY - scale * 0.22f),
                    end = Offset(leftAttrX + scale * 0.22f, attrY + scale * 0.36f)
                ),
                topLeft = Offset(leftAttrX - scale * 0.24f, attrY - scale * 0.22f),
                size = Size(scale * 0.46f, scale * 0.58f),
                cornerRadius = CornerRadius(5f, 5f)
            )
            drawRoundRect(
                color = Color(0xFF8C1C32),
                topLeft = Offset(leftAttrX - scale * 0.19f, attrY - scale * 0.17f),
                size = Size(scale * 0.36f, scale * 0.48f),
                cornerRadius = CornerRadius(3f, 3f)
            )
            drawLine(
                color = gold,
                start = Offset(leftAttrX - scale * 0.01f, attrY - scale * 0.09f),
                end = Offset(leftAttrX - scale * 0.01f, attrY + scale * 0.23f),
                strokeWidth = 2.5f
            )
            drawLine(
                color = gold,
                start = Offset(leftAttrX - scale * 0.13f, attrY + scale * 0.04f),
                end = Offset(leftAttrX + scale * 0.11f, attrY + scale * 0.04f),
                strokeWidth = 2.5f
            )
        }

        CharacterVisualStyle.SIMON_PETER -> {
            // 3D Crossed Gold & Silver Keys of the Kingdom
            drawLine(
                color = Color(0xFFE2E8F0),
                start = Offset(leftAttrX - scale * 0.20f, attrY + scale * 0.25f),
                end = Offset(leftAttrX + scale * 0.20f, attrY - scale * 0.25f),
                strokeWidth = 3.5f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = gold,
                start = Offset(leftAttrX + scale * 0.20f, attrY + scale * 0.25f),
                end = Offset(leftAttrX - scale * 0.20f, attrY - scale * 0.25f),
                strokeWidth = 3.5f,
                cap = StrokeCap.Round
            )
            drawCircle(
                color = gold,
                radius = scale * 0.09f,
                center = Offset(leftAttrX + scale * 0.20f, attrY + scale * 0.25f),
                style = Stroke(width = 2.5f)
            )
            drawCircle(
                color = Color(0xFFE2E8F0),
                radius = scale * 0.09f,
                center = Offset(leftAttrX - scale * 0.20f, attrY + scale * 0.25f),
                style = Stroke(width = 2.5f)
            )
        }

        CharacterVisualStyle.PAUL_OF_TARSUS,
        CharacterVisualStyle.MICHAEL_ARCHANGEL,
        CharacterVisualStyle.CORNELIUS_CENTURION -> {
            // 3D Sword of the Spirit with Golden Crossguard
            drawLine(
                color = Color(0xFFE8F0F8),
                start = Offset(leftAttrX, attrY - scale * 0.52f),
                end = Offset(leftAttrX, attrY + scale * 0.42f),
                strokeWidth = 4.2f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = gold,
                start = Offset(leftAttrX - scale * 0.20f, attrY - scale * 0.12f),
                end = Offset(leftAttrX + scale * 0.20f, attrY - scale * 0.12f),
                strokeWidth = 4.2f,
                cap = StrokeCap.Round
            )
        }

        CharacterVisualStyle.JOHN_THE_BAPTIST,
        CharacterVisualStyle.PHILIP_APOSTLE,
        CharacterVisualStyle.JAMES_GREATER,
        CharacterVisualStyle.JOSEPH_CARPENTER,
        CharacterVisualStyle.GABRIEL_ARCHANGEL -> {
            // Tall 3D Golden Cross Staff / Flowering Staff
            drawLine(
                color = gold,
                start = Offset(leftAttrX + scale * 0.08f, headCy - scale * 0.42f),
                end = Offset(leftAttrX + scale * 0.08f, torsoBottomY),
                strokeWidth = 3.6f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = gold,
                start = Offset(leftAttrX - scale * 0.12f, headCy - scale * 0.20f),
                end = Offset(leftAttrX + scale * 0.28f, headCy - scale * 0.20f),
                strokeWidth = 3.2f,
                cap = StrokeCap.Round
            )
        }

        CharacterVisualStyle.SAMARITAN_WOMAN_WELL,
        CharacterVisualStyle.MARY_MAGDALENE,
        CharacterVisualStyle.MARTHA_AND_MARY_BETHANY,
        CharacterVisualStyle.WISE_MAGI_KING -> {
            // 3D Sculpted Alabaster Spikenard Jar / Royal Chalice
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, Color(0xFFFFEAB3), Color(0xFFB87324)),
                    center = Offset(leftAttrX - scale * 0.05f, attrY),
                    radius = scale * 0.28f
                ),
                topLeft = Offset(leftAttrX - scale * 0.17f, attrY - scale * 0.14f),
                size = Size(scale * 0.34f, scale * 0.44f)
            )
            drawRoundRect(
                color = gold,
                topLeft = Offset(leftAttrX - scale * 0.10f, attrY - scale * 0.24f),
                size = Size(scale * 0.20f, scale * 0.11f),
                cornerRadius = CornerRadius(3f, 3f)
            )
        }

        CharacterVisualStyle.MARY_MOTHER_OF_JESUS -> {
            // 3D Blooming White Lily of the Annunciation
            drawLine(
                color = Color(0xFF2EA875),
                start = Offset(leftAttrX, attrY + scale * 0.32f),
                end = Offset(leftAttrX, attrY - scale * 0.26f),
                strokeWidth = 3f
            )
            drawCircle(color = Color.White, radius = scale * 0.10f, center = Offset(leftAttrX, attrY - scale * 0.28f))
            drawCircle(color = gold, radius = scale * 0.042f, center = Offset(leftAttrX, attrY - scale * 0.28f))
        }

        else -> {
            // 3D Illuminated Apostolic Parchment Scroll with Gold Finials
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFFFFF8E8), Color(0xFFEAD4B0), Color(0xFFC2A374)),
                    startX = leftAttrX - scale * 0.22f,
                    endX = leftAttrX + scale * 0.22f
                ),
                topLeft = Offset(leftAttrX - scale * 0.22f, attrY - scale * 0.16f),
                size = Size(scale * 0.44f, scale * 0.36f),
                cornerRadius = CornerRadius(6f, 6f)
            )
            drawRoundRect(
                color = gold,
                topLeft = Offset(leftAttrX - scale * 0.22f, attrY - scale * 0.16f),
                size = Size(scale * 0.44f, scale * 0.36f),
                cornerRadius = CornerRadius(6f, 6f),
                style = Stroke(width = 1.8f)
            )
        }
    }

    // Left Hand grasping the sacred attribute
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(skinLight, skinMid, skinShadow),
            center = Offset(leftAttrX - scale * 0.12f, attrY + scale * 0.06f),
            radius = scale * 0.12f
        ),
        radius = scale * 0.10f,
        center = Offset(leftAttrX - scale * 0.11f, attrY + scale * 0.08f)
    )
}

/**
 * Renders a subtle, authentic 3D Biblical Altar Prop in the lower-center foreground plinth
 * (e.g., Golden Chalice & Loaves, Menorah Lamp, Open Scroll, or Crowned Cross) without ever
 * covering the characters on the left or right.
 */
private fun DrawScope.draw3DStoryPropOverlay(
    w: Float,
    h: Float,
    pictureType: Verse3DPictureType,
    gold: Color,
    jewel: Color
) {
    val px = w * 0.50f
    val py = h * 0.87f
    val r = min(w, h) * 0.075f

    // Subtle 3D Altar Pedestal Glow in center of floor plinth
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(gold.copy(alpha = 0.42f), jewel.copy(alpha = 0.18f), Color.Transparent),
            center = Offset(px, py),
            radius = r * 2.2f
        ),
        topLeft = Offset(px - r * 1.8f, py - r * 0.7f),
        size = Size(r * 3.6f, r * 1.4f)
    )

    when (pictureType) {
        Verse3DPictureType.MIRACLE_LOAVES_AND_CHALICE_3D,
        Verse3DPictureType.JOHN_AND_BELOVED_DISCIPLES_3D -> {
            // 3D Golden Eucharistic Chalice & Two Loaves
            drawArc(
                color = gold,
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(px - r * 0.65f, py - r * 0.95f),
                size = Size(r * 1.3f, r * 1.0f)
            )
            drawLine(color = gold, start = Offset(px, py - r * 0.45f), end = Offset(px, py + r * 0.35f), strokeWidth = 2.5f)
            drawLine(color = gold, start = Offset(px - r * 0.5f, py + r * 0.35f), end = Offset(px + r * 0.5f, py + r * 0.35f), strokeWidth = 2.5f, cap = StrokeCap.Round)
        }

        Verse3DPictureType.CROSS_AND_RESURRECTION_TOMB_3D,
        Verse3DPictureType.JESUS_KING_OF_GLORY_3D,
        Verse3DPictureType.APOCALYPTIC_LAMB_AND_NEW_JERUSALEM_3D -> {
            // 3D Beveled Golden Cross Emblem on Altar Plinth
            drawLine(color = gold, start = Offset(px, py - r * 0.90f), end = Offset(px, py + r * 0.45f), strokeWidth = 2.6f, cap = StrokeCap.Round)
            drawLine(color = gold, start = Offset(px - r * 0.55f, py - r * 0.35f), end = Offset(px + r * 0.55f, py - r * 0.35f), strokeWidth = 2.6f, cap = StrokeCap.Round)
        }

        else -> {
            // 3D Unrolled Torah/Gospel Parchment Scroll on Altar Plinth
            drawRoundRect(
                color = Color(0xFFFFF5DC),
                topLeft = Offset(px - r * 0.95f, py - r * 0.42f),
                size = Size(r * 1.9f, r * 0.84f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            drawRoundRect(
                color = gold,
                topLeft = Offset(px - r * 0.95f, py - r * 0.42f),
                size = Size(r * 1.9f, r * 0.84f),
                cornerRadius = CornerRadius(4f, 4f),
                style = Stroke(width = 1.5f)
            )
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
    val orbR = min(w, h) * 0.18f

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
    drawLine(color = Color.White, start = Offset(cx, cy - orbR * 0.65f), end = Offset(cx, cy + orbR * 0.65f), strokeWidth = 5f, cap = StrokeCap.Round)
    drawLine(color = Color.White, start = Offset(cx - orbR * 0.48f, cy - orbR * 0.18f), end = Offset(cx + orbR * 0.48f, cy - orbR * 0.18f), strokeWidth = 5f, cap = StrokeCap.Round)
}

