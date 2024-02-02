package com.example.myapplication.chats.domain

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.reposetory.LocalReposetoryHelper

class ChatsViewModelFactory(private val localReposetoryHelper: LocalReposetoryHelper,private val application:Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelForChats::class.java)) {
            return ViewModelForChats(localReposetoryHelper,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}