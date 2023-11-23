package com.example.myapplication.viewmodel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.elements.Event
import com.example.myapplication.profile.BlankFragment
import com.example.myapplication.profile.ProfileInfo
import com.example.myapplication.reposetory.LocalReposetoryHelper

open class MyViewModel(private val localReposetoryHelper: LocalReposetoryHelper,profileId: Long):ViewModel() {


    val userProfile: LiveData<ProfileInfo> = MutableLiveData()
//    val FirstName: MutableLiveData<String> = MutableLiveData()
//    val SecondName: MutableLiveData<String> = MutableLiveData()




    private fun updateUserProfile(){
        Log.d("MyLog","Инит")
        Log.d("MyLog","${localReposetoryHelper.getAllInfo()}")
        (userProfile as MutableLiveData).value = localReposetoryHelper.getAllInfo()
    }
    fun addUser(profileInfo: ProfileInfo){
        Log.d("MyLog","Во вьюхе заварушка")
        localReposetoryHelper.addProfile(profileInfo)
        updateUserProfile()
    }
    fun uppdateProfile(FirstName:String,SecondName:String){
        localReposetoryHelper.updateProfile(FirstName,SecondName)
        updateUserProfile()
    }

    //makes for RcView ( events )
    val userEvents: LiveData<List<Event>> = MutableLiveData()
    val UserEventRightNow:MutableLiveData<Event> = MutableLiveData()
    fun UdpateUserEventRightNow(event: Event){
        UserEventRightNow.value = event

    }

    fun addEventToReposetory(event: Event,profileId:Long){
        localReposetoryHelper.addEventForRcView(event,profileId)
        updateUserEventsList(profileId)
        UserEventRightNow.value = event


    }
    fun updateUserEventsList(profileId: Long){
        Log.d("MyLog","Лист выгружен")

        (userEvents as MutableLiveData).value = localReposetoryHelper.getAllEvents(profileId)
        Log.d("MyLog","${userEvents.value}")
    }

    fun DeleteEvent(event: Event,profileId: Long){
        localReposetoryHelper.deleteAll(event)
        updateUserEventsList(profileId)
    }
    init {

        updateUserProfile()
        updateUserEventsList(profileId)

    }


}