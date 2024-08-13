package com.example.friendnet.di.datasources

import android.content.Context
import androidx.room.Room
import com.example.friendnet.data.local.storage.FileManager
import com.example.friendnet.data.local.db.DataBase
import com.example.friendnet.data.remote.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    fun provideFileManager(@ApplicationContext context: Context): FileManager {
        return FileManager(context)
    }

    @Provides
    @Singleton
    fun provideFireBaseStorage():FirebaseStorage{
        return FirebaseStorage()
    }

    @Provides
    @Singleton
    fun provideLocalDataBase(@ApplicationContext context: Context):DataBase {
       return Room.databaseBuilder(
            context,
            DataBase::class.java,
            "database.db"
        ).build()
    }





}