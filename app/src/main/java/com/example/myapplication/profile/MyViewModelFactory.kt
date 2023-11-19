package com.example.myapplication.profile



import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.reposetory.LocalReposetoryHelper
import com.example.myapplication.viewmodel.MyViewModel

class MyViewModelFactory(private val localReposetoryHelper: LocalReposetoryHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyViewModel::class.java)) {
            return MyViewModel(localReposetoryHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
