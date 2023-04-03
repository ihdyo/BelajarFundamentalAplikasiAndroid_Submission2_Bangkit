package com.ihdyo.githubuser.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ihdyo.githubuser.data.Result
import com.ihdyo.githubuser.data.UserRepository
import com.ihdyo.githubuser.data.remote.response.SimpleUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {

    val themeSetting: Flow<Boolean> = repository.getTheme()

    private val _users = MutableStateFlow<Result<ArrayList<SimpleUser>>>(Result.Loading)
    val users = _users.asStateFlow()

    init {
        searchByUsername("\"\"")
    }

    fun searchByUsername(query: String) {
        _users.value = Result.Loading
        viewModelScope.launch {
            repository.searchByUsername(query).collect {
                _users.value = it
            }
        }
    }
}