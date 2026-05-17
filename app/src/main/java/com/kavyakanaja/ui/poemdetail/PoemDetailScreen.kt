package com.kavyakanaja.ui.poemdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kavyakanaja.domain.model.DifficultWord
import com.kavyakanaja.ui.components.PoemInteractiveText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoemDetailScreen(
    viewModel: PoemDetailViewModel,
    onNavigateBack: () -> Unit,
    onOpenPoet: (String) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedWord by remember { mutableStateOf<DifficultWord?>(null) }
    var showEnglish by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Text(
                        text = state.poem?.title ?: "Poem",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                    )
                },
            )
        },
    ) { innerPadding ->
        when {
            state.isLoading -> {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator()
                }
            }
            state.poem == null -> {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(24.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "This poem could not be found.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = onNavigateBack) {
                        Text("Go back")
                    }
                }
            }
            else -> {
                val poem = requireNotNull(state.poem)
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = state.poetName?.let { "— $it" } ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )

                    RowAudioBar(
                        audioAvailable = state.audioAvailable,
                        isPlaying = state.isPlaying,
                        onToggle = { viewModel.togglePlayback() },
                    )

                    Text(
                        text = "Tap underlined words for meanings.",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )

                    PoemInteractiveText(
                        poemText = poem.text,
                        difficultWords = poem.difficultWords,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth(),
                        onWordClick = { selectedWord = it },
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = if (showEnglish) "Meaning (English)" else "ಭಾವಾರ್ಥ",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = "English",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Switch(
                                    checked = showEnglish,
                                    onCheckedChange = { showEnglish = it },
                                )
                            }
                            Text(
                                text = if (showEnglish) {
                                    poem.meaningEnglish ?: "English meaning is not available yet for this poem."
                                } else {
                                    poem.meaning
                                },
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { onOpenPoet(poem.poetId) },
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null)
                            Text("Poet's corner")
                        }
                    }
                }
            }
        }
    }

    selectedWord?.let { word ->
        AlertDialog(
            onDismissRequest = { selectedWord = null },
            confirmButton = {
                TextButton(onClick = { selectedWord = null }) {
                    Text("Close")
                }
            },
            title = { Text(word.surface, style = MaterialTheme.typography.titleMedium) },
            text = {
                Text(word.meaning, style = MaterialTheme.typography.bodyMedium)
            },
        )
    }
}

@Composable
private fun RowAudioBar(
    audioAvailable: Boolean,
    isPlaying: Boolean,
    onToggle: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (!audioAvailable) {
            Text(
                text = "Audio file not packaged for this poem.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
            )
        }
        FilledTonalButton(
            onClick = onToggle,
            enabled = audioAvailable,
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
            )
            Text(
                text = if (isPlaying) "Pause recitation" else "Play recitation",
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}
