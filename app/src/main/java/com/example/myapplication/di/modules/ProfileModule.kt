package com.example.myapplication.di.modules

import com.example.myapplication.data.local.storage.FileManager
import com.example.myapplication.data.local.LocalReposetoryHelper
import com.example.myapplication.data.local.db.DataBase
import com.example.myapplication.data.remote.FirebaseStorage
import com.example.myapplication.data.reposetory.profile.LocalProfileReposetoryImpl
import com.example.myapplication.data.reposetory.profile.RemoteUserReposImpl
import com.example.myapplication.util.api.NetworkApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Provides
    @Singleton
    fun provideRemoteProfileReposetory(userApi:NetworkApi,firebaseStorage: FirebaseStorage): RemoteUserReposImpl {
        return RemoteUserReposImpl(userApi,firebaseStorage)
    }

    @Provides
    @Singleton
    fun provideLocalProfileReposetory(
        dataBase: DataBase,
        fileManager: FileManager
    ): LocalProfileReposetoryImpl {
        return LocalProfileReposetoryImpl(dataBase,fileManager)
    }
}