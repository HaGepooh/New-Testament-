package com.example.ui.art

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalContext
import com.example.data.BiblicalCharacter
import com.example.data.CharacterVisualStyle
import kotlin.math.min

/**
 * Hardware-accelerated Classical Masterwork Character Portrait Canvas for all 34 Biblical Characters.
 * Maps each character to their specific classical oil painting in [BiblicalOilPaintingPlate]
 * and renders via [Modifier.drawWithCache] for 60fps scrolling.
 */
@Composable
fun CharacterPortraitCanvas(
    character: BiblicalCharacter,
    modifier: Modifier = Modifier,
    animateHalo: Boolean = false,
    showArchFrame: Boolean = true
) {
    val context = LocalContext.current
    val portraitSpec = remember(character.visualStyle) {
        resolveCharacterPortraitOilSpec(character.visualStyle)
    }
    val bitmap = remember(context, portraitSpec.plate) {
        MasterworkOilPaintingCache.getPainting(context, portraitSpec.plate)
    }

    Box(
        modifier = modifier
            .drawWithCache {
                val _id = character.id
                val w = size.width
                val h = size.height

                onDrawBehind {
                    clipRect {
                        if (bitmap != null) {
                            drawBiblicalOilPaintingSpec(
                                bitmap = bitmap,
                                spec = portraitSpec,
                                w = w,
                                h = h,
                                accentGold = character.haloAccentColor
                            )
                        } else {
                            val minDim = min(w, h)
                            drawMasterworkPortraitFigure(
                                cx = w * 0.52f,
                                headCy = h * 0.33f,
                                scale = minDim * 0.27f,
                                style = character.visualStyle,
                                primaryRobe = character.primaryRobeColor,
                                mantle = character.mantleColor,
                                gold = character.haloAccentColor,
                                facingDirection = -1f,
                                poseMode = MasterworkPoseMode.SERMON_POINTING_HEAVENWARD,
                                isSeatedOnRock = true
                            )
                        }

                        if (showArchFrame) {
                            val minDim = min(w, h)
                            drawRect(
                                color = Color(0xFF2B231B).copy(alpha = 0.26f),
                                topLeft = Offset.Zero,
                                size = Size(w, h),
                                style = Stroke(width = (minDim * 0.012f).coerceIn(1.0f, 3.0f))
                            )
                        }
                    }
                }
            }
    )
}

/**
 * Reusable high-detail figurative renderer delegating to [drawMasterworkPortraitFigure].
 */
fun DrawScope.drawDetailedBiblicalPortrait(
    cx: Float,
    headCy: Float,
    scale: Float,
    style: CharacterVisualStyle,
    primaryRobe: Color,
    mantle: Color,
    gold: Color,
    pulse: Float = 0.5f
) {
    drawMasterworkPortraitFigure(
        cx = cx,
        headCy = headCy,
        scale = scale,
        style = style,
        primaryRobe = primaryRobe,
        mantle = mantle,
        gold = gold,
        facingDirection = -1f,
        poseMode = MasterworkPoseMode.SERMON_POINTING_HEAVENWARD,
        isSeatedOnRock = true
    )
}
