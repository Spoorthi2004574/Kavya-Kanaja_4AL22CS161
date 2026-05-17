package com.kavyakanaja.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.kavyakanaja.domain.model.DifficultWord

internal data class TextSegment(
    val text: String,
    val word: DifficultWord?,
)

internal fun buildLineSegments(line: String, words: List<DifficultWord>): List<TextSegment> {
    if (line.isEmpty()) return emptyList()
    val ordered = words.distinctBy { it.surface }.sortedByDescending { it.surface.length }
    val segments = mutableListOf<TextSegment>()
    var remaining = line
    while (remaining.isNotEmpty()) {
        var bestIndex = Int.MAX_VALUE
        var bestWord: DifficultWord? = null
        for (w in ordered) {
            val idx = remaining.indexOf(w.surface)
            if (idx < 0) continue
            when {
                idx < bestIndex -> {
                    bestIndex = idx
                    bestWord = w
                }
                idx == bestIndex && bestWord != null && w.surface.length > bestWord.surface.length -> {
                    bestWord = w
                }
            }
        }
        if (bestWord != null && bestIndex < Int.MAX_VALUE) {
            if (bestIndex > 0) {
                segments += TextSegment(remaining.take(bestIndex), null)
            }
            segments += TextSegment(bestWord.surface, bestWord)
            remaining = remaining.drop(bestIndex + bestWord.surface.length)
        } else {
            segments += TextSegment(remaining, null)
            break
        }
    }
    return segments
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun PoemInteractiveText(
    poemText: String,
    difficultWords: List<DifficultWord>,
    style: TextStyle,
    modifier: Modifier = Modifier,
    onWordClick: (DifficultWord) -> Unit,
) {
    val lines = remember(poemText) { poemText.lines() }

    Column(modifier = modifier.fillMaxWidth()) {
        lines.forEach { line ->
            if (line.isEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                ) {
                    val segments = buildLineSegments(line, difficultWords)
                    segments.forEach { segment ->
                        val segmentStyle = style.merge(
                            SpanStyle(
                                color = if (segment.word != null) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onBackground
                                },
                                textDecoration = if (segment.word != null) {
                                    TextDecoration.Underline
                                } else {
                                    TextDecoration.None
                                },
                            ),
                        )
                        Text(
                            text = segment.text,
                            style = segmentStyle,
                            modifier = Modifier.clickable(
                                enabled = segment.word != null,
                                onClick = { segment.word?.let(onWordClick) },
                            ),
                        )
                    }
                }
            }
        }
    }
}
