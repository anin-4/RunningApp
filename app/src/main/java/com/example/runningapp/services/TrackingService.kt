package com.example.runningapp.services

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleService
import com.example.runningapp.utils.Constants.ACTION_PAUSE_SERVICE
import com.example.runningapp.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningapp.utils.Constants.ACTION_STOP_SERVICE
import com.example.runningapp.utils.Constants.TAG

class TrackingService: LifecycleService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    Log.d(TAG,"Started or resumed service")
                }
                ACTION_PAUSE_SERVICE -> {
                    Log.d(TAG,"Paused service")
                }
                ACTION_STOP_SERVICE -> {
                    Log.d(TAG,"Stopped service")
                }
                else -> Log.d(TAG,"error")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}