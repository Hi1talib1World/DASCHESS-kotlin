package com.denzo.daschess

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denzo.daschess.data.ChessRepository
import com.denzo.daschess.models.ChessPuzzle
import kotlinx.coroutines.launch

class PuzzlesViewModel(private val repository: ChessRepository) : ViewModel() {

    private val _featuredPuzzle = MutableLiveData<ChessPuzzle>()
    val featuredPuzzle: LiveData<ChessPuzzle> = _featuredPuzzle

    private val _puzzleList = MutableLiveData<List<ChessPuzzle>>()
    val puzzleList: LiveData<List<ChessPuzzle>> = _puzzleList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadPuzzlesData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _featuredPuzzle.value = repository.getDailyPuzzle()
                _puzzleList.value = repository.getPuzzles()
            } catch (e: Exception) {
                _error.value = "Failed to load puzzles: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}