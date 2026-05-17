package com.kavyakanaja.ui.poemdetail

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kavyakanaja.domain.PoemRepository
import com.kavyakanaja.domain.model.Poem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PoemDetailUiState(
    val isLoading: Boolean = true,
    val poem: Poem? = null,
    val poetName: String? = null,
    val isPlaying: Boolean = false,
    val audioAvailable: Boolean = false,
)

class PoemDetailViewModel(
    application: Application,
    private val repository: PoemRepository,
    private val poemId: String,
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(PoemDetailUiState())
    val uiState: StateFlow<PoemDetailUiState> = _uiState.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null

    init {
        viewModelScope.launch {
            val poem = repository.getPoemById(poemId)
            val poet = poem?.let { repository.getPoetById(it.poetId) }
            val app = getApplication<Application>()
            val audioAvailable = poem?.let { p ->
                app.resources.getIdentifier(p.audioFileName, "raw", app.packageName) != 0
            } ?: false
            _uiState.update {
                it.copy(
                    isLoading = false,
                    poem = poem,
                    poetName = poet?.name,
                    audioAvailable = audioAvailable,
                    isPlaying = false,
                )
            }
        }
    }

    fun togglePlayback() {
        val poem = _uiState.value.poem ?: return
        val app = getApplication<Application>()
        val resId = app.resources.getIdentifier(poem.audioFileName, "raw", app.packageName)
        if (resId == 0) return

        val current = mediaPlayer
        when {
            current != null && current.isPlaying -> {
                current.pause()
                _uiState.update { it.copy(isPlaying = false) }
            }
            current != null && !current.isPlaying -> {
                current.start()
                _uiState.update { it.copy(isPlaying = true) }
            }
            else -> {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer.create(app, resId).apply {
                    setOnCompletionListener { player ->
                        _uiState.update { state -> state.copy(isPlaying = false) }
                        player.seekTo(0, MediaPlayer.SEEK_CLOSEST_SYNC)
                    }
                }
                mediaPlayer?.start()
                _uiState.update { it.copy(isPlaying = true) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
