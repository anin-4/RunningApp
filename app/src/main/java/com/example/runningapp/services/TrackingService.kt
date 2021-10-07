package com.example.runningapp.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.runningapp.R
import com.example.runningapp.ui.MainActivity
import com.example.runningapp.utils.Constants.ACTION_PAUSE_SERVICE
import com.example.runningapp.utils.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.example.runningapp.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningapp.utils.Constants.ACTION_STOP_SERVICE
import com.example.runningapp.utils.Constants.FASTEST_LOCATION_INTERVAL
import com.example.runningapp.utils.Constants.LOCATION_TRACKING_AVG_TIME
import com.example.runningapp.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.example.runningapp.utils.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.runningapp.utils.Constants.NOTIFICATION_ID
import com.example.runningapp.utils.Constants.TAG
import com.example.runningapp.utils.TrackingUtility
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng


typealias polyline = MutableList<LatLng>
typealias polylines = MutableList<polyline>

class TrackingService: LifecycleService() {

    private var isFirstRun= true

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object{
        val isTracking= MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<polylines>()
    }



    private fun postInitialValue(){
        isTracking.postValue(true)
        pathPoints.postValue(mutableListOf())
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValue()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this, {
            updateLocationTracking(it)
        })
    }

    private fun addEmptyPolyLine(){
        pathPoints.value?.apply{
            add(mutableListOf())
            pathPoints.postValue(this)
        }?: pathPoints.postValue(mutableListOf(mutableListOf()))
    }

    private fun addPathPoints(location : Location?){
        location?.let{
            val pos = LatLng(location.latitude,location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            if(isTracking.value!!){
                locationResult.locations.let{locations ->
                    for (location in locations){
                        addPathPoints(location)
                        Log.d(TAG, "onLocationResult: ${location.latitude} ${location.longitude}")
                    }

                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking:Boolean){
        if(isTracking){
            if(TrackingUtility.hasLocationPermissions(this)){
                val request = LocationRequest.create().apply {
                    interval = LOCATION_TRACKING_AVG_TIME
                    fastestInterval = FASTEST_LOCATION_INTERVAL
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY

                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,locationCallback,
                    Looper.getMainLooper())
            }
        }
        else{
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if(isFirstRun){
                        startForegroundService()
                        isFirstRun=false
                    }
                    else
                    Log.d(TAG,"resuming service")
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

    private fun startForegroundService(){
        val notificationManager= getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        isTracking.postValue(true)
        addEmptyPolyLine()

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
            createNotificationChannel(notificationManager)

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
            .setContentTitle("Running App")
            .setContentText("00:00:00")
            .setContentIntent(getMainActivityPendingIntent())
        startForeground(NOTIFICATION_ID,notificationBuilder.build())
    }

    private fun getMainActivityPendingIntent()= PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also{
            it.action=ACTION_SHOW_TRACKING_FRAGMENT
        },
        FLAG_UPDATE_CURRENT
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel= NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )

            notificationManager.createNotificationChannel(channel)
    }
}