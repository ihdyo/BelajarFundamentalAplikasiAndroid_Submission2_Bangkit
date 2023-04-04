package com.ihdyo.githubuser.data.module

import android.content.Context
import androidx.room.Room
import com.ihdyo.githubuser.data.local.UserDao
import com.ihdyo.githubuser.data.local.UserDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    fun provideUserDao(userDatabase: UserDatabase): UserDao = userDatabase.userDao()

    @Provides
    @Singleton
    fun provideUserDatabase(@ApplicationContext context: Context): UserDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            UserDatabase::class.java,
            "note_database"
        ).build()
    }
}