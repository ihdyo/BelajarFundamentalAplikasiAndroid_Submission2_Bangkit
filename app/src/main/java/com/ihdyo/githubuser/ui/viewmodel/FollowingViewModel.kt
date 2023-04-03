package com.ihdyo.githubuser.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ihdyo.githubuser.data.Result
import com.ihdyo.githubuser.data.UserRepository
import com.ihdyo.githubuser.data.remote.response.SimpleUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowingViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {

    private val _isLoaded = MutableStateFlow(false)
    val isLoaded = _isLoaded.asStateFlow()

    private val _following = MutableStateFlow<Result<ArrayList<SimpleUser>>>(Result.Loading)
    val following = _following.asStateFlow()

    fun getFollowing(username: String) {
        _following.value = Result.Loading
        viewModelScope.launch {
            repository.getFollowing(username).catch { e ->
                _following.value = Result.Error(e.message.toString())
            }.collect {
                _following.value = it
            }
        }

        _isLoaded.value = true
    }
}