package com.example.myapplication.profile.rcview

import android.media.MediaPlayer

interface ItemListener {
    fun onClickDelete(position:Int)

    fun onClickStartListen(position: Int,mediaPlayer: MediaPlayer)

    fun onClickStopListen(position: Int,mediaPlayer: MediaPlayer)

}