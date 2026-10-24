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
import com.example.data.CharacterVisualStyle
import com.example.model.ArtPalette
import com.example.model.ArtStyleMode
import com.example.model.ChapterArtMetadata
import com.example.model.SceneArchetype
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun SacredArtCanvas(
    metadata: ChapterArtMetadata,
    artStyleMode: ArtStyleMode,
    modifier: Modifier = Modifier,
    animateShimmer: Boolean = true,
    showOrnamentalFrame: Boolean = true
) {
    val pulse = if (animateShimmer) {
        val infiniteTransition = rememberInfiniteTransition(label = "sacred_art_shimmer")
        val shimmerPhase by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 4800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "shimmer_phase"
        )
        shimmerPhase
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
            val palette = metadata.palette
            val seed = metadata.patternSeed

            // 1. Rich Multi-Stop Celestial Sky & Atmospheric Background
            drawSkyAndAtmosphere(w, h, palette, seed, pulse)

            // 2. Stained-Glass Rose Window Mandala in Upper Sky
            drawRoseWindowAura(w, h, palette, seed, pulse, artStyleMode)

            // 3. Narrative Biblical Scene with Detailed Figures of Jesus, Disciples & Biblical Characters
            when (metadata.sceneArchetype) {
                SceneArchetype.NATIVITY_STAR -> drawNativityStarScene(w, h, palette, pulse)
                SceneArchetype.JORDAN_BAPTISM -> drawJordanBaptismScene(w, h, palette, pulse)
                SceneArchetype.SERMON_MOUNT -> drawSermonMountScene(w, h, palette, pulse)
                SceneArchetype.SEA_OF_GALILEE_BOAT -> drawSeaOfGalileeBoatScene(w, h, palette, pulse)
                SceneArchetype.LOAVES_AND_FISHES -> drawLoavesAndFishesScene(w, h, palette, pulse)
                SceneArchetype.GOOD_SHEPHERD -> drawGoodShepherdScene(w, h, palette, pulse)
                SceneArchetype.TRANSFIGURATION -> drawTransfigurationScene(w, h, palette, pulse)
                SceneArchetype.JERUSALEM_PALMS -> drawJerusalemPalmsScene(w, h, palette, pulse)
                SceneArchetype.LAST_SUPPER_CHALICE -> drawLastSupperChaliceScene(w, h, palette, pulse)
                SceneArchetype.GETHSEMANE_OLIVES -> drawGethsemaneOlivesScene(w, h, palette, pulse)
                SceneArchetype.CALVARY_DAWN -> drawCalvaryDawnScene(w, h, palette, pulse)
                SceneArchetype.EMPTY_TOMB_SUNRISE -> drawEmptyTombSunriseScene(w, h, palette, pulse)
                SceneArchetype.ASCENSION_CLOUDS -> drawAscensionCloudsScene(w, h, palette, pulse)
                SceneArchetype.PENTECOST_FLAMES -> drawPentecostFlamesScene(w, h, palette, pulse)
                SceneArchetype.TEMPLE_SOLOMON_PORCH -> drawTempleSolomonPorchScene(w, h, palette, pulse)
                SceneArchetype.DAMASCUS_LIGHT -> drawDamascusLightScene(w, h, palette, pulse)
                SceneArchetype.MEDITERRANEAN_VOYAGE -> drawMediterraneanVoyageScene(w, h, palette, pulse)
                SceneArchetype.AREOPAGUS_ATHENS -> drawAreopagusAthensScene(w, h, palette, pulse)
                SceneArchetype.SCROLL_AND_LAMP -> drawScrollAndLampScene(w, h, palette, pulse)
                SceneArchetype.ARMOR_OF_FAITH -> drawArmorOfFaithScene(w, h, palette, pulse)
                SceneArchetype.ANCHOR_OF_HOPE -> drawAnchorOfHopeScene(w, h, palette, pulse)
                SceneArchetype.SEVEN_LAMPSTANDS -> drawSevenLampstandsScene(w, h, palette, pulse)
                SceneArchetype.THRONE_AND_RAINBOW -> drawThroneAndRainbowScene(w, h, palette, pulse)
                SceneArchetype.NEW_JERUSALEM_TREE -> drawNewJerusalemTreeScene(w, h, palette, pulse)
                SceneArchetype.DAVID_HARP_PASTURES -> drawDavidHarpPasturesScene(w, h, palette, pulse)
                SceneArchetype.SOLOMON_WISDOM_PILLARS -> drawSolomonWisdomPillarsScene(w, h, palette, pulse)
            }

            // 4. Mode-Specific Artistic Overlay (Stained Glass Leadwork vs Gold Codex vs Fresco)
            when (artStyleMode) {
                ArtStyleMode.STAINED_GLASS -> drawStainedGlassLeadwork(w, h, palette, seed)
                ArtStyleMode.ILLUMINATED_CODEX -> drawIlluminatedCodexFiligree(w, h, palette, pulse)
                ArtStyleMode.FRESCO_PAINTING -> drawFrescoLightBloom(w, h, palette, pulse)
            }

            // 5. Optional Gold Leaf Manuscript Border
            if (showOrnamentalFrame) {
                drawGoldLeafBorder(w, h, palette.goldLeaf)
            }
        }
    }
}

private fun DrawScope.drawSkyAndAtmosphere(
    w: Float,
    h: Float,
    palette: ArtPalette,
    seed: Int,
    pulse: Float
) {
    drawRect(
        brush = Brush.verticalGradient(
            0.0f to palette.skyTop,
            0.52f to palette.skyMiddle,
            0.85f to palette.skyHorizon,
            1.0f to palette.waterOrGround
        ),
        size = Size(w, h)
    )

    val sunX = w * (0.35f + ((seed % 7 + 7) % 7) * 0.05f)
    val sunY = h * 0.34f
    val glowRadius = min(w, h) * (0.55f + 0.08f * pulse)

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                palette.goldLeaf.copy(alpha = 0.55f),
                palette.skyHorizon.copy(alpha = 0.28f),
                Color.Transparent
            ),
            center = Offset(sunX, sunY),
            radius = glowRadius
        ),
        radius = glowRadius,
        center = Offset(sunX, sunY)
    )

    val starCount = 16
    for (i in 0 until starCount) {
        val sx = ((seed * (i + 3) * 37) % 1000).let { (it + 1000) % 1000 } / 1000f * w
        val sy = ((seed * (i + 7) * 53) % 450).let { (it + 450) % 450 } / 1000f * h
        val starAlpha = 0.25f + 0.45f * (((i + (pulse * 4).toInt()) % 3) / 2f)
        drawCircle(
            color = palette.goldLeaf.copy(alpha = starAlpha),
            radius = if (i % 4 == 0) 3.2f else 1.8f,
            center = Offset(sx, sy)
        )
    }
}

private fun DrawScope.drawRoseWindowAura(
    w: Float,
    h: Float,
    palette: ArtPalette,
    seed: Int,
    pulse: Float,
    artStyleMode: ArtStyleMode
) {
    val cx = w * 0.5f
    val cy = h * 0.34f
    val maxR = min(w, h) * 0.38f
    val petals = 12 + (seed % 3) * 2
    val alphaFactor = if (artStyleMode == ArtStyleMode.STAINED_GLASS) 0.34f else 0.18f

    for (i in 0 until petals) {
        val angle1 = (i * 2.0 * PI / petals).toFloat()
        val angle2 = ((i + 0.5) * 2.0 * PI / petals).toFloat()
        val angle3 = ((i + 1.0) * 2.0 * PI / petals).toFloat()

        val petalColor = when (i % 3) {
            0 -> palette.primaryJewel.copy(alpha = alphaFactor)
            1 -> palette.secondaryJewel.copy(alpha = alphaFactor)
            else -> palette.goldLeaf.copy(alpha = alphaFactor * 0.9f)
        }

        val path = Path().apply {
            moveTo(cx, cy)
            lineTo(cx + cos(angle1) * maxR, cy + sin(angle1) * maxR)
            quadraticTo(
                cx + cos(angle2) * (maxR * 1.15f),
                cy + sin(angle2) * (maxR * 1.15f),
                cx + cos(angle3) * maxR,
                cy + sin(angle3) * maxR
            )
            close()
        }
        drawPath(path, color = petalColor)
    }

    drawCircle(
        color = palette.goldLeaf.copy(alpha = 0.42f + 0.12f * pulse),
        radius = maxR * 0.68f,
        center = Offset(cx, cy),
        style = Stroke(width = 2.5f)
    )
    drawCircle(
        color = palette.goldLeaf.copy(alpha = 0.65f),
        radius = maxR * 0.32f,
        center = Offset(cx, cy),
        style = Stroke(width = 3f)
    )
}

private fun DrawScope.drawLayeredHills(
    w: Float,
    h: Float,
    backColor: Color,
    midColor: Color,
    frontColor: Color
) {
    val backHill = Path().apply {
        moveTo(0f, h * 0.68f)
        quadraticTo(w * 0.28f, h * 0.54f, w * 0.58f, h * 0.66f)
        quadraticTo(w * 0.82f, h * 0.56f, w, h * 0.64f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(backHill, color = backColor)

    val midHill = Path().apply {
        moveTo(0f, h * 0.76f)
        quadraticTo(w * 0.35f, h * 0.64f, w * 0.68f, h * 0.75f)
        quadraticTo(w * 0.88f, h * 0.69f, w, h * 0.74f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(midHill, color = midColor)

    val frontHill = Path().apply {
        moveTo(0f, h * 0.85f)
        quadraticTo(w * 0.45f, h * 0.77f, w, h * 0.86f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(frontHill, color = frontColor)
}

private fun DrawScope.drawStarOfBethlehem(
    cx: Float,
    cy: Float,
    radius: Float,
    color: Color,
    pulse: Float
) {
    val rOuter = radius * (1f + 0.08f * pulse)
    val rInner = radius * 0.22f
    val path = Path()
    for (i in 0 until 16) {
        val angle = (i * PI / 8.0 - PI / 2.0).toFloat()
        val r = if (i % 2 == 0) {
            if (i % 4 == 0) rOuter * 1.35f else rOuter * 0.75f
        } else {
            rInner
        }
        val x = cx + cos(angle) * r
        val y = cy + sin(angle) * r
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawCircle(
        brush = Brush.radialGradient(
            listOf(Color.White.copy(alpha = 0.85f), color.copy(alpha = 0.4f), Color.Transparent),
            center = Offset(cx, cy),
            radius = rOuter * 1.5f
        ),
        radius = rOuter * 1.5f,
        center = Offset(cx, cy)
    )
    drawPath(path, color = Color.White)
    drawPath(path, color = color.copy(alpha = 0.85f))
}

private fun DrawScope.drawDove(cx: Float, cy: Float, scale: Float, color: Color, gold: Color) {
    drawCircle(
        color = gold.copy(alpha = 0.35f),
        radius = scale * 1.35f,
        center = Offset(cx, cy)
    )
    drawCircle(
        color = gold,
        radius = scale * 1.35f,
        center = Offset(cx, cy),
        style = Stroke(width = 2.5f)
    )
    val wings = Path().apply {
        moveTo(cx, cy + scale * 0.35f)
        quadraticTo(cx - scale * 0.95f, cy - scale * 0.2f, cx - scale * 1.2f, cy - scale * 0.85f)
        quadraticTo(cx - scale * 0.45f, cy - scale * 0.55f, cx, cy - scale * 0.15f)
        quadraticTo(cx + scale * 0.45f, cy - scale * 0.55f, cx + scale * 1.2f, cy - scale * 0.85f)
        quadraticTo(cx + scale * 0.95f, cy - scale * 0.2f, cx, cy + scale * 0.35f)
        close()
    }
    drawPath(wings, color = color)
    val tail = Path().apply {
        moveTo(cx, cy + scale * 0.2f)
        lineTo(cx - scale * 0.32f, cy + scale * 0.78f)
        lineTo(cx + scale * 0.32f, cy + scale * 0.78f)
        close()
    }
    drawPath(tail, color = color)
}

private fun DrawScope.drawOliveTree(baseX: Float, baseY: Float, treeHeight: Float, trunkColor: Color, canopyColor: Color, gold: Color) {
    val trunk = Path().apply {
        moveTo(baseX - treeHeight * 0.08f, baseY)
        quadraticTo(baseX - treeHeight * 0.03f, baseY - treeHeight * 0.45f, baseX - treeHeight * 0.16f, baseY - treeHeight * 0.65f)
        lineTo(baseX + treeHeight * 0.16f, baseY - treeHeight * 0.65f)
        quadraticTo(baseX + treeHeight * 0.04f, baseY - treeHeight * 0.45f, baseX + treeHeight * 0.08f, baseY)
        close()
    }
    drawPath(trunk, color = trunkColor)
    drawCircle(color = canopyColor, radius = treeHeight * 0.32f, center = Offset(baseX, baseY - treeHeight * 0.75f))
    drawCircle(color = canopyColor.copy(alpha = 0.9f), radius = treeHeight * 0.25f, center = Offset(baseX - treeHeight * 0.24f, baseY - treeHeight * 0.64f))
    drawCircle(color = canopyColor.copy(alpha = 0.9f), radius = treeHeight * 0.25f, center = Offset(baseX + treeHeight * 0.24f, baseY - treeHeight * 0.64f))
    drawCircle(
        color = gold.copy(alpha = 0.45f),
        radius = treeHeight * 0.32f,
        center = Offset(baseX, baseY - treeHeight * 0.75f),
        style = Stroke(width = 2f)
    )
}

// ==================== 24 NARRATIVE SCENES WITH EXPRESSIVE BIBLICAL FIGURES ====================

private fun DrawScope.drawNativityStarScene(w: Float, h: Float, p: ArtPalette, pulse: Float) {
    drawLayeredHills(w, h, p.primaryJewel.copy(alpha = 0.55f), p.waterOrGround, p.foregroundSilhouette)
    val beam = Path().apply {
        moveTo(w * 0.5f, h * 0.20f)
        lineTo(w * 0.26f, h)
        lineTo(w * 0.74f, h)
        close()
    }
    drawPath(
        beam,
        brush = Brush.verticalGradient(
            listOf(p.goldLeaf.copy(alpha = 0.55f), p.goldLeaf.copy(alpha = 0.08f))
        )
    )
    // Mary Mother of Jesus & Holy Family figures under the Star
    val figScale = min(w, h) * 0.12f
    drawDetailedBiblicalPortrait(
        cx = w * 0.36f,
        headCy = h * 0.62f,
        scale = figScale,
        style = CharacterVisualStyle.MARY_MOTHER_OF_JESUS,
        primaryRobe = Color(0xFF1D4E89),
        mantle = Color(0xFF8C1D3B),
        gold = p.goldLeaf,
        pulse = pulse
    )
    drawDetailedBiblicalPortrait(
        cx = w * 0.64f,
        headCy = h * 0.62f,
        scale = figScale,
        style = CharacterVisualStyle.JAMES_GREATER,
        primaryRobe = Color(0xFF593D28),
        mantle = Color(0xFF2B4C7E),
        gold = p.goldLeaf,
        pulse = pulse
    )
    // Radiant Manger Cradle in Center
    drawOval(
        color = p.goldLeaf,
        topLeft = Offset(w * 0.43f, h * 0.72f),
        size = Size(w * 0.14f, h * 0.10f)
    )
    drawCircle(color = Color(0xFFF5CBA7), radius = figScale * 0.28f, center = Offset(w * 0.5f, h * 0.74f))
    drawStarOfBethlehem(w * 0.5f, h * 0.19f, min(w, h) * 0.12f, p.goldLeaf, pulse)
}

private fun DrawScope.drawJordanBaptismScene(w: Float, h: Float, p: ArtPalette, pulse: Float) {
    drawLayeredHills(w, h, p.secondaryJewel.copy(alpha = 0.6f), p.primaryJewel.copy(alpha = 0.7f), p.foregroundSilhouette)
    val river = Path().apply {
        moveTo(w * 0.35f, h * 0.58f)
        cubicTo(w * 0.25f, h * 0.72f, w * 0.72f, h * 0.82f, w * 0.18f, h)
        lineTo(w * 0.82f, h)
        cubicTo(w * 0.88f, h * 0.82f, w * 0.52f, h * 0.72f, w * 0.65f, h * 0.58f)
        close()
    }
    drawPath(
        river,
        brush = Brush.verticalGradient(
            listOf(Color(0xFF4AD2E8), Color(0xFF1A6B8C), p.waterOrGround)
        )
    )
    // Jesus in center of Jordan River + John the Baptist on right bank
    val figScale = min(w, h) * 0.135f
    drawDetailedBiblicalPortrait(
        cx = w * 0.44f,
        headCy = h * 0.58f,
        scale = figScale,
        style = CharacterVisualStyle.JESUS_PANTOCRATOR,
        primaryRobe = Color(0xFF9E1F38),
        mantle = Color(0xFF1B4B8A),
        gold = p.goldLeaf,
        pulse = pulse
    )
    drawDetailedBiblicalPortrait(
        cx = w * 0.72f,
        headCy = h * 0.55f,
        scale = figScale * 0.92f,
        style = CharacterVisualStyle.JOHN_THE_BAPTIST,
        primaryRobe = Color(0xFF7A5632),
        mantle = Color(0xFF28594B),
        gold = p.goldLeaf,
        pulse = pulse
    )
    drawDove(w * 0.44f, h * 0.24f, min(w, h) * 0.09f * (0.95f + 0.08f * pulse), Color.White, p.goldLeaf)
}

private fun DrawScope.drawSermonMountScene(w: Float, h: Float, p: ArtPalette, pulse: Float) {
    drawLayeredHills(w, h, Color(0xFF2E6B5E), Color(0xFF1F5447), p.foregroundSilhouette)
    drawOliveTree(w * 0.14f, h * 0.82f, h * 0.26f, p.foregroundSilhouette, p.primaryJewel, p.goldLeaf)
    drawOliveTree(w * 0.86f, h * 0.84f, h * 0.24f, p.foregroundSilhouette, p.secondaryJewel, p.goldLeaf)

    // Disciples Peter & John listening on the hillside, with Jesus teaching in the center
    val figScale = min(w, h) * 0.13f
    drawDetailedBiblicalPortrait(
        cx = w * 0.26f,
        headCy = h * 0.65f,
        scale = figScale * 0.85f,
        style = CharacterVisualStyle.SIMON_PETER,
        primaryRobe = Color(0xFF1D5E6E),
        mantle = Color(0xFFD9932E),
        gold = p.goldLeaf,
        pulse = pulse
    )
    drawDetailedBiblicalPortrait(
        cx = w * 0.74f,
        headCy = h * 0.65f,
        scale = figScale * 0.85f,
        style = CharacterVisualStyle.JOHN_BELOVED,
        primaryRobe = Color(0xFF1D6B52),
        mantle = Color(0xFFA82440),
        gold = p.goldLeaf,
        pulse = pulse
    )
    drawDetailedBiblicalPortrait(
        cx = w * 0.50f,
        headCy = h * 0.50f,
        scale = figScale * 1.12f,
        style = CharacterVisualStyle.JESUS_PANTOCRATOR,
        primaryRobe = Color(0xFF9E1F38),
        mantle = Color(0xFF1B4B8A),
        gold = p.goldLeaf,
        pulse = pulse
    )
}

private fun DrawScope.drawSeaOfGalileeBoatScene(w: Float, h: Float, p: ArtPalette, pulse: Float) {
    for (layer in 0..2) {
        val waveY = h * (0.64f + layer * 0.10f)
        val waveColor = when (layer) {
            0 -> p.secondaryJewel.copy(alpha = 0.8f)
            1 -> Color(0xFF18587A)
            else -> Color(0xFF103E59)
        }
        val wavePath = Path().apply {
            moveTo(0f, waveY)
            quadraticTo(w * 0.25f, waveY - 20f, w * 0.5f, waveY)
            quadraticTo(w * 0.75f, waveY + 20f, w, waveY - 8f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(wavePath, color = waveColor)
    }

    val boatX = w * 0.5f
    val boatY = h * 0.72f
    // Mast & Sail behind Jesus and Peter
    drawLine(
        color = p.goldLeaf,
        start = Offset(boatX - w * 0.08f, boatY - h * 0.44f),
        end = Offset(boatX - w * 0.08f, boatY),
        strokeWidth = 5f
    )
    val sail = Path().apply {
        moveTo(boatX - w * 0.08f, boatY - h * 0.42f)
        quadraticTo(boatX - w * 0.28f, boatY - h * 0.26f, boatX - w * 0.22f, boatY - h * 0.08f)
        lineTo(boatX - w * 0.06f, boatY - h * 0.08f)
        close()
    }
    drawPath(sail, color = Color(0xFFFFF6E5).copy(alpha = 0.88f))
    drawPath(sail, color = p.goldLeaf, style = Stroke(width = 2.5f))

    // Figures of Jesus calming the storm & Simon Peter in the boat
    val figScale = min(w, h) * 0.115f
    drawDetailedBiblicalPortrait(
        cx = boatX - w * 0.10f,
        headCy = boatY - h * 0.16f,
        scale = figScale * 0.85f,
        style = CharacterVisualStyle.SIMON_PETER,
        primaryRobe = Color(0xFF1D5E6E),
        mantle = Color(0xFFD9932E),
        gold = p.goldLeaf,
        pulse = pulse
    )
    drawDetailedBiblicalPortrait(
        cx = boatX + w * 0.10f,
        headCy = boatY - h * 0.21f,
        scale = figScale,
        style = CharacterVisualStyle.JESUS_PANTOCRATOR,
        primaryRobe = Color(0xFF9E1F38),
        mantle = Color(0xFF1B4B8A),
        gold = p.goldLeaf,
        pulse = pulse
    )

    // Wooden Galilean Hull in Foreground
    val hull = Path().apply {
        moveTo(boatX - w * 0.28f, boatY - 12f)
        lineTo(boatX + w * 0.28f, boatY - 12f)
        lineTo(boatX + w * 0.20f, boatY + 34f)
        lineTo(boatX - w * 0.20f, boatY + 34f)
        close()
    }
    drawPath(hull, color = Color(0xFF5C3317))
    drawPath(hull, color = p.goldLeaf, style = Stroke(width = 3f))
}

private fun DrawScope.drawLoavesAndFishesScene(w: Float, h: Float, p: ArtPalette, pulse: Float) {
    drawLayeredHills(w, h, p.secondaryJewel.copy(alpha = 0.55f), p.waterOrGround, p.foregroundSilhouette)
    val figScale = min(w, h) * 0.125f
    // Jesus blessing the loaves with Philip & Andrew
    drawDetailedBiblicalPortrait(
        cx = w * 0.25f,
        headCy = h * 0.58f,
        scale = figScale * 0.84f,
        style = CharacterVisualStyle.PHILIP_APOSTLE,
        primaryRobe = Color(0xFF286652),
        mantle = Color(0xFFC47D2B),
        gold = p.goldLeaf,
        pulse = pulse
    )
    drawDetailedBiblicalPortrait(
        cx = w * 0.75f,
        headCy = h * 0.58f,
        scale = figScale * 0.84f,
        style = CharacterVisualStyle.ANDREW_APOSTLE,
        primaryRobe = Color(0xFF1B5278),
        mantle = Color(0xFF2E7A62),
        gold = p.goldLeaf,
        pulse = pulse
    )
    drawDetailedBiblicalPortrait(
        cx = w * 0.50f,
        headCy = h * 0.46f,
        scale = figScale,
        style = CharacterVisualStyle.JESUS_PANTOCRATOR,
        primaryRobe = Color(0xFF9E1F38),
        mantle = Color(0xFF1B4B8A),
        gold = p.goldLeaf,
        pulse = pulse
    )
    // Woven basket of five golden loaves in foreground
    val cx = w * 0.5f
    val cy = h * 0.82f
    for (i in -2..2) {
        drawOval(
            color = Color(0xFFE8A435),
            topLeft = Offset(cx + i * 24f - 22f, cy - 22f),
            size = Size(44f, 24f)
        )
    }
    val basket = Path().apply {
        moveTo(cx - 75f, cy - 6f)
        lineTo(cx + 75f, cy - 6f)
        lineTo(cx + 52f, cy + 36f)
        lineTo(cx - 52f, cy + 36f)
        close()
    }
    drawPath(basket, color = Color(0xFF9C6428))
    drawPath(basket, color = p.goldLeaf, style = Stroke(width = 2.5f))
}

private fun DrawScope.drawGoodShepherdScene(w: Float, h: Float, p: ArtPalette, pulse: Float) {
    drawLayeredHills(w, h, Color(0xFF2E856E), Color(0xFF20604F), p.foregroundSilhouette)
    val figScale = min(w, h) * 0.145f
    drawDetailedBiblicalPortrait(
        cx = w * 0.5f,
        headCy = h * 0.48f,
        scale = figScale,
        style = CharacterVisualStyle.JESUS_PANTOCRATOR,
        primaryRobe = Color(0xFF9E1F38),
        mantle = Color(0xFF1B4B8A),
        gold = p.goldLeaf,
        pulse = pulse
    )
    // Golden Shepherd's Crook beside Jesus
    val staffX = w * 0.34f
    drawLine(
        color = p.goldLeaf,
        start = Offset(staffX, h * 0.34f),
        end = Offset(staffX, h * 0.88f),
        strokeWidth = 6f,
        cap = StrokeCap.Round
    )
    drawArc(
        color = p.goldLeaf,
        startAngle = 180f,
        sweepAngle = 250f,
        useCenter = false,
        topLeft = Offset(staffX - 24f, h * 0.28f),
        size = Size(48f, 48f),
        style = Stroke(width = 6f, cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawTransfigurationScene(w: Float, h: Float, p: ArtPalette, pulse: Float) {
    val mountain = Path().apply {
        moveTo(w * 0.05f, h)
        lineTo(w * 0.5f, h * 0.42f)
        lineTo(w * 0.95f, h)
        close()
    }
    drawPath(mountain, color = p.foregroundSilhouette)
    drawPath(mountain, color = p.goldLeaf, style = Stroke(width = 3f))

    val cx = w * 0.5f
    val cy = h * 0.42f
    drawOval(
        brush = Brush.radialGradient(
            listOf(Color.White, p.goldLeaf, Color.Transparent),
            center = Offset(cx, cy),
            radius = min(w, h) * 0.34f
        ),
        topLeft = Offset(cx - 85f, cy - 115f),
        size = Size(170f, 230f)
    )
    val figScale = min(w, h) * 0.13f
    // Moses & Elijah flanking Transfigured Christ in White & Gold
    drawDetailedBiblicalPortrait(
        cx = w * 0.24f,
        headCy = h * 0.60f,
        scale = figScale * 0.82f,
        style = CharacterVisualStyle.ANDREW_APOSTLE,
        primaryRobe = Color(0xFF28467A),
        mantle = Color(0xFF8C3B2B),
        gold = p.goldLeaf,
        pulse = pulse
    )
    drawDetailedBiblicalPortrait(
        cx = w * 0.76f,
        headCy = h * 0.60f,
        scale = figScale * 0.82f,
        style = CharacterVisualStyle.JOHN_THE_BAPTIST,
        primaryRobe = Color(0xFF6E4726),
        mantle = Color(0xFF2E6B5E),
        gold = p.goldLeaf,
        pulse = pulse
    )
    drawDetailedBiblicalPortrait(
        cx = cx,
        headCy = h * 0.44f,
        scale = figScale * 1.1f,
        style = CharacterVisualStyle.JESUS_PANTOCRATOR,
        primaryRobe = Color(0xFFFFFBF0),
        mantle = p.goldLeaf,
        gold = p.goldLeaf,
        pulse = pulse
    )
}

private fun DrawScope.drawJerusalemPalmsScene(w: Float, h: Float, p: ArtPalette, pulse: Float) {
    val cityBaseY = h * 0.82f
    drawArc(
        color = p.goldLeaf,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(w * 0.35f, cityBaseY - h * 0.36f),
        size = Size(w * 0.30f, h * 0.28f)
    )
    drawRect(
        color = p.foregroundSilhouette,
        topLeft = Offset(w * 0.20f, cityBaseY - h * 0.22f),
        size = Size(w * 0.60f, h * 0.26f)
    )
    // Jesus entering before the Golden Gate of Jerusalem
    val figScale = min(w, h) * 0.135f
    drawDetailedBiblicalPortrait(
        cx = w * 0.5f,
        headCy = h * 0.52f,
        scale = figScale,
        style = CharacterVisualStyle.JESUS_PANTOCRATOR,
        primaryRobe = Color(0xFF9E1F38),
        mantle = Color(0xFF1B4B8A),
        gold = p.goldLeaf,
        pulse = pulse
    )
    // Arching Palm Fronds Left & Right
    for (dir in listOf(-1f, 1f)) {
        val startX = if (dir < 0) w * 0.08f else w * 0.92f
        val frond = Path().apply {
            moveTo(startX, h * 0.92f)
            quadraticTo(w * 0.5f - dir * 48f, h * 0.45f, w * 0.5f + dir * 18f, h * 0.22f)
        }
        drawPath(frond, color = Color(0xFF2FA875), style = Stroke(width = 7f, cap = StrokeCap.Round))
    }
}

private fun DrawScope.drawLastSupperChaliceScene(w: Float, h: Float, p: ArtPalette, pulse: Float) {
    val figScale = min(w, h) * 0.125f
    // Simon Peter, Jesus Christ, and John the Beloved at the Upper Room Table
    drawDetailedBiblicalPortrait(
        cx = w * 0.24f,
        headCy = h * 0.50f,
        scale = figScale * 0.88f,
        style = CharacterVisualStyle.SIMON_PETER,
        primaryRobe = Color(0xFF1D5E6E),
        mantle = Color(0xFFD9932E),
        gold = p.goldLeaf,
        pulse = pulse
    )
    drawDetailedBiblicalPortrait(
        cx = w * 0.76f,
        headCy = h * 0.50f,
        scale = figScale * 0.88f,
        style = CharacterVisualStyle.JOHN_BELOVED,
        primaryRobe = Color(0xFF1D6B52),
        mantle = Color(0xFFA82440),
        gold = p.goldLeaf,
        pulse = pulse
    )
    drawDetailedBiblicalPortrait(
        cx = w * 0.50f,
        headCy = h * 0.44f,
        scale = figScale * 1.05f,
        style = CharacterVisualStyle.JESUS_PANTOCRATOR,
        primaryRobe = Color(0xFF9E1F38),
        mantle = Color(0xFF1B4B8A),
        gold = p.goldLeaf,
        pulse = pulse
    )

    // Upper Room Altar Table & Golden Chalice in Foreground
    val baseY = h * 0.84f
    drawRect(color = Color(0xFFFFF8EC), topLeft = Offset(w * 0.08f, baseY), size = Size(w * 0.84f, h * 0.16f))
    drawLine(color = p.goldLeaf, start = Offset(w * 0.08f, baseY), end = Offset(w * 0.92f, baseY), strokeWidth = 4f)
    val cx = w * 0.5f
    val cup = Path().apply {
        moveTo(cx - 28f, baseY - 68f)
        lineTo(cx + 28f, baseY - 68f)
        quadraticTo(cx + 22f, baseY - 32f, cx, baseY - 30f)
        quadraticTo(cx - 22f, baseY - 32f, cx - 28f, baseY - 68f)
        close()
    }
    drawPath(cup, color = p.goldLeaf)
    drawLine(color = p.goldLeaf, start = Offset(cx, baseY - 30f), end = Offset(cx, baseY - 4f), strokeWidth = 7f)
    drawOval(color = Color(0xFFC92A42), topLeft = Offset(cx - 25f, baseY - 72f), size = Size(50f, 10f))
}

private fun DrawScope.drawGethsemaneOlivesScene(w: Float, h: Float, p: ArtPalette, pulse: Float) {
    drawLayeredHills(w, h, p.skyMiddle, p.waterOrGround, p.foregroundSilhouette)
    drawCircle(color = Color(0xFFFFF5D6), radius = min(w, h) * 0.11f, center = Offset(w * 0.78f, h * 0.24f))
    drawOliveTree(w * 0.22f, h * 0.85f, h * 0.40f, p.foregroundSilhouette, Color(0xFF285948), p.goldLeaf)
    // Jesus praying in Gethsemane beneath the moonlit olive trees
    val figScale = min(w, h) * 0.135f
    drawDetailedBiblicalPortrait(
        cx = w * 0.56f,
        headCy = h * 0.54f,
        scale = figScale,
        style = CharacterVisualStyle.JESUS_PANTOCRATOR,
        primaryRobe = Color(0xFF9E1F38),
        mantle = Color(0xFF1B4B8A),
        gold = p.goldLeaf,
        pulse = pulse
    )
}

private fun DrawScope.drawCalvaryDawnScene(w: Float, h: Float, p: ArtPalette, pulse: Float) {
    val hill = Path().apply {
        moveTo(0f, h * 0.85f)
        quadraticTo(w * 0.5f, h * 0.56f, w, h * 0.85f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(hill, color = p.foregroundSilhouette)
    drawCircle(
        brush = Brush.radialGradient(
            listOf(p.goldLeaf.copy(alpha = 0.7f), Color.Transparent),
            center = Offset(w * 0.5f, h * 0.40f),
            radius = min(w, h) * 0.34f
        ),
        radius = min(w, h) * 0.34f,
        center = Offset(w * 0.5f, h * 0.40f)
    )
    // Center Cross & Side Crosses
    drawLine(color = p.foregroundSilhouette, start = Offset(w * 0.5f, h * 0.18f), end = Offset(w * 0.5f, h * 0.66f), strokeWidth = 10f)
    drawLine(color = p.foregroundSilhouette, start = Offset(w * 0.39f, h * 0.30f), end = Offset(w * 0.61f, h * 0.30f), strokeWidth = 9f)
    // Mary Mother of Jesus & John the Beloved standing beneath the Cross
    val figScale = min(w, h) * 0.11f
    drawDetailedBiblicalPortrait(
        cx = w * 0.26f,
        headCy = h * 0.62f,
        scale = figScale,
        style = CharacterVisualStyle.MARY_MOTHER_OF_JESUS,
        primaryRobe = Color(0xFF1D4E89),
        mantle = Color(0xFF821D3B),
        gold = p.goldLeaf,
        pulse = pulse
    )
    drawDetailedBiblicalPortrait(
        cx = w * 0.74f,
        headCy = h * 0.62f,
        scale = figScale,
        style = CharacterVisualStyle.JOHN_BELOVED,
        primaryRobe = Color(0xFF1D6B52),
        mantle = Color(0xFFA82440),
        gold = p.goldLeaf,
        pulse = pulse
    )
}

private fun DrawScope.drawEmptyTombSunriseScene(w: Float, h: Float, p: ArtPalette, pulse: Float) {
    drawLayeredHills(w, h, p.primaryJewel.copy(alpha = 0.5f), p.waterOrGround, p.foregroundSilhouette)
    val tombX = w * 0.30f
    val tombY = h * 0.78f
    drawArc(
        color = p.foregroundSilhouette,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(w * 0.04f, h * 0.38f),
        size = Size(w * 0.52f, h * 0.76f)
    )
    drawArc(
        brush = Brush.radialGradient(
            listOf(Color.White, p.goldLeaf, p.skyHorizon),
            center = Offset(tombX, tombY - 30f),
            radius = 90f
        ),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(tombX - 50f, tombY - 105f),
        size = Size(100f, 150f)
    )
    // Risen Christ & Mary Magdalene in the Resurrection Garden
    val figScale = min(w, h) * 0.125f
    drawDetailedBiblicalPortrait(
        cx = w * 0.54f,
        headCy = h * 0.50f,
        scale = figScale,
        style = CharacterVisualStyle.JESUS_PANTOCRATOR,
        primaryRobe = Color(0xFFFFFAF0),
        mantle = p.goldLeaf,
        gold = p.goldLeaf,
        pulse = pulse
    )
    drawDetailedBiblicalPortrait(
        cx = w * 0.78f,
        headCy = h * 0.58f,
        scale = figScale * 0.88f,
        style = CharacterVisualStyle.MARY_MAGDALENE,
        primaryRobe = Color(0xFF9E2B46),
        mantle = Color(0xFF345E7D),
        gold = p.goldLeaf,
        pulse = pulse
    )
}

private fun DrawScope.drawAscensionCloudsScene(w: Float, h: Float, p: ArtPalette, pulse: Float) {
    drawLayeredHills(w, h, p.secondaryJewel.copy(alpha = 0.5f), p.waterOrGround, p.foregroundSilhouette)
    for (i in 0..4) {
        val cx = w * (0.26f + i * 0.12f)
        val cy = h * (0.54f - (if (i == 2) 0.06f else 0f))
        drawCircle(color = Color.White.copy(alpha = 0.85f), radius = 44f, center = Offset(cx, cy))
        drawCircle(color = p.goldLeaf, radius = 44f, center = Offset(cx, cy), style = Stroke(width = 2.5f))
    }
    val figScale = min(w, h) * 0.125f
    drawDetailedBiblicalPortrait(
        cx = w * 0.5f,
        headCy = h * 0.36f,
        scale = figScale,
        style = CharacterVisualStyle.JESUS_PANTOCRATOR,
        primaryRobe = Color(0xFF9E1F38),
        mantle = Color(0xFF1B4B8A),
        gold = p.goldLeaf,
        pulse = pulse
    )
}

private fun DrawScope.drawPentecostFlamesScene(w: Float, h: Float, p: ArtPalette, pulse: Float) {
    drawDove(w * 0.5f, h * 0.22f, min(w, h) * 0.085f, Color.White, p.goldLeaf)
    val figScale = min(w, h) * 0.115f
    // Peter, Mary Mother of Jesus, and John in the Upper Room with Pentecost Flames above their halos
    val figures = listOf(
        Triple(w * 0.24f, CharacterVisualStyle.SIMON_PETER, Pair(Color(0xFF1D5E6E), Color(0xFFD9932E))),
        Triple(w * 0.50f, CharacterVisualStyle.MARY_MOTHER_OF_JESUS, Pair(Color(0xFF1D4E89), Color(0xFF821D3B))),
        Triple(w * 0.76f, CharacterVisualStyle.JOHN_BELOVED, Pair(Color(0xFF1D6B52), Color(0xFFA82440)))
    )
    for ((fx, style, colors) in figures) {
        drawDetailedBiblicalPortrait(
            cx = fx,
            headCy = h * 0.56f,
            scale = figScale,
            style = style,
            primaryRobe = colors.first,
            mantle = colors.second,
            gold = p.goldLeaf,
            pulse = pulse
        )
        // Tongue of Sacred Flame above each head
        val flameY = h * 0.38f
        val flame = Path().apply {
            moveTo(fx, flameY - 28f * (0.9f + 0.2f * pulse))
            quadraticTo(fx + 14f, flameY - 6f, fx, flameY + 12f)
            quadraticTo(fx - 14f, flameY - 6f, fx, flameY - 28f * (0.9f + 0.2f * pulse))
            close()
        }
        drawPath(flame, brush = Brush.verticalGradient(listOf(p.goldLeaf, Color(0xFFE63946))))
    }
}

private fun DrawScope.drawTempleSolomonPorchScene(w: Float, h: Float, p: ArtPalette, pulse: Float) {
    val colBottom = h * 0.88f
    val colTop = h * 0.28f
    for (i in 0..3) {
        val cx = w * (0.14f + i * 0.24f)
        drawRect(color = Color(0xFFF5EFE0), topLeft = Offset(cx - 14f, colTop), size = Size(28f, colBottom - colTop))
        drawRect(color = p.goldLeaf, topLeft = Offset(cx - 22f, colTop - 10f), size = Size(44f, 12f))
    }
    val pediment = Path().apply {
        moveTo(w * 0.08f, colTop - 10f)
        lineTo(w * 0.5f, h * 0.12f)
        lineTo(w * 0.92f, colTop - 10f)
        close()
    }
    drawPath(pediment, color = p.primaryJewel.copy(alpha = 0.85f))
    drawPath(pediment, color = p.goldLeaf, style = Stroke(width = 3.5f))

    // Peter & John proclaiming in the Temple Portico
    val figScale = min(w, h) * 0.125f
    drawDetailedBiblicalPortrait(
        cx = w * 0.38f,
        headCy = h * 0.54f,
        scale = figScale,
        style = CharacterVisualStyle.SIMON_PETER,
        primaryRobe = Color(0xFF1D5E6E),
        mantle = Color(0xFFD9932E),
        gold = p.goldLeaf,
        pulse = pulse
    )
    drawDetailedBiblicalPortrait(
        cx = w * 0.64f,
        headCy = h * 0.54f,
        scale = figScale * 0.92f,
        style = CharacterVisualStyle.JOHN_BELOVED,
        primaryRobe = Color(0xFF1D6B52),
        mantle = Color(0xFFA82440),
        gold = p.goldLeaf,
        pulse = pulse
    )
}

private fun DrawScope.drawDamascusLightScene(w: Float, h: Float, p: ArtPalette, pulse: Float) {
    drawLayeredHills(w, h, p.primaryJewel.copy(alpha = 0.5f), p.waterOrGround, p.foregroundSilhouette)
    val shaft = Path().apply {
        moveTo(w * 0.15f, 0f)
        lineTo(w * 0.44f, 0f)
        lineTo(w * 0.80f, h)
        lineTo(w * 0.32f, h)
        close()
    }
    drawPath(
        shaft,
        brush = Brush.linearGradient(
            listOf(Color.White.copy(alpha = 0.9f), p.goldLeaf.copy(alpha = 0.55f), Color.Transparent)
        )
    )
    // Paul of Tarsus illuminated by the heavenly beam
    val figScale = min(w, h) * 0.14f
    drawDetailedBiblicalPortrait(
        cx = w * 0.54f,
        headCy = h * 0.52f,
        scale = figScale,
        style = CharacterVisualStyle.PAUL_OF_TARSUS,
        primaryRobe = Color(0xFF234270),
        mantle = Color(0xFF9C2740),
        gold = p.goldLeaf,
        pulse = pulse
    )
    drawStarOfBethlehem(w * 0.30f, h * 0.18f, min(w, h) * 0.12f, p.goldLeaf, pulse)
}

private fun DrawScope.drawMediterraneanVoyageScene(w: Float, h: Float, p: ArtPalette, pulse: Float) {
    drawSeaOfGalileeBoatScene(w, h, p, pulse)
    drawRect(color = p.goldLeaf, topLeft = Offset(w * 0.84f, h * 0.44f), size = Size(14f, 52f))
    drawCircle(color = Color.White, radius = 12f * (0.9f + 0.2f * pulse), center = Offset(w * 0.84f + 7f, h * 0.42f))
}

private fun DrawScope.drawAreopagusAthensScene(w: Float, h: Float, p: ArtPalette, pulse: Float) {
    val colBottom = h * 0.88f
    val colTop = h * 0.30f
    for (i in listOf(0.16f, 0.84f)) {
        val cx = w * i
        drawRect(color = Color(0xFFF5EFE0), topLeft = Offset(cx - 16f, colTop), size = Size(32f, colBottom - colTop))
        drawRect(color = p.goldLeaf, topLeft = Offset(cx - 24f, colTop - 10f), size = Size(48f, 12f))
    }
    // Paul preaching on Mars Hill in Athens
    val figScale = min(w, h) * 0.14f
    drawDetailedBiblicalPortrait(
        cx = w * 0.5f,
        headCy = h * 0.50f,
        scale = figScale,
        style = CharacterVisualStyle.PAUL_OF_TARSUS,
        primaryRobe = Color(0xFF234270),
        mantle = Color(0xFF9C2740),
        gold = p.goldLeaf,
        pulse = pulse
    )
}

private fun DrawScope.drawScrollAndLampScene(w: Float, h: Float, p: ArtPalette, pulse: Float) {
    // Apostolic Scribe (Paul / Evangelist) writing the Epistle beside a glowing lamp
    val figScale = min(w, h) * 0.135f
    drawDetailedBiblicalPortrait(
        cx = w * 0.50f,
        headCy = h * 0.46f,
        scale = figScale,
        style = CharacterVisualStyle.PAUL_OF_TARSUS,
        primaryRobe = p.secondaryJewel,
        mantle = p.primaryJewel,
        gold = p.goldLeaf,
        pulse = pulse
    )
    // Illuminated Scroll in foreground desk
    val cx = w * 0.5f
    val cy = h * 0.80f
    drawRoundRect(
        color = Color(0xFFFFF5E1),
        topLeft = Offset(cx - 95f, cy - 28f),
        size = Size(190f, 56f),
        cornerRadius = CornerRadius(10f, 10f)
    )
    drawRoundRect(color = p.goldLeaf, topLeft = Offset(cx - 105f, cy - 34f), size = Size(16f, 68f), cornerRadius = CornerRadius(6f, 6f))
    drawRoundRect(color = p.goldLeaf, topLeft = Offset(cx + 89f, cy - 34f), size = Size(16f, 68f), cornerRadius = CornerRadius(6f, 6f))
}

private fun DrawScope.drawArmorOfFaithScene(w: Float, h: Float, p: ArtPalette, pulse: Float) {
    val figScale = min(w, h) * 0.135f
    drawDetailedBiblicalPortrait(
        cx = w * 0.5f,
        headCy = h * 0.44f,
        scale = figScale,
        style = CharacterVisualStyle.PAUL_OF_TARSUS,
        primaryRobe = p.primaryJewel,
        mantle = p.secondaryJewel,
        gold = p.goldLeaf,
        pulse = pulse
    )
    // Shield of Faith Emblem in lower center
    val cx = w * 0.5f
    val cy = h * 0.76f
    val shield = Path().apply {
        moveTo(cx - 48f, cy - 42f)
        lineTo(cx + 48f, cy - 42f)
        quadraticTo(cx + 48f, cy + 12f, cx, cy + 52f)
        quadraticTo(cx - 48f, cy + 12f, cx - 48f, cy - 42f)
        close()
    }
    drawPath(shield, color = p.primaryJewel)
    drawPath(shield, color = p.goldLeaf, style = Stroke(width = 4.5f))
    drawLine(color = p.goldLeaf, start = Offset(cx, cy - 30f), end = Offset(cx, cy + 36f), strokeWidth = 8f, cap = StrokeCap.Round)
    drawLine(color = p.goldLeaf, start = Offset(cx - 28f, cy - 6f), end = Offset(cx + 28f, cy - 6f), strokeWidth = 8f, cap = StrokeCap.Round)
}

private fun DrawScope.drawAnchorOfHopeScene(w: Float, h: Float, p: ArtPalette, pulse: Float) {
    val figScale = min(w, h) * 0.13f
    drawDetailedBiblicalPortrait(
        cx = w * 0.34f,
        headCy = h * 0.52f,
        scale = figScale,
        style = CharacterVisualStyle.SIMON_PETER,
        primaryRobe = Color(0xFF1D5E6E),
        mantle = Color(0xFFD9932E),
        gold = p.goldLeaf,
        pulse = pulse
    )
    val cx = w * 0.68f
    val cy = h * 0.56f
    drawCircle(color = p.goldLeaf, radius = 18f, center = Offset(cx, cy - 75f), style = Stroke(width = 6f))
    drawLine(color = p.goldLeaf, start = Offset(cx, cy - 56f), end = Offset(cx, cy + 68f), strokeWidth = 8f, cap = StrokeCap.Round)
    drawLine(color = p.goldLeaf, start = Offset(cx - 38f, cy - 28f), end = Offset(cx + 38f, cy - 28f), strokeWidth = 7f, cap = StrokeCap.Round)
    drawArc(
        color = p.goldLeaf,
        startAngle = 20f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(cx - 65f, cy - 12f),
        size = Size(130f, 88f),
        style = Stroke(width = 8f, cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawSevenLampstandsScene(w: Float, h: Float, p: ArtPalette, pulse: Float) {
    val cx = w * 0.5f
    val baseY = h * 0.86f
    // Seven Golden Lampstands Flanking the Risen Christ
    for (branch in listOf(-3, -2, -1, 1, 2, 3)) {
        val lx = cx + branch * (w * 0.11f)
        val topY = h * (0.36f + kotlin.math.abs(branch) * 0.03f)
        drawLine(color = p.goldLeaf, start = Offset(lx, topY), end = Offset(lx, baseY), strokeWidth = 5f)
        drawCircle(color = Color.White, radius = 7f * (0.9f + 0.2f * pulse), center = Offset(lx, topY - 8f))
        drawCircle(color = p.goldLeaf, radius = 13f, center = Offset(lx, topY - 8f))
    }
    // Glorified Risen Christ in the Midst of the Seven Lampstands
    val figScale = min(w, h) * 0.14f
    drawDetailedBiblicalPortrait(
        cx = cx,
        headCy = h * 0.48f,
        scale = figScale,
        style = CharacterVisualStyle.JESUS_PANTOCRATOR,
        primaryRobe = Color(0xFFFFFBF0),
        mantle = p.goldLeaf,
        gold = p.goldLeaf,
        pulse = pulse
    )
}

private fun DrawScope.drawThroneAndRainbowScene(w: Float, h: Float, p: ArtPalette, pulse: Float) {
    val cx = w * 0.5f
    val cy = h * 0.52f
    val rainbowColors = listOf(Color(0xFF2EC4B6), p.goldLeaf, p.primaryJewel)
    rainbowColors.forEachIndexed { idx, col ->
        val r = min(w, h) * (0.40f - idx * 0.032f)
        drawArc(
            color = col.copy(alpha = 0.78f),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(cx - r, cy - r),
            size = Size(r * 2f, r * 2f),
            style = Stroke(width = 9f, cap = StrokeCap.Round)
        )
    }
    // Christ Enthroned in Majesty
    val figScale = min(w, h) * 0.14f
    drawDetailedBiblicalPortrait(
        cx = cx,
        headCy = h * 0.46f,
        scale = figScale,
        style = CharacterVisualStyle.JESUS_PANTOCRATOR,
        primaryRobe = Color(0xFF9E1F38),
        mantle = Color(0xFF1B4B8A),
        gold = p.goldLeaf,
        pulse = pulse
    )
}

private fun DrawScope.drawNewJerusalemTreeScene(w: Float, h: Float, p: ArtPalette, pulse: Float) {
    val cx = w * 0.5f
    for (i in -3..3) {
        val bx = cx + i * 36f - 16f
        val bh = 75f - kotlin.math.abs(i) * 10f
        drawRect(
            color = p.goldLeaf.copy(alpha = 0.85f),
            topLeft = Offset(bx, h * 0.52f - bh),
            size = Size(30f, bh)
        )
    }
    val river = Path().apply {
        moveTo(cx - 18f, h * 0.52f)
        lineTo(cx + 18f, h * 0.52f)
        lineTo(cx + 115f, h)
        lineTo(cx - 115f, h)
        close()
    }
    drawPath(
        river,
        brush = Brush.verticalGradient(listOf(Color(0xFFA8F9FF), Color(0xFF2896B8), p.primaryJewel))
    )
    drawOliveTree(w * 0.20f, h * 0.88f, h * 0.36f, p.foregroundSilhouette, Color(0xFF2AA876), p.goldLeaf)
    drawOliveTree(w * 0.80f, h * 0.88f, h * 0.36f, p.foregroundSilhouette, Color(0xFF2AA876), p.goldLeaf)

    // John the Beloved beholding the New Jerusalem
    val figScale = min(w, h) * 0.115f
    drawDetailedBiblicalPortrait(
        cx = w * 0.50f,
        headCy = h * 0.58f,
        scale = figScale,
        style = CharacterVisualStyle.JESUS_PANTOCRATOR,
        primaryRobe = Color(0xFF9E1F38),
        mantle = Color(0xFF1B4B8A),
        gold = p.goldLeaf,
        pulse = pulse
    )
}

private fun DrawScope.drawDavidHarpPasturesScene(w: Float, h: Float, p: ArtPalette, pulse: Float) {
    // Lush Emerald Pastures of Bethlehem & Still Waters River
    drawLayeredHills(w, h, Color(0xFF287D65), Color(0xFF1A5E4B), p.foregroundSilhouette)
    val river = Path().apply {
        moveTo(w * 0.42f, h * 0.62f)
        cubicTo(w * 0.28f, h * 0.75f, w * 0.68f, h * 0.85f, w * 0.20f, h)
        lineTo(w * 0.80f, h)
        cubicTo(w * 0.86f, h * 0.84f, w * 0.54f, h * 0.74f, w * 0.58f, h * 0.62f)
        close()
    }
    drawPath(
        river,
        brush = Brush.verticalGradient(
            listOf(Color(0xFF5CE6F5), Color(0xFF1D749B), p.waterOrGround)
        )
    )
    drawOliveTree(w * 0.14f, h * 0.84f, h * 0.28f, p.foregroundSilhouette, Color(0xFF2AA876), p.goldLeaf)
    drawOliveTree(w * 0.86f, h * 0.84f, h * 0.28f, p.foregroundSilhouette, Color(0xFF2AA876), p.goldLeaf)

    val figScale = min(w, h) * 0.135f
    // Asaph Chief Musician on left, King David Psalmist in center with Golden Harp
    drawDetailedBiblicalPortrait(
        cx = w * 0.26f,
        headCy = h * 0.58f,
        scale = figScale * 0.84f,
        style = CharacterVisualStyle.ASAPH_AND_KORAH,
        primaryRobe = Color(0xFF1E6E78),
        mantle = Color(0xFFD99A2B),
        gold = p.goldLeaf,
        pulse = pulse
    )
    drawDetailedBiblicalPortrait(
        cx = w * 0.54f,
        headCy = h * 0.48f,
        scale = figScale * 1.06f,
        style = CharacterVisualStyle.KING_DAVID_PSALMIST,
        primaryRobe = Color(0xFF1B5E7A),
        mantle = Color(0xFF9E1F38),
        gold = p.goldLeaf,
        pulse = pulse
    )
    // Sculpted 3D Golden Kinnor Harp in foreground
    val hx = w * 0.76f
    val hy = h * 0.72f
    val harpFrame = Path().apply {
        moveTo(hx - 24f, hy + 28f)
        lineTo(hx + 24f, hy + 28f)
        quadraticTo(hx + 34f, hy - 34f, hx + 18f, hy - 54f)
        quadraticTo(hx, hy - 44f, hx - 22f, hy - 48f)
        quadraticTo(hx - 30f, hy - 10f, hx - 24f, hy + 28f)
        close()
    }
    drawPath(harpFrame, color = p.goldLeaf, style = Stroke(width = 5.5f, cap = StrokeCap.Round))
    for (s in -2..2) {
        val sx = hx + s * 7.5f
        drawLine(
            color = Color.White.copy(alpha = 0.85f),
            start = Offset(sx, hy - 42f),
            end = Offset(sx, hy + 24f),
            strokeWidth = 1.8f
        )
    }
}

private fun DrawScope.drawSolomonWisdomPillarsScene(w: Float, h: Float, p: ArtPalette, pulse: Float) {
    // Seven Sculpted Pillars of Wisdom
    val colBottom = h * 0.88f
    val colTop = h * 0.26f
    for (i in 0..6) {
        val cx = w * (0.08f + i * 0.14f)
        drawRect(
            brush = Brush.horizontalGradient(
                listOf(Color(0xFFD9CEB8), Color(0xFFFFFAF0), Color(0xFFB8A98E)),
                startX = cx - 10f,
                endX = cx + 10f
            ),
            topLeft = Offset(cx - 10f, colTop),
            size = Size(20f, colBottom - colTop)
        )
        drawRect(color = p.goldLeaf, topLeft = Offset(cx - 15f, colTop - 8f), size = Size(30f, 10f))
        drawRect(color = p.goldLeaf, topLeft = Offset(cx - 15f, colBottom - 6f), size = Size(30f, 10f))
    }

    val figScale = min(w, h) * 0.135f
    // King Solomon and Lady Wisdom in the Seven-Pillared Royal Court
    drawDetailedBiblicalPortrait(
        cx = w * 0.36f,
        headCy = h * 0.50f,
        scale = figScale * 1.04f,
        style = CharacterVisualStyle.KING_SOLOMON_WISDOM,
        primaryRobe = Color(0xFF4B2378),
        mantle = Color(0xFFD99426),
        gold = p.goldLeaf,
        pulse = pulse
    )
    drawDetailedBiblicalPortrait(
        cx = w * 0.68f,
        headCy = h * 0.54f,
        scale = figScale * 0.92f,
        style = CharacterVisualStyle.LADY_WISDOM_VIRTUOUS,
        primaryRobe = Color(0xFF9E1F46),
        mantle = Color(0xFF28558A),
        gold = p.goldLeaf,
        pulse = pulse
    )
}

// ==================== OVERLAYS & GILDED BORDERS ====================

private fun DrawScope.drawStainedGlassLeadwork(w: Float, h: Float, palette: ArtPalette, seed: Int) {
    val leadColor = Color(0xFF120E1C).copy(alpha = 0.55f)
    val cx = w * 0.5f
    val cy = h * 0.34f
    val archPath = Path().apply {
        moveTo(16f, h - 16f)
        lineTo(16f, h * 0.38f)
        quadraticTo(16f, 20f, cx, 16f)
        quadraticTo(w - 16f, 20f, w - 16f, h * 0.38f)
        lineTo(w - 16f, h - 16f)
    }
    drawPath(archPath, color = leadColor, style = Stroke(width = 4.5f))
    drawPath(archPath, color = palette.goldLeaf.copy(alpha = 0.55f), style = Stroke(width = 1.5f))

    val spokes = 8 + (seed % 3) * 2
    for (i in 0 until spokes) {
        val angle = (i * 2.0 * PI / spokes).toFloat()
        drawLine(
            color = leadColor,
            start = Offset(cx + cos(angle) * 28f, cy + sin(angle) * 28f),
            end = Offset(cx + cos(angle) * min(w, h) * 0.48f, cy + sin(angle) * min(w, h) * 0.48f),
            strokeWidth = 2.5f
        )
    }
}

private fun DrawScope.drawIlluminatedCodexFiligree(w: Float, h: Float, palette: ArtPalette, pulse: Float) {
    val gold = palette.goldLeaf.copy(alpha = 0.75f + 0.15f * pulse)
    val m = 22f
    drawRoundRect(
        color = gold,
        topLeft = Offset(m, m),
        size = Size(w - m * 2, h - m * 2),
        cornerRadius = CornerRadius(14f, 14f),
        style = Stroke(width = 2.5f)
    )
    val corners = listOf(
        Offset(m + 14f, m + 14f),
        Offset(w - m - 14f, m + 14f),
        Offset(m + 14f, h - m - 14f),
        Offset(w - m - 14f, h - m - 14f)
    )
    for (c in corners) {
        drawCircle(color = gold, radius = 9f, center = c)
        drawCircle(color = palette.primaryJewel, radius = 5f, center = c)
    }
}

private fun DrawScope.drawFrescoLightBloom(w: Float, h: Float, palette: ArtPalette, pulse: Float) {
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(
                palette.goldLeaf.copy(alpha = 0.16f + 0.08f * pulse),
                Color.Transparent,
                Color.Black.copy(alpha = 0.25f)
            ),
            center = Offset(w * 0.5f, h * 0.4f),
            radius = maxOf(w, h) * 0.7f
        ),
        size = Size(w, h)
    )
}

private fun DrawScope.drawGoldLeafBorder(w: Float, h: Float, goldColor: Color) {
    drawRect(
        color = goldColor.copy(alpha = 0.45f),
        topLeft = Offset(6f, 6f),
        size = Size(w - 12f, h - 12f),
        style = Stroke(width = 2f)
    )
}
