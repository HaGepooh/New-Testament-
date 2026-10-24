package com.example.ui.art

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import com.example.data.BiblicalCharacter
import com.example.data.CharacterVisualStyle
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun CharacterPortraitCanvas(
    character: BiblicalCharacter,
    modifier: Modifier = Modifier,
    animateHalo: Boolean = true,
    showArchFrame: Boolean = true
) {
    val activePulse = if (animateHalo) {
        val infiniteTransition = rememberInfiniteTransition(label = "character_halo_shimmer")
        val pulse by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 4200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "halo_pulse"
        )
        pulse
    } else {
        0.5f
    }

    Canvas(
        modifier = modifier.graphicsLayer {
            compositingStrategy = CompositingStrategy.Offscreen
        }
    ) {
        clipRect {
            val w = size.width
            val h = size.height

            // 1. Rich 3D Atmospheric Background & Stained-Glass Cathedral Arch
            drawRect(
                brush = Brush.verticalGradient(
                    0.0f to character.backgroundSkyTop,
                    0.55f to character.backgroundSkyBottom,
                    1.0f to Color(0xFF0A0714)
                ),
                size = Size(w, h)
            )

            // 3D Floor Perspective Stage & Volumetric Spotlight Cone
            val stagePath = Path().apply {
                moveTo(0f, h)
                lineTo(w * 0.14f, h * 0.72f)
                lineTo(w * 0.86f, h * 0.72f)
                lineTo(w, h)
                close()
            }
            drawPath(
                path = stagePath,
                brush = Brush.verticalGradient(
                    listOf(
                        character.haloAccentColor.copy(alpha = 0.18f),
                        Color(0xFF06040C).copy(alpha = 0.88f)
                    ),
                    startY = h * 0.72f,
                    endY = h
                )
            )

            // Radial gold 3D volumetric light behind figure
            val headCenterX = w * 0.5f
            val headCenterY = h * 0.34f
            val auraR = min(w, h) * (0.48f + 0.04f * activePulse)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        character.haloAccentColor.copy(alpha = 0.52f),
                        character.primaryRobeColor.copy(alpha = 0.24f),
                        Color.Transparent
                    ),
                    center = Offset(headCenterX, headCenterY),
                    radius = auraR
                ),
                radius = auraR,
                center = Offset(headCenterX, headCenterY)
            )

            // Soft 3D Ambient Occlusion Drop Shadow behind figure
            drawOval(
                color = Color.Black.copy(alpha = 0.45f),
                topLeft = Offset(w * 0.22f, h * 0.24f),
                size = Size(w * 0.58f, h * 0.72f)
            )

            // 2. Detailed Sacred Figure Portrait (Head, Halo, Features, Robes, Hands & Attributes)
            drawDetailedBiblicalPortrait(
                cx = headCenterX,
                headCy = headCenterY,
                scale = min(w, h) * 0.25f,
                style = character.visualStyle,
                primaryRobe = character.primaryRobeColor,
                mantle = character.mantleColor,
                gold = character.haloAccentColor,
                pulse = activePulse
            )

            // 3D Specular Glass Gloss Reflection across upper-left corner
            val glossPath = Path().apply {
                moveTo(0f, 0f)
                lineTo(w * 0.65f, 0f)
                lineTo(0f, h * 0.45f)
                close()
            }
            drawPath(
                path = glossPath,
                brush = Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.14f),
                        Color.Transparent
                    ),
                    start = Offset.Zero,
                    end = Offset(w * 0.5f, h * 0.4f)
                )
            )

            // 3. Optional Gilded Byzantine Arch Frame
            if (showArchFrame) {
                drawByzantinePortraitArch(w, h, character.haloAccentColor)
            }
        }
    }
}

/**
 * Reusable high-detail figurative renderer for both standalone portraits and multi-figure
 * biblical chapter scenes (Jesus and the Disciples in boats, on the mount, at the Last Supper, etc.).
 */
fun DrawScope.drawDetailedBiblicalPortrait(
    cx: Float,
    headCy: Float,
    scale: Float,
    style: CharacterVisualStyle,
    primaryRobe: Color,
    mantle: Color,
    gold: Color,
    pulse: Float
) {
    val isJesus = style == CharacterVisualStyle.JESUS_PANTOCRATOR
    val isMaryMother = style == CharacterVisualStyle.MARY_MOTHER_OF_JESUS
    val isMaryMagdalene = style == CharacterVisualStyle.MARY_MAGDALENE
    val isVeiledWoman = style in setOf(
        CharacterVisualStyle.MARY_MOTHER_OF_JESUS,
        CharacterVisualStyle.MARY_MAGDALENE,
        CharacterVisualStyle.SAMARITAN_WOMAN_WELL,
        CharacterVisualStyle.MARTHA_AND_MARY_BETHANY,
        CharacterVisualStyle.LYDIA_OF_THYATIRA
    )
    val isArchangel = style == CharacterVisualStyle.GABRIEL_ARCHANGEL ||
        style == CharacterVisualStyle.MICHAEL_ARCHANGEL

    // ---------------- A0. 3D ARCHANGEL WINGS (FOR GABRIEL & MICHAEL) ----------------
    if (isArchangel) {
        val wingPrimary = if (style == CharacterVisualStyle.GABRIEL_ARCHANGEL) Color(0xFFF2F6FF) else Color(0xFFD93848)
        val wingTip = gold
        for (dir in listOf(-1f, 1f)) {
            val wingPath = Path().apply {
                moveTo(cx + dir * scale * 0.25f, headCy + scale * 0.55f)
                cubicTo(
                    cx + dir * scale * 1.45f, headCy - scale * 0.75f,
                    cx + dir * scale * 1.85f, headCy + scale * 0.15f,
                    cx + dir * scale * 1.35f, headCy + scale * 1.75f
                )
                quadraticTo(
                    cx + dir * scale * 0.75f, headCy + scale * 1.25f,
                    cx + dir * scale * 0.25f, headCy + scale * 0.95f
                )
                close()
            }
            drawPath(
                path = wingPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, wingPrimary, wingTip),
                    startY = headCy - scale * 0.6f,
                    endY = headCy + scale * 1.75f
                )
            )
            drawPath(wingPath, color = gold, style = Stroke(width = 2.5f))
        }
    }

    // ---------------- A. NIMBUS HALO ----------------
    val haloRadius = scale * (0.88f + 0.03f * pulse)
    drawCircle(
        brush = Brush.radialGradient(
            listOf(
                gold.copy(alpha = 0.92f),
                Color(0xFFE09F2D).copy(alpha = 0.78f),
                gold.copy(alpha = 0.35f)
            ),
            center = Offset(cx, headCy),
            radius = haloRadius
        ),
        radius = haloRadius,
        center = Offset(cx, headCy)
    )

    // Radial halo sunbeams
    val rays = 16
    for (i in 0 until rays) {
        val angle = (i * 2.0 * PI / rays).toFloat()
        drawLine(
            color = Color.White.copy(alpha = 0.35f),
            start = Offset(cx + cos(angle) * (haloRadius * 0.45f), headCy + sin(angle) * (haloRadius * 0.45f)),
            end = Offset(cx + cos(angle) * (haloRadius * 0.92f), headCy + sin(angle) * (haloRadius * 0.92f)),
            strokeWidth = 2f
        )
    }

    if (isJesus) {
        // Cruciform Halo Bars for Christ (Top, Left, Right)
        val barWidth = scale * 0.24f
        drawRect(
            color = Color(0xFF9E1F38),
            topLeft = Offset(cx - barWidth / 2f, headCy - haloRadius * 0.95f),
            size = Size(barWidth, haloRadius * 0.55f)
        )
        drawRect(
            color = Color.White,
            topLeft = Offset(cx - barWidth / 2f, headCy - haloRadius * 0.95f),
            size = Size(barWidth, haloRadius * 0.55f),
            style = Stroke(width = 2.5f)
        )
        drawRect(
            color = Color(0xFF9E1F38),
            topLeft = Offset(cx - haloRadius * 0.95f, headCy - barWidth / 2f),
            size = Size(haloRadius * 1.90f, barWidth)
        )
        drawRect(
            color = Color.White,
            topLeft = Offset(cx - haloRadius * 0.95f, headCy - barWidth / 2f),
            size = Size(haloRadius * 1.90f, barWidth),
            style = Stroke(width = 2.5f)
        )
    }

    // Outer Halo Rim & Pearl Dots
    drawCircle(
        color = Color.White.copy(alpha = 0.85f),
        radius = haloRadius,
        center = Offset(cx, headCy),
        style = Stroke(width = 3f)
    )
    drawCircle(
        color = Color(0xFF8C4A15),
        radius = haloRadius * 0.91f,
        center = Offset(cx, headCy),
        style = Stroke(width = 1.8f)
    )

    // ---------------- B. BACK HAIR OR VEIL ----------------
    val hairColor = when (style) {
        CharacterVisualStyle.SIMON_PETER,
        CharacterVisualStyle.ANDREW_APOSTLE,
        CharacterVisualStyle.MOSES_AND_ELIJAH_PROPHETS,
        CharacterVisualStyle.NICODEMUS_TEACHER -> Color(0xFFD8DEE6) // Silver-white
        CharacterVisualStyle.GABRIEL_ARCHANGEL,
        CharacterVisualStyle.MICHAEL_ARCHANGEL -> Color(0xFFE6BE6A) // Golden-blond celestial locks
        CharacterVisualStyle.JOHN_BELOVED,
        CharacterVisualStyle.MARY_MAGDALENE,
        CharacterVisualStyle.MARTHA_AND_MARY_BETHANY -> Color(0xFF6E3524) // Warm auburn-chestnut
        CharacterVisualStyle.JESUS_PANTOCRATOR,
        CharacterVisualStyle.JOHN_THE_BAPTIST -> Color(0xFF3B2316) // Deep Nazarene chestnut
        else -> Color(0xFF2D1E17) // Dark brown
    }

    if (isVeiledWoman || style == CharacterVisualStyle.NICODEMUS_TEACHER) {
        // Flowing Maphorion Veil or Tallit Prayer Shawl behind shoulders
        val veilColor = if (isMaryMother || style == CharacterVisualStyle.NICODEMUS_TEACHER) mantle else primaryRobe
        val veilPath = Path().apply {
            moveTo(cx, headCy - scale * 0.56f)
            cubicTo(
                cx - scale * 0.65f, headCy - scale * 0.45f,
                cx - scale * 0.82f, headCy + scale * 0.45f,
                cx - scale * 0.95f, headCy + scale * 1.35f
            )
            lineTo(cx + scale * 0.95f, headCy + scale * 1.35f)
            cubicTo(
                cx + scale * 0.82f, headCy + scale * 0.45f,
                cx + scale * 0.65f, headCy - scale * 0.45f,
                cx, headCy - scale * 0.56f
            )
            close()
        }
        drawPath(veilPath, color = veilColor)
        drawPath(veilPath, color = gold, style = Stroke(width = 3f))
    } else {
        // Shoulder-length or fuller back hair
        val hairLength = if (isJesus || isArchangel || style == CharacterVisualStyle.JOHN_THE_BAPTIST || style == CharacterVisualStyle.JOHN_BELOVED || style == CharacterVisualStyle.ANDREW_APOSTLE || style == CharacterVisualStyle.MOSES_AND_ELIJAH_PROPHETS) {
            scale * 0.56f
        } else {
            scale * 0.28f
        }
        drawOval(
            color = hairColor,
            topLeft = Offset(cx - scale * 0.44f, headCy - scale * 0.48f),
            size = Size(scale * 0.88f, scale * 0.65f + hairLength)
        )
    }

    // ---------------- C. SHOULDERS, TUNIC (CHITON) & MANTLE (HIMATION) ----------------
    val shoulderTopY = headCy + scale * 0.48f
    val torsoBottomY = headCy + scale * 2.35f

    // Base Tunic (Chiton)
    val tunicPath = Path().apply {
        moveTo(cx - scale * 0.35f, shoulderTopY)
        quadraticTo(cx - scale * 1.05f, shoulderTopY + scale * 0.18f, cx - scale * 1.18f, torsoBottomY)
        lineTo(cx + scale * 1.18f, torsoBottomY)
        quadraticTo(cx + scale * 1.05f, shoulderTopY + scale * 0.18f, cx + scale * 0.35f, shoulderTopY)
        close()
    }
    drawPath(tunicPath, color = primaryRobe)

    // Gold Clavus Strip on Tunic (traditional Roman/Byzantine vertical gold stripe)
    drawRect(
        color = gold.copy(alpha = 0.85f),
        topLeft = Offset(cx + scale * 0.24f, shoulderTopY + scale * 0.05f),
        size = Size(scale * 0.10f, torsoBottomY - shoulderTopY)
    )

    // Draped Outer Mantle (Himation) across left shoulder & chest
    val mantlePath = Path().apply {
        moveTo(cx - scale * 0.38f, shoulderTopY)
        quadraticTo(cx - scale * 1.08f, shoulderTopY + scale * 0.18f, cx - scale * 1.18f, torsoBottomY)
        lineTo(cx + scale * 0.55f, torsoBottomY)
        quadraticTo(cx + scale * 0.35f, shoulderTopY + scale * 0.65f, cx - scale * 0.38f, shoulderTopY)
        close()
    }
    drawPath(mantlePath, color = mantle)
    drawPath(mantlePath, color = gold.copy(alpha = 0.75f), style = Stroke(width = 3f))

    // Draped Fabric Fold Lines on Mantle
    for (fold in 1..3) {
        val fy = shoulderTopY + scale * (0.28f * fold)
        drawLine(
            color = Color.Black.copy(alpha = 0.22f),
            start = Offset(cx - scale * 0.75f, fy),
            end = Offset(cx + scale * (0.15f * fold - 0.1f), fy + scale * 0.32f),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )
    }

    // Neck & Collar
    val skinTone = Color(0xFFE5B58D)
    val skinShadow = Color(0xFFC99268)
    drawRoundRect(
        color = skinShadow,
        topLeft = Offset(cx - scale * 0.16f, headCy + scale * 0.28f),
        size = Size(scale * 0.32f, scale * 0.34f),
        cornerRadius = CornerRadius(12f, 12f)
    )

    // Gold Embroidered Collar Rim
    drawArc(
        color = gold,
        startAngle = 15f,
        sweepAngle = 150f,
        useCenter = false,
        topLeft = Offset(cx - scale * 0.30f, shoulderTopY - scale * 0.14f),
        size = Size(scale * 0.60f, scale * 0.32f),
        style = Stroke(width = 4.5f, cap = StrokeCap.Round)
    )

    // ---------------- D. FACE, CROWN HAIR, BEARD & FACIAL FEATURES ----------------
    // Face Oval
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFF2C8A2), skinTone, skinShadow),
            center = Offset(cx - scale * 0.05f, headCy - scale * 0.05f),
            radius = scale * 0.45f
        ),
        topLeft = Offset(cx - scale * 0.33f, headCy - scale * 0.42f),
        size = Size(scale * 0.66f, scale * 0.84f)
    )

    // Crown Hair / Forehead Framing / Crowns & Helmets
    if (isVeiledWoman) {
        val hoodColor = if (isMaryMother) mantle else primaryRobe
        val hood = Path().apply {
            moveTo(cx - scale * 0.34f, headCy - scale * 0.10f)
            quadraticTo(cx, headCy - scale * 0.56f, cx + scale * 0.34f, headCy - scale * 0.10f)
            quadraticTo(cx, headCy - scale * 0.34f, cx - scale * 0.34f, headCy - scale * 0.10f)
            close()
        }
        drawPath(hood, color = hoodColor)
        drawPath(hood, color = gold, style = Stroke(width = 2.5f))
        if (isMaryMother) {
            drawCircle(color = gold, radius = scale * 0.045f, center = Offset(cx, headCy - scale * 0.38f))
            drawCircle(color = gold, radius = scale * 0.045f, center = Offset(cx - scale * 0.56f, shoulderTopY + scale * 0.32f))
            drawCircle(color = gold, radius = scale * 0.045f, center = Offset(cx + scale * 0.56f, shoulderTopY + scale * 0.32f))
        }
    } else if (style == CharacterVisualStyle.WISE_MAGI_KING) {
        // 3D Jeweled Eastern Royal Crown
        val crownPath = Path().apply {
            moveTo(cx - scale * 0.36f, headCy - scale * 0.22f)
            lineTo(cx - scale * 0.40f, headCy - scale * 0.58f)
            lineTo(cx - scale * 0.18f, headCy - scale * 0.42f)
            lineTo(cx, headCy - scale * 0.66f)
            lineTo(cx + scale * 0.18f, headCy - scale * 0.42f)
            lineTo(cx + scale * 0.40f, headCy - scale * 0.58f)
            lineTo(cx + scale * 0.36f, headCy - scale * 0.22f)
            close()
        }
        drawPath(crownPath, color = gold)
        drawPath(crownPath, color = Color.White, style = Stroke(width = 2f))
        drawCircle(color = Color(0xFFD91E48), radius = scale * 0.05f, center = Offset(cx, headCy - scale * 0.36f))
    } else if (style == CharacterVisualStyle.CORNELIUS_CENTURION) {
        // 3D Gilded Roman Helmet with Crimson Horsehair Crest
        drawArc(
            color = Color(0xFFD92638),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(cx - scale * 0.44f, headCy - scale * 0.78f),
            size = Size(scale * 0.88f, scale * 0.56f)
        )
        drawArc(
            color = gold,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(cx - scale * 0.36f, headCy - scale * 0.52f),
            size = Size(scale * 0.72f, scale * 0.44f)
        )
    } else if (style == CharacterVisualStyle.PAUL_OF_TARSUS) {
        // High scholarly brow with side locks
        drawOval(
            color = hairColor,
            topLeft = Offset(cx - scale * 0.36f, headCy - scale * 0.32f),
            size = Size(scale * 0.14f, scale * 0.36f)
        )
        drawOval(
            color = hairColor,
            topLeft = Offset(cx + scale * 0.22f, headCy - scale * 0.32f),
            size = Size(scale * 0.14f, scale * 0.36f)
        )
    } else {
        // Center-parted or curly crown hair
        val topHair = Path().apply {
            moveTo(cx - scale * 0.35f, headCy - scale * 0.06f)
            quadraticTo(cx - scale * 0.36f, headCy - scale * 0.50f, cx, headCy - scale * 0.45f)
            quadraticTo(cx + scale * 0.36f, headCy - scale * 0.50f, cx + scale * 0.35f, headCy - scale * 0.06f)
            quadraticTo(cx + scale * 0.18f, headCy - scale * 0.34f, cx, headCy - scale * 0.30f)
            quadraticTo(cx - scale * 0.18f, headCy - scale * 0.34f, cx - scale * 0.35f, headCy - scale * 0.06f)
            close()
        }
        drawPath(topHair, color = hairColor)
    }

    // Eyebrows & Expressive Eyes
    val eyeY = headCy - scale * 0.06f
    val eyeOffsetX = scale * 0.13f
    for (dir in listOf(-1f, 1f)) {
        val ex = cx + dir * eyeOffsetX
        // Eyebrow
        drawArc(
            color = Color(0xFF2B1A12),
            startAngle = 200f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(ex - scale * 0.09f, eyeY - scale * 0.10f),
            size = Size(scale * 0.18f, scale * 0.10f),
            style = Stroke(width = 2.8f, cap = StrokeCap.Round)
        )
        // Sclera (White of eye)
        drawOval(
            color = Color(0xFFFFFAF2),
            topLeft = Offset(ex - scale * 0.065f, eyeY - scale * 0.035f),
            size = Size(scale * 0.13f, scale * 0.07f)
        )
        // Iris & Pupil
        drawCircle(
            color = Color(0xFF3E2415),
            radius = scale * 0.032f,
            center = Offset(ex, eyeY)
        )
        // Catchlight glint
        drawCircle(
            color = Color.White,
            radius = scale * 0.011f,
            center = Offset(ex - scale * 0.01f, eyeY - scale * 0.01f)
        )
    }

    // Nose Bridge & Tip
    drawLine(
        color = skinShadow,
        start = Offset(cx, eyeY - scale * 0.02f),
        end = Offset(cx - scale * 0.02f, headCy + scale * 0.11f),
        strokeWidth = 2.5f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = skinShadow,
        start = Offset(cx - scale * 0.04f, headCy + scale * 0.12f),
        end = Offset(cx + scale * 0.04f, headCy + scale * 0.12f),
        strokeWidth = 2.5f,
        cap = StrokeCap.Round
    )

    // Beard & Mustache (for bearded figures) vs Serene Lips (for veiled women, angels, John, Stephen, Timothy, Cornelius)
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
            CharacterVisualStyle.ANDREW_APOSTLE -> scale * 0.56f
            CharacterVisualStyle.SIMON_PETER -> scale * 0.42f
            else -> scale * 0.46f
        }
        val beardPath = Path().apply {
            moveTo(cx - scale * 0.31f, headCy + scale * 0.04f)
            quadraticTo(cx - scale * 0.30f, headCy + beardLength * 0.85f, cx, headCy + beardLength)
            quadraticTo(cx + scale * 0.30f, headCy + beardLength * 0.85f, cx + scale * 0.31f, headCy + scale * 0.04f)
            quadraticTo(cx + scale * 0.15f, headCy + scale * 0.24f, cx, headCy + scale * 0.22f)
            quadraticTo(cx - scale * 0.15f, headCy + scale * 0.24f, cx - scale * 0.31f, headCy + scale * 0.04f)
            close()
        }
        drawPath(beardPath, color = hairColor)

        // Mustache
        val mustache = Path().apply {
            moveTo(cx, headCy + scale * 0.16f)
            quadraticTo(cx - scale * 0.14f, headCy + scale * 0.16f, cx - scale * 0.20f, headCy + scale * 0.24f)
            moveTo(cx, headCy + scale * 0.16f)
            quadraticTo(cx + scale * 0.14f, headCy + scale * 0.16f, cx + scale * 0.20f, headCy + scale * 0.24f)
        }
        drawPath(mustache, color = hairColor, style = Stroke(width = 4f, cap = StrokeCap.Round))
    }

    // Serene Mouth Line
    drawLine(
        color = Color(0xFFA8544C),
        start = Offset(cx - scale * 0.07f, headCy + scale * 0.21f),
        end = Offset(cx + scale * 0.07f, headCy + scale * 0.21f),
        strokeWidth = 3f,
        cap = StrokeCap.Round
    )

    // ---------------- E. FOREGROUND HANDS & SACRED ATTRIBUTES ----------------
    val handY = shoulderTopY + scale * 0.56f
    val leftAttrX = cx + scale * 0.48f   // Viewer's right (figure's left hand)
    val rightBlessX = cx - scale * 0.42f // Viewer's left (figure's right hand)

    // Right Hand in Blessing or Prayer
    drawCircle(color = skinTone, radius = scale * 0.13f, center = Offset(rightBlessX, handY))
    // Two raised blessing fingers
    drawLine(
        color = skinTone,
        start = Offset(rightBlessX - scale * 0.03f, handY),
        end = Offset(rightBlessX - scale * 0.03f, handY - scale * 0.22f),
        strokeWidth = scale * 0.055f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = skinTone,
        start = Offset(rightBlessX + scale * 0.04f, handY),
        end = Offset(rightBlessX + scale * 0.04f, handY - scale * 0.20f),
        strokeWidth = scale * 0.055f,
        cap = StrokeCap.Round
    )

    // Left Hand Holding Character-Specific Sacred Attribute
    when (style) {
        CharacterVisualStyle.JESUS_PANTOCRATOR,
        CharacterVisualStyle.MATTHEW_EVANGELIST,
        CharacterVisualStyle.MARK_EVANGELIST -> {
            // Jeweled Golden Gospel Codex
            drawRoundRect(
                color = gold,
                topLeft = Offset(leftAttrX - scale * 0.28f, handY - scale * 0.24f),
                size = Size(scale * 0.52f, scale * 0.66f),
                cornerRadius = CornerRadius(6f, 6f)
            )
            drawRoundRect(
                color = Color(0xFF8C1C32),
                topLeft = Offset(leftAttrX - scale * 0.23f, handY - scale * 0.19f),
                size = Size(scale * 0.42f, scale * 0.56f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            // Gold Cross on Gospel Cover
            drawLine(
                color = gold,
                start = Offset(leftAttrX - scale * 0.02f, handY - scale * 0.10f),
                end = Offset(leftAttrX - scale * 0.02f, handY + scale * 0.28f),
                strokeWidth = 4f
            )
            drawLine(
                color = gold,
                start = Offset(leftAttrX - scale * 0.16f, handY + scale * 0.06f),
                end = Offset(leftAttrX + scale * 0.12f, handY + scale * 0.06f),
                strokeWidth = 4f
            )
        }

        CharacterVisualStyle.SIMON_PETER -> {
            // Crossed Gold & Silver Keys of the Kingdom
            drawLine(
                color = Color(0xFFE2E8F0),
                start = Offset(leftAttrX - scale * 0.22f, handY + scale * 0.28f),
                end = Offset(leftAttrX + scale * 0.22f, handY - scale * 0.28f),
                strokeWidth = 5f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = gold,
                start = Offset(leftAttrX + scale * 0.22f, handY + scale * 0.28f),
                end = Offset(leftAttrX - scale * 0.22f, handY - scale * 0.28f),
                strokeWidth = 5f,
                cap = StrokeCap.Round
            )
            drawCircle(
                color = gold,
                radius = scale * 0.10f,
                center = Offset(leftAttrX + scale * 0.22f, handY + scale * 0.28f),
                style = Stroke(width = 4f)
            )
            drawCircle(
                color = Color(0xFFE2E8F0),
                radius = scale * 0.10f,
                center = Offset(leftAttrX - scale * 0.22f, handY + scale * 0.28f),
                style = Stroke(width = 4f)
            )
        }

        CharacterVisualStyle.PAUL_OF_TARSUS -> {
            // Sword of the Spirit & Rolled Scroll
            drawLine(
                color = Color(0xFFE8EEF5),
                start = Offset(leftAttrX, handY - scale * 0.55f),
                end = Offset(leftAttrX, handY + scale * 0.45f),
                strokeWidth = 6f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = gold,
                start = Offset(leftAttrX - scale * 0.22f, handY - scale * 0.12f),
                end = Offset(leftAttrX + scale * 0.22f, handY - scale * 0.12f),
                strokeWidth = 6f,
                cap = StrokeCap.Round
            )
        }

        CharacterVisualStyle.JOHN_THE_BAPTIST,
        CharacterVisualStyle.PHILIP_APOSTLE,
        CharacterVisualStyle.JAMES_GREATER -> {
            // Tall Reed Cross Staff
            drawLine(
                color = gold,
                start = Offset(leftAttrX + scale * 0.10f, headCy - scale * 0.45f),
                end = Offset(leftAttrX + scale * 0.10f, torsoBottomY),
                strokeWidth = 5f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = gold,
                start = Offset(leftAttrX - scale * 0.12f, headCy - scale * 0.22f),
                end = Offset(leftAttrX + scale * 0.32f, headCy - scale * 0.22f),
                strokeWidth = 5f,
                cap = StrokeCap.Round
            )
        }

        CharacterVisualStyle.MARY_MAGDALENE -> {
            // White Alabaster Jar of Spikenard
            drawOval(
                color = Color(0xFFFFF8EC),
                topLeft = Offset(leftAttrX - scale * 0.18f, handY - scale * 0.15f),
                size = Size(scale * 0.36f, scale * 0.48f)
            )
            drawRect(
                color = gold,
                topLeft = Offset(leftAttrX - scale * 0.10f, handY - scale * 0.26f),
                size = Size(scale * 0.20f, scale * 0.12f)
            )
        }

        CharacterVisualStyle.MARY_MOTHER_OF_JESUS -> {
            // Blooming White Lily of the Annunciation
            drawLine(
                color = Color(0xFF2EA875),
                start = Offset(leftAttrX, handY + scale * 0.35f),
                end = Offset(leftAttrX, handY - scale * 0.28f),
                strokeWidth = 4f
            )
            drawCircle(color = Color.White, radius = scale * 0.11f, center = Offset(leftAttrX, handY - scale * 0.30f))
            drawCircle(color = gold, radius = scale * 0.045f, center = Offset(leftAttrX, handY - scale * 0.30f))
        }

        else -> {
            // Illuminated Apostolic Scroll
            drawRoundRect(
                color = Color(0xFFFFF5E1),
                topLeft = Offset(leftAttrX - scale * 0.24f, handY - scale * 0.18f),
                size = Size(scale * 0.48f, scale * 0.38f),
                cornerRadius = CornerRadius(8f, 8f)
            )
            drawRoundRect(
                color = gold,
                topLeft = Offset(leftAttrX - scale * 0.24f, handY - scale * 0.18f),
                size = Size(scale * 0.48f, scale * 0.38f),
                cornerRadius = CornerRadius(8f, 8f),
                style = Stroke(width = 2.5f)
            )
        }
    }

    // Left Hand fingers grasping attribute
    drawCircle(color = skinTone, radius = scale * 0.11f, center = Offset(leftAttrX - scale * 0.12f, handY + scale * 0.08f))
}

private fun DrawScope.drawByzantinePortraitArch(w: Float, h: Float, goldColor: Color) {
    val m = 10f
    val archPath = Path().apply {
        moveTo(m, h - m)
        lineTo(m, h * 0.36f)
        quadraticTo(m, m, w * 0.5f, m)
        quadraticTo(w - m, m, w - m, h * 0.36f)
        lineTo(w - m, h - m)
        close()
    }
    drawPath(archPath, color = goldColor.copy(alpha = 0.65f), style = Stroke(width = 3f))
}
