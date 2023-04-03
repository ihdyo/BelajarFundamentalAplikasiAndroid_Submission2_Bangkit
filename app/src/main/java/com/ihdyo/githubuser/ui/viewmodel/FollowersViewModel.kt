package com.ihdyo.githubuser.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ihdyo.githubuser.data.Result
import com.ihdyo.githubuser.data.UserRepository
import com.ihdyo.githubuser.data.remote.response.SimpleUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowersViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {

    private val _isLoaded = MutableStateFlow(false)
    val isLoaded = _isLoaded.asStateFlow()

    private val _followers = MutableStateFlow<Result<ArrayList<SimpleUser>>>(Result.Loading)
    val followers = _followers.asStateFlow()

    fun getFollowers(username: String) {
        _followers.value = Result.Loading
        viewModelScope.launch {
            repository.getFollowers(username).collect {
                _followers.value = it
            }
        }

        _isLoaded.value = true
    }
}