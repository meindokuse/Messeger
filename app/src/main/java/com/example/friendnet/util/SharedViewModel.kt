package com.example.friendnet.util

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class SharedViewModel(): ViewModel(){



    val isKeyBoardActive = MutableLiveData<Boolean>()

    fun setKeyBoardStatus(isActive:Boolean){
        Log.d("MyLog","Статус изменился")

        isKeyBoardActive.value = isActive
    }



}
