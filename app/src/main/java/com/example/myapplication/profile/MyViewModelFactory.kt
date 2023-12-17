package com.example.myapplication.profile



import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.reposetory.LocalReposetoryHelper
import com.example.myapplication.viewmodel.MyViewModel


class MyViewModelFactory(private val localReposetoryHelper: LocalReposetoryHelper,private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyViewModel::class.java)) {
            return MyViewModel(localReposetoryHelper,1,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
