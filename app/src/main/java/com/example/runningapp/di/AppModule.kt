package com.example.runningapp.di

import android.content.Context
import androidx.room.Room
import com.example.runningapp.database.DataBase
import com.example.runningapp.database.RunningAppDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDataBaseInstance(@ApplicationContext context:Context):DataBase{
        return Room.databaseBuilder(
            context,
            DataBase::class.java,
            "running_table"
        ).build()
    }

    @Singleton
    @Provides
    fun provideDao(dataBase: DataBase):RunningAppDao= dataBase.getRunningAppDao()
}