package com.example.myapplication.viewmodel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.elements.Event
import com.example.myapplication.profile.BlankFragment
import com.example.myapplication.profile.ProfileInfo
import com.example.myapplication.reposetory.LocalReposetoryHelper

open class MyViewModel(private val localReposetoryHelper: LocalReposetoryHelper):ViewModel() {


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
    val NowEvent:  MutableLiveData<Event> = MutableLiveData()
    fun addEventToReposetory(event: Event,profileId:Long){
        localReposetoryHelper.addEventForRcView(event,profileId)
        updateUserEventsList(profileId)


    }
    fun updateUserEventsList(profileId: Long){

        (userEvents as MutableLiveData).value = localReposetoryHelper.getAllEvents(profileId)
        updateNowEvent()
    }
    fun updateNowEvent(){
        if(userEvents.value != null) {
            NowEvent.value = userEvents.value?.get(userEvents.value!!.size - 1)
        }

//        }else NowEvent.value = Event("Тут могли бы быть ваши активности","В данный момент список пуст")
    }
    fun DeleteEvent(event: Event){
        localReposetoryHelper.deleteAll(event)
        updateUserEventsList(profileId = 1)
    }
    init {
        updateUserProfile()
        updateUserEventsList(profileId = 1)

    }


}