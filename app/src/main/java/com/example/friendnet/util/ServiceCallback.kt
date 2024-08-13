package com.example.friendnet.util

interface ServiceCallback {
    fun showPanel(title:String)
    fun hidePanel()

    fun updateProgressUiHolder(currentPosition:Int)

    fun updateUiHolderComplete()


}