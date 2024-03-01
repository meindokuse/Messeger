package com.example.myapplication.data.remote

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseStorage() {

    private val storageRef = FirebaseStorage.getInstance().reference
    private val userImageRef = storageRef.child("users/files")

//    fun loadData(
//        userId: String,
//        filePath: Uri,
//        filename: String
//    ): Boolean {
//        return try {
//            val fileRef = userImageRef.child(userId).child(filename)
//            val task = fileRef.putFile(filePath)
//            task.isSuccessful
//        } catch (e: Exception) {
//            Log.d("MyLog", "loadData firebase $e")
//            false
//        }
//
//    }

    suspend fun loadData(
        userId: String,
        filePath: Uri,
        filename: String
    ): Boolean = suspendCoroutine { continuation ->
        try {
            val fileRef = userImageRef.child(userId).child(filename)
            val uploadTask = fileRef.putFile(filePath)

            uploadTask.addOnSuccessListener { _ ->
                Log.d("MyLog", "Загрузка файла успешно завершена")
                continuation.resume(true) // Возобновить продолжение с результатом успешной загрузки
            }.addOnFailureListener { exception ->
                Log.e("MyLog", "Ошибка загрузки файла: ${exception.message}")
                continuation.resume(false) // Возобновить продолжение с результатом неудачной загрузки
            }
        } catch (e: Exception) {
            Log.e("MyLog", "Ошибка при попытке загрузки файла: ${e.message}")
            continuation.resume(false) // Возобновить продолжение с результатом неудачной загрузки
        }
    }

    suspend fun getData(userId: String, fileName: String): Uri? {
        val fileRef = userImageRef.child(userId).child(fileName)
        return fileRef.downloadUrl.await()

    }
}