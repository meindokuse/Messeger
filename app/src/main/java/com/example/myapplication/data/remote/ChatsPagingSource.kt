package com.example.myapplication.data.remote

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.myapplication.domain.reposetory.chats.RemoteChatsReposetory
import com.example.myapplication.models.ItemChat
import com.example.myapplication.util.api.RetrofitInstanse
import retrofit2.HttpException

class ChatsPagingSource(
    private val remoteChatsReposetory: RemoteChatsReposetory,
    private val userId:String
): PagingSource<Int, ItemChat>() {

    override fun getRefreshKey(state: PagingState<Int, ItemChat>): Int? {
        val position = state.anchorPosition ?: return null
        val page  = state.closestPageToPosition(position) ?: return null

        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ItemChat> {

        val page = params.key ?: 1
        val pageSize = 5

        val response = remoteChatsReposetory.getAllChats(userId,page,pageSize)
        return if (response != null){
           val chats = response.sortedBy {
                it.mes_time
            }
            val nextKey = if (chats.size < pageSize) null else page + 1
            val prevKey = if (page == 1) null else page - 1
            Log.d("MyLog","ChatsPagingSource новый чат $chats")
            LoadResult.Page(chats,prevKey,nextKey)
        } else LoadResult.Error(HttpException(response))

    }
}