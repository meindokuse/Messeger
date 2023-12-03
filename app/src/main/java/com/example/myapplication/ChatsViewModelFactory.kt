package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.reposetory.LocalReposetoryHelper
import com.example.myapplication.viewmodel.MyViewModel

class ChatsViewModelFactory(private val localReposetoryHelper: LocalReposetoryHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelForChats::class.java)) {
            return ViewModelForChats(localReposetoryHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}