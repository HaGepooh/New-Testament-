package com.example.ui.art

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.ArtStyleMode
import com.example.model.ChapterArtMetadata

@Composable
fun FullScreenArtLightbox(
    metadata: ChapterArtMetadata,
    currentStyleMode: ArtStyleMode,
    onStyleModeChange: (ArtStyleMode) -> Unit,
    onDismiss: () -> Unit,
    onOpenChapterInReader: () -> Unit
) {
    BackHandler(onBack = onDismiss)

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var showOverlayChrome by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF09070F))
            .testTag("fullscreen_art_lightbox")
    ) {
        // Interactive Zoomable & Pannable Sacred Art Canvas
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        val newScale = (scale * zoom).coerceIn(1f, 3.5f)
                        scale = newScale
                        offset = if (newScale > 1f) {
                            Offset(
                                x = (offset.x + pan.x).coerceIn(-600f, 600f),
                                y = (offset.y + pan.y).coerceIn(-600f, 600f)
                            )
                        } else {
                            Offset.Zero
                        }
                    }
                }
                .clickable { showOverlayChrome = !showOverlayChrome }
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
        ) {
            SacredArtCanvas(
                metadata = metadata,
                artStyleMode = currentStyleMode,
                modifier = Modifier.fillMaxSize(),
                animateShimmer = true,
                showOrnamentalFrame = true
            )
        }

        // Top Bar Controls
        AnimatedVisibility(
            visible = showOverlayChrome,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.78f), Color.Transparent)
                        )
                    )
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.Black.copy(alpha = 0.55f),
                    modifier = Modifier.border(
                        1.dp,
                        metadata.palette.goldLeaf.copy(alpha = 0.6f),
                        RoundedCornerShape(50)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = metadata.palette.goldLeaf,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${metadata.bookName} ${metadata.chapterNumber} • ${metadata.keyThemeTag}",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (scale > 1.05f) {
                        IconButton(
                            onClick = {
                                scale = 1f
                                offset = Offset.Zero
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .testTag("lightbox_reset_zoom_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CenterFocusStrong,
                                contentDescription = "Reset zoom",
                                tint = Color.White
                            )
                        }
                    }

                    IconButton(
                        onClick = { showOverlayChrome = !showOverlayChrome },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .testTag("lightbox_toggle_chrome_button")
                    ) {
                        Icon(
                            imageVector = if (showOverlayChrome) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle artwork details",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .testTag("lightbox_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close full-screen artwork",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // Floating Button to Restore Chrome when Hidden
        if (!showOverlayChrome) {
            IconButton(
                onClick = { showOverlayChrome = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(16.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.55f))
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "Show artwork details",
                    tint = Color.White
                )
            }
        }

        // Bottom Visual Meditation & Art Style Switcher Card
        AnimatedVisibility(
            visible = showOverlayChrome,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color(0xD90A0812),
                                Color(0xF50A0812)
                            )
                        )
                    )
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Art Style Mode Selector Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ArtStyleMode.entries.forEach { mode ->
                        val selected = mode == currentStyleMode
                        FilterChip(
                            selected = selected,
                            onClick = { onStyleModeChange(mode) },
                            label = {
                                Text(
                                    text = mode.label,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = metadata.palette.goldLeaf.copy(alpha = 0.25f),
                                selectedLabelColor = metadata.palette.goldLeaf,
                                containerColor = Color.White.copy(alpha = 0.08f),
                                labelColor = Color.White.copy(alpha = 0.85f)
                            ),
                            modifier = Modifier.testTag("art_style_chip_${mode.name.lowercase()}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = metadata.artTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = metadata.sceneArchetype.displayName,
                    style = MaterialTheme.typography.labelLarge,
                    color = metadata.palette.goldLeaf
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = metadata.visualMeditation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.88f)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "“${metadata.keyVersePreview}”",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFFFFEAB5),
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pinch to zoom • Tap art for clean view",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )

                    Button(
                        onClick = onOpenChapterInReader,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = metadata.palette.goldLeaf,
                            contentColor = Color(0xFF1E1405)
                        ),
                        modifier = Modifier.testTag("lightbox_read_chapter_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Read ${metadata.bookName} ${metadata.chapterNumber}",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
