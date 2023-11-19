package com.example.myapplication



import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MyViewModelFactory(private val localReposetoryHelper: LocalReposetoryHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyViewModel::class.java)) {
            return MyViewModel(localReposetoryHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
