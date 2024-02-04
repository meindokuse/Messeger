package com.example.myapplication.reposetory

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

class FirebaseRepository(userId: String) {
    private val storageRef = FirebaseStorage.getInstance().reference
    private val userRef = storageRef.child("users").child(userId)

    fun loadImage(filePath: Uri, fileName: String,
                 success: (Uri) -> Unit,
                 error: () -> Unit)
    {
        val fileRef = userRef.child(fileName)
        fileRef.putFile(filePath)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener {uri->
                    success(uri)
                }
            }
            .addOnFailureListener {
                error()
            }
    }

    fun getImage(fileName: String, success: (Uri) -> Unit, error: () -> Unit) {
        val fileRef = userRef.child(fileName)
        fileRef.downloadUrl
            .addOnSuccessListener { uri ->
                success(uri)
            }
            .addOnFailureListener {
                error()
            }
    }
}