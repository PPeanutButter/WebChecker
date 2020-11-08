package com.peanut.gd.check

import android.content.Context
import android.content.SharedPreferences

object SettingManager {
    private var sharedPreferences: SharedPreferences? = null

    fun init(context: Context) {
        if (null == sharedPreferences)
            sharedPreferences = context.getSharedPreferences(
                context.packageName + "_preferences",
                Context.MODE_PRIVATE
            )
    }

    fun <T> getValue(key: String, defaultValue: T): T {
        @Suppress("UNCHECKED_CAST")
        (return when (defaultValue) {
            is Boolean -> sharedPreferences!!.getBoolean(key, defaultValue) as T
            is String -> sharedPreferences!!.getString(key, defaultValue) as T
            is Int -> sharedPreferences!!.getInt(key, defaultValue) as T
            is Float -> sharedPreferences!!.getFloat(key, defaultValue) as T
            is Long -> sharedPreferences!!.getLong(key, defaultValue) as T
            else -> sharedPreferences!!.getStringSet(key, defaultValue as Set<String>) as T
        })
    }

    fun <T> setValue(key: String, value: T) {
        @Suppress("UNCHECKED_CAST")
        when (value) {
            is Boolean -> sharedPreferences?.edit()?.putBoolean(key, value)?.apply()
            is String -> sharedPreferences?.edit()?.putString(key, value)?.apply()
            is Int -> sharedPreferences?.edit()?.putInt(key, value)?.apply()
            is Float -> sharedPreferences?.edit()?.putFloat(key, value)?.apply()
            is Long -> sharedPreferences?.edit()?.putLong(key, value)?.apply()
            else -> sharedPreferences?.edit()?.putStringSet(key, value as Set<String>)?.apply()
        }
    }
}