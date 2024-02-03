package com.example.myapplication.profile.domain



import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.reposetory.LocalReposetoryHelper


class MyViewModelFactory(private val localReposetoryHelper: LocalReposetoryHelper,private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(localReposetoryHelper,1,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
