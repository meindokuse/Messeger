package com.example.myapplication.data.remote

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class FirebaseStorage() {

    private val storageRef = FirebaseStorage.getInstance().reference
    private val userImageRef = storageRef.child("users/files")

     fun loadData(
         userId: String,
        filePath: Uri,
        filename: String
    ): Boolean {
        val fileRef = userImageRef.child(userId).child(filename)
        val task = fileRef.putFile(filePath)
         return task.isSuccessful

    }

     suspend fun getData(userId: String,fileName: String): Uri? {
        val fileRef = userImageRef.child(userId).child(fileName)
        return fileRef.downloadUrl.await()

    }
}