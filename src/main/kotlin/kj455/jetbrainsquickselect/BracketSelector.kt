package kj455.jetbrainsquickselect

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange

/**
 * Handles bracket pair detection and selection
 */
class BracketSelector(private val editor: Editor) {
    private val document: Document = editor.document

    /**
     * Find all occurrences of a character in a line
     */
    private fun findOccurrences(line: Int, char: Char): List<Int> {
        if (line < 0 || line >= document.lineCount) return emptyList()

        val lineStartOffset = document.getLineStartOffset(line)
        val lineEndOffset = document.getLineEndOffset(line)
        val lineText = document.getText(TextRange(lineStartOffset, lineEndOffset))

        return lineText.indices.filter { lineText[it] == char }
    }

    /**
     * Find the next occurrence of a character, accounting for nesting
     */
    private fun findNext(
        line: Int,
        char: Char,
        startIndex: Int = 0,
        nestChar: Char? = null,
        nested: Int = 0
    ): Pair<Int, Int>? {
        if (line >= document.lineCount) return null

        val occurrences = findOccurrences(line, char).filter { it >= startIndex }
        val nests = nestChar?.let { findOccurrences(line, it).filter { it >= startIndex } } ?: emptyList()

        var occurrenceIndex = 0
        var nestsIndex = 0
        var currentNested = nested

        while ((occurrenceIndex < occurrences.size || nestsIndex < nests.size) && currentNested >= 0) {
            val nextOccurrence = occurrences.getOrNull(occurrenceIndex)
            val nextNest = nests.getOrNull(nestsIndex)

            when {
                nextOccurrence != null && (nextNest == null || nextOccurrence < nextNest) -> {
                    if (currentNested == 0) {
                        return Pair(line, occurrences[occurrenceIndex])
                    }
                    currentNested--
                    occurrenceIndex++
                }
                nextNest != null -> {
                    currentNested++
                    nestsIndex++
                }
                else -> break
            }
        }

        return findNext(line + 1, char, 0, nestChar, currentNested)
    }

    /**
     * Find the previous occurrence of a character, accounting for nesting
     */
    private fun findPrevious(
        line: Int,
        char: Char,
        startIndex: Int? = null,
        nestChar: Char? = null,
        nested: Int = 0
    ): Pair<Int, Int>? {
        if (line < 0) return null

        val actualStartIndex = startIndex ?: document.getLineEndOffset(line) - document.getLineStartOffset(line)
        val occurrences = findOccurrences(line, char).filter { it <= actualStartIndex }
        val nests = nestChar?.let { findOccurrences(line, it).filter { it <= actualStartIndex } } ?: emptyList()

        var occurrenceIndex = occurrences.size - 1
        var nestsIndex = nests.size - 1
        var currentNested = nested

        while ((occurrenceIndex >= 0 || nestsIndex >= 0) && currentNested >= 0) {
            val prevOccurrence = occurrences.getOrNull(occurrenceIndex)
            val prevNest = nests.getOrNull(nestsIndex)

            when {
                prevOccurrence != null && (prevNest == null || prevOccurrence > prevNest) -> {
                    if (currentNested == 0) {
                        return Pair(line, occurrences[occurrenceIndex])
                    }
                    currentNested--
                    occurrenceIndex--
                }
                prevNest != null -> {
                    currentNested++
                    nestsIndex--
                }
                else -> break
            }
        }

        return findPrevious(line - 1, char, null, nestChar, currentNested)
    }

    /**
     * Select text between matching brackets
     */
    fun selectBetweenBrackets(startChar: Char, endChar: Char, outer: Boolean = false): Boolean {
        val caret = editor.caretModel.currentCaret
        val offset = caret.offset
        val lineNumber = document.getLineNumber(offset)
        val lineStartOffset = document.getLineStartOffset(lineNumber)
        val positionInLine = offset - lineStartOffset

        // Find the opening bracket before cursor
        val startPos = findPrevious(lineNumber, startChar, positionInLine, endChar)
            ?: return false

        val (startLine, startCol) = startPos
        val startOffset = document.getLineStartOffset(startLine) + startCol

        // Find the closing bracket after the opening bracket
        val endPos = findNext(startLine, endChar, startCol + 1, startChar)
            ?: return false

        val (endLine, endCol) = endPos
        val endOffset = document.getLineStartOffset(endLine) + endCol

        // Calculate selection range
        val selectionStart = if (outer) startOffset else startOffset + 1
        val selectionEnd = if (outer) endOffset + 1 else endOffset

        // Check if we should expand to outer selection
        val currentStart = caret.selectionStart
        val currentEnd = caret.selectionEnd

        val shouldExpandOuter = !outer &&
            currentStart == startOffset + 1 &&
            currentEnd == endOffset

        if (shouldExpandOuter) {
            // Expand to include brackets
            caret.setSelection(startOffset, endOffset + 1)
        } else {
            caret.setSelection(selectionStart, selectionEnd)
        }

        return true
    }

    /**
     * Select text between matching quotes (same character for start and end)
     * Supports multiline for backticks
     */
    fun selectBetweenQuotes(quoteChar: Char, outer: Boolean = false, multiline: Boolean = false): Boolean {
        val caret = editor.caretModel.currentCaret
        val offset = caret.offset
        val lineNumber = document.getLineNumber(offset)
        val lineStartOffset = document.getLineStartOffset(lineNumber)
        val positionInLine = offset - lineStartOffset

        val matches = findOccurrences(lineNumber, quoteChar)

        // Find the next match after cursor
        val next = matches.firstOrNull { it > positionInLine }
        val nextIndex = if (next != null) matches.indexOf(next) else -1

        if (matches.size > 1 && matches.size % 2 == 0) {
            // Handle single line quotes
            if (nextIndex == -1) return false

            val startIndex = if (nextIndex % 2 != 0) nextIndex - 1 else nextIndex
            val startCol = matches[startIndex]
            val endCol = matches[startIndex + 1]

            val startOffset = lineStartOffset + startCol
            val endOffset = lineStartOffset + endCol

            // Check if we should expand to outer selection
            val currentStart = caret.selectionStart
            val currentEnd = caret.selectionEnd

            val shouldExpandOuter = !outer &&
                currentStart == startOffset + 1 &&
                currentEnd == endOffset

            val offset = if (shouldExpandOuter || outer) 1 else 0

            val selectionStart = startOffset + 1 - offset
            val selectionEnd = endOffset + offset

            caret.setSelection(selectionStart, selectionEnd)
            return true
        } else if (multiline) {
            // Handle multiline quotes (e.g., backticks)
            val startPos = findPreviousQuote(lineNumber, quoteChar, positionInLine)
                ?: if (nextIndex >= 0) Pair(lineNumber, matches[nextIndex]) else return false

            val (startLine, startCol) = startPos
            val startOffset = document.getLineStartOffset(startLine) + startCol

            val endPos = findNextQuote(startLine, quoteChar, startCol + 1)
                ?: return false

            val (endLine, endCol) = endPos
            val endOffset = document.getLineStartOffset(endLine) + endCol

            // Check if we should expand to outer selection
            val currentStart = caret.selectionStart
            val currentEnd = caret.selectionEnd

            val shouldExpandOuter = !outer &&
                currentStart == startOffset + 1 &&
                currentEnd == endOffset

            val offset = if (shouldExpandOuter || outer) 1 else 0

            val selectionStart = startOffset + 1 - offset
            val selectionEnd = endOffset + offset

            caret.setSelection(selectionStart, selectionEnd)
            return true
        }

        return false
    }

    /**
     * Find the previous quote character (for multiline quotes)
     */
    private fun findPreviousQuote(line: Int, char: Char, startIndex: Int? = null): Pair<Int, Int>? {
        if (line < 0) return null

        val actualStartIndex = startIndex ?: document.getLineEndOffset(line) - document.getLineStartOffset(line)
        val occurrences = findOccurrences(line, char).filter { it <= actualStartIndex }

        return if (occurrences.isNotEmpty()) {
            Pair(line, occurrences.last())
        } else {
            findPreviousQuote(line - 1, char, null)
        }
    }

    /**
     * Find the next quote character (for multiline quotes)
     */
    private fun findNextQuote(line: Int, char: Char, startIndex: Int = 0): Pair<Int, Int>? {
        if (line >= document.lineCount) return null

        val occurrences = findOccurrences(line, char).filter { it >= startIndex }

        return if (occurrences.isNotEmpty()) {
            Pair(line, occurrences.first())
        } else {
            findNextQuote(line + 1, char, 0)
        }
    }
}
