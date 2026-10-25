package com.example.ui.art

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalContext
import com.example.model.ArtStyleMode
import com.example.model.ChapterArtMetadata

/**
 * Hardware-accelerated Classical Masterwork Chapter Canvas for all 441 chapters
 * (27 New Testament Books + 150 Psalms + 31 Proverbs).
 *
 * Selects the chapter's matching classical European oil painting from the 24-painting museum
 * collection (`BiblicalOilPaintingPlate`) so different chapters feature their authentic Bible
 * life stories, characters, and the Holy Spirit at 60fps.
 */
@Composable
fun SacredArtCanvas(
    metadata: ChapterArtMetadata,
    artStyleMode: ArtStyleMode,
    modifier: Modifier = Modifier,
    animateShimmer: Boolean = false,
    showOrnamentalFrame: Boolean = true
) {
    val context = LocalContext.current
    val primarySlide = remember(metadata.bookId, metadata.chapterNumber) {
        ThreeDBibleArtEngine.getChapter3DSlides(metadata).first()
    }
    val oilSpec = remember(metadata.bookId, metadata.chapterNumber, artStyleMode) {
        resolveChapterOilPaintingSpec(
            bookId = metadata.bookId,
            chapterNumber = metadata.chapterNumber,
            slideIndex = 0,
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
                                leadCharacter = primarySlide.leadCharacter,
                                companionCharacter = primarySlide.companionCharacter,
                                thirdCharacter = primarySlide.thirdCharacter,
                                environmentStage = primarySlide.environmentStage,
                                pictureType = primarySlide.pictureType,
                                sceneVariantSeed = metadata.patternSeed + metadata.chapterNumber * 17,
                                variationIndex = artStyleMode.ordinal,
                                accentGold = metadata.palette.goldLeaf
                            )
                        }

                        if (artStyleMode == ArtStyleMode.ILLUMINATED_CODEX && showOrnamentalFrame) {
                            val padX = (w * 0.035f).coerceAtLeast(6f)
                            val padY = (h * 0.045f).coerceAtLeast(6f)
                            drawRoundRect(
                                color = metadata.palette.goldLeaf.copy(alpha = 0.50f),
                                topLeft = Offset(padX, padY),
                                size = Size((w - padX * 2f).coerceAtLeast(4f), (h - padY * 2f).coerceAtLeast(4f)),
                                cornerRadius = CornerRadius(8f, 8f),
                                style = Stroke(width = 1.4f)
                            )
                        } else if (artStyleMode == ArtStyleMode.STAINED_GLASS) {
                            val cx = w * 0.50f
                            val cy = h * 0.28f
                            drawCircle(
                                color = Color(0xFFE5B53B).copy(alpha = 0.32f),
                                radius = minOf(w, h) * 0.20f,
                                center = Offset(cx, cy),
                                style = Stroke(width = 1.5f)
                            )
                        }
                    }
                }
            }
    )
}
