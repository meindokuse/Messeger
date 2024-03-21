package com.example.myapplication.data.reposetory.chats

//class ChatsLocalReposetoryImpl(
//    private val localReposetoryHelper: LocalReposetoryHelper,
//    private val fileManager: FileManager
//): ChatLocalReposetory {
//    override fun initChats(): List<ItemChat>? {
//       return localReposetoryHelper.GetAllChats()
//    }
//
//    override fun createNewChat(listChats: List<ItemChat>,) {
//        localReposetoryHelper.AddNewChats(listChats)
//        listChats.forEach {
//            fileManager.saveImageToInternalStorage(it.avatar.toUri(),it.avatar)
//        }
//    }
//    override fun updateChat(itemChat: ItemChat) {
//        localReposetoryHelper.updateChat(itemChat)
//    }
//
//}