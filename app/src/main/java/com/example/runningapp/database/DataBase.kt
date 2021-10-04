package com.example.runningapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [RunningAppEntity::class],version = 1)
@TypeConverters(Converters::class)
abstract class DataBase:RoomDatabase() {

    abstract fun getRunningAppDao():RunningAppDao

}