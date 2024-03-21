package com.example.myapplication.data.reposetory.chats

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.myapplication.models.ItemChat

//@OptIn(ExperimentalPagingApi::class)
//class Mediator(): RemoteMediator<Int, ItemChat>() {
//    override suspend fun load(
//        loadType: LoadType,
//        state: PagingState<Int, ItemChat>
//    ): MediatorResult {
//        TODO("Not yet implemented")
//    }
//
//}