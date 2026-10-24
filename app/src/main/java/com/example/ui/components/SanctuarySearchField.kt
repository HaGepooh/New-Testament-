package com.example.ui.components

import android.os.SystemClock
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

/**
 * Result of processing an incoming [TextFieldValue] through [SanctuaryInputDeduplicator].
 */
data class DeduplicationResult(
    val sanitizedValue: TextFieldValue,
    val wasDeduplicated: Boolean
)

/**
 * Creates a [TextFieldValue] for [SanctuarySearchField] and [SanctuaryNoteField].
 *
 * CRITICAL ANDROID IME & COMPOSE `EditProcessor` INVARIANT:
 * - When [syncTick] is `0` (used on all normal, non-deduplicated keystrokes), this returns a
 *   plain [AnnotatedString] with ZERO string annotations and preserves the active IME [composition].
 *   This guarantees that inside Jetpack Compose's `EditProcessor.reset(value, textInputSession)`,
 *   `mBufferState.annotatedString == value.annotatedString` evaluates to `true`, so Compose NEVER
 *   calls `mBuffer.commitComposition()` or `InputMethodManager.restartInput(view)` during normal
 *   typing on Gboard, Samsung Keyboard, SwiftKey, or hardware keyboards.
 * - When [syncTick] is `> 0` (used ONLY when a duplicate keystroke was rejected and `EditProcessor`
 *   must be forced to overwrite its internal buffer), a zero-length `"sanctuary_sync"` annotation
 *   is attached so `newValue.equals(oldValue)` is `false` and `CoreTextField` resets `EditProcessor`.
 */
fun createSyncedTextFieldValue(
    text: String,
    selection: TextRange,
    syncTick: Int,
    composition: TextRange? = null
): TextFieldValue {
    val safeSelection = TextRange(
        start = selection.start.coerceIn(0, text.length),
        end = selection.end.coerceIn(0, text.length)
    )
    val safeComposition = composition?.let { comp ->
        val start = comp.start.coerceIn(0, text.length)
        val end = comp.end.coerceIn(0, text.length)
        if (start < end) TextRange(start, end) else null
    }
    val annotated = if (syncTick > 0) {
        buildAnnotatedString {
            append(text)
            addStringAnnotation(
                tag = "sanctuary_sync",
                annotation = syncTick.toString(),
                start = 0,
                end = 0
            )
        }
    } else {
        AnnotatedString(text)
    }
    return TextFieldValue(
        annotatedString = annotated,
        selection = safeSelection,
        composition = safeComposition
    )
}

/**
 * Deterministic keystroke and IME deduplicator that prevents a single key press or soft-keyboard
 * tap from producing two identical letters on any Android device or streaming emulator bridge,
 * while never interrupting normal multi-letter typing or IME composing spans.
 */
class SanctuaryInputDeduplicator(
    private val duplicateWindowMs: Long = 160L,
    private val firstCharDuplicateWindowMs: Long = 450L
) {
    var lastInsertedChar: Char? = null
        private set
    var lastInsertTimestampMs: Long = -10_000L
        private set

    private var lastKeyDownChar: Char? = null
    private var lastKeyDownTimestampMs: Long = -10_000L
    private var syncCounter: Int = 0

    fun nextSyncTick(): Int {
        syncCounter++
        return syncCounter
    }

    fun currentSyncTick(): Int = syncCounter

    fun reset() {
        lastInsertedChar = null
        lastInsertTimestampMs = -10_000L
        lastKeyDownChar = null
        lastKeyDownTimestampMs = -10_000L
    }

    /**
     * Intercepts hardware/bridge [KeyEventType.KeyDown] events before they reach `BasicTextField`.
     * Consumes only rapid hardware/emulator key-bounce duplicates within [duplicateWindowMs]
     * so legitimate double letters (e.g. "Matthew", "Philippians", "good", "all") are never blocked.
     */
    fun shouldConsumeKeyDown(
        utf16CodePoint: Int,
        isDeleteKey: Boolean,
        nowMs: Long
    ): Boolean {
        if (isDeleteKey) {
            reset()
            return false
        }
        if (utf16CodePoint <= 0 || Character.isISOControl(utf16CodePoint)) {
            return false
        }
        val keyChar = utf16CodePoint.toChar()
        val windowMs = if (keyChar.isDigit()) 60L else duplicateWindowMs

        // Block if a previous KeyDown for the same character fired within the bounce window
        val prevKey = lastKeyDownChar
        if (prevKey != null &&
            prevKey.equals(keyChar, ignoreCase = true) &&
            (nowMs - lastKeyDownTimestampMs) in 0L..windowMs
        ) {
            return true
        }

        // Block if InputConnection.commitText already inserted this character within the bounce window
        val prevInserted = lastInsertedChar
        if (prevInserted != null &&
            prevInserted.equals(keyChar, ignoreCase = true) &&
            (nowMs - lastInsertTimestampMs) in 0L..windowMs
        ) {
            return true
        }

        lastKeyDownChar = keyChar
        lastKeyDownTimestampMs = nowMs
        return false
    }

    /**
     * Sanitizes an incoming [TextFieldValue] from `OutlinedTextField.onValueChange`.
     *
     * - On normal edits (`wasDeduplicated == false`), returns a plain [TextFieldValue] with
     *   `syncTick = 0` (no annotations) and preserves `incomingValue.composition` so the Android
     *   IME session is never restarted or interrupted.
     * - On rejected duplicates (`wasDeduplicated == true`), increments `syncCounter` and strips
     *   `composition` so `CoreTextField` immediately resets its internal `EditingBuffer`.
     */
    fun processValueChange(
        currentValue: TextFieldValue,
        incomingValue: TextFieldValue,
        nowMs: Long
    ): DeduplicationResult {
        val oldText = currentValue.text
        val rawNewText = incomingValue.text

        // Identical text (only selection/composition changed)
        if (rawNewText == oldText) {
            return DeduplicationResult(
                sanitizedValue = createSyncedTextFieldValue(
                    text = oldText,
                    selection = incomingValue.selection,
                    syncTick = 0,
                    composition = incomingValue.composition
                ),
                wasDeduplicated = false
            )
        }

        // Deletion or clear: reset state and keep plain AnnotatedString so Backspace repeats smoothly
        if (rawNewText.length < oldText.length) {
            reset()
            return DeduplicationResult(
                sanitizedValue = createSyncedTextFieldValue(
                    text = rawNewText,
                    selection = incomingValue.selection,
                    syncTick = 0,
                    composition = incomingValue.composition
                ),
                wasDeduplicated = false
            )
        }

        // Pathway A: Full composing buffer echo (e.g., "ma" -> "mama" or "ma" -> "mamat")
        if (oldText.length >= 2 &&
            rawNewText.startsWith(oldText + oldText, ignoreCase = true) &&
            rawNewText.length <= oldText.length * 2 + 2 &&
            (nowMs - lastInsertTimestampMs) in 0L..firstCharDuplicateWindowMs
        ) {
            val remainder = rawNewText.substring(oldText.length * 2)
            val dedupedText = oldText + remainder
            lastInsertedChar = dedupedText.lastOrNull()
            lastInsertTimestampMs = nowMs
            val tick = nextSyncTick()
            return DeduplicationResult(
                sanitizedValue = createSyncedTextFieldValue(
                    text = dedupedText,
                    selection = TextRange(dedupedText.length),
                    syncTick = tick,
                    composition = null
                ),
                wasDeduplicated = true
            )
        }

        val insertion = extractInsertionSlice(oldText, rawNewText)

        if (insertion != null) {
            val insertedText = insertion.inserted

            // Pathway B: Single-callback batched double character insertion (e.g. "" -> "jj", "m" -> "maa", "" -> "Mm")
            if (insertedText.length == 2 &&
                insertedText[0].equals(insertedText[1], ignoreCase = true) &&
                !insertedText[0].isDigit()
            ) {
                val keptChar = insertedText[0]
                val dedupedText = oldText.substring(0, insertion.prefixLength) +
                    keptChar +
                    oldText.substring(insertion.prefixLength)
                val cursor = (insertion.prefixLength + 1).coerceIn(0, dedupedText.length)

                lastInsertedChar = keptChar
                lastInsertTimestampMs = nowMs
                val tick = nextSyncTick()

                return DeduplicationResult(
                    sanitizedValue = createSyncedTextFieldValue(
                        text = dedupedText,
                        selection = TextRange(cursor),
                        syncTick = tick,
                        composition = null
                    ),
                    wasDeduplicated = true
                )
            }

            // Pathway C: Soft-keyboard composing re-commit of first char + new char (e.g. "m" -> "mma")
            val pathwayCWindow = if (oldText.length == 1) firstCharDuplicateWindowMs else duplicateWindowMs
            if (insertedText.length == 2 &&
                oldText.length <= 2 &&
                insertion.prefixLength > 0 &&
                insertedText[0].equals(oldText[insertion.prefixLength - 1], ignoreCase = true) &&
                !insertedText[0].equals(insertedText[1], ignoreCase = true) &&
                (nowMs - lastInsertTimestampMs) in 0L..pathwayCWindow
            ) {
                val newChar = insertedText[1]
                val dedupedText = oldText.substring(0, insertion.prefixLength) +
                    newChar +
                    oldText.substring(insertion.prefixLength)
                val cursor = (insertion.prefixLength + 1).coerceIn(0, dedupedText.length)

                lastInsertedChar = newChar
                lastInsertTimestampMs = nowMs
                val tick = nextSyncTick()

                return DeduplicationResult(
                    sanitizedValue = createSyncedTextFieldValue(
                        text = dedupedText,
                        selection = TextRange(cursor),
                        syncTick = tick,
                        composition = null
                    ),
                    wasDeduplicated = true
                )
            }

            // Pathway D: Single-character insertion that duplicates the immediately preceding insertion
            if (insertedText.length == 1) {
                val insertedChar = insertedText[0]
                val timeDelta = nowMs - lastInsertTimestampMs
                val prevChar = lastInsertedChar

                // Determine effective bounce window:
                // - Digits ('0'..'9'): 60ms so typing "Psalm 119" or "Psalm 22" is never blocked.
                // - First character ("p" -> "pp", "M" -> "Mm"): 450ms because no Bible query starts with
                //   a doubled consonant/letter (except "Aaron", handled below) and the first keystroke
                //   frame mounts the Clear button & initial filter.
                // - Active multi-char soft-keyboard composing ("Mat" -> "Matt"): 70ms so fast two-thumb
                //   typing of double letters on Gboard/Samsung Keyboard is never blocked.
                // - Non-composing / hardware / bridge mid-word ("jo" -> "joo"): 160ms.
                val effectiveWindowMs = when {
                    insertedChar.isDigit() -> 60L
                    oldText.length == 1 && !insertedChar.equals('a', ignoreCase = true) -> firstCharDuplicateWindowMs
                    oldText.length >= 2 &&
                        currentValue.composition != null &&
                        incomingValue.composition != null -> 70L
                    else -> duplicateWindowMs
                }

                val matchesRecentInsert = prevChar != null &&
                    insertedChar.equals(prevChar, ignoreCase = true) &&
                    timeDelta in 0L..effectiveWindowMs

                val adjacentToSameChar =
                    (insertion.prefixLength > 0 &&
                        oldText[insertion.prefixLength - 1].equals(insertedChar, ignoreCase = true)) ||
                        (insertion.prefixLength < oldText.length &&
                            oldText[insertion.prefixLength].equals(insertedChar, ignoreCase = true))

                if (matchesRecentInsert && adjacentToSameChar) {
                    val tick = nextSyncTick()
                    return DeduplicationResult(
                        sanitizedValue = createSyncedTextFieldValue(
                            text = oldText,
                            selection = TextRange(oldText.length),
                            syncTick = tick,
                            composition = null
                        ),
                        wasDeduplicated = true
                    )
                }

                lastInsertedChar = insertedChar
                lastInsertTimestampMs = nowMs
                return DeduplicationResult(
                    sanitizedValue = createSyncedTextFieldValue(
                        text = rawNewText,
                        selection = incomingValue.selection,
                        syncTick = 0,
                        composition = incomingValue.composition
                    ),
                    wasDeduplicated = false
                )
            }
        }

        // Bulk paste / swipe / suggestion chip / multi-character input (e.g. performTextInput("Matthew"))
        lastInsertedChar = rawNewText.lastOrNull()
        lastInsertTimestampMs = nowMs
        return DeduplicationResult(
            sanitizedValue = createSyncedTextFieldValue(
                text = rawNewText,
                selection = incomingValue.selection,
                syncTick = 0,
                composition = incomingValue.composition
            ),
            wasDeduplicated = false
        )
    }

    private data class InsertionSlice(
        val prefixLength: Int,
        val suffixLength: Int,
        val inserted: String
    )

    private fun extractInsertionSlice(oldText: String, newText: String): InsertionSlice? {
        if (newText.length <= oldText.length) return null
        var prefixLen = 0
        while (prefixLen < oldText.length &&
            oldText[prefixLen].equals(newText[prefixLen], ignoreCase = true)
        ) {
            prefixLen++
        }

        var suffixLen = 0
        val maxSuffix = oldText.length - prefixLen
        while (suffixLen < maxSuffix &&
            oldText[oldText.length - 1 - suffixLen].equals(
                newText[newText.length - 1 - suffixLen],
                ignoreCase = true
            )
        ) {
            suffixLen++
        }

        if (prefixLen + suffixLen != oldText.length) {
            return null
        }

        val inserted = newText.substring(prefixLen, newText.length - suffixLen)
        return InsertionSlice(
            prefixLength = prefixLen,
            suffixLength = suffixLen,
            inserted = inserted
        )
    }
}

/**
 * Normalizes a string by lowercasing and collapsing consecutive identical letters
 * (e.g. `"Matthew"` -> `"mathew"`, `"Philippians"` -> `"philipians"`).
 */
fun collapseConsecutiveRepeats(input: String): String {
    if (input.isEmpty()) return ""
    val sb = StringBuilder(input.length)
    var prev = input[0].lowercaseChar()
    sb.append(prev)
    for (i in 1 until input.length) {
        val curr = input[i].lowercaseChar()
        if (curr != prev) {
            sb.append(curr)
            prev = curr
        }
    }
    return sb.toString()
}

/**
 * Matches a search [query] against any of [fields], supporting:
 * 1. Exact case-insensitive substring matching.
 * 2. Double-letter-collapsed matching so `"Mathew"` and `"Matthew"` both match `"Matthew"`.
 * 3. Multi-word tokenized matching so queries like `"Psalm 23"`, `"Proverbs 3"`, `"Matthew 5"`,
 *    or `"King David"` match across the combined fields smoothly on all Android devices.
 */
fun matchesSanctuarySearch(query: String, vararg fields: String): Boolean {
    val trimmed = query.trim()
    if (trimmed.isEmpty()) return true
    val collapsedQuery = collapseConsecutiveRepeats(trimmed)
    val directMatch = fields.any { field ->
        field.contains(trimmed, ignoreCase = true) ||
            (collapsedQuery.isNotEmpty() && collapseConsecutiveRepeats(field).contains(collapsedQuery))
    }
    if (directMatch) return true

    val tokens = trimmed.split(Regex("[\\s,:;.-]+")).filter { it.isNotBlank() }
    if (tokens.size <= 1) return false

    val nonNumericTokens = tokens.filter { token -> token.any { !it.isDigit() } }
    if (nonNumericTokens.isEmpty()) return false

    val combinedFields = fields.joinToString(" ")
    val collapsedCombined = collapseConsecutiveRepeats(combinedFields)

    val allTokensMatchCombined = tokens.all { token ->
        val collapsedToken = collapseConsecutiveRepeats(token)
        combinedFields.contains(token, ignoreCase = true) ||
            (collapsedToken.isNotEmpty() && collapsedCombined.contains(collapsedToken))
    }
    if (allTokensMatchCombined) return true

    // Also allow book + chapter queries like "Psalm 23" or "Matthew 5" to match a book item
    // whose fields contain the book name ("Psalms", "Matthew") even if chapter numbers aren't in book fields
    return nonNumericTokens.all { token ->
        val collapsedToken = collapseConsecutiveRepeats(token)
        combinedFields.contains(token, ignoreCase = true) ||
            (collapsedToken.isNotEmpty() && collapsedCombined.contains(collapsedToken))
    }
}

private class SyncedFieldStateHolder(initialValue: TextFieldValue) {
    var currentValue: TextFieldValue = initialValue
    val pendingDispatchedTexts: MutableList<String> = mutableListOf()
}

/**
 * Smooth, zero-lag search text field that eliminates double-letter typing and prevents dropped
 * characters or broken IME composing sessions across all Android devices, soft keyboards,
 * hardware keyboards, and browser-streamed emulators.
 */
@Composable
fun SanctuarySearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholderText: String,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val deduplicator = remember { SanctuaryInputDeduplicator() }

    val initialFieldValue = remember {
        createSyncedTextFieldValue(
            text = query,
            selection = TextRange(query.length),
            syncTick = 0,
            composition = null
        )
    }
    var textFieldValue by remember { mutableStateOf(initialFieldValue) }
    val stateHolder = remember { SyncedFieldStateHolder(initialFieldValue) }

    // Only sync external `query` changes (e.g., external reset or filter action), NEVER overwrite
    // in-flight user typing when `StateFlow` is simply catching up to `onQueryChange`.
    LaunchedEffect(query) {
        val currentText = stateHolder.currentValue.text
        if (query == currentText) {
            stateHolder.pendingDispatchedTexts.clear()
        } else {
            val pendingIdx = stateHolder.pendingDispatchedTexts.indexOf(query)
            if (pendingIdx >= 0) {
                // `query` is an asynchronous StateFlow echo of text we already dispatched; remove
                // it and any older echoes without touching the active TextFieldValue or IME session!
                stateHolder.pendingDispatchedTexts.subList(0, pendingIdx + 1).clear()
            } else {
                // Genuine external mutation (e.g., external clear)
                if (query.isEmpty()) {
                    deduplicator.reset()
                }
                val synced = createSyncedTextFieldValue(
                    text = query,
                    selection = TextRange(query.length),
                    syncTick = 0,
                    composition = null
                )
                stateHolder.currentValue = synced
                stateHolder.pendingDispatchedTexts.clear()
                textFieldValue = synced
            }
        }
    }

    OutlinedTextField(
        value = textFieldValue,
        onValueChange = { incomingValue: TextFieldValue ->
            val nowMs = SystemClock.uptimeMillis()
            val previousValue = stateHolder.currentValue
            val result = deduplicator.processValueChange(
                currentValue = previousValue,
                incomingValue = incomingValue,
                nowMs = nowMs
            )
            stateHolder.currentValue = result.sanitizedValue
            textFieldValue = result.sanitizedValue

            val newText = result.sanitizedValue.text
            if (newText != previousValue.text) {
                stateHolder.pendingDispatchedTexts.add(newText)
                onQueryChange(newText)
            }
        },
        placeholder = { Text(placeholderText) },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            if (textFieldValue.text.isNotEmpty()) {
                IconButton(
                    onClick = {
                        deduplicator.reset()
                        val cleared = createSyncedTextFieldValue(
                            text = "",
                            selection = TextRange.Zero,
                            syncTick = 0,
                            composition = null
                        )
                        stateHolder.currentValue = cleared
                        stateHolder.pendingDispatchedTexts.clear()
                        stateHolder.pendingDispatchedTexts.add("")
                        textFieldValue = cleared
                        onQueryChange("")
                    }
                ) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear search")
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { focusManager.clearFocus() }
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.onPreviewKeyEvent { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyDown) {
                val isDelete = keyEvent.key == Key.Backspace || keyEvent.key == Key.Delete
                deduplicator.shouldConsumeKeyDown(
                    utf16CodePoint = keyEvent.utf16CodePoint,
                    isDeleteKey = isDelete,
                    nowMs = SystemClock.uptimeMillis()
                )
            } else {
                false
            }
        }
    )
}

/**
 * Multi-line note input field using [SanctuaryInputDeduplicator] so typing study reflections
 * also works smoothly without duplicate letters or dropped text on any Android device.
 */
@Composable
fun SanctuaryNoteField(
    value: String,
    onValueChange: (String) -> Unit,
    labelText: String,
    placeholderText: String,
    modifier: Modifier = Modifier,
    minLines: Int = 2,
    maxLines: Int = 4
) {
    val deduplicator = remember { SanctuaryInputDeduplicator() }

    val initialFieldValue = remember {
        createSyncedTextFieldValue(
            text = value,
            selection = TextRange(value.length),
            syncTick = 0,
            composition = null
        )
    }
    var textFieldValue by remember { mutableStateOf(initialFieldValue) }
    val stateHolder = remember { SyncedFieldStateHolder(initialFieldValue) }

    LaunchedEffect(value) {
        val currentText = stateHolder.currentValue.text
        if (value == currentText) {
            stateHolder.pendingDispatchedTexts.clear()
        } else {
            val pendingIdx = stateHolder.pendingDispatchedTexts.indexOf(value)
            if (pendingIdx >= 0) {
                stateHolder.pendingDispatchedTexts.subList(0, pendingIdx + 1).clear()
            } else {
                if (value.isEmpty()) {
                    deduplicator.reset()
                }
                val synced = createSyncedTextFieldValue(
                    text = value,
                    selection = TextRange(value.length),
                    syncTick = 0,
                    composition = null
                )
                stateHolder.currentValue = synced
                stateHolder.pendingDispatchedTexts.clear()
                textFieldValue = synced
            }
        }
    }

    OutlinedTextField(
        value = textFieldValue,
        onValueChange = { incomingValue: TextFieldValue ->
            val nowMs = SystemClock.uptimeMillis()
            val previousValue = stateHolder.currentValue
            val result = deduplicator.processValueChange(
                currentValue = previousValue,
                incomingValue = incomingValue,
                nowMs = nowMs
            )
            stateHolder.currentValue = result.sanitizedValue
            textFieldValue = result.sanitizedValue

            val newText = result.sanitizedValue.text
            if (newText != previousValue.text) {
                stateHolder.pendingDispatchedTexts.add(newText)
                onValueChange(newText)
            }
        },
        label = { Text(labelText) },
        placeholder = { Text(placeholderText) },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Text
        ),
        minLines = minLines,
        maxLines = maxLines,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.onPreviewKeyEvent { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyDown) {
                val isDelete = keyEvent.key == Key.Backspace || keyEvent.key == Key.Delete
                deduplicator.shouldConsumeKeyDown(
                    utf16CodePoint = keyEvent.utf16CodePoint,
                    isDeleteKey = isDelete,
                    nowMs = SystemClock.uptimeMillis()
                )
            } else {
                false
            }
        }
    )
}
