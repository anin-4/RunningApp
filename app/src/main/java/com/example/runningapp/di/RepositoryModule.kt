package com.example.runningapp.di

import com.example.runningapp.database.RunningAppDao
import com.example.runningapp.repository.MainRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRepo(
        runningAppDao: RunningAppDao
    )= MainRepo(runningAppDao)

}