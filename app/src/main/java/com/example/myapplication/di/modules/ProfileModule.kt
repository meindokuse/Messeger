package com.example.myapplication.di.modules

import com.example.myapplication.data.local.FileManager
import com.example.myapplication.data.local.LocalReposetoryHelper
import com.example.myapplication.data.remote.FirebaseStorage
import com.example.myapplication.data.reposetory.profile.LocalProfileReposetoryImpl
import com.example.myapplication.data.reposetory.profile.RemoteUserReposImpl
import com.example.myapplication.di.MyApp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.internal.managers.ApplicationComponentManager
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Singleton

@Module
@InstallIn(ActivityRetainedComponent::class)
object ProfileModule {

    @Provides
    @ActivityRetainedScoped
    fun provideRemoteProfileReposetory(firebaseStorage: FirebaseStorage): RemoteUserReposImpl {
        return RemoteUserReposImpl(firebaseStorage)
    }

    @Provides
    @ActivityRetainedScoped
    fun provideLocalProfileReposetory(
        localReposetoryHelper: LocalReposetoryHelper,
        fileManager: FileManager
    ): LocalProfileReposetoryImpl {
        return LocalProfileReposetoryImpl(localReposetoryHelper,fileManager)
    }
}