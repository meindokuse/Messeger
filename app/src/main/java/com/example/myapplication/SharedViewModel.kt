package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.reposetory.LocalReposetoryHelper

open class SharedViewModel(private val localReposetoryHelper: LocalReposetoryHelper): ViewModel(){

    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> get() = _userId
    init {
        _userId.value = localReposetoryHelper.getUserId()
    }
}
