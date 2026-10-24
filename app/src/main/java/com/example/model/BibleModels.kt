package com.example.model

import androidx.compose.ui.graphics.Color

enum class TestamentCategory(
    val title: String,
    val subtitle: String,
    val accentHex: Long
) {
    GOSPELS(
        title = "The Four Gospels",
        subtitle = "Incarnation, Ministry, Miracles & Resurrection of Christ",
        accentHex = 0xFFE6A832
    ),
    ACTS(
        title = "Acts of the Apostles",
        subtitle = "Pentecost, Early Church & Apostolic Missions",
        accentHex = 0xFFD94E5A
    ),
    PAULINE_EPISTLES(
        title = "Pauline Epistles",
        subtitle = "Theology of Grace, Church Order & Pastoral Letters",
        accentHex = 0xFF3E8EDE
    ),
    GENERAL_EPISTLES(
        title = "General Epistles",
        subtitle = "Universal Letters of Living Faith, Wisdom & Hope",
        accentHex = 0xFF2EA87E
    ),
    PROPHECY(
        title = "Apocalyptic Prophecy",
        subtitle = "The Revelation of Christ & The New Jerusalem",
        accentHex = 0xFF9E63E8
    ),
    POETRY_AND_WISDOM(
        title = "Psalms & Proverbs",
        subtitle = "Sacred Songs of King David & Royal Proverbs of Solomon",
        accentHex = 0xFFF5B83D
    )
}

enum class SceneArchetype(
    val displayName: String,
    val themeCategory: String
) {
    NATIVITY_STAR("Star of Bethlehem & Judean Hills", "Incarnation & Light"),
    JORDAN_BAPTISM("Dove of the Spirit over the Jordan", "Incarnation & Light"),
    SERMON_MOUNT("Olive Groves on the Mount of Beatitudes", "Teachings & Parables"),
    SEA_OF_GALILEE_BOAT("Vessel on the Sea of Galilee", "Miracles & Wonders"),
    LOAVES_AND_FISHES("Five Barley Loaves & Fishes by the Lake", "Miracles & Wonders"),
    GOOD_SHEPHERD("Green Pastures & The Shepherd's Gate", "Teachings & Parables"),
    TRANSFIGURATION("Radiant Glory on Mount Tabor", "Incarnation & Light"),
    JERUSALEM_PALMS("Golden Gates of Jerusalem & Palm Fronds", "Passion & Glory"),
    LAST_SUPPER_CHALICE("Sacred Chalice & Broken Bread in the Upper Room", "Passion & Glory"),
    GETHSEMANE_OLIVES("Moonlit Olive Garden of Gethsemane", "Passion & Glory"),
    CALVARY_DAWN("Three Crosses against Resurrection Dawn", "Passion & Glory"),
    EMPTY_TOMB_SUNRISE("Rolled Stone & Blinding Resurrection Light", "Passion & Glory"),
    ASCENSION_CLOUDS("Cloud Chariot above the Mount of Olives", "Incarnation & Light"),
    PENTECOST_FLAMES("Tongues of Sacred Fire & Descending Dove", "Apostolic Mission"),
    TEMPLE_SOLOMON_PORCH("Marble Columns of Solomon's Portico", "Apostolic Mission"),
    DAMASCUS_LIGHT("Blinding Celestial Light on the Damascus Road", "Apostolic Mission"),
    MEDITERRANEAN_VOYAGE("Pauline Sailboat across Starry Aegean Waters", "Apostolic Mission"),
    AREOPAGUS_ATHENS("Classical Colonnade of Mars Hill at Twilight", "Apostolic Mission"),
    SCROLL_AND_LAMP("Illuminated Parchment Scroll & Glowing Oil Lamp", "Wisdom & Epistles"),
    ARMOR_OF_FAITH("Shield of Faith, Crown & Sword of the Spirit", "Wisdom & Epistles"),
    ANCHOR_OF_HOPE("Golden Anchor of the Soul & Olive Crown", "Wisdom & Epistles"),
    SEVEN_LAMPSTANDS("Seven Golden Menorah Lampstands & Seven Stars", "Apocalyptic Vision"),
    THRONE_AND_RAINBOW("Emerald Rainbow Throne & Sea of Crystal Glass", "Apocalyptic Vision"),
    NEW_JERUSALEM_TREE("River of Crystal Water & The Tree of Life", "Apocalyptic Vision"),
    DAVID_HARP_PASTURES("David's Golden Harp & Still Waters of Zion", "Psalms & Proverbs"),
    SOLOMON_WISDOM_PILLARS("Seven Pillars of Royal Wisdom & Crown of Gold", "Psalms & Proverbs")
}

enum class ArtStyleMode(val label: String, val description: String) {
    STAINED_GLASS("Cathedral Glass", "Faceted jewel-tone rose tracery & leadwork"),
    FRESCO_PAINTING("Luminous Fresco", "Painterly gradients, atmospheric rays & depth"),
    ILLUMINATED_CODEX("Gold Leaf Codex", "Byzantine gilded borders, halos & filigree")
}

enum class BibleTranslation(val code: String, val displayName: String, val badge: String) {
    WEB("web", "World English Bible", "WEB"),
    KJV("kjv", "King James Version", "KJV")
}

enum class HighlightColor(
    val id: String,
    val label: String,
    val swatchColor: Color,
    val tintBackground: Color
) {
    GOLD("GOLD", "Byzantine Gold", Color(0xFFE6A832), Color(0x45F4C44E)),
    RUBY("RUBY", "Cana Ruby", Color(0xFFD94A64), Color(0x3DD94E67)),
    EMERALD("EMERALD", "Galilee Emerald", Color(0xFF2EA87E), Color(0x3D38B287)),
    SAPPHIRE("SAPPHIRE", "Lapis Sapphire", Color(0xFF438BE6), Color(0x3D4A90E2))
}

data class ArtPalette(
    val skyTop: Color,
    val skyMiddle: Color,
    val skyHorizon: Color,
    val primaryJewel: Color,
    val secondaryJewel: Color,
    val goldLeaf: Color,
    val foregroundSilhouette: Color,
    val waterOrGround: Color
)

data class BookInfo(
    val id: String,
    val apiName: String,
    val name: String,
    val shortName: String,
    val greekTitle: String,
    val author: String,
    val dateWritten: String,
    val chapterCount: Int,
    val category: TestamentCategory,
    val summary: String,
    val basePalette: ArtPalette,
    val defaultArchetype: SceneArchetype
)

data class ChapterArtMetadata(
    val bookId: String,
    val bookName: String,
    val chapterNumber: Int,
    val artTitle: String,
    val artSubtitle: String,
    val sceneArchetype: SceneArchetype,
    val visualMeditation: String,
    val keyThemeTag: String,
    val keyVersePreview: String,
    val palette: ArtPalette,
    val patternSeed: Int
)

data class VerseItem(
    val bookId: String,
    val chapterNumber: Int,
    val verseNumber: Int,
    val text: String,
    val isWordsOfChrist: Boolean = false
)

data class ChapterContent(
    val metadata: ChapterArtMetadata,
    val bookInfo: BookInfo,
    val verses: List<VerseItem>,
    val translation: BibleTranslation,
    val isFromLiveNetwork: Boolean
)
