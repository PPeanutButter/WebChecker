package com.peanut.gd.check

import android.content.*
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.security.MessageDigest
import java.util.*

object AddInFuns {
    fun String.md5() = MessageDigest.getInstance("MD5").digest(this.toByteArray()).hex()
    private fun ByteArray.hex() = joinToString("") { "%02X".format(it) }
    fun String.MD5() = this.md5().toUpperCase(Locale.CHINA)
    fun String.v(tag: String = "auto msg") = Log.v(tag, this)
    fun String.i(tag: String = "auto msg") = Log.i(tag, this)
    fun String.e(tag: String = "auto msg") = Log.e(tag, this)
    fun String.w(tag: String = "auto msg") = Log.w(tag, this)

    fun Pair<String,String>.v() = this.first.v(this.second)
    fun Pair<String,String>.i() = this.first.i(this.second)
    fun Pair<String,String>.e() = this.first.e(this.second)
    fun Pair<String,String>.w() = this.first.w(this.second)

    fun String.execAssetsSql(db: SQLiteDatabase?, context: Context) {
        try {
            val sqls =
                BufferedReader(InputStreamReader(context.resources.assets.open(this))).readLines()
            for (sql in sqls) {
                if (sql.startsWith("--")) continue
                sql.i("database update")
                try {
                    db?.execSQL(sql)
                } catch (e: SQLiteException) {
                    e.localizedMessage?.e("sql error")
                }
            }
        } catch (e: Exception) {
            e.localizedMessage?.e("file error")
        }
    }

    fun ContentValues.insertTo(table: String, database: DataBase? = Settings.dataBase) =
        database?.sqLiteDatabase?.insert(table, null, this)

}