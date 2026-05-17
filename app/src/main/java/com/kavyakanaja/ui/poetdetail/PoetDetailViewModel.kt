package com.kavyakanaja.ui.poetdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kavyakanaja.domain.PoemRepository
import com.kavyakanaja.domain.model.Poet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PoetDetailUiState(
    val isLoading: Boolean = true,
    val poet: Poet? = null,
)

class PoetDetailViewModel(
    private val repository: PoemRepository,
    private val poetId: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PoetDetailUiState())
    val uiState: StateFlow<PoetDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val poet = repository.getPoetById(poetId)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    poet = poet,
                )
            }
        }
    }
}
