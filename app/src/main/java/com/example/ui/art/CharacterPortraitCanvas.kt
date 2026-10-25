package com.example.ui.art

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import com.example.data.BiblicalCharacter
import com.example.data.CharacterVisualStyle
import kotlin.math.min

/**
 * Hardware-accelerated Storybook Illustrated Character Portrait Canvas for all 34 Biblical Characters.
 * Uses [Modifier.drawWithCache] so character galleries, reader cast strips, and detail modals
 * render smoothly at 60fps with the Royal Blue & Beveled Gold Storybook Frame, cascading green
 * corner leaf vines, 4-point golden stars, luminous sky-blue stage backdrop, and high-detail
 * storybook character illustration.
 */
@Composable
fun CharacterPortraitCanvas(
    character: BiblicalCharacter,
    modifier: Modifier = Modifier,
    animateHalo: Boolean = false,
    showArchFrame: Boolean = true
) {
    val cacheKey = remember(character.id, showArchFrame) {
        "${character.id}_$showArchFrame"
    }

    Box(
        modifier = modifier
            .drawWithCache {
                val _k = cacheKey
                val w = size.width
                val h = size.height

                onDrawBehind {
                    clipRect {
                        val minDim = min(w, h)
                        val padX = (w * 0.055f).coerceAtLeast(4f)
                        val padY = (h * 0.065f).coerceAtLeast(4f)
                        val innerLeft = padX
                        val innerTop = padY
                        val innerW = (w - padX * 2f).coerceAtLeast(8f)
                        val innerH = (h - padY * 2f).coerceAtLeast(8f)
                        val innerBottom = innerTop + innerH
                        val cornerR = (minDim * 0.08f).coerceIn(6f, 22f)

                        // 1. Royal Azure / Cerulean Blue Outer Sanctuary Frame
                        drawRect(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color(0xFF1976D2),
                                    Color(0xFF1E88E5),
                                    Color(0xFF1565C0)
                                )
                            ),
                            size = Size(w, h)
                        )

                        // 2. Luminous Sky-Blue / Ice-Blue Rounded Inner Stage
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                0.0f to Color(0xFFCCEBFB),
                                0.55f to Color(0xFFE8F7FF),
                                1.0f to Color(0xFFD8EFFC),
                                startY = innerTop,
                                endY = innerBottom
                            ),
                            topLeft = Offset(innerLeft, innerTop),
                            size = Size(innerW, innerH),
                            cornerRadius = CornerRadius(cornerR, cornerR)
                        )

                        // Soft radial sunlight glow behind character
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.80f),
                                    Color(0xFFFFF9C4).copy(alpha = 0.35f),
                                    Color.Transparent
                                ),
                                center = Offset(w * 0.50f, innerTop + innerH * 0.38f),
                                radius = minDim * 0.45f
                            ),
                            radius = minDim * 0.45f,
                            center = Offset(w * 0.50f, innerTop + innerH * 0.38f)
                        )

                        // 3. High-Detail Storybook Biblical Character Figure
                        val poseMode = when (character.visualStyle) {
                            CharacterVisualStyle.JESUS_PANTOCRATOR -> StorybookPoseMode.TEACHING_OPEN_ARMS
                            CharacterVisualStyle.PAUL_OF_TARSUS,
                            CharacterVisualStyle.MATTHEW_EVANGELIST,
                            CharacterVisualStyle.MARK_EVANGELIST,
                            CharacterVisualStyle.LUKE_PHYSICIAN -> StorybookPoseMode.READING_OPEN_SCROLL
                            CharacterVisualStyle.NICODEMUS_TEACHER -> StorybookPoseMode.HIGH_PRIEST_MITER_STAFF
                            CharacterVisualStyle.MOSES_AND_ELIJAH_PROPHETS -> StorybookPoseMode.PHARISEE_ELDER_SHAWL
                            else -> StorybookPoseMode.SACRED_ATTRIBUTE_POSE
                        }

                        drawStorybookBiblicalFigure(
                            cx = w * 0.50f,
                            headCy = innerTop + innerH * 0.38f,
                            scale = minDim * 0.24f,
                            style = character.visualStyle,
                            primaryRobe = if (character.visualStyle == CharacterVisualStyle.JESUS_PANTOCRATOR) {
                                Color(0xFFF7F5F0)
                            } else {
                                character.primaryRobeColor
                            },
                            mantle = if (character.visualStyle == CharacterVisualStyle.JESUS_PANTOCRATOR) {
                                Color(0xFF1E68B8)
                            } else {
                                character.mantleColor
                            },
                            gold = character.haloAccentColor,
                            facingDirection = 1f,
                            poseMode = poseMode,
                            showHalo = true,
                            useSoftGlowHalo = (character.visualStyle == CharacterVisualStyle.JESUS_PANTOCRATOR)
                        )

                        // Mask bottom overflow below the inner rounded panel
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
                        }

                        // 4. Beveled 3D Metallic Gold Inner Frame & Cascading Botanical Corner Vines
                        val goldStrokeW = (minDim * 0.025f).coerceIn(2.2f, 6.0f)
                        drawRoundRect(
                            color = Color(0xFF3E2723).copy(alpha = 0.78f),
                            topLeft = Offset(innerLeft - goldStrokeW * 0.4f, innerTop - goldStrokeW * 0.4f),
                            size = Size(innerW + goldStrokeW * 0.8f, innerH + goldStrokeW * 0.8f),
                            cornerRadius = CornerRadius(cornerR + 1.5f, cornerR + 1.5f),
                            style = Stroke(width = (goldStrokeW * 0.35f).coerceAtLeast(1f))
                        )
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

                        if (showArchFrame) {
                            drawStorybookCornerVinesAndStars(
                                w = w,
                                h = h,
                                innerLeft = innerLeft,
                                innerTop = innerTop,
                                innerW = innerW,
                                innerH = innerH
                            )
                        }
                    }
                }
            }
    )
}

/**
 * Reusable high-detail figurative renderer delegating to [drawStorybookBiblicalFigure].
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
    drawStorybookBiblicalFigure(
        cx = cx,
        headCy = headCy,
        scale = scale,
        style = style,
        primaryRobe = primaryRobe,
        mantle = mantle,
        gold = gold,
        facingDirection = 1f,
        poseMode = if (style == CharacterVisualStyle.JESUS_PANTOCRATOR) {
            StorybookPoseMode.TEACHING_OPEN_ARMS
        } else {
            StorybookPoseMode.SACRED_ATTRIBUTE_POSE
        },
        showHalo = true,
        useSoftGlowHalo = (style == CharacterVisualStyle.JESUS_PANTOCRATOR)
    )
}
