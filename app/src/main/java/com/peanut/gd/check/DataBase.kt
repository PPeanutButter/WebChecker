package com.peanut.gd.check

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * SQL代码参见:
 * http://www.w3school.com.cn/sql/sql_update.asp
 */
class DataBase(context: Context?, name: String?, cursorFactory: SQLiteDatabase.CursorFactory? = null, version: Int = 1) {
    var sqLiteDatabase: SQLiteDatabase? = null

    init {
        val dataBase = Kernel(context, name, cursorFactory, version)
        sqLiteDatabase = dataBase.writableDatabase
    }

    fun execSQL(sql: String?,usage:String) :String{
        return try {
            Log.e(usage, sql?:"")
            sqLiteDatabase!!.execSQL(sql)
            "OK"
        } catch (e: SQLiteException) {
            e.printStackTrace()
            e.localizedMessage?:"Failed"
        }
    }

    fun rawQuery(sql: String?): Cursor {
        Log.i("rawQuery", sql?:"")
        return sqLiteDatabase!!.rawQuery(sql, null)
    }

    public fun close() {
        sqLiteDatabase!!.close()
    }
}

internal class Kernel(context: Context?, name: String?, cursorFactory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, cursorFactory, version) {
    override fun onCreate(db: SQLiteDatabase) {
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
    }

}