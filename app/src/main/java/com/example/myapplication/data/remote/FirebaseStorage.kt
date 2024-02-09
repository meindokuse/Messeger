package com.example.myapplication.data.remote

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class FirebaseStorageForImage(userId: String) :
    com.example.myapplication.domain.reposetory.FirebaseStorage {

    private val storageRef = FirebaseStorage.getInstance().reference
    private val userImageRef = storageRef.child("users/images").child(userId)

    override suspend fun loadData(
        filePath: Uri?,
        filename: String
    ): Boolean {
        val fileRef = userImageRef.child(filename)
        val task = fileRef.putFile(filePath!!)
        return task.isSuccessful

    }

    override suspend fun getData(fileName: String): Uri? {
        val fileRef = userImageRef.child(fileName)
        return fileRef.downloadUrl.await()

    }
}