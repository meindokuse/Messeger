package com.example.myapplication.reposetory

import android.content.Context
import android.util.Log
import com.example.myapplication.elements.Event
import com.example.myapplication.profile.ProfileInfo

class LocalReposetoryHelper(context: Context) {
    private val localReposetory: LocalReposetory = LocalReposetory(context)
    //makes for profile
    fun getAllInfo(): ProfileInfo {
        return localReposetory.getAll()
    }

    fun addProfile(profileInfo: ProfileInfo){
        Log.d("MyLog", "Updating user profile")
        localReposetory.addOrChangeProfile(profileInfo)
    }
    fun updateProfile(FirstName:String,SecondName:String){
        localReposetory.updateProfile(FirstName,SecondName)
    }

    // makes for RecyclerView ( events )
    fun addEventForRcView(event: Event,profileID:Long){
        localReposetory.addEvent(event,profileID)
    }
    fun getAllEvents(profileID: Long): List<Event>{
        return localReposetory.getAllEvents(profileID)
    }
    // heve to fun
    fun deleteAll(event: Event){
        localReposetory.deleteEvent(event)
    }

}