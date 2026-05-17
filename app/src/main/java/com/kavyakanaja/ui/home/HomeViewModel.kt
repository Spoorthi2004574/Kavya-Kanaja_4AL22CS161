package com.kavyakanaja.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kavyakanaja.domain.PoemRepository
import com.kavyakanaja.domain.model.Poem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HomeUiState(
    val isLoading: Boolean = true,
    val poem: Poem? = null,
    val poetName: String? = null,
    val errorMessage: String? = null,
)

class HomeViewModel(
    private val repository: PoemRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refresh(LocalDate.now())
    }

    fun refresh(date: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                val poem = repository.getPoemOfTheDay(date)
                val poet = repository.getPoetById(poem.poetId)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        poem = poem,
                        poetName = poet?.name,
                        errorMessage = null,
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        poem = null,
                        poetName = null,
                        errorMessage = error.message ?: "Could not load poems.",
                    )
                }
            }
        }
    }
}
