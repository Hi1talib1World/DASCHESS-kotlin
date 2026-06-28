package com.denzo.daschess

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denzo.daschess.data.ChessRepository
import com.denzo.daschess.models.MatchHistoryItem
import com.denzo.daschess.models.UserStats
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: ChessRepository) : ViewModel() {

    private val _userStats = MutableLiveData<UserStats>()
    val userStats: LiveData<UserStats> = _userStats

    private val _historyMatches = MutableLiveData<List<MatchHistoryItem>>()
    val historyMatches: LiveData<List<MatchHistoryItem>> = _historyMatches

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadHistoryData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // In a real app, we might have a specific repository method for full history
                // For now, we use the same mock methods
                _userStats.value = repository.getUserStats()
                _historyMatches.value = repository.getRecentMatches()
            } catch (e: Exception) {
                _error.value = "Failed to load history: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}