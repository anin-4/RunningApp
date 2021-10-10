package com.example.runningapp.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.runningapp.R
import com.example.runningapp.databinding.FragmentRunBinding
import com.example.runningapp.ui.adapters.RunFragmentAdapter
import com.example.runningapp.ui.viewmodels.MainViewModel
import com.example.runningapp.utils.Constants.REQUEST_CODE_PERMISSION
import com.example.runningapp.utils.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


@AndroidEntryPoint
class FragmentRun : Fragment(), EasyPermissions.PermissionCallbacks {

    private lateinit var binding:FragmentRunBinding
    private val viewModel:MainViewModel by viewModels()
    private val runFragmentAdapter = RunFragmentAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding= FragmentRunBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions()
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentRun_to_fragmentTracking2)
        }
        binding.rvRuns.apply {
            layoutManager= LinearLayoutManager(requireContext())
            adapter=runFragmentAdapter
        }

        viewModel.getItemSortedByDistance().observe(viewLifecycleOwner,{
            runFragmentAdapter.items=it

        })
    }

    private fun requestPermissions(){
        if(TrackingUtility.hasLocationPermissions(requireContext()))
            return
        else{
            if(Build.VERSION.SDK_INT<Build.VERSION_CODES.Q){
                EasyPermissions.requestPermissions(
                    this,
                    "Please Provide the needed Permissions",
                    REQUEST_CODE_PERMISSION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION

                )
            }
            else{
                EasyPermissions.requestPermissions(
                    this,
                    "Please provide the needed Permissions to use the app",
                    REQUEST_CODE_PERMISSION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            }
        }

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms))
            AppSettingsDialog.Builder(this).build().show()
        else{
            requestPermissions()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }
}