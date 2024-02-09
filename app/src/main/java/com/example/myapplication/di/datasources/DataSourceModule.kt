package com.example.myapplication.di.datasources

import android.content.Context
import com.example.myapplication.data.local.FileManager
import com.example.myapplication.data.local.LocalReposetoryHelper
import com.example.myapplication.data.remote.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Singleton


@Module
@InstallIn(ActivityRetainedComponent::class)
object DataSourceModule {

    @Provides
    @ActivityRetainedScoped
    fun provideFileManager(@ApplicationContext context: Context):FileManager{
        return FileManager(context)
    }
    @Provides
    @ActivityRetainedScoped
    fun provideLocalReposetory(@ApplicationContext context: Context):LocalReposetoryHelper{
        return LocalReposetoryHelper(context)
    }

    @Provides
    @ActivityRetainedScoped
    fun provideFireBaseStorage():FirebaseStorage{
        return FirebaseStorage()
    }
}