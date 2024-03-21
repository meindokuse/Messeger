package com.example.myapplication.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myapplication.data.local.db.dao.ChatDao
import com.example.myapplication.data.local.db.dao.MessageDao
import com.example.myapplication.data.local.db.dao.ProfileDao
import com.example.myapplication.data.models.local.ChatEntity
import com.example.myapplication.data.models.local.MessageEntity
import com.example.myapplication.data.models.local.ProfileEntity


@Database(
    entities = [ProfileEntity::class,ChatEntity::class,MessageEntity::class],
    version = 1
)
abstract class DataBase: RoomDatabase() {

    abstract val profileDao: ProfileDao
    abstract val messageDao: MessageDao
    abstract val chatDao: ChatDao
}