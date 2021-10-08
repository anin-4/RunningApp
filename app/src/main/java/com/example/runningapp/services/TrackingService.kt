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
import com.example.runningapp.utils.Constants.TIMER_UPDATE_INTERVAL
import com.example.runningapp.utils.TrackingUtility
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


typealias polyline = MutableList<LatLng>
typealias polylines = MutableList<polyline>

@AndroidEntryPoint
class TrackingService: LifecycleService() {

    private var isFirstRun= true

    private var timeInSeconds = MutableLiveData<Long>()

    @Inject
   lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    lateinit var currentNotificationBuilder: NotificationCompat.Builder

    private var isTimerEnabled =false
    private var lapTime = 0L
    private var runTime = 0L
    private var timeStarted = 0L
    private var lastSecondTimeStamp = 0L

    companion object{
        val isTracking= MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<polylines>()
        var timeInMillis = MutableLiveData<Long>()
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValue()
        currentNotificationBuilder=baseNotificationBuilder
//        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this, {
            updateLocationTracking(it)
            updateNotificationTrackingState(it)
        })
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
                        startTimer()
                }
                ACTION_PAUSE_SERVICE -> {
                    Log.d(TAG,"Paused service")
                    pauseService()
                }
                ACTION_STOP_SERVICE -> {
                    Log.d(TAG,"Stopped service")
                }
                else -> Log.d(TAG,"error")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun pauseService(){
        isTracking.postValue(false)
        isTimerEnabled=false
    }

    //below three functions are about adding initial values and adding latlng coordinates
    private fun postInitialValue(){
        isTracking.postValue(true)
        pathPoints.postValue(mutableListOf())
        timeInSeconds.postValue(0L)
        timeInMillis.postValue(0L)
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

    //below two function are about tracking locations and updating the livedata
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


    //from this function to the last function it is foreground service part with notification
    private fun startForegroundService(){
        startTimer()
        val notificationManager= getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        isTracking.postValue(true)

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
            createNotificationChannel(notificationManager)

        startForeground(NOTIFICATION_ID,baseNotificationBuilder.build())

        timeInSeconds.observe(this, {
            val notification = currentNotificationBuilder
                .setContentText(TrackingUtility.getFormattedStopWatchTime(it * 1000L))
            notificationManager.notify(NOTIFICATION_ID, notification.build())
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel= NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )

            notificationManager.createNotificationChannel(channel)
    }

    //Timer related functions will come here
    private fun startTimer(){
        addEmptyPolyLine()
        isTracking.postValue(true)
        isTimerEnabled=true
        timeStarted= System.currentTimeMillis()

        CoroutineScope(Dispatchers.Main).launch{
            while(isTracking.value!!){

                lapTime= System.currentTimeMillis()-timeStarted

                timeInMillis.postValue(lapTime+runTime)
               if(timeInMillis.value!! >= lastSecondTimeStamp + 1000L){
                   timeInSeconds.postValue(timeInSeconds.value!! + 1)
                   lastSecondTimeStamp+=1000L
               }
                delay(TIMER_UPDATE_INTERVAL)

            }
            runTime+=lapTime
        }
    }
    //function to change the action of the notification
    private fun updateNotificationTrackingState(isTracking:Boolean){
        val notificationActionText = if(isTracking) "Pause" else "Resume"
        val pendingIntent = if(isTracking) {
            val pauseIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService(this, 1, pauseIntent, FLAG_UPDATE_CURRENT)
        } else {
            val resumeIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            PendingIntent.getService(this, 2, resumeIntent, FLAG_UPDATE_CURRENT)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        currentNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(currentNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }
        currentNotificationBuilder = baseNotificationBuilder
            .addAction(R.drawable.ic_pause_black_24dp, notificationActionText, pendingIntent)
        notificationManager.notify(NOTIFICATION_ID, currentNotificationBuilder.build())
    }
}

