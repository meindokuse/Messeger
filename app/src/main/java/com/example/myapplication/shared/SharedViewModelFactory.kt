package com.example.myapplication.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.reposetory.LocalReposetoryHelper

class SharedViewModelFactory(private val localReposetoryHelper: LocalReposetoryHelper):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)){
            return SharedViewModel(localReposetoryHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


}