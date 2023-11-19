package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class LocalReposetory(context: Context):SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {


    companion object{
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "LocalReposeroty.db"
        private const val TABLE_NAME = "profile"

        private const val KEY_ID = "id"
        private const val KEY_FIRSTNAME = "first_name"
        private const val KEY_SECONDNAME = "second_name"
        private const val KEY_AGE = "age"
        private const val KEY_CITY = "city"
        private const val KEY_SCHOOL = "school"
        private const val KEY_TARGET_CLASS = "target_class"


    }


    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_NAME ($KEY_ID INTEGER PRIMARY KEY,$KEY_FIRSTNAME TEXT,$KEY_SECONDNAME TEXT,$KEY_SCHOOL TEXT,$KEY_CITY TEXT,$KEY_AGE TEXT,$KEY_TARGET_CLASS TEXT)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)

    }

    fun addOrChangeProfile(profileInfo: ProfileInfo){
        val db  = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_FIRSTNAME,profileInfo.firstname)
            put(KEY_SECONDNAME,profileInfo.secondname)
            put(KEY_AGE,profileInfo.age)
            put(KEY_CITY,profileInfo.city)
            put(KEY_SCHOOL,profileInfo.scholl)
            put(KEY_TARGET_CLASS,profileInfo.targetClass)
        }
        db.insert(TABLE_NAME,null,values)
        Log.d("MyLog","В бд ")

        db.close()

    }

    fun getAll(): ProfileInfo {
        val db = this.readableDatabase
        val profileInfo: ProfileInfo
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            profileInfo = ProfileInfo(
                cursor.getString(cursor.getColumnIndex(KEY_FIRSTNAME)),
                cursor.getString(cursor.getColumnIndex(KEY_SECONDNAME)),
                cursor.getString(cursor.getColumnIndex(KEY_SCHOOL)),
                cursor.getString(cursor.getColumnIndex(KEY_CITY)),
                cursor.getString(cursor.getColumnIndex(KEY_AGE)),
                cursor.getString(cursor.getColumnIndex(KEY_TARGET_CLASS))
            )
        } else {
            // если курсор пуст, вернем объект с пустыми значениями
            profileInfo = ProfileInfo("", "", "", "", "", "")
        }

        cursor.close()
        db.close()
        return profileInfo
    }

    fun updateProfile(FirstName:String,SecondName:String){
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_FIRSTNAME, FirstName)
            put(KEY_SECONDNAME, SecondName)

        }
        val whereClause = "$KEY_ID = ?"
        val whereArgs = arrayOf("1")
        db.update(TABLE_NAME,values,whereClause,whereArgs)
        db.close()

    }

}