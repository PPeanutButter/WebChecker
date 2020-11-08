package com.peanut.gd.check

import android.content.Context
import com.peanut.gd.check.AddInFuns.execAssetsSql

object Settings {
    lateinit var dataBase: DataBase

    fun init(context:Context){
        dataBase = DataBase(context,"data.db")
        "init.sql".execAssetsSql(dataBase.sqLiteDatabase,context)
    }
}