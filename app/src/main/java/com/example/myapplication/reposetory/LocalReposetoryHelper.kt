package com.example.myapplication

import android.content.Context
import android.util.Log

class LocalReposetoryHelper(context: Context) {
    private val localReposetory:LocalReposetory = LocalReposetory(context)
    fun getAllInfo():ProfileInfo{
        return localReposetory.getAll()
    }

    fun addProfile(profileInfo: ProfileInfo){
        Log.d("MyLog", "Updating user profile")
        localReposetory.addOrChangeProfile(profileInfo)
    }
    fun updateProfile(FirstName:String,SecondName:String){
        localReposetory.updateProfile(FirstName,SecondName)
    }

}