package com.example.runningapp.repository

import com.example.runningapp.database.RunningAppDao
import com.example.runningapp.database.RunningAppEntity
import javax.inject.Inject

class MainRepo @Inject constructor(
    private var runningAppDao: RunningAppDao
){

    suspend fun insertItem(item:RunningAppEntity)= runningAppDao.pushItem(item)

    suspend fun deleteItem(item: RunningAppEntity)= runningAppDao.removeItem(item)

    fun getAllItemSortedByTime()= runningAppDao.getAllItemsSortedByTime()

    fun getAllItemSortedByCalorie()= runningAppDao.getAllItemsSortedByCalorie()

    fun getAllItemSortedByDistanceCovered() = runningAppDao.getAllItemsSortedByDistanceCovered()

    fun getAllItemSortedBySpeed()=runningAppDao.getAllItemsSortedBySpeed()

    fun getAllItemSortedByTimeOfRun()= runningAppDao.getAllItemsSortedByTimeOfRun()

    fun getTotalRunInMillis()= runningAppDao.getTotalTimeOfRunInMillis()

    fun getTotalCaloriesBurnt() =runningAppDao.getTotalCaloriesBurned()

    fun getTotalDistance()= runningAppDao.getTotalDistanceCovered()

    fun getTotalAvgSpeed()= runningAppDao.getTotalAverageSpeed()
}