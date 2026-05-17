package com.kavyakanaja.ui.poetlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kavyakanaja.domain.PoemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PoetListItem(
    val id: String,
    val name: String,
    val bio: String,
    val imageDrawableName: String?,
)

data class PoetListUiState(
    val isLoading: Boolean = true,
    val poets: List<PoetListItem> = emptyList(),
    val errorMessage: String? = null,
)

class PoetListViewModel(
    private val repository: PoemRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PoetListUiState())
    val uiState: StateFlow<PoetListUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, poets = emptyList()) }
            runCatching {
                val catalog = repository.getCatalog()
                catalog.poetsById.values.map { poet ->
                    PoetListItem(
                        id = poet.id,
                        name = poet.name,
                        bio = poet.bio,
                        imageDrawableName = poet.imageDrawableName,
                    )
                }.sortedBy { it.name }
            }.onSuccess { poets ->
                _uiState.update { it.copy(isLoading = false, poets = poets, errorMessage = null) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        poets = emptyList(),
                        errorMessage = error.message ?: "Could not load poets.",
                    )
                }
            }
        }
    }
}

