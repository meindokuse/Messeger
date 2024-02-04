package com.example.myapplication.shared

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class FileManager(val context: Context) {

    fun saveImageToInternalStorage(bitmap: Bitmap, fileName: String): String {
        val file = File(context.filesDir, "$fileName.jpg")
        try {
            FileOutputStream(file).use { fileOutputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file.absolutePath
    }

    fun updateImageInInternalStorage(newBitmap: Bitmap, oldName: String, fileName: String): String {
        // Удаляем старый файл
        deleteImageFromInternalStorage(oldName)
        // Сохраняем новый файл
        return saveImageToInternalStorage(newBitmap, fileName)
    }

    fun deleteImageFromInternalStorage(fileName: String) {
        Log.d("MyLog","deleteImageFromInternalStorage")
        val file = File(fileName)
        if (file.exists()) {
            file.delete()
            Log.d("MyLog", "Изображение $fileName успешно удалено.")
        } else {
            Log.d("MyLog", "Изображение $fileName не существует.")
        }
    }
}