package com.ihdyo.githubuser.data

import android.util.Log
import com.ihdyo.githubuser.data.local.UserEntity
import com.ihdyo.githubuser.data.local.UserDao
import com.ihdyo.githubuser.data.remote.response.SimpleUser
import com.ihdyo.githubuser.data.remote.response.User
import com.ihdyo.githubuser.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val userDao: UserDao,
    private val preferences: AppPreferences
) {
    fun searchByUsername(q: String): Flow<Result<ArrayList<SimpleUser>>> = flow {
        emit(Result.Loading)
        try {
            val users = apiService.searchUsername(token = API_TOKEN, q).items
            emit(Result.Success(users))
        } catch (e: Exception) {
            Log.d(TAG, "searchUserByUsername: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getFollowing(id: String): Flow<Result<ArrayList<SimpleUser>>> = flow {
        emit(Result.Loading)
        try {
            val users = apiService.getUserFollowing(token = API_TOKEN, id)
            emit(Result.Success(users))
        } catch (e: Exception) {
            Log.d(TAG, "getUserFollowing: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getFollowers(id: String): Flow<Result<ArrayList<SimpleUser>>> = flow {
        emit(Result.Loading)
        try {
            val users = apiService.getUserFollowers(token = API_TOKEN, id)
            emit(Result.Success(users))
        } catch (e: Exception) {
            Log.d(TAG, "getUserFollowers: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getDetail(id: String): Flow<Result<User>> = flow {
        emit(Result.Loading)
        try {
            val user = apiService.getUserDetail(token = API_TOKEN, id)
            emit(Result.Success(user))
        } catch (e: Exception) {
            Log.d(TAG, "getUserDetail: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun isFavorite(id: String): Flow<Boolean> = userDao.isFavoriteUser(id)

    fun getAllFavorite(): Flow<List<UserEntity>> = userDao.getAllUsers()

    suspend fun deleteFromFavorite(user: UserEntity) {
        userDao.delete(user)
    }

    suspend fun addToFavorite(user: UserEntity) {
        userDao.insert(user)
    }

    fun getTheme(): Flow<Boolean> = preferences.getThemeSetting()

    suspend fun saveTheme(isDarkModeActive: Boolean) {
        preferences.saveThemeSetting(isDarkModeActive)
    }

    companion object {
        private const val API_TOKEN = "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"
        private val TAG = UserRepository::class.java.simpleName
    }
}