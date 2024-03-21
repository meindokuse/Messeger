package com.example.myapplication.di.modules

import com.example.myapplication.data.local.storage.FileManager
import com.example.myapplication.data.local.LocalReposetoryHelper
import com.example.myapplication.data.remote.FirebaseStorage
import com.example.myapplication.data.reposetory.profile.LocalProfileReposetoryImpl
import com.example.myapplication.data.reposetory.profile.RemoteUserReposImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

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