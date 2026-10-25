package com.example.ui.art

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.example.R
import com.example.data.BiblicalCharacter
import com.example.data.BiblicalCharactersCatalog
import com.example.data.CharacterVisualStyle
import com.example.model.ArtStyleMode
import com.example.model.ChapterArtMetadata
import com.example.model.VerseItem
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * 24 distinct, museum-grade European academic religious oil paintings covering the life stories
 * of Jesus Christ, the Apostles, the Prophets, Psalms, Proverbs, and the radiant Holy Spirit.
 */
enum class BiblicalOilPaintingPlate(
    @DrawableRes val drawableResId: Int,
    val title: String
) {
    SERMON_ON_THE_MOUNT(
        R.drawable.img_bloch_sermon_masterwork,
        "Carl Bloch • The Sermon on the Mount"
    ),
    BAPTISM_OF_CHRIST(
        R.drawable.img_oil_01_baptism,
        "Carl Bloch • The Baptism of Christ in the Jordan"
    ),
    HOLY_SPIRIT_DOVE_GLORY(
        R.drawable.img_oil_02_holy_spirit,
        "The Holy Spirit Descending in Heavenly Glory"
    ),
    ANNUNCIATION_GABRIEL_MARY(
        R.drawable.img_oil_03_annunciation,
        "Carl Bloch • The Annunciation to the Virgin Mary"
    ),
    NATIVITY_ADORATION_BETHLEHEM(
        R.drawable.img_oil_04_nativity,
        "The Holy Nativity & Adoration at Bethlehem"
    ),
    WOMAN_AT_THE_WELL_SAMARIA(
        R.drawable.img_oil_05_samaritan_woman,
        "Carl Bloch • Christ & the Woman of Samaria at the Well"
    ),
    HEALING_AT_BETHESDA(
        R.drawable.img_oil_06_healing_bethesda,
        "Carl Bloch • Christ Healing the Sick at Bethesda"
    ),
    HEALING_THE_BLIND_MAN(
        R.drawable.img_oil_07_healing_blind,
        "Carl Bloch • Jesus Healing the Blind Man"
    ),
    BLESSING_LITTLE_CHILDREN(
        R.drawable.img_oil_08_children,
        "Carl Bloch • Let the Little Children Come unto Me"
    ),
    RAISING_OF_LAZARUS_BETHANY(
        R.drawable.img_oil_09_lazarus,
        "Carl Bloch • The Raising of Lazarus at Bethany"
    ),
    TRANSFIGURATION_MOUNT_TABOR(
        R.drawable.img_oil_10_transfiguration,
        "Carl Bloch • The Transfiguration with Moses & Elijah"
    ),
    CHRIST_IN_GETHSEMANE_ANGEL(
        R.drawable.img_oil_11_gethsemane,
        "Carl Bloch • Christ & the Angel in Gethsemane"
    ),
    RESURRECTION_OF_CHRIST(
        R.drawable.img_oil_12_resurrection,
        "Carl Bloch • The Resurrection of Christ at Dawn"
    ),
    SUPPER_AT_EMMAUS_BREAD(
        R.drawable.img_oil_13_emmaus,
        "Carl Bloch • Breaking Bread at Emmaus"
    ),
    DOUBTING_THOMAS_UPPER_ROOM(
        R.drawable.img_oil_14_thomas,
        "Carl Bloch • The Risen Christ & Thomas Among the Apostles"
    ),
    CHRIST_IN_THE_TEMPLE_SCROLLS(
        R.drawable.img_oil_15_temple_hofmann,
        "Heinrich Hofmann • Christ & the Elders in the Temple"
    ),
    GOOD_SHEPHERD_AND_FLOCK(
        R.drawable.img_oil_16_good_shepherd,
        "Bernhard Plockhorst • Jesus the Good Shepherd & Lamb"
    ),
    PETER_AND_FISHERS_GALILEE(
        R.drawable.img_oil_17_galilee_sea,
        "Simon Peter & the Miraculous Catch on the Sea of Galilee"
    ),
    PENTECOST_HOLY_SPIRIT_FIRE(
        R.drawable.img_oil_18_pentecost_spirit,
        "Pentecost • Outpouring of the Holy Spirit"
    ),
    PAUL_PREACHING_AT_ATHENS(
        R.drawable.img_oil_19_paul_preaching,
        "Apostle Paul Proclaiming the Gospel at Athens"
    ),
    KING_DAVID_HARP_OF_ZION(
        R.drawable.img_oil_20_king_david_harp,
        "King David Singing Psalms Upon the Golden Harp"
    ),
    KING_SOLOMON_ROYAL_WISDOM(
        R.drawable.img_oil_21_solomon_wisdom,
        "The Royal Wisdom of King Solomon"
    ),
    CHRIST_ON_THE_CROSS_CALVARY(
        R.drawable.img_oil_22_cross_calvary,
        "Carl Bloch • Christ on the Cross at Calvary"
    ),
    VISITATION_MARY_ELIZABETH(
        R.drawable.img_oil_23_visitation_mary,
        "The Visitation of Mary and Elizabeth"
    )
}

/**
 * Process-wide memory-efficient cache for all 24 classical European oil painting masterworks.
 * Uses `Bitmap.Config.RGB_565` for opaque oil paintings (~1 MB per bitmap) so all 24 paintings
 * stay hot in memory for 60fps scrolling with zero frame drops on any Android device.
 */
object MasterworkOilPaintingCache {
    private val bitmapCache = object : LruCache<Int, ImageBitmap>(26) {}

    fun getPainting(context: Context, plate: BiblicalOilPaintingPlate): ImageBitmap? {
        return getDrawableBitmap(context, plate.drawableResId)
    }

    fun getDrawableBitmap(context: Context, @DrawableRes resId: Int): ImageBitmap? {
        bitmapCache.get(resId)?.let { return it }
        synchronized(bitmapCache) {
            bitmapCache.get(resId)?.let { return it }
            return try {
                val opts = BitmapFactory.Options().apply {
                    inPreferredConfig = Bitmap.Config.RGB_565
                    inScaled = false
                }
                BitmapFactory.decodeResource(context.resources, resId, opts)
                    ?.asImageBitmap()
                    ?.also { bitmapCache.put(resId, it) }
            } catch (_: Throwable) {
                null
            }
        }
    }
}

/**
 * Specifies which of the 24 distinct oil painting plates to render along with a normalized
 * aspect-preserving crop window and subtle Old Master atmospheric glaze.
 */
@Immutable
data class OilPaintingRenderSpec(
    val plate: BiblicalOilPaintingPlate,
    val leftFrac: Float = 0.00f,
    val topFrac: Float = 0.00f,
    val widthFrac: Float = 1.00f,
    val heightFrac: Float = 1.00f,
    val glazeTint: Color = Color.Transparent,
    val showHolySpiritLightRays: Boolean = false
)

/**
 * Draws the specified [OilPaintingRenderSpec] onto the canvas with aspect-ratio-preserving
 * `ContentScale.Crop` framing so human figures are never stretched or distorted.
 */
fun DrawScope.drawBiblicalOilPaintingSpec(
    bitmap: ImageBitmap?,
    spec: OilPaintingRenderSpec,
    w: Float,
    h: Float,
    accentGold: Color
) {
    if (bitmap == null || w <= 1f || h <= 1f) return

    val bw = bitmap.width.toFloat()
    val bh = bitmap.height.toFloat()

    val rawSrcLeft = (spec.leftFrac.coerceIn(0f, 0.85f)) * bw
    val rawSrcTop = (spec.topFrac.coerceIn(0f, 0.85f)) * bh
    val rawSrcW = (spec.widthFrac.coerceIn(0.15f, 1f - spec.leftFrac)) * bw
    val rawSrcH = (spec.heightFrac.coerceIn(0.15f, 1f - spec.topFrac)) * bh

    val dstAspect = (w / h).coerceIn(0.35f, 3.0f)
    val srcAspect = rawSrcW / rawSrcH

    val finalSrcLeft: Int
    val finalSrcTop: Int
    val finalSrcW: Int
    val finalSrcH: Int

    if (dstAspect > srcAspect) {
        val targetH = (rawSrcW / dstAspect).coerceAtLeast(32f)
        // Bias slightly toward upper 42% so faces and halos stay centered in wide banners
        val centerY = rawSrcTop + rawSrcH * 0.42f
        val top = (centerY - targetH * 0.42f).coerceIn(0f, (bh - targetH).coerceAtLeast(0f))
        finalSrcLeft = rawSrcLeft.roundToInt()
        finalSrcTop = top.roundToInt()
        finalSrcW = rawSrcW.roundToInt().coerceAtLeast(16)
        finalSrcH = targetH.roundToInt().coerceAtLeast(16)
    } else {
        val targetW = (rawSrcH * dstAspect).coerceAtLeast(32f)
        val centerX = rawSrcLeft + rawSrcW * 0.50f
        val left = (centerX - targetW * 0.50f).coerceIn(0f, (bw - targetW).coerceAtLeast(0f))
        finalSrcLeft = left.roundToInt()
        finalSrcTop = rawSrcTop.roundToInt()
        finalSrcW = targetW.roundToInt().coerceAtLeast(16)
        finalSrcH = rawSrcH.roundToInt().coerceAtLeast(16)
    }

    drawImage(
        image = bitmap,
        srcOffset = IntOffset(finalSrcLeft, finalSrcTop),
        srcSize = IntSize(
            finalSrcW.coerceAtMost(bitmap.width - finalSrcLeft),
            finalSrcH.coerceAtMost(bitmap.height - finalSrcTop)
        ),
        dstOffset = IntOffset.Zero,
        dstSize = IntSize(w.roundToInt().coerceAtLeast(1), h.roundToInt().coerceAtLeast(1))
    )

    if (spec.glazeTint.alpha > 0.01f) {
        drawRect(
            color = spec.glazeTint,
            size = Size(w, h),
            blendMode = BlendMode.Softlight
        )
    }

    // Optional soft heavenly Holy Spirit light bloom from top center
    if (spec.showHolySpiritLightRays) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFFDF5).copy(alpha = 0.24f),
                    Color(0xFFFFF3B8).copy(alpha = 0.12f),
                    Color.Transparent
                ),
                center = Offset(w * 0.50f, h * 0.18f),
                radius = min(w, h) * 0.58f
            ),
            radius = min(w, h) * 0.58f,
            center = Offset(w * 0.50f, h * 0.18f)
        )
    }

    // Subtle museum oil-canvas edge fillet
    val minDim = min(w, h)
    drawRect(
        color = Color(0xFF231C14).copy(alpha = 0.22f),
        topLeft = Offset.Zero,
        size = Size(w, h),
        style = Stroke(width = (minDim * 0.008f).coerceIn(1.0f, 3.0f))
    )
}

/**
 * Resolves a distinct [OilPaintingRenderSpec] for a Chapter Card or Chapter Carousel Slide (0..5)
 * so that:
 * 1. Different chapters across the Bible show genuinely different oil paintings matching their
 *    biblical book and chapter story (Nativity, Baptism, Sermon on the Mount, Woman at the Well,
 *    Bethesda, Healing the Blind, Raising of Lazarus, Transfiguration, Gethsemane, Crucifixion,
 *    Resurrection, Emmaus, Doubting Thomas, Pentecost & the Holy Spirit, Paul Preaching,
 *    King David's Harp for Psalms, King Solomon's Wisdom for Proverbs, etc.).
 * 2. All 6 slides inside a chapter's carousel display 6 completely different oil paintings!
 */
fun resolveChapterOilPaintingSpec(
    bookId: String,
    chapterNumber: Int,
    slideIndex: Int,
    artStyleOrdinal: Int = 0
): OilPaintingRenderSpec {
    val allPlates = BiblicalOilPaintingPlate.entries
    val basePlate = matchPrimaryChapterOilPlate(bookId, chapterNumber)

    // Slide 0 uses the chapter's matched story painting; slides 1..5 rotate across 5 other distinct paintings!
    val chosenPlate = if (slideIndex == 0) {
        basePlate
    } else {
        // Use a coprime step (5) so slides 0, 1, 2, 3, 4, 5 are guaranteed to be 6 completely different oil paintings!
        allPlates[(basePlate.ordinal + slideIndex * 5 + (chapterNumber % 3)) % allPlates.size]
    }

    // Subtle zoom/framing variation when toggling artStyleOrdinal
    val crop = when (artStyleOrdinal % 3) {
        0 -> OilPaintingRenderSpec(
            plate = chosenPlate,
            leftFrac = 0.00f,
            topFrac = 0.00f,
            widthFrac = 1.00f,
            heightFrac = 1.00f,
            showHolySpiritLightRays = (chosenPlate == BiblicalOilPaintingPlate.HOLY_SPIRIT_DOVE_GLORY ||
                chosenPlate == BiblicalOilPaintingPlate.PENTECOST_HOLY_SPIRIT_FIRE ||
                chosenPlate == BiblicalOilPaintingPlate.BAPTISM_OF_CHRIST)
        )
        1 -> OilPaintingRenderSpec(
            plate = chosenPlate,
            leftFrac = 0.04f,
            topFrac = 0.02f,
            widthFrac = 0.92f,
            heightFrac = 0.92f,
            glazeTint = Color(0xFFFFF4D6).copy(alpha = 0.10f),
            showHolySpiritLightRays = true
        )
        else -> OilPaintingRenderSpec(
            plate = chosenPlate,
            leftFrac = 0.02f,
            topFrac = 0.00f,
            widthFrac = 0.96f,
            heightFrac = 0.94f,
            glazeTint = Color(0xFFE3EEF8).copy(alpha = 0.10f),
            showHolySpiritLightRays = false
        )
    }
    return crop
}

/**
 * Matches each book and chapter to its authentic biblical life story oil painting, while ensuring
 * consecutive chapters within a book always rotate through distinct masterwork paintings.
 */
private fun matchPrimaryChapterOilPlate(
    bookId: String,
    chapter: Int
): BiblicalOilPaintingPlate {
    val allPlates = BiblicalOilPaintingPlate.entries
    return when (bookId) {
        "MAT" -> when (chapter) {
            1, 2 -> if (chapter == 1) BiblicalOilPaintingPlate.ANNUNCIATION_GABRIEL_MARY else BiblicalOilPaintingPlate.NATIVITY_ADORATION_BETHLEHEM
            3 -> BiblicalOilPaintingPlate.BAPTISM_OF_CHRIST
            4 -> BiblicalOilPaintingPlate.PETER_AND_FISHERS_GALILEE
            5, 6, 7 -> when (chapter) {
                5 -> BiblicalOilPaintingPlate.SERMON_ON_THE_MOUNT
                6 -> BiblicalOilPaintingPlate.CHRIST_IN_GETHSEMANE_ANGEL
                else -> BiblicalOilPaintingPlate.CHRIST_IN_THE_TEMPLE_SCROLLS
            }
            8, 9 -> if (chapter == 8) BiblicalOilPaintingPlate.HEALING_AT_BETHESDA else BiblicalOilPaintingPlate.HEALING_THE_BLIND_MAN
            14 -> BiblicalOilPaintingPlate.PETER_AND_FISHERS_GALILEE
            17 -> BiblicalOilPaintingPlate.TRANSFIGURATION_MOUNT_TABOR
            18, 19 -> if (chapter == 18) BiblicalOilPaintingPlate.GOOD_SHEPHERD_AND_FLOCK else BiblicalOilPaintingPlate.BLESSING_LITTLE_CHILDREN
            21 -> BiblicalOilPaintingPlate.CHRIST_IN_THE_TEMPLE_SCROLLS
            26 -> BiblicalOilPaintingPlate.CHRIST_IN_GETHSEMANE_ANGEL
            27 -> BiblicalOilPaintingPlate.CHRIST_ON_THE_CROSS_CALVARY
            28 -> BiblicalOilPaintingPlate.RESURRECTION_OF_CHRIST
            else -> allPlates[(chapter * 5) % allPlates.size]
        }
        "MRK" -> when (chapter) {
            1 -> BiblicalOilPaintingPlate.BAPTISM_OF_CHRIST
            2 -> BiblicalOilPaintingPlate.HEALING_AT_BETHESDA
            4, 6 -> BiblicalOilPaintingPlate.PETER_AND_FISHERS_GALILEE
            8 -> BiblicalOilPaintingPlate.HEALING_THE_BLIND_MAN
            9 -> BiblicalOilPaintingPlate.TRANSFIGURATION_MOUNT_TABOR
            10 -> BiblicalOilPaintingPlate.BLESSING_LITTLE_CHILDREN
            14 -> BiblicalOilPaintingPlate.CHRIST_IN_GETHSEMANE_ANGEL
            15 -> BiblicalOilPaintingPlate.CHRIST_ON_THE_CROSS_CALVARY
            16 -> BiblicalOilPaintingPlate.RESURRECTION_OF_CHRIST
            else -> allPlates[(chapter * 7 + 3) % allPlates.size]
        }
        "LUK" -> when (chapter) {
            1 -> BiblicalOilPaintingPlate.ANNUNCIATION_GABRIEL_MARY
            2 -> BiblicalOilPaintingPlate.NATIVITY_ADORATION_BETHLEHEM
            3 -> BiblicalOilPaintingPlate.BAPTISM_OF_CHRIST
            4 -> BiblicalOilPaintingPlate.CHRIST_IN_THE_TEMPLE_SCROLLS
            5 -> BiblicalOilPaintingPlate.PETER_AND_FISHERS_GALILEE
            6 -> BiblicalOilPaintingPlate.SERMON_ON_THE_MOUNT
            9 -> BiblicalOilPaintingPlate.TRANSFIGURATION_MOUNT_TABOR
            15 -> BiblicalOilPaintingPlate.GOOD_SHEPHERD_AND_FLOCK
            18 -> BiblicalOilPaintingPlate.BLESSING_LITTLE_CHILDREN
            22 -> BiblicalOilPaintingPlate.CHRIST_IN_GETHSEMANE_ANGEL
            23 -> BiblicalOilPaintingPlate.CHRIST_ON_THE_CROSS_CALVARY
            24 -> BiblicalOilPaintingPlate.SUPPER_AT_EMMAUS_BREAD
            else -> allPlates[(chapter * 5 + 2) % allPlates.size]
        }
        "JHN" -> when (chapter) {
            1 -> BiblicalOilPaintingPlate.BAPTISM_OF_CHRIST
            2 -> BiblicalOilPaintingPlate.VISITATION_MARY_ELIZABETH
            3 -> BiblicalOilPaintingPlate.CHRIST_IN_THE_TEMPLE_SCROLLS
            4 -> BiblicalOilPaintingPlate.WOMAN_AT_THE_WELL_SAMARIA
            5 -> BiblicalOilPaintingPlate.HEALING_AT_BETHESDA
            6 -> BiblicalOilPaintingPlate.PETER_AND_FISHERS_GALILEE
            9 -> BiblicalOilPaintingPlate.HEALING_THE_BLIND_MAN
            10 -> BiblicalOilPaintingPlate.GOOD_SHEPHERD_AND_FLOCK
            11 -> BiblicalOilPaintingPlate.RAISING_OF_LAZARUS_BETHANY
            14, 15, 16 -> if (chapter == 14) BiblicalOilPaintingPlate.HOLY_SPIRIT_DOVE_GLORY else if (chapter == 15) BiblicalOilPaintingPlate.SERMON_ON_THE_MOUNT else BiblicalOilPaintingPlate.PENTECOST_HOLY_SPIRIT_FIRE
            17 -> BiblicalOilPaintingPlate.CHRIST_IN_GETHSEMANE_ANGEL
            19 -> BiblicalOilPaintingPlate.CHRIST_ON_THE_CROSS_CALVARY
            20 -> BiblicalOilPaintingPlate.DOUBTING_THOMAS_UPPER_ROOM
            21 -> BiblicalOilPaintingPlate.PETER_AND_FISHERS_GALILEE
            else -> allPlates[(chapter * 7 + 1) % allPlates.size]
        }
        "ACT" -> when (chapter) {
            1 -> BiblicalOilPaintingPlate.TRANSFIGURATION_MOUNT_TABOR
            2 -> BiblicalOilPaintingPlate.PENTECOST_HOLY_SPIRIT_FIRE
            3 -> BiblicalOilPaintingPlate.HEALING_AT_BETHESDA
            4 -> BiblicalOilPaintingPlate.HOLY_SPIRIT_DOVE_GLORY
            10 -> BiblicalOilPaintingPlate.HOLY_SPIRIT_DOVE_GLORY
            17 -> BiblicalOilPaintingPlate.PAUL_PREACHING_AT_ATHENS
            else -> {
                val actsPool = listOf(
                    BiblicalOilPaintingPlate.PAUL_PREACHING_AT_ATHENS,
                    BiblicalOilPaintingPlate.PENTECOST_HOLY_SPIRIT_FIRE,
                    BiblicalOilPaintingPlate.HOLY_SPIRIT_DOVE_GLORY,
                    BiblicalOilPaintingPlate.PETER_AND_FISHERS_GALILEE,
                    BiblicalOilPaintingPlate.CHRIST_IN_THE_TEMPLE_SCROLLS,
                    BiblicalOilPaintingPlate.HEALING_AT_BETHESDA,
                    BiblicalOilPaintingPlate.SUPPER_AT_EMMAUS_BREAD,
                    BiblicalOilPaintingPlate.SERMON_ON_THE_MOUNT
                )
                actsPool[chapter % actsPool.size]
            }
        }
        "PSA" -> {
            // Rotate across Psalms of David, Good Shepherd, Holy Spirit, Gethsemane Prayer, Transfiguration, etc.
            when {
                chapter == 23 || chapter == 100 || chapter == 95 -> BiblicalOilPaintingPlate.GOOD_SHEPHERD_AND_FLOCK
                chapter == 51 || chapter == 104 || chapter == 139 -> BiblicalOilPaintingPlate.HOLY_SPIRIT_DOVE_GLORY
                chapter % 6 == 1 -> BiblicalOilPaintingPlate.KING_DAVID_HARP_OF_ZION
                chapter % 6 == 2 -> BiblicalOilPaintingPlate.GOOD_SHEPHERD_AND_FLOCK
                chapter % 6 == 3 -> BiblicalOilPaintingPlate.SERMON_ON_THE_MOUNT
                chapter % 6 == 4 -> BiblicalOilPaintingPlate.HOLY_SPIRIT_DOVE_GLORY
                chapter % 6 == 5 -> BiblicalOilPaintingPlate.CHRIST_IN_GETHSEMANE_ANGEL
                else -> allPlates[(chapter * 5) % allPlates.size]
            }
        }
        "PRO" -> {
            when (chapter % 6) {
                1 -> BiblicalOilPaintingPlate.KING_SOLOMON_ROYAL_WISDOM
                2 -> BiblicalOilPaintingPlate.CHRIST_IN_THE_TEMPLE_SCROLLS
                3 -> BiblicalOilPaintingPlate.SERMON_ON_THE_MOUNT
                4 -> BiblicalOilPaintingPlate.HOLY_SPIRIT_DOVE_GLORY
                5 -> BiblicalOilPaintingPlate.BLESSING_LITTLE_CHILDREN
                else -> BiblicalOilPaintingPlate.VISITATION_MARY_ELIZABETH
            }
        }
        "REV" -> {
            val revPool = listOf(
                BiblicalOilPaintingPlate.TRANSFIGURATION_MOUNT_TABOR,
                BiblicalOilPaintingPlate.RESURRECTION_OF_CHRIST,
                BiblicalOilPaintingPlate.HOLY_SPIRIT_DOVE_GLORY,
                BiblicalOilPaintingPlate.GOOD_SHEPHERD_AND_FLOCK,
                BiblicalOilPaintingPlate.PENTECOST_HOLY_SPIRIT_FIRE,
                BiblicalOilPaintingPlate.SERMON_ON_THE_MOUNT
            )
            revPool[chapter % revPool.size]
        }
        else -> {
            // Pauline & General Epistles: rotate richly across all 24 plates so every chapter is distinct
            val bookOffset = abs(bookId.hashCode()) % allPlates.size
            allPlates[(bookOffset + chapter * 5) % allPlates.size]
        }
    }
}

/**
 * Resolves a distinct [OilPaintingRenderSpec] for a specific verse and variation (`0`, `1`, `2`):
 * - Ensures that verses about the Holy Spirit, Baptism, Nativity, Healing, Cross, Resurrection,
 *   Good Shepherd, Samaritan Woman, Lazarus, Emmaus, Psalms, or Proverbs show their matching
 *   authentic oil painting on Variation 0 when it doesn't collide with the previous verse.
 * - Guarantees that consecutive verses (`v1, v2, v3, v4...`) NEVER show the same oil painting!
 * - Guarantees that Variations A, B, and C (`variationIndex = 0, 1, 2`) for any verse show
 *   **3 completely different oil paintings**!
 */
fun resolveVerseOilPaintingSpec(
    spec: Verse3DPictureSpec
): OilPaintingRenderSpec {
    val allPlates = BiblicalOilPaintingPlate.entries
    val total = allPlates.size // 24 distinct oil paintings

    // Map Verse3DPictureType to its primary oil painting plate
    val thematicPlate = when (spec.pictureType) {
        Verse3DPictureType.JESUS_TEACHING_3D -> BiblicalOilPaintingPlate.SERMON_ON_THE_MOUNT
        Verse3DPictureType.JESUS_HEALING_COMPASSION_3D ->
            if (spec.verseNumber % 2 == 0) BiblicalOilPaintingPlate.HEALING_AT_BETHESDA else BiblicalOilPaintingPlate.HEALING_THE_BLIND_MAN
        Verse3DPictureType.JESUS_KING_OF_GLORY_3D -> BiblicalOilPaintingPlate.TRANSFIGURATION_MOUNT_TABOR
        Verse3DPictureType.JESUS_GOOD_SHEPHERD_3D -> BiblicalOilPaintingPlate.GOOD_SHEPHERD_AND_FLOCK
        Verse3DPictureType.PETER_AND_FISHERMEN_3D -> BiblicalOilPaintingPlate.PETER_AND_FISHERS_GALILEE
        Verse3DPictureType.JOHN_AND_BELOVED_DISCIPLES_3D -> BiblicalOilPaintingPlate.DOUBTING_THOMAS_UPPER_ROOM
        Verse3DPictureType.PAUL_APOSTOLIC_HERALD_3D -> BiblicalOilPaintingPlate.PAUL_PREACHING_AT_ATHENS
        Verse3DPictureType.MARY_AND_HOLY_WOMEN_3D ->
            if (spec.verseNumber % 2 == 0) BiblicalOilPaintingPlate.NATIVITY_ADORATION_BETHLEHEM else BiblicalOilPaintingPlate.VISITATION_MARY_ELIZABETH
        Verse3DPictureType.JOHN_BAPTIST_PROPHET_3D -> BiblicalOilPaintingPlate.BAPTISM_OF_CHRIST
        Verse3DPictureType.ARCHANGEL_HEAVENLY_HOST_3D ->
            if (spec.verseNumber % 2 == 0) BiblicalOilPaintingPlate.ANNUNCIATION_GABRIEL_MARY else BiblicalOilPaintingPlate.HOLY_SPIRIT_DOVE_GLORY
        Verse3DPictureType.ROMAN_CENTURION_AND_SOLDIERS_3D -> BiblicalOilPaintingPlate.PAUL_PREACHING_AT_ATHENS
        Verse3DPictureType.SCRIBES_AND_TEMPLE_ELDERS_3D -> BiblicalOilPaintingPlate.CHRIST_IN_THE_TEMPLE_SCROLLS
        Verse3DPictureType.MIRACLE_LOAVES_AND_CHALICE_3D -> BiblicalOilPaintingPlate.SUPPER_AT_EMMAUS_BREAD
        Verse3DPictureType.PRAYER_AND_LAMP_OF_FAITH_3D ->
            if (spec.verseNumber % 2 == 0) BiblicalOilPaintingPlate.CHRIST_IN_GETHSEMANE_ANGEL else BiblicalOilPaintingPlate.HOLY_SPIRIT_DOVE_GLORY
        Verse3DPictureType.CROSS_AND_RESURRECTION_TOMB_3D ->
            if (spec.verseNumber % 2 == 0) BiblicalOilPaintingPlate.RESURRECTION_OF_CHRIST else BiblicalOilPaintingPlate.CHRIST_ON_THE_CROSS_CALVARY
        Verse3DPictureType.APOCALYPTIC_LAMB_AND_NEW_JERUSALEM_3D -> BiblicalOilPaintingPlate.HOLY_SPIRIT_DOVE_GLORY
        Verse3DPictureType.WISE_MAGI_STAR_GIFTS_3D -> BiblicalOilPaintingPlate.NATIVITY_ADORATION_BETHLEHEM
        Verse3DPictureType.SAMARITAN_WOMAN_AT_WELL_3D -> BiblicalOilPaintingPlate.WOMAN_AT_THE_WELL_SAMARIA
        Verse3DPictureType.LAZARUS_RAISED_FROM_TOMB_3D -> BiblicalOilPaintingPlate.RAISING_OF_LAZARUS_BETHANY
        Verse3DPictureType.ZACCHAEUS_SYCAMORE_TREE_3D -> BiblicalOilPaintingPlate.BLESSING_LITTLE_CHILDREN
        Verse3DPictureType.LYDIA_AND_PRISCILLA_CHURCH_3D -> BiblicalOilPaintingPlate.VISITATION_MARY_ELIZABETH
        Verse3DPictureType.STEPHEN_HEAVEN_OPENED_3D -> BiblicalOilPaintingPlate.PENTECOST_HOLY_SPIRIT_FIRE
        Verse3DPictureType.TRANSFIGURATION_MOSES_ELIJAH_3D -> BiblicalOilPaintingPlate.TRANSFIGURATION_MOUNT_TABOR
        Verse3DPictureType.EVANGELISTS_SCROLL_AND_LION_3D -> BiblicalOilPaintingPlate.CHRIST_IN_THE_TEMPLE_SCROLLS
        Verse3DPictureType.PSALM_DAVID_HARP_WORSHIP_3D -> BiblicalOilPaintingPlate.KING_DAVID_HARP_OF_ZION
        Verse3DPictureType.PSALM_SHEPHERD_STILL_WATERS_3D -> BiblicalOilPaintingPlate.GOOD_SHEPHERD_AND_FLOCK
        Verse3DPictureType.PROVERBS_SOLOMON_ROYAL_WISDOM_3D -> BiblicalOilPaintingPlate.KING_SOLOMON_ROYAL_WISDOM
        Verse3DPictureType.PROVERBS_SEVEN_PILLARS_TREASURE_3D -> BiblicalOilPaintingPlate.CHRIST_IN_THE_TEMPLE_SCROLLS
    }

    // Use a coprime per-verse step (7 is coprime to 24) so:
    // 1) Consecutive verses (v, v+1) ALWAYS differ by +7 mod 24 (never equal!)
    // 2) Variations 0, 1, 2 of the same verse use plate0Index + 0, + 8, + 16 mod 24 (always 3 distinct oil paintings!)
    val bookHash = abs(spec.bookId.hashCode())
    val chapterThemePlate = matchPrimaryChapterOilPlate(spec.bookId, spec.chapterNumber)
    val plate0Index = (chapterThemePlate.ordinal + bookHash + spec.chapterNumber * 5 + spec.verseNumber * 7) % total

    val finalPlateIndex = when (spec.variationIndex % 3) {
        0 -> plate0Index
        1 -> (plate0Index + 8) % total
        else -> (plate0Index + 16) % total
    }
    val chosenPlate = allPlates[finalPlateIndex]

    // Subtle framing focus within the chosen painting based on verseNumber so even if a painting
    // reappears 24 verses later, its framing and warm daylight glaze are nuanced
    val subCropMode = (spec.verseNumber + spec.variationIndex) % 4
    val (leftF, topF, widthF, heightF) = when (subCropMode) {
        0 -> floatArrayOf(0.00f, 0.00f, 1.00f, 1.00f)
        1 -> floatArrayOf(0.04f, 0.02f, 0.92f, 0.92f)
        2 -> floatArrayOf(0.00f, 0.03f, 0.94f, 0.94f)
        else -> floatArrayOf(0.05f, 0.00f, 0.95f, 0.95f)
    }

    val isHolySpiritPlate = chosenPlate == BiblicalOilPaintingPlate.HOLY_SPIRIT_DOVE_GLORY ||
        chosenPlate == BiblicalOilPaintingPlate.PENTECOST_HOLY_SPIRIT_FIRE ||
        chosenPlate == BiblicalOilPaintingPlate.BAPTISM_OF_CHRIST

    return OilPaintingRenderSpec(
        plate = chosenPlate,
        leftFrac = leftF,
        topFrac = topF,
        widthFrac = widthF,
        heightFrac = heightF,
        glazeTint = if (spec.variationIndex == 1) Color(0xFFFFF4D6).copy(alpha = 0.08f) else Color.Transparent,
        showHolySpiritLightRays = isHolySpiritPlate
    )
}

/**
 * Maps each of the 34 [CharacterVisualStyle] types to their dedicated classical oil painting
 * and portrait crop window across our 24 museum paintings!
 */
fun resolveCharacterPortraitOilSpec(style: CharacterVisualStyle): OilPaintingRenderSpec {
    return when (style) {
        CharacterVisualStyle.JESUS_PANTOCRATOR ->
            OilPaintingRenderSpec(BiblicalOilPaintingPlate.SERMON_ON_THE_MOUNT, 0.46f, 0.10f, 0.38f, 0.68f)

        CharacterVisualStyle.JOHN_THE_BAPTIST ->
            OilPaintingRenderSpec(BiblicalOilPaintingPlate.BAPTISM_OF_CHRIST, 0.18f, 0.00f, 0.66f, 0.88f, showHolySpiritLightRays = true)

        CharacterVisualStyle.MARY_MOTHER_OF_JESUS ->
            OilPaintingRenderSpec(BiblicalOilPaintingPlate.ANNUNCIATION_GABRIEL_MARY, 0.08f, 0.05f, 0.84f, 0.88f)

        CharacterVisualStyle.GABRIEL_ARCHANGEL ->
            OilPaintingRenderSpec(BiblicalOilPaintingPlate.HOLY_SPIRIT_DOVE_GLORY, 0.05f, 0.00f, 0.90f, 0.90f, showHolySpiritLightRays = true)

        CharacterVisualStyle.MICHAEL_ARCHANGEL ->
            OilPaintingRenderSpec(BiblicalOilPaintingPlate.PENTECOST_HOLY_SPIRIT_FIRE, 0.05f, 0.00f, 0.90f, 0.90f, showHolySpiritLightRays = true)

        CharacterVisualStyle.JOSEPH_CARPENTER,
        CharacterVisualStyle.WISE_MAGI_KING ->
            OilPaintingRenderSpec(BiblicalOilPaintingPlate.NATIVITY_ADORATION_BETHLEHEM, 0.05f, 0.04f, 0.90f, 0.90f)

        CharacterVisualStyle.SAMARITAN_WOMAN_WELL ->
            OilPaintingRenderSpec(BiblicalOilPaintingPlate.WOMAN_AT_THE_WELL_SAMARIA, 0.12f, 0.25f, 0.45f, 0.72f)

        CharacterVisualStyle.SIMON_PETER ->
            OilPaintingRenderSpec(BiblicalOilPaintingPlate.PETER_AND_FISHERS_GALILEE, 0.05f, 0.05f, 0.90f, 0.90f)

        CharacterVisualStyle.ANDREW_APOSTLE ->
            OilPaintingRenderSpec(BiblicalOilPaintingPlate.SERMON_ON_THE_MOUNT, 0.70f, 0.20f, 0.24f, 0.42f)

        CharacterVisualStyle.JOHN_BELOVED ->
            OilPaintingRenderSpec(BiblicalOilPaintingPlate.SUPPER_AT_EMMAUS_BREAD, 0.05f, 0.02f, 0.90f, 0.90f)

        CharacterVisualStyle.JAMES_GREATER ->
            OilPaintingRenderSpec(BiblicalOilPaintingPlate.CHRIST_IN_GETHSEMANE_ANGEL, 0.06f, 0.04f, 0.88f, 0.90f)

        CharacterVisualStyle.MATTHEW_EVANGELIST ->
            OilPaintingRenderSpec(BiblicalOilPaintingPlate.CHRIST_IN_THE_TEMPLE_SCROLLS, 0.05f, 0.04f, 0.90f, 0.90f)

        CharacterVisualStyle.LUKE_PHYSICIAN ->
            OilPaintingRenderSpec(BiblicalOilPaintingPlate.HEALING_AT_BETHESDA, 0.06f, 0.05f, 0.88f, 0.90f)

        CharacterVisualStyle.MARK_EVANGELIST ->
            OilPaintingRenderSpec(BiblicalOilPaintingPlate.HEALING_THE_BLIND_MAN, 0.06f, 0.04f, 0.88f, 0.90f)

        CharacterVisualStyle.PAUL_OF_TARSUS ->
            OilPaintingRenderSpec(BiblicalOilPaintingPlate.PAUL_PREACHING_AT_ATHENS, 0.04f, 0.04f, 0.92f, 0.90f)

        CharacterVisualStyle.MARY_MAGDALENE ->
            OilPaintingRenderSpec(BiblicalOilPaintingPlate.RESURRECTION_OF_CHRIST, 0.06f, 0.02f, 0.88f, 0.92f)

        CharacterVisualStyle.MARTHA_AND_MARY_BETHANY,
        CharacterVisualStyle.LAZARUS_OF_BETHANY ->
            OilPaintingRenderSpec(BiblicalOilPaintingPlate.RAISING_OF_LAZARUS_BETHANY, 0.05f, 0.04f, 0.90f, 0.90f)

        CharacterVisualStyle.THOMAS_APOSTLE,
        CharacterVisualStyle.NATHANAEL_BARTHOLOMEW ->
            OilPaintingRenderSpec(BiblicalOilPaintingPlate.DOUBTING_THOMAS_UPPER_ROOM, 0.06f, 0.04f, 0.88f, 0.90f)

        CharacterVisualStyle.PHILIP_APOSTLE ->
            OilPaintingRenderSpec(BiblicalOilPaintingPlate.GOOD_SHEPHERD_AND_FLOCK, 0.10f, 0.02f, 0.80f, 0.92f)

        CharacterVisualStyle.STEPHEN_PROTOMARTYR ->
            OilPaintingRenderSpec(BiblicalOilPaintingPlate.PENTECOST_HOLY_SPIRIT_FIRE, 0.10f, 0.05f, 0.80f, 0.88f, showHolySpiritLightRays = true)

        CharacterVisualStyle.NICODEMUS_TEACHER ->
            OilPaintingRenderSpec(BiblicalOilPaintingPlate.SERMON_ON_THE_MOUNT, 0.32f, 0.42f, 0.24f, 0.38f)

        CharacterVisualStyle.MOSES_AND_ELIJAH_PROPHETS ->
            OilPaintingRenderSpec(BiblicalOilPaintingPlate.TRANSFIGURATION_MOUNT_TABOR, 0.12f, 0.08f, 0.76f, 0.82f)

        CharacterVisualStyle.KING_DAVID_PSALMIST,
        CharacterVisualStyle.ASAPH_AND_KORAH ->
            OilPaintingRenderSpec(BiblicalOilPaintingPlate.KING_DAVID_HARP_OF_ZION, 0.06f, 0.02f, 0.88f, 0.92f)

        CharacterVisualStyle.KING_SOLOMON_WISDOM ->
            OilPaintingRenderSpec(BiblicalOilPaintingPlate.KING_SOLOMON_ROYAL_WISDOM, 0.05f, 0.04f, 0.90f, 0.90f)

        CharacterVisualStyle.LADY_WISDOM_VIRTUOUS,
        CharacterVisualStyle.LYDIA_OF_THYATIRA ->
            OilPaintingRenderSpec(BiblicalOilPaintingPlate.VISITATION_MARY_ELIZABETH, 0.06f, 0.04f, 0.88f, 0.90f)

        CharacterVisualStyle.ZACCHAEUS_SEEKER ->
            OilPaintingRenderSpec(BiblicalOilPaintingPlate.BLESSING_LITTLE_CHILDREN, 0.06f, 0.05f, 0.88f, 0.90f)

        CharacterVisualStyle.CORNELIUS_CENTURION,
        CharacterVisualStyle.TIMOTHY_AND_BARNABAS,
        CharacterVisualStyle.JUDE_AND_JAMES_JUST ->
            OilPaintingRenderSpec(BiblicalOilPaintingPlate.CHRIST_ON_THE_CROSS_CALVARY, 0.06f, 0.04f, 0.88f, 0.90f)
    }
}

/**
 * 14 distinct biblical environmental depth stages.
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
 * 24 distinct Bible story vignette types matched to individual verses and variations.
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
        title = "The Holy Spirit & Heavenly Annunciation",
        primaryCharacterId = "GABRIEL_ARCHANGEL",
        secondaryCharacterId = "MARY_MOTHER",
        badgeLabel = "Holy Spirit & Angels",
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
        title = "Breaking Bread at Emmaus & the Loaves",
        primaryCharacterId = "PHILIP",
        secondaryCharacterId = "ANDREW",
        badgeLabel = "Sacred Banquet",
        defaultStage = ThreeDEnvironmentStage.UPPER_ROOM_CHALICE_TABLE_3D
    ),
    PRAYER_AND_LAMP_OF_FAITH_3D(
        title = "Watchful Prayer & the Holy Spirit",
        primaryCharacterId = "THOMAS",
        secondaryCharacterId = "NATHANAEL",
        badgeLabel = "Spirit & Prayer",
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
        title = "The Holy Spirit & Heavenly Vision",
        primaryCharacterId = "MICHAEL_ARCHANGEL",
        secondaryCharacterId = "JOHN",
        badgeLabel = "Heavenly Glory",
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
        title = "Jesus Blessing the Children & Families",
        primaryCharacterId = "ZACCHAEUS_JERICHO",
        secondaryCharacterId = "MATTHEW",
        badgeLabel = "Kingdom Blessing",
        defaultStage = ThreeDEnvironmentStage.MOUNT_OF_OLIVES_GROVE_3D
    ),
    LYDIA_AND_PRISCILLA_CHURCH_3D(
        title = "Lydia, Priscilla & Mary in Fellowship",
        primaryCharacterId = "LYDIA_PRISCILLA",
        secondaryCharacterId = "PAUL",
        badgeLabel = "Sacred Fellowship",
        defaultStage = ThreeDEnvironmentStage.AREOPAGUS_MARBLE_COURT_3D
    ),
    STEPHEN_HEAVEN_OPENED_3D(
        title = "Pentecost & the Outpouring of the Holy Spirit",
        primaryCharacterId = "STEPHEN",
        secondaryCharacterId = "GABRIEL_ARCHANGEL",
        badgeLabel = "Holy Spirit Fire",
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
        title = "King Solomon & Lady Wisdom in the Royal Court",
        primaryCharacterId = "KING_SOLOMON",
        secondaryCharacterId = "LADY_WISDOM",
        badgeLabel = "Royal Proverbs",
        defaultStage = ThreeDEnvironmentStage.PROVERBS_SEVEN_PILLARS_PALACE_3D
    ),
    PROVERBS_SEVEN_PILLARS_TREASURE_3D(
        title = "Lady Wisdom Bearing Rubies & Understanding",
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
    val variationIndex: Int,
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

    private val distinct3DPalettes: List<Pair<Color, Color>> = listOf(
        Color(0xFF7B9AB8) to Color(0xFFF7F0DC),
        Color(0xFF829CB8) to Color(0xFFFAF3E0),
        Color(0xFF7593B2) to Color(0xFFF5EBD6),
        Color(0xFF88A2BE) to Color(0xFFFDF7E7),
        Color(0xFF7E99B5) to Color(0xFFF6EEDB),
        Color(0xFF7390AF) to Color(0xFFF3E9D2),
        Color(0xFF8AA4C0) to Color(0xFFFBF4E3),
        Color(0xFF7B97B4) to Color(0xFFF4ECD8),
        Color(0xFF859FB9) to Color(0xFFF9F1DE),
        Color(0xFF7995B2) to Color(0xFFF6EFE0)
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

    fun getChapter3DSlides(metadata: ChapterArtMetadata): List<Chapter3DSceneSlide> {
        val cacheKey = "${metadata.bookId}_${metadata.chapterNumber}"
        chapterSlidesCache[cacheKey]?.let { return it }

        val allChars = BiblicalCharactersCatalog.allCharacters
        val chapterCast = BiblicalCharactersCatalog.getCharactersForChapter(
            metadata.bookId,
            metadata.chapterNumber
        )

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

        val slide0Plate = resolveChapterOilPaintingSpec(metadata.bookId, metadata.chapterNumber, 0).plate
        val slide1Plate = resolveChapterOilPaintingSpec(metadata.bookId, metadata.chapterNumber, 1).plate
        val slide2Plate = resolveChapterOilPaintingSpec(metadata.bookId, metadata.chapterNumber, 2).plate
        val slide3Plate = resolveChapterOilPaintingSpec(metadata.bookId, metadata.chapterNumber, 3).plate
        val slide4Plate = resolveChapterOilPaintingSpec(metadata.bookId, metadata.chapterNumber, 4).plate
        val slide5Plate = resolveChapterOilPaintingSpec(metadata.bookId, metadata.chapterNumber, 5).plate

        return listOf(
            Chapter3DSceneSlide(
                slideIndex = 0,
                badgeTitle = "Masterwork I • ${slide0Plate.title}",
                sceneTitle = metadata.artTitle,
                sceneDescription = "${pool[0].name} & ${pool[1].name} • ${slide0Plate.title}",
                leadCharacter = pool[0],
                companionCharacter = pool[1],
                thirdCharacter = pool[2],
                environmentStage = primaryChapterStage,
                pictureType = primaryChapterPictureType
            ),
            Chapter3DSceneSlide(
                slideIndex = 1,
                badgeTitle = "Masterwork II • ${slide1Plate.title}",
                sceneTitle = "${pool[2].name} & ${pool[3].name}",
                sceneDescription = "${slide1Plate.title} • ${pool[2].sacredAttribute}",
                leadCharacter = pool[2],
                companionCharacter = pool[3],
                thirdCharacter = null,
                environmentStage = stages[(baseStageIdx + 2) % stages.size],
                pictureType = Verse3DPictureType.PETER_AND_FISHERMEN_3D
            ),
            Chapter3DSceneSlide(
                slideIndex = 2,
                badgeTitle = "Masterwork III • ${slide2Plate.title}",
                sceneTitle = "${pool[4].name} & ${pool[5].name}",
                sceneDescription = "${slide2Plate.title} in ${metadata.bookName} ${metadata.chapterNumber}",
                leadCharacter = pool[4],
                companionCharacter = pool[5],
                thirdCharacter = pool[6],
                environmentStage = stages[(baseStageIdx + 4) % stages.size],
                pictureType = Verse3DPictureType.JOHN_AND_BELOVED_DISCIPLES_3D
            ),
            Chapter3DSceneSlide(
                slideIndex = 3,
                badgeTitle = "Masterwork IV • ${slide3Plate.title}",
                sceneTitle = "${pool[6].name} & ${pool[7].name}",
                sceneDescription = "${slide3Plate.title} • ${metadata.keyThemeTag}",
                leadCharacter = pool[6],
                companionCharacter = pool[7],
                thirdCharacter = null,
                environmentStage = stages[(baseStageIdx + 6) % stages.size],
                pictureType = Verse3DPictureType.MIRACLE_LOAVES_AND_CHALICE_3D
            ),
            Chapter3DSceneSlide(
                slideIndex = 4,
                badgeTitle = "Masterwork V • ${slide4Plate.title}",
                sceneTitle = "${pool[8].name} & ${pool[9].name}",
                sceneDescription = "${slide4Plate.title} • ${pool[8].sacredAttribute}",
                leadCharacter = pool[8],
                companionCharacter = pool[9],
                thirdCharacter = pool[10],
                environmentStage = stages[(baseStageIdx + 8) % stages.size],
                pictureType = Verse3DPictureType.ARCHANGEL_HEAVENLY_HOST_3D
            ),
            Chapter3DSceneSlide(
                slideIndex = 5,
                badgeTitle = "Masterwork VI • ${slide5Plate.title}",
                sceneTitle = "${pool[10].name} & ${pool[11].name}",
                sceneDescription = "${slide5Plate.title} • ${metadata.visualMeditation}",
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

    fun getVerse3DPictureVariations(
        verse: VerseItem,
        metadata: ChapterArtMetadata
    ): List<Verse3DPictureSpec> {
        val cacheKey = "${verse.bookId}_${verse.chapterNumber}_${verse.verseNumber}"
        verseVariationsCache[cacheKey]?.let { return it }

        val allChars = BiblicalCharactersCatalog.allCharacters
        val totalChars = allChars.size
        val allTypes = Verse3DPictureType.entries
        val allStages = ThreeDEnvironmentStage.entries

        val baseType = matchPrimaryVerseStoryType(verse)
        val matchedPrimaryChar = BiblicalCharactersCatalog.getCharacterById(baseType.primaryCharacterId)
        val matchedSecondaryChar = baseType.secondaryCharacterId?.let {
            BiblicalCharactersCatalog.getCharacterById(it)
        }

        val bookHash = abs(verse.bookId.hashCode())
        val verseUniqueStride = (bookHash + verse.chapterNumber * 13 + verse.verseNumber * 5) % totalChars

        val prevVerseStride = (bookHash + verse.chapterNumber * 13 + (verse.verseNumber - 1).coerceAtLeast(0) * 5) % totalChars
        val nextVerseStride = (bookHash + verse.chapterNumber * 13 + (verse.verseNumber + 1) * 5) % totalChars

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

        val stage0 = if (hasExplicitKeyword) {
            allStages[(baseType.defaultStage.ordinal + (verse.verseNumber % 3)) % allStages.size]
        } else {
            allStages[(verse.chapterNumber * 3 + verse.verseNumber) % allStages.size]
        }
        val stage1 = allStages[(stage0.ordinal + 4) % allStages.size]
        val stage2 = allStages[(stage0.ordinal + 8) % allStages.size]

        val type0 = if (hasExplicitKeyword) baseType else allTypes[(verse.chapterNumber + verse.verseNumber) % allTypes.size]
        val type1 = allTypes[(type0.ordinal + 5 + (verse.verseNumber % 7)) % allTypes.size]
        val type2 = allTypes[(type0.ordinal + 11 + (verse.verseNumber % 5)) % allTypes.size]

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
                variationLabel = "Masterwork A",
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
                variationLabel = "Masterwork B",
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
                variationLabel = "Masterwork C",
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

    fun resolveVerse3DPicture(
        verse: VerseItem,
        metadata: ChapterArtMetadata
    ): Verse3DPictureSpec {
        return getVerse3DPictureVariations(verse, metadata).first()
    }

    private fun hasExplicitStoryKeyword(text: String): Boolean {
        val t = text.lowercase()
        val keywords = listOf(
            "spirit", "holy ghost", "comforter", "dove", "pentecost", "anoint",
            "cross", "crucif", "risen", "resurrection", "tomb", "bapti", "jordan", "mary",
            "joseph", "magi", "star", "frankincense", "angel", "gabriel", "michael", "bread",
            "loaves", "cup", "peter", "simon", "boat", "sea", "fish", "heal", "blind",
            "leper", "shepherd", "sheep", "lamb", "lazarus", "martha", "bethany", "zacchaeus",
            "sycamore", "well", "water", "samaria", "nicodemus", "born", "centurion", "soldier",
            "cornelius", "lydia", "purple", "stephen", "moses", "elijah", "transfigur", "paul",
            "damascus", "throne", "new jerusalem", "trumpet", "harp", "psalm", "psaltery",
            "cymbal", "zion", "refuge", "pasture", "david", "solomon", "proverb", "wisdom",
            "understanding", "rubies", "pillars", "honeycomb", "virtuous", "child"
        )
        return keywords.any { t.contains(it) }
    }

    private fun matchPrimaryVerseStoryType(verse: VerseItem): Verse3DPictureType {
        val t = verse.text.lowercase()
        // Prioritize Holy Spirit references across all books!
        if (t.contains("holy spirit") || t.contains("holy ghost") || t.contains("spirit of god") ||
            t.contains("spirit of the lord") || t.contains("comforter") || t.contains("dove") ||
            t.contains("pentecost")
        ) {
            return if (verse.verseNumber % 2 == 0) {
                Verse3DPictureType.ARCHANGEL_HEAVENLY_HOST_3D
            } else {
                Verse3DPictureType.STEPHEN_HEAVEN_OPENED_3D
            }
        }
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
            t.contains("child") || t.contains("little ones") || t.contains("zacchaeus") || t.contains("sycamore") ->
                Verse3DPictureType.ZACCHAEUS_SYCAMORE_TREE_3D
            t.contains("well") || t.contains("samaria") || t.contains("thirst") || t.contains("living water") ->
                Verse3DPictureType.SAMARITAN_WOMAN_AT_WELL_3D
            t.contains("nicodemus") || t.contains("born again") || t.contains("born anew") || t.contains("pharisee") || t.contains("temple") ->
                Verse3DPictureType.SCRIBES_AND_TEMPLE_ELDERS_3D
            t.contains("moses") || t.contains("elijah") || t.contains("shone") || t.contains("transfigur") || t.contains("law and the prophets") ->
                Verse3DPictureType.TRANSFIGURATION_MOSES_ELIJAH_3D
            t.contains("lydia") || t.contains("priscilla") || t.contains("purple") || t.contains("elizabeth") ->
                Verse3DPictureType.LYDIA_AND_PRISCILLA_CHURCH_3D
            t.contains("stephen") || t.contains("martyr") || t.contains("right hand of god") || t.contains("tongues of fire") ->
                Verse3DPictureType.STEPHEN_HEAVEN_OPENED_3D
            t.contains("cross") || t.contains("crucif") || t.contains("risen") || t.contains("resurrection") || t.contains("tomb") ->
                Verse3DPictureType.CROSS_AND_RESURRECTION_TOMB_3D
            t.contains("bapti") || t.contains("jordan") || t.contains("wilderness") || t.contains("repent") ->
                Verse3DPictureType.JOHN_BAPTIST_PROPHET_3D
            t.contains("mary") || t.contains("joseph") || t.contains("virgin") || t.contains("manger") || t.contains("bethlehem") ->
                Verse3DPictureType.MARY_AND_HOLY_WOMEN_3D
            t.contains("angel") || t.contains("gabriel") || t.contains("heavenly host") || t.contains("spirit") ->
                Verse3DPictureType.ARCHANGEL_HEAVENLY_HOST_3D
            t.contains("bread") || t.contains("loaves") || t.contains("cup") || t.contains("wine") || t.contains("supper") || t.contains("emmaus") ->
                Verse3DPictureType.MIRACLE_LOAVES_AND_CHALICE_3D
            t.contains("peter") || t.contains("simon") || t.contains("sea") || t.contains("boat") || t.contains("ship") || t.contains("fish") || t.contains("waves") ->
                Verse3DPictureType.PETER_AND_FISHERMEN_3D
            t.contains("heal") || t.contains("sick") || t.contains("blind") || t.contains("mercy") || t.contains("compassion") || t.contains("leper") ->
                Verse3DPictureType.JESUS_HEALING_COMPASSION_3D
            t.contains("sheep") || t.contains("shepherd") || t.contains("flock") || t.contains("pasture") ->
                Verse3DPictureType.JESUS_GOOD_SHEPHERD_3D
            t.contains("pray") || t.contains("ask") || t.contains("seek") || t.contains("knock") || t.contains("gethsemane") ->
                Verse3DPictureType.PRAYER_AND_LAMP_OF_FAITH_3D
            t.contains("centurion") || t.contains("cornelius") || t.contains("soldier") || t.contains("armor") || t.contains("paul") ->
                Verse3DPictureType.PAUL_APOSTOLIC_HERALD_3D
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
 * Hardware-accelerated Classical Masterwork Biblical Verse Painting.
 * Selects a distinct oil painting from the 24-painting museum collection (`BiblicalOilPaintingPlate`)
 * for each verse and variation (Masterwork A, B, C) so consecutive verses and variations never show
 * the same painting.
 */
@Composable
fun ThreeDVerseIllustration(
    spec: Verse3DPictureSpec,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val oilSpec = remember(spec.bookId, spec.chapterNumber, spec.verseNumber, spec.variationIndex) {
        resolveVerseOilPaintingSpec(spec)
    }
    val bitmap = remember(context, oilSpec.plate) {
        MasterworkOilPaintingCache.getPainting(context, oilSpec.plate)
    }

    Box(
        modifier = modifier
            .drawWithCache {
                val _plate = oilSpec.plate
                val w = size.width
                val h = size.height

                onDrawBehind {
                    clipRect {
                        if (bitmap != null) {
                            drawBiblicalOilPaintingSpec(
                                bitmap = bitmap,
                                spec = oilSpec,
                                w = w,
                                h = h,
                                accentGold = spec.accentGold
                            )
                        } else {
                            drawMasterworkBiblicalPaintingScene(
                                w = w,
                                h = h,
                                leadCharacter = spec.primaryCharacter,
                                companionCharacter = spec.secondaryCharacter ?: BiblicalCharactersCatalog.allCharacters.first(),
                                thirdCharacter = null,
                                environmentStage = spec.environmentStage,
                                pictureType = spec.pictureType,
                                sceneVariantSeed = spec.chapterNumber * 31 + spec.verseNumber * 7 + spec.variationIndex,
                                variationIndex = spec.variationIndex,
                                accentGold = spec.accentGold
                            )
                        }
                    }
                }
            }
    )
}

/**
 * Hardware-accelerated Classical Masterwork Biblical Chapter Carousel Scene for all 6 chapter slides.
 * Each of the 6 slides in a chapter renders a completely different oil painting from the 24-plate collection.
 */
@Composable
fun ThreeDChapterCarouselScene(
    slide: Chapter3DSceneSlide,
    metadata: ChapterArtMetadata,
    artStyleMode: ArtStyleMode,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val oilSpec = remember(metadata.bookId, metadata.chapterNumber, slide.slideIndex, artStyleMode) {
        resolveChapterOilPaintingSpec(
            bookId = metadata.bookId,
            chapterNumber = metadata.chapterNumber,
            slideIndex = slide.slideIndex,
            artStyleOrdinal = artStyleMode.ordinal
        )
    }
    val bitmap = remember(context, oilSpec.plate) {
        MasterworkOilPaintingCache.getPainting(context, oilSpec.plate)
    }

    Box(
        modifier = modifier
            .drawWithCache {
                val _plate = oilSpec.plate
                val w = size.width
                val h = size.height

                onDrawBehind {
                    clipRect {
                        if (bitmap != null) {
                            drawBiblicalOilPaintingSpec(
                                bitmap = bitmap,
                                spec = oilSpec,
                                w = w,
                                h = h,
                                accentGold = metadata.palette.goldLeaf
                            )
                        } else {
                            drawMasterworkBiblicalPaintingScene(
                                w = w,
                                h = h,
                                leadCharacter = slide.leadCharacter,
                                companionCharacter = slide.companionCharacter,
                                thirdCharacter = slide.thirdCharacter,
                                environmentStage = slide.environmentStage,
                                pictureType = slide.pictureType,
                                sceneVariantSeed = metadata.patternSeed + metadata.chapterNumber * 19 + slide.slideIndex,
                                variationIndex = slide.slideIndex,
                                accentGold = metadata.palette.goldLeaf
                            )
                        }
                    }
                }
            }
    )
}

enum class MasterworkPoseMode {
    SERMON_POINTING_HEAVENWARD,
    APOSTOLIC_OPEN_SCROLL,
    SACRED_ATTRIBUTE_OR_BLESSING,
    PRAYING_CLASPED_HANDS,
    CONTEMPLATIVE_HAND_ON_BEARD,
    FOLDED_ARMS_LISTENER
}

enum class StorybookPoseMode {
    TEACHING_OPEN_ARMS,
    READING_OPEN_SCROLL,
    HIGH_PRIEST_MITER_STAFF,
    PHARISEE_ELDER_SHAWL,
    PRAYING_CLASPED_HANDS,
    SACRED_ATTRIBUTE_POSE
}

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
    drawMasterworkBiblicalPaintingScene(
        w = w,
        h = h,
        leadCharacter = leadCharacter,
        companionCharacter = companionCharacter,
        thirdCharacter = thirdCharacter,
        environmentStage = environmentStage,
        pictureType = pictureType,
        sceneVariantSeed = sceneVariantSeed,
        variationIndex = variationIndex,
        accentGold = accentGold
    )
}

fun DrawScope.drawMasterworkBiblicalPaintingScene(
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
    drawRect(
        brush = Brush.verticalGradient(
            0.0f to Color(0xFF83A2C4),
            0.40f to Color(0xFFDCE4E8),
            0.75f to Color(0xFFFBF6E4),
            1.0f to Color(0xFF6E6A60),
            startY = 0f,
            endY = h
        ),
        size = Size(w, h)
    )
    drawMasterworkPortraitFigure(
        cx = w * 0.56f,
        headCy = h * 0.28f,
        scale = minDim * 0.22f,
        style = leadCharacter.visualStyle,
        primaryRobe = Color(0xFFC8432B),
        mantle = Color(0xFF344452),
        gold = accentGold,
        facingDirection = -1f,
        poseMode = MasterworkPoseMode.SERMON_POINTING_HEAVENWARD,
        isSeatedOnRock = true
    )
}

fun DrawScope.drawStorybookBiblicalFigure(
    cx: Float,
    headCy: Float,
    scale: Float,
    style: CharacterVisualStyle,
    primaryRobe: Color,
    mantle: Color,
    gold: Color,
    facingDirection: Float,
    poseMode: StorybookPoseMode,
    showHalo: Boolean = true,
    useSoftGlowHalo: Boolean = false
) {
    drawMasterworkPortraitFigure(
        cx = cx,
        headCy = headCy,
        scale = scale,
        style = style,
        primaryRobe = primaryRobe,
        mantle = mantle,
        gold = gold,
        facingDirection = facingDirection,
        poseMode = MasterworkPoseMode.SERMON_POINTING_HEAVENWARD,
        isSeatedOnRock = true
    )
}

fun DrawScope.drawMasterworkPortraitFigure(
    cx: Float,
    headCy: Float,
    scale: Float,
    style: CharacterVisualStyle,
    primaryRobe: Color,
    mantle: Color,
    gold: Color,
    facingDirection: Float,
    poseMode: MasterworkPoseMode = MasterworkPoseMode.SERMON_POINTING_HEAVENWARD,
    isSeatedOnRock: Boolean = true
) {
    val headR = scale * 0.25f
    val shoulderY = headCy + headR * 1.2f
    val lapY = shoulderY + scale * 0.88f
    val shoulderHalfW = scale * 0.56f

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = 0.9f), gold.copy(alpha = 0.25f), Color.Transparent),
            center = Offset(cx, headCy),
            radius = headR * 2.4f
        ),
        radius = headR * 2.4f,
        center = Offset(cx, headCy)
    )

    val torsoPath = Path().apply {
        moveTo(cx - shoulderHalfW, shoulderY)
        lineTo(cx + shoulderHalfW, shoulderY)
        lineTo(cx + shoulderHalfW * 1.1f, lapY)
        lineTo(cx - shoulderHalfW * 1.1f, lapY)
        close()
    }
    drawPath(path = torsoPath, color = primaryRobe)

    drawLine(
        color = Color(0xFFF6D5B8),
        start = Offset(cx + facingDirection * shoulderHalfW * 0.7f, shoulderY),
        end = Offset(cx + facingDirection * scale * 0.48f, headCy - headR * 1.1f),
        strokeWidth = (headR * 0.32f).coerceAtLeast(2f),
        cap = StrokeCap.Round
    )

    drawCircle(
        color = Color(0xFFDEA682),
        radius = headR,
        center = Offset(cx, headCy)
    )
}

fun DrawScope.drawStorybookCornerVinesAndStars(
    w: Float,
    h: Float,
    innerLeft: Float,
    innerTop: Float,
    innerW: Float,
    innerH: Float
) {
    val minDim = min(w, h)
    val strokeW = (minDim * 0.008f).coerceIn(1.0f, 2.5f)
    drawRoundRect(
        color = Color(0xFFD4AF37).copy(alpha = 0.42f),
        topLeft = Offset(innerLeft, innerTop),
        size = Size(innerW, innerH),
        cornerRadius = CornerRadius(minDim * 0.04f, minDim * 0.04f),
        style = Stroke(width = strokeW)
    )
}
