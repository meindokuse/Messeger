package com.example.myapplication.util

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.local.LocalReposetoryHelper

open class SharedViewModel(): ViewModel(){



    val isKeyBoardActive = MutableLiveData<Boolean>()

    fun setKeyBoardStatus(isActive:Boolean){
        Log.d("MyLog","Статус изменился")

        isKeyBoardActive.value = isActive
    }

}
