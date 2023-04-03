package com.ihdyo.githubuser.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ihdyo.githubuser.data.Result
import com.ihdyo.githubuser.data.UserRepository
import com.ihdyo.githubuser.data.local.UserEntity
import com.ihdyo.githubuser.data.remote.response.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {

    private val _userDetail = MutableStateFlow<Result<User>>(Result.Loading)
    val userDetail = _userDetail.asStateFlow()

    private val _isLoaded = MutableStateFlow(false)
    val isLoaded = _isLoaded.asStateFlow()

    fun getDetail(username: String) {
        _userDetail.value = Result.Loading
        viewModelScope.launch {
            repository.getDetail(username).collect {
                _userDetail.value = it
            }
        }
        _isLoaded.value = true
    }

    fun addToFavorite(user: UserEntity) {
        viewModelScope.launch {
            repository.addToFavorite(user)
        }
    }

    fun deleteFromFavorite(user: UserEntity) {
        viewModelScope.launch {
            repository.deleteFromFavorite(user)
        }
    }

    fun isFavorite(id: String): Flow<Boolean> = repository.isFavorite(id)
}