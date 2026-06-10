package com.denzo.daschess

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denzo.daschess.data.ChessRepository
import com.denzo.daschess.models.ChessPuzzle
import com.denzo.daschess.models.MatchHistoryItem
import com.denzo.daschess.models.UserStats
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ChessRepository) : ViewModel() {

    private val _userStats = MutableLiveData<UserStats>()
    val userStats: LiveData<UserStats> = _userStats

    private val _recentMatches = MutableLiveData<List<MatchHistoryItem>>()
    val recentMatches: LiveData<List<MatchHistoryItem>> = _recentMatches

    private val _dailyPuzzle = MutableLiveData<ChessPuzzle>()
    val dailyPuzzle: LiveData<ChessPuzzle> = _dailyPuzzle

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _userStats.value = repository.getUserStats()
                _dailyPuzzle.value = repository.getDailyPuzzle()
                _recentMatches.value = repository.getRecentMatches()
            } catch (e: Exception) {
                _error.value = "Failed to load dashboard: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}