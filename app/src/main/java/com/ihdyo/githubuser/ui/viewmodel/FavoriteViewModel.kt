package com.ihdyo.githubuser.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ihdyo.githubuser.data.UserRepository
import com.ihdyo.githubuser.data.local.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {

    private val _favorites = MutableStateFlow(listOf<UserEntity>())
    val favorite = _favorites.asStateFlow()

    init {
        getFavorite()
    }

    private fun getFavorite() {
        viewModelScope.launch {
            repository.getAllFavorite().collect {
                _favorites.value = it
            }
        }
    }
}