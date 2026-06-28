package com.denzo.daschess

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denzo.daschess.data.ChessRepository
import com.denzo.daschess.models.UserStats
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: ChessRepository) : ViewModel() {

    private val _userStats = MutableLiveData<UserStats>()
    val userStats: LiveData<UserStats> = _userStats

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadProfileData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _userStats.value = repository.getUserStats()
            } catch (e: Exception) {
                _error.value = "Failed to load profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}