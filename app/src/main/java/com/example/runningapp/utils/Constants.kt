package com.example.runningapp.utils

import android.graphics.Color

object Constants {

    const val REQUEST_CODE_PERMISSION=0

    const val ACTION_START_OR_RESUME_SERVICE="ACTION_START_OR_RESUME_SERVICE"

    const val ACTION_STOP_SERVICE="ACTION_STOP_SERVICE"

    const val ACTION_PAUSE_SERVICE="ACTION_PAUSE_SERVICE"

    const val TAG="Debug"

    const val NOTIFICATION_CHANNEL_ID="tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME="Tracking"
    const val NOTIFICATION_ID=1

    const val ACTION_SHOW_TRACKING_FRAGMENT="showTrackingFragment"

    const val LOCATION_TRACKING_AVG_TIME=5000L
    const val FASTEST_LOCATION_INTERVAL= 2000L

    const val POLYLINE_COLOR = Color.RED
    const val POLYLINE_WIDTH = 8f
    const val MAP_ZOOM = 15f
}