package com.example.runningapp.database

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface RunningAppDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun pushItem(item:RunningAppEntity)

    @Delete
    suspend fun removeItem(item: RunningAppEntity)

    @Query("select * from running_table order by timeStamp desc")
    fun getAllItemsSortedByTime(): LiveData<List<RunningAppEntity>>

    @Query("select * from running_table order by caloriesBurnt desc")
    fun getAllItemsSortedByCalorie(): LiveData<List<RunningAppEntity>>

    @Query("select * from running_table order by distanceCoveredM desc")
    fun getAllItemsSortedByDistanceCovered(): LiveData<List<RunningAppEntity>>

    @Query("select * from running_table order by avgSpeedRunKMH desc")
    fun getAllItemsSortedBySpeed(): LiveData<List<RunningAppEntity>>

    @Query("select * from running_table order by timeInMillis desc")
    fun getAllItemsSortedByTimeOfRun(): LiveData<List<RunningAppEntity>>

    @Query("select sum(timeInMillis) from running_table")
    fun getTotalTimeOfRunInMillis():LiveData<Long>

    @Query("select sum(caloriesBurnt) from running_table")
    fun getTotalCaloriesBurned():LiveData<Int>


    @Query("select sum(distanceCoveredM) from running_table")
    fun getTotalDistanceCovered():LiveData<Int>

    @Query("select avg(avgSpeedRunKMH) from running_table")
    fun getTotalAverageSpeed():LiveData<Float>




}