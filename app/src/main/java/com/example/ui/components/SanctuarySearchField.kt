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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
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
 * Creates a [TextFieldValue] carrying a zero-length `"sanctuary_sync"` annotation keyed by [syncTick].
 *
 * Why this is critical for Jetpack Compose `BasicTextField` / `CoreTextField`:
 * When `BasicTextField` receives a duplicate keystroke (e.g. `"a" -> "aa"`), its internal
 * `EditProcessor.mBuffer` is mutated to `"aa"` BEFORE `onValueChange` is called. If `onValueChange`
 * rejects `"aa"` and returns a plain `TextFieldValue("a")` that is `.equals()` to the previous
 * `TextFieldValue("a")`, Compose's slot table skips recomposing `CoreTextField`, so
 * `EditProcessor.reset()` never runs and `"aa"` remains stuck on screen.
 *
 * Embedding [syncTick] in `AnnotatedString.annotations` makes `newValue.equals(oldValue)` evaluate
 * to `false` whenever a duplicate is rejected, forcing `OutlinedTextField` -> `BasicTextField` ->
 * `CoreTextField` to recompose and execute `EditProcessor.reset(value, inputSession)` to overwrite
 * `mBuffer` back to `"a"`.
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
    val annotated = buildAnnotatedString {
        append(text)
        addStringAnnotation(
            tag = "sanctuary_sync",
            annotation = syncTick.toString(),
            start = 0,
            end = 0
        )
    }
    return TextFieldValue(
        annotatedString = annotated,
        selection = safeSelection,
        composition = composition
    )
}

/**
 * Deterministic keystroke and IME deduplicator that prevents a single key press or soft-keyboard
 * tap from producing two identical letters on any Android device or streaming emulator bridge.
 *
 * Handles all five duplication pathways:
 * 1. Dual hardware `KeyEvent` + `InputConnection.commitText` events emitted for a single keystroke.
 * 2. Single-callback batched double insertions (e.g. `"" -> "aa"` or `"m" -> "maa"`).
 * 3. Two-callback sequential duplicate insertions (`"" -> "a"` followed by `"a" -> "aa"`).
 * 4. Soft-keyboard composing re-commit + new char (`"m" -> "mma"` collapsed to `"ma"`).
 * 5. Full composing buffer echo (`"ma" -> "mama"` or `"ma" -> "mamat"`).
 */
class SanctuaryInputDeduplicator(
    private val duplicateWindowMs: Long = 1200L
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
     * Returns `true` if the event is a duplicate keystroke that should be consumed immediately.
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

        // Block if a previous KeyDown for the same character fired within the duplicate window
        val prevKey = lastKeyDownChar
        if (prevKey != null &&
            prevKey.equals(keyChar, ignoreCase = true) &&
            (nowMs - lastKeyDownTimestampMs) in 0L..duplicateWindowMs
        ) {
            return true
        }

        // Block if InputConnection.commitText already inserted this character within the window
        val prevInserted = lastInsertedChar
        if (prevInserted != null &&
            prevInserted.equals(keyChar, ignoreCase = true) &&
            (nowMs - lastInsertTimestampMs) in 0L..duplicateWindowMs
        ) {
            return true
        }

        lastKeyDownChar = keyChar
        lastKeyDownTimestampMs = nowMs
        return false
    }

    /**
     * Sanitizes an incoming [TextFieldValue] from `BasicTextField.onValueChange`.
     * Preserves IME `composition` on normal edits so soft keyboards do not restart input mid-word,
     * and strips `composition` + increments `syncCounter` when a duplicate is rejected so
     * `CoreTextField` immediately resets its internal `EditingBuffer`.
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
                    syncTick = syncCounter,
                    composition = incomingValue.composition
                ),
                wasDeduplicated = false
            )
        }

        // Deletion or clear: reset state so user can immediately re-type any character
        if (rawNewText.length < oldText.length) {
            reset()
            return DeduplicationResult(
                sanitizedValue = createSyncedTextFieldValue(
                    text = rawNewText,
                    selection = incomingValue.selection,
                    syncTick = syncCounter,
                    composition = incomingValue.composition
                ),
                wasDeduplicated = false
            )
        }

        // Pathway A: Full composing buffer echo (e.g., "ma" -> "mama" or "ma" -> "mamat")
        if (oldText.isNotEmpty() &&
            rawNewText.startsWith(oldText + oldText, ignoreCase = true) &&
            rawNewText.length <= oldText.length * 2 + 2
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

            // Pathway B: Single-callback batched double character insertion (e.g. "" -> "aa", "m" -> "maa", "" -> "Mm")
            if (insertedText.length == 2 &&
                insertedText[0].equals(insertedText[1], ignoreCase = true)
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

            // Pathway C: Soft-keyboard composing re-commit of previous char + new char (e.g. "m" -> "mma")
            if (insertedText.length == 2 &&
                insertion.prefixLength > 0 &&
                insertedText[0].equals(oldText[insertion.prefixLength - 1], ignoreCase = true) &&
                (nowMs - lastInsertTimestampMs) in 0L..duplicateWindowMs
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

                val matchesRecentInsert = prevChar != null &&
                    insertedChar.equals(prevChar, ignoreCase = true) &&
                    timeDelta in 0L..duplicateWindowMs

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
                        syncTick = syncCounter,
                        composition = incomingValue.composition
                    ),
                    wasDeduplicated = false
                )
            }
        }

        // Bulk paste / multi-character test input (e.g. performTextInput("Matthew"))
        lastInsertedChar = rawNewText.lastOrNull()
        lastInsertTimestampMs = nowMs
        return DeduplicationResult(
            sanitizedValue = createSyncedTextFieldValue(
                text = rawNewText,
                selection = incomingValue.selection,
                syncTick = syncCounter,
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
 * Matches a search [query] against any of [fields], supporting both exact case-insensitive
 * substring matching and double-letter-collapsed matching so queries like `"Mathew"` or `"Matthew"`
 * both match `"Matthew"` smoothly on all Android devices.
 */
fun matchesSanctuarySearch(query: String, vararg fields: String): Boolean {
    val trimmed = query.trim()
    if (trimmed.isEmpty()) return true
    val collapsedQuery = collapseConsecutiveRepeats(trimmed)
    return fields.any { field ->
        field.contains(trimmed, ignoreCase = true) ||
            (collapsedQuery.isNotEmpty() && collapseConsecutiveRepeats(field).contains(collapsedQuery))
    }
}

/**
 * Smooth, zero-lag search text field that eliminates double-letter typing across all Android
 * devices, soft keyboards, hardware keyboards, and browser-streamed emulators.
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

    var textFieldValue by remember {
        mutableStateOf(
            value = createSyncedTextFieldValue(
                text = query,
                selection = TextRange(query.length),
                syncTick = 0,
                composition = null
            ),
            policy = neverEqualPolicy()
        )
    }

    var forceSyncTick by remember { mutableIntStateOf(0) }

    LaunchedEffect(query) {
        if (query != textFieldValue.text) {
            if (query.isEmpty()) {
                deduplicator.reset()
            }
            val tick = deduplicator.nextSyncTick()
            textFieldValue = createSyncedTextFieldValue(
                text = query,
                selection = TextRange(query.length),
                syncTick = tick,
                composition = null
            )
            forceSyncTick = tick
        }
    }

    val handleValueChange: (TextFieldValue) -> Unit = remember(forceSyncTick) {
        { incomingValue: TextFieldValue ->
            val nowMs = SystemClock.uptimeMillis()
            val result = deduplicator.processValueChange(
                currentValue = textFieldValue,
                incomingValue = incomingValue,
                nowMs = nowMs
            )
            val previousText = textFieldValue.text
            textFieldValue = result.sanitizedValue
            if (result.wasDeduplicated) {
                forceSyncTick = deduplicator.currentSyncTick()
            }
            if (result.sanitizedValue.text != previousText || result.sanitizedValue.text != query) {
                onQueryChange(result.sanitizedValue.text)
            }
        }
    }

    OutlinedTextField(
        value = textFieldValue,
        onValueChange = handleValueChange,
        placeholder = { Text(placeholderText) },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            if (textFieldValue.text.isNotEmpty()) {
                IconButton(
                    onClick = {
                        deduplicator.reset()
                        val tick = deduplicator.nextSyncTick()
                        textFieldValue = createSyncedTextFieldValue(
                            text = "",
                            selection = TextRange.Zero,
                            syncTick = tick,
                            composition = null
                        )
                        forceSyncTick = tick
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
 * also works smoothly without duplicate letters on any Android device.
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

    var textFieldValue by remember {
        mutableStateOf(
            value = createSyncedTextFieldValue(
                text = value,
                selection = TextRange(value.length),
                syncTick = 0,
                composition = null
            ),
            policy = neverEqualPolicy()
        )
    }

    var forceSyncTick by remember { mutableIntStateOf(0) }

    LaunchedEffect(value) {
        if (value != textFieldValue.text) {
            if (value.isEmpty()) {
                deduplicator.reset()
            }
            val tick = deduplicator.nextSyncTick()
            textFieldValue = createSyncedTextFieldValue(
                text = value,
                selection = TextRange(value.length),
                syncTick = tick,
                composition = null
            )
            forceSyncTick = tick
        }
    }

    val handleValueChange: (TextFieldValue) -> Unit = remember(forceSyncTick) {
        { incomingValue: TextFieldValue ->
            val nowMs = SystemClock.uptimeMillis()
            val result = deduplicator.processValueChange(
                currentValue = textFieldValue,
                incomingValue = incomingValue,
                nowMs = nowMs
            )
            val previousText = textFieldValue.text
            textFieldValue = result.sanitizedValue
            if (result.wasDeduplicated) {
                forceSyncTick = deduplicator.currentSyncTick()
            }
            if (result.sanitizedValue.text != previousText || result.sanitizedValue.text != value) {
                onValueChange(result.sanitizedValue.text)
            }
        }
    }

    OutlinedTextField(
        value = textFieldValue,
        onValueChange = handleValueChange,
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
