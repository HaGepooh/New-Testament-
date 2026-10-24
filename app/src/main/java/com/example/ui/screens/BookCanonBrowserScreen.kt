package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.ChapterArtRegistry
import com.example.data.NewTestamentBooks
import com.example.model.ArtStyleMode
import com.example.model.BookInfo
import com.example.model.TestamentCategory
import com.example.ui.art.SacredArtCanvas

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookCanonBrowserScreen(
    selectedBookId: String,
    selectedCategory: TestamentCategory?,
    searchQuery: String,
    artStyleMode: ArtStyleMode,
    completedChapterKeys: Set<String>,
    onCategorySelected: (TestamentCategory?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSelectBookAndChapter: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedBookId by remember { mutableStateOf<String?>(selectedBookId) }

    val filteredBooks = remember(selectedCategory, searchQuery) {
        NewTestamentBooks.allBooks.filter { book ->
            val matchesCat = selectedCategory == null || book.category == selectedCategory
            val matchesSearch = com.example.ui.components.matchesSanctuarySearch(
                searchQuery,
                book.name,
                book.summary,
                book.author,
                book.greekTitle
            )
            matchesCat && matchesSearch
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 760.dp)
        ) {
            // Pinned Header & Search Bar outside LazyColumn for rock-solid IME focus & zero lag
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "The Sacred Canon",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "29 Sacred Books • 441 Illustrated Chapters (New Testament, Psalms & Proverbs)",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                com.example.ui.components.SanctuarySearchField(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    placeholderText = "Search Psalms, Proverbs, Gospels, Epistles, or authors…",
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("canon_search_input")
                )

                // Category Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { onCategorySelected(null) },
                            label = { Text("All 29 Books") },
                            modifier = Modifier.testTag("category_chip_all")
                        )
                    }
                    items(TestamentCategory.entries) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { onCategorySelected(cat) },
                            label = { Text(cat.title) },
                            modifier = Modifier.testTag("category_chip_${cat.name.lowercase()}")
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("canon_books_list"),
                contentPadding = PaddingValues(bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Book Cards with Expandable Illustrated Chapter Grids
                items(filteredBooks, key = { it.id }) { book ->
                    val isExpanded = expandedBookId == book.id
                    val completedCount = (1..book.chapterCount).count { ch ->
                        completedChapterKeys.contains("${book.id}_$ch")
                    }

                    BookCanonCard(
                        book = book,
                        isExpanded = isExpanded,
                        completedCount = completedCount,
                        artStyleMode = artStyleMode,
                        completedChapterKeys = completedChapterKeys,
                        onToggleExpand = {
                            expandedBookId = if (isExpanded) null else book.id
                        },
                        onSelectChapter = { chNum ->
                            onSelectBookAndChapter(book.id, chNum)
                        }
                    )
                }

                item(key = "canon_hagezpooh_signature") {
                    com.example.ui.components.HagezPoohSignatureCard(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BookCanonCard(
    book: BookInfo,
    isExpanded: Boolean,
    completedCount: Int,
    artStyleMode: ArtStyleMode,
    completedChapterKeys: Set<String>,
    onToggleExpand: () -> Unit,
    onSelectChapter: (Int) -> Unit
) {
    val firstChapterArt = remember(book.id) {
        ChapterArtRegistry.getChapterMetadata(book.id, 1)
    }
    val progressFraction = (completedCount.toFloat() / book.chapterCount.toFloat()).coerceIn(0f, 1f)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = if (isExpanded) 1.5.dp else 1.dp,
            color = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onToggleExpand() }
            .testTag("book_card_${book.id}")
    ) {
        Column {
            // Top Illustrated Banner Preview for the Book
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp)
            ) {
                SacredArtCanvas(
                    metadata = firstChapterArt,
                    artStyleMode = artStyleMode,
                    modifier = Modifier.fillMaxSize(),
                    animateShimmer = false,
                    showOrnamentalFrame = false
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xD90D0A18),
                                    Color(0x700D0A18),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(book.category.accentHex).copy(alpha = 0.28f),
                            border = BorderStroke(1.dp, Color(book.category.accentHex))
                        ) {
                            Text(
                                text = book.category.title,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = book.name,
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${book.greekTitle} • ${book.author} (${book.dateWritten})",
                            style = MaterialTheme.typography.labelMedium,
                            fontStyle = FontStyle.Italic,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, book.basePalette.goldLeaf.copy(alpha = 0.7f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "${book.chapterCount} Ch",
                                style = MaterialTheme.typography.labelLarge,
                                color = book.basePalette.goldLeaf,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Book Summary & Progress Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = book.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (completedCount > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$completedCount of ${book.chapterCount} chapters read",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .width(120.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }
                }

                // Expandable Visual Chapter Art Grid (paginated in 30-chapter chunks for large books like Psalms)
                AnimatedVisibility(visible = isExpanded) {
                    val pageSize = 30
                    val totalPages = (book.chapterCount + pageSize - 1) / pageSize
                    var selectedPage by remember(book.id) { mutableStateOf(0) }
                    val startCh = selectedPage * pageSize + 1
                    val endCh = minOf(book.chapterCount, (selectedPage + 1) * pageSize)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Select an Illustrated Chapter of ${book.name}:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        if (totalPages > 1) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("chapter_range_pills_${book.id}")
                            ) {
                                items(totalPages) { pageIdx ->
                                    val pStart = pageIdx * pageSize + 1
                                    val pEnd = minOf(book.chapterCount, (pageIdx + 1) * pageSize)
                                    FilterChip(
                                        selected = selectedPage == pageIdx,
                                        onClick = { selectedPage = pageIdx },
                                        label = { Text("Ch $pStart–$pEnd") },
                                        modifier = Modifier.testTag("chapter_range_${book.id}_$pageIdx")
                                    )
                                }
                            }
                        }

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            maxItemsInEachRow = 2
                        ) {
                            (startCh..endCh).forEach { chNum ->
                                val chMeta = remember(book.id, chNum) {
                                    ChapterArtRegistry.getChapterMetadata(book.id, chNum)
                                }
                                val isDone = completedChapterKeys.contains("${book.id}_$chNum")

                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isDone) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onSelectChapter(chNum) }
                                        .testTag("canon_chapter_card_${book.id}_$chNum")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        // Mini Chapter Illustration Thumbnail
                                        Box(
                                            modifier = Modifier
                                                .size(52.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                        ) {
                                            SacredArtCanvas(
                                                metadata = chMeta,
                                                artStyleMode = artStyleMode,
                                                modifier = Modifier.fillMaxSize(),
                                                animateShimmer = false,
                                                showOrnamentalFrame = false
                                            )
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier
                                                    .align(Alignment.TopStart)
                                                    .padding(3.dp)
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.Black.copy(alpha = 0.7f))
                                            ) {
                                                Text(
                                                    text = "$chNum",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = chMeta.palette.goldLeaf,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    text = "Chapter $chNum",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                if (isDone) {
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircle,
                                                        contentDescription = "Completed",
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = chMeta.artTitle,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
