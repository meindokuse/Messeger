package com.example.friendnet.util

import com.example.friendnet.ui.MainActivity

object ActivityHolder {

    private var currentActivity: MainActivity? = null

    fun setCurrentActivity(activity: MainActivity) {
        currentActivity = activity
    }

    fun getCurrentActivity(): MainActivity? {
        return currentActivity
    }
}