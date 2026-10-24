package com.example.ui.art

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.BiblicalCharacter
import com.example.data.NewTestamentBooks

@Composable
fun CharacterDetailModal(
    character: BiblicalCharacter,
    onDismiss: () -> Unit,
    onOpenKeyChapter: (String, Int) -> Unit
) {
    BackHandler(onBack = onDismiss)

    var scale by remember(character.id) { mutableFloatStateOf(1f) }
    var offset by remember(character.id) { mutableStateOf(Offset.Zero) }
    val keyBook = remember(character.keyBookId) {
        NewTestamentBooks.getBookById(character.keyBookId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xF50B0914))
            .testTag("character_detail_modal"),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp)
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = character.haloAccentColor.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, character.haloAccentColor.copy(alpha = 0.7f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = character.haloAccentColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = character.groupLabel,
                            style = MaterialTheme.typography.labelLarge,
                            color = character.haloAccentColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .testTag("character_modal_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close character portrait",
                        tint = Color.White
                    )
                }
            }

            // Large Zoomable Sacred Character Portrait Frame
            Card(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(2.dp, character.haloAccentColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(character.id) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                val newScale = (scale * zoom).coerceIn(1f, 2.8f)
                                scale = newScale
                                offset = if (newScale > 1f) {
                                    Offset(
                                        x = (offset.x + pan.x).coerceIn(-350f, 350f),
                                        y = (offset.y + pan.y).coerceIn(-350f, 350f)
                                    )
                                } else {
                                    Offset.Zero
                                }
                            }
                        }
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                ) {
                    CharacterPortraitCanvas(
                        character = character,
                        modifier = Modifier.fillMaxSize(),
                        animateHalo = true,
                        showArchFrame = true
                    )
                }
            }

            // Character Name, Ancient Name & Title
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = character.ancientName,
                    style = MaterialTheme.typography.labelLarge,
                    color = character.haloAccentColor
                )
                Text(
                    text = character.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            // Sacred Iconographic Attributes Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A162B)),
                border = BorderStroke(1.dp, character.haloAccentColor.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Sacred Attributes & Portrait Symbolism",
                        style = MaterialTheme.typography.labelLarge,
                        color = character.haloAccentColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Emblems: ${character.sacredAttribute}",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = character.portraitSymbolism,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            // Biblical Biography Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF171424)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "New Testament Life & Witness",
                        style = MaterialTheme.typography.labelLarge,
                        color = character.haloAccentColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = character.biography,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.88f)
                    )
                }
            }

            // Key Verse Quote & Action Button
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, character.haloAccentColor.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "“${character.keyVerseQuote}”",
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFFFFEAB5)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${keyBook.name} Chapter ${character.keyChapterNumber}",
                            style = MaterialTheme.typography.labelLarge,
                            color = character.haloAccentColor
                        )

                        Button(
                            onClick = {
                                onOpenKeyChapter(character.keyBookId, character.keyChapterNumber)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = character.haloAccentColor,
                                contentColor = Color(0xFF1E1405)
                            ),
                            modifier = Modifier.testTag("character_open_chapter_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Read ${keyBook.name} ${character.keyChapterNumber}",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
