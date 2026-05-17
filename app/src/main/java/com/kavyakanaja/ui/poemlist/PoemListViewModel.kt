package com.kavyakanaja.ui.poemlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kavyakanaja.domain.PoemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PoemListItem(
    val id: String,
    val title: String,
    val poetName: String,
)

data class PoemListUiState(
    val isLoading: Boolean = true,
    val poems: List<PoemListItem> = emptyList(),
    val errorMessage: String? = null,
)

class PoemListViewModel(
    private val repository: PoemRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PoemListUiState())
    val uiState: StateFlow<PoemListUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, poems = emptyList()) }
            runCatching {
                val catalog = repository.getCatalog()
                catalog.poems.map { poem ->
                    val poetName = catalog.poetsById[poem.poetId]?.name ?: "Unknown poet"
                    PoemListItem(
                        id = poem.id,
                        title = poem.title,
                        poetName = poetName,
                    )
                }
            }.onSuccess { poems ->
                _uiState.update { it.copy(isLoading = false, poems = poems, errorMessage = null) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        poems = emptyList(),
                        errorMessage = error.message ?: "Could not load poems.",
                    )
                }
            }
        }
    }
}

