package com.example.friendnet.ui.profile.rcview

import android.media.MediaPlayer

interface ItemListener {
    fun onClickDelete(position:Int)

    fun onClickStartListen(position: Int)

    fun onClickStopListen(position: Int)

}