package com.example.friendnet.data.local.storage

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.friendnet.util.AudioRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class FileManager(private val context: Context) {

    suspend fun saveImageToInternalStorage(imageUri: Uri, fileName: String): String {
        withContext(Dispatchers.IO){
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
        }
        return fileName
    }

    suspend fun updateImageInInternalStorage(newImageUri: Uri, fileName: String): String {
        // Удаляем старый файл
        deleteFileFromInternalStorage(fileName)
        // Сохраняем новый файл
        return saveImageToInternalStorage(newImageUri, fileName)
    }

    fun deleteFileFromInternalStorage(fileName: String) {
        Log.d("MyLog", "deleteImageFromInternalStorage")
        val file = File(context.filesDir,fileName)
        if (file.exists()) {
            file.delete()
            Log.d("MyLog", "Изображение $fileName успешно удалено.")
        } else {
            Log.d("MyLog", "Изображение $fileName не существует.")
        }
    }

//    fun getFilePath(fileName:String):Uri?{
//        val file = File(context.filesDir,fileName)
//        if(file.exists()) file.absolutePath
//    }


    private val audioRecorder = AudioRecorder(context)

    fun startRecordAudio():File?{
        return audioRecorder.startRecording()
    }

    fun stopAudioRecord():Boolean{
       return audioRecorder.stopRecording()
    }

    fun clearAudioRecorder(){
        audioRecorder.clearRecordingFull()
    }

}



