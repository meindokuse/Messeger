package com.example.myapplication.shared

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.LocalReposetoryHelper

open class SharedViewModel(private val localReposetoryHelper: LocalReposetoryHelper): ViewModel(){

    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> get() = _userId

    val isKeyBoardActive = MutableLiveData<Boolean>()

    fun setKeyBoardStatus(isActive:Boolean){
        Log.d("MyLog","Статус изменился")

        isKeyBoardActive.value = isActive

    }
    init {
        _userId.value = localReposetoryHelper.getUserId()
    }
}
