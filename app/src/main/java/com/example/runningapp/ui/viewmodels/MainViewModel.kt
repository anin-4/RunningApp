package com.example.runningapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runningapp.database.RunningAppEntity
import com.example.runningapp.repository.MainRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    val mainRepo: MainRepo
):ViewModel() {

    fun insertIntoDatabase(runningAppEntity: RunningAppEntity){
        viewModelScope.launch {
            mainRepo.insertItem(runningAppEntity)
        }
    }

    fun getItemSortedByDistance() = mainRepo.getAllItemSortedByDistanceCovered()

}