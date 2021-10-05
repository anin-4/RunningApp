package com.example.runningapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.example.runningapp.R
import com.example.runningapp.databinding.ActivityMainBinding
import com.example.runningapp.utils.hide
import com.example.runningapp.utils.show
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.bottomNavigationView.setupWithNavController(Navigation.findNavController(this,R.id.navHostFragment))

        Navigation.findNavController(this,R.id.navHostFragment).addOnDestinationChangedListener { _, destination, _ ->
                when(destination.id){
                    R.id.fragmentSettings,R.id.fragmentRun,R.id.fragmentStatistics -> binding.bottomNavigationView.show()
                    else -> binding.bottomNavigationView.hide()
                }

        }
    }
}