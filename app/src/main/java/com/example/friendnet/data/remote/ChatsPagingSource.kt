package com.example.friendnet.data.remote

//class ChatsPagingSource(
//    private val chatRepository: ChatRepository,
//    private val userId:String
//): PagingSource<Int, ItemChat>() {
//
//    override fun getRefreshKey(state: PagingState<Int, ItemChat>): Int? {
//        val position = state.anchorPosition ?: return null
//        val page  = state.closestPageToPosition(position) ?: return null
//
//        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
//    }
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ItemChat> {
//
//        val page = params.key ?: 1
//        val pageSize = 5
//
//        val response = chatRepository.getAllChats(userId,page,pageSize).map {
//            it.toChatEntity()
//        }
//        return if (response.isNotEmpty()){
//           val chats = response.sortedBy {
//                it.mesTime
//            }
//            val nextKey = if (chats.size < pageSize) null else page + 1
//            val prevKey = if (page == 1) null else page - 1
//            Log.d("MyLog","ChatsPagingSource новый чат $chats")
//            LoadResult.Page(chats,prevKey,nextKey)
//        } else LoadResult.Error(HttpException(response))
//
//    }
//}