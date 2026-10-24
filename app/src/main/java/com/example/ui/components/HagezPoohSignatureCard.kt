package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.theme.PlayfairDisplayFontFamily

/**
 * Illuminated manuscript colophon and creator signature card displaying
 * "This app made by HagezPooh" on the final page and chapter footers.
 */
@Composable
fun HagezPoohSignatureCard(
    modifier: Modifier = Modifier
) {
    val goldAccent = Color(0xFFFFD54F)
    val deepRoyalBg = Color(0xFF141026)
    val crimsonVelvet = Color(0xFF28153B)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = deepRoyalBg),
        border = BorderStroke(1.5.dp, goldAccent.copy(alpha = 0.78f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("hagezpooh_signature_card")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            deepRoyalBg,
                            crimsonVelvet,
                            deepRoyalBg
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = goldAccent.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, goldAccent.copy(alpha = 0.65f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = goldAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "SACRED EDITION SIGNATURE",
                            style = MaterialTheme.typography.labelSmall,
                            color = goldAccent,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = goldAccent,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "This app made by HagezPooh",
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = PlayfairDisplayFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("hagezpooh_signature_text")
                )

                Text(
                    text = "Crafted & Illuminated by HagezPooh • 260 New Testament 3D Chapters",
                    style = MaterialTheme.typography.labelMedium,
                    fontStyle = FontStyle.Italic,
                    color = goldAccent.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
