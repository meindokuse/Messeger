package com.example.friendnet.data.remote

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseStorage() {

    private val storageRef = FirebaseStorage.getInstance().reference
    private val userImageRef = storageRef.child("users/files")


    suspend fun loadData(
        userId: String,
        filePath: Uri,
        filename: String
    ): Boolean = suspendCoroutine { continuation ->
        try {
            val fileRef = userImageRef.child(userId).child(filename)
            val uploadTask = fileRef.putFile(filePath)
            Log.d("MyLog","загрузка файла начата")

            uploadTask.addOnSuccessListener { _ ->
                Log.d("MyLog", "Загрузка файла успешно завершена")
                continuation.resume(true) // Возобновить продолжение с результатом успешной загрузки
            }.addOnFailureListener { exception ->
                Log.e("MyLog", "Ошибка загрузки файла: ${exception.message}")
                continuation.resume(false) // Возобновить продолжение с результатом неудачной загрузки
            }.addOnProgressListener {taskSnapshot->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                Log.d("MyLog","loadProgress $progress")
            }
        } catch (e: Exception) {
            Log.e("MyLog", "Ошибка при попытке загрузки файла: ${e.message}")
            continuation.resume(false) // Возобновить продолжение с результатом неудачной загрузки
        }
    }

    suspend fun getData(userId: String, fileName: String): Uri? {
       return try {
            val fileRef = userImageRef.child(userId).child(fileName)
             fileRef.downloadUrl.await()
        } catch (e:Exception) {
           Log.e("MyLog", "Ошибка при попытке файла : ${e.message}")
           null
        }


    }
}