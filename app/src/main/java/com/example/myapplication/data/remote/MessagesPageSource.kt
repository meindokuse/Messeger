package com.example.myapplication.data.remote

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.myapplication.data.reposetory.message.RemoteMessagesReposetoryImpl
import com.example.myapplication.models.MessageInChat
import com.example.myapplication.util.api.MessageResponse
import com.example.myapplication.util.api.RetrofitInstanse
import retrofit2.HttpException

class MessagesPageSource(
    private val reposetoryMessages:RemoteMessagesReposetoryImpl,
    private val queryId:String
):PagingSource<Int,MessageInChat>() {

    override fun getRefreshKey(state: PagingState<Int, MessageInChat>): Int? {
        val position = state.anchorPosition ?: return null
        val page  = state.closestPageToPosition(position) ?: return null

        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MessageInChat> {

        val page = params.key ?: 1
        val pageSize = 20

        val response = reposetoryMessages.getListMessages(queryId,page,pageSize)
        val valide = response?.get(0)?.type == 1
        Log.d("MyLog","messages in page source $valide ")
        return if (response != null){
           val messages = response.sortedByDescending {
               it.time
           }
            val nextKey = if (messages.size < pageSize) null else page + 1
            val prevKey = if (page == 1) null else page - 1
            LoadResult.Page(messages,prevKey,nextKey)
        } else LoadResult.Error(HttpException(response))

    }
}