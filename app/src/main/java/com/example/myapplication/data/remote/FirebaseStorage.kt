package com.example.myapplication.data.remote

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class FirebaseStorage() {

    private val storageRef = FirebaseStorage.getInstance().reference
    private val userImageRef = storageRef.child("users/files")

   suspend fun loadData(
        userId: String,
        filePath: Uri,
        filename: String
    ): Boolean {
       return try {
           val fileRef = userImageRef.child(userId).child(filename)
           val task = fileRef.putFile(filePath)
            task.isSuccessful
        } catch (e:Exception){
            Log.d("MyLog","loadData firebase $e")
            false
        }

    }

    suspend fun getData(userId: String, fileName: String): Uri? {
        val fileRef = userImageRef.child(userId).child(fileName)
        return fileRef.downloadUrl.await()

    }
}