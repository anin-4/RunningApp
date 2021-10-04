package com.example.runningapp.database

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "running_table")
data class RunningAppEntity(
    @PrimaryKey(autoGenerate = true)
    var id:Int?=null,
    var image:Bitmap?=null,
    var timeStamp:Long=0L,
    var caloriesBurnt:Int=0,
    var distanceCoveredM:Int=0,
    var avgSpeedRunKMH:Float=0f,
    var timeInMillis:Long=0L,
)