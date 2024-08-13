package com.example.friendnet.di.modules

import com.example.friendnet.data.local.storage.FileManager
import com.example.friendnet.data.local.db.DataBase
import com.example.friendnet.data.remote.FirebaseStorage
import com.example.friendnet.data.reposetory.profile.LocalProfileReposetoryImpl
import com.example.friendnet.data.reposetory.profile.RemoteUserReposImpl
import com.example.friendnet.util.api.NetworkApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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