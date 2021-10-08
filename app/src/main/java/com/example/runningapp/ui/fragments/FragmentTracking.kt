package com.example.runningapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.runningapp.databinding.FragmentTrackingBinding
import com.example.runningapp.services.TrackingService
import com.example.runningapp.services.polyline
import com.example.runningapp.utils.Constants.ACTION_PAUSE_SERVICE
import com.example.runningapp.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningapp.utils.Constants.MAP_ZOOM
import com.example.runningapp.utils.Constants.POLYLINE_COLOR
import com.example.runningapp.utils.Constants.POLYLINE_WIDTH
import com.example.runningapp.utils.TrackingUtility
import com.example.runningapp.utils.hide
import com.example.runningapp.utils.show
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions

class FragmentTracking : Fragment() {
    private lateinit var binding:FragmentTrackingBinding
    private var map:GoogleMap?= null

    private var isTracking:Boolean= false
    private var polyLines = mutableListOf<polyline>()

    private var currentTimeMillis =  0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding= FragmentTrackingBinding.inflate(inflater,container,false)
        binding.mapView?.onCreate(savedInstanceState)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.getMapAsync {
            map=it
            addAllThePoints()
        }

        binding.btnToggleRun.setOnClickListener{
            toggleRun()
        }

        subscribeToObservers()

    }

    private fun subscribeToObservers(){
            TrackingService.isTracking.observe(viewLifecycleOwner,{
                updateTracking(it)
            })
            TrackingService.pathPoints.observe(viewLifecycleOwner,{
                polyLines=it
                addLatestPolyLines()
                moveCamera()
            })
            TrackingService.timeInMillis.observe(viewLifecycleOwner,{
                currentTimeMillis=it
                val formattedTimeInMillis = TrackingUtility.getFormattedStopWatchTime(currentTimeMillis,true)
                binding.tvTimer.text= formattedTimeInMillis
            })
    }

    private fun toggleRun(){
        if(isTracking){
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }
        else{
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun updateTracking(isTracking:Boolean){
        this.isTracking=isTracking
        if(!isTracking){
            binding.btnToggleRun.text = "START"
            binding.btnFinishRun.show()
        }
        else{
            binding.btnToggleRun.text="STOP"
            binding.btnFinishRun.hide()
        }
    }


    private fun moveCamera(){
        if(polyLines.isNotEmpty() && polyLines.last().size>0){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    polyLines.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun addAllThePoints(){
        for(polyLine in polyLines){
            val polyLineOptions = PolylineOptions()
                .width(POLYLINE_WIDTH)
                .color(POLYLINE_COLOR)
                .addAll(polyLine)

            map?.addPolyline(polyLineOptions)
        }
    }

    private fun addLatestPolyLines(){
        if(polyLines.size>0 && polyLines.last().size>1){
            val preLastPoint= polyLines.last()[polyLines.last().size-2]
            val lastPoint = polyLines.last().last()

            val polyLineOptions= PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastPoint)
                .add(lastPoint)

            map?.addPolyline(polyLineOptions)
        }
    }


    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    override fun onResume() {
        super.onResume()
        binding.mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView?.onSaveInstanceState(outState)
    }

}