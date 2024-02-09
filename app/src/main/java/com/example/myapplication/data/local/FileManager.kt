package com.example.myapplication.data.source.local

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class FileManager(private val context: Context) {

    fun saveImageToInternalStorage(imageUri: Uri, fileName: String): String {
        val file = File(context.filesDir, fileName)
        try {
            context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                FileOutputStream(file).use { fileOutputStream ->
                    inputStream.copyTo(fileOutputStream)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return fileName
    }

    fun updateImageInInternalStorage(newImageUri: Uri, fileName: String): String {
        // Удаляем старый файл
        deleteImageFromInternalStorage(fileName)
        // Сохраняем новый файл
        return saveImageToInternalStorage(newImageUri, fileName)
    }

    fun deleteImageFromInternalStorage(fileName: String) {
        Log.d("MyLog", "deleteImageFromInternalStorage")
        val file = File(context.filesDir,fileName)
        if (file.exists()) {
            file.delete()
            Log.d("MyLog", "Изображение $fileName успешно удалено.")
        } else {
            Log.d("MyLog", "Изображение $fileName не существует.")
        }
    }

    fun saveBitmapToInternalStorage(bitmap: Bitmap, fileName: String): String {
        val file = File(context.filesDir, "$fileName.jpg")
        try {
            FileOutputStream(file).use { fileOutputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return fileName
    }
}



