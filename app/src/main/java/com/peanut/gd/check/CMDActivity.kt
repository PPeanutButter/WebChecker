package com.peanut.gd.zdjkxxbz

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : Activity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        li.removeAllViews()
        SettingManager.init(this)
        Settings.init(this)
        val workManager = WorkManager.getInstance(this)
        if (SettingManager.getValue("UUID","") == "") {
            val constraints = Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            val checkRequest = PeriodicWorkRequest.Builder(AutoPostWorker::class.java, 24L, TimeUnit.HOURS)
                    .setConstraints(constraints)
                    .build()
            workManager.enqueue(checkRequest)
            print("Services:" + checkRequest.id.toString() + " starting", Color.RED)
            SettingManager.setValue("UUID", checkRequest.id.toString())
        } else print("Services:" + SettingManager.getValue("UUID","NULL") + " are running", Color.GREEN)
        print("Cookie: JSESSIONID=374fc1c5-b379-4ae3-a5fe-5dfafd653584", Color.GREEN)
        print("requestBody:\n"+"body.json".readText())
        print("=================logs=================",Color.GREEN)
        val rowSet = Settings.dataBase.rawQuery("select startTime,state,data from logs order by startTime desc")
        var i = 0
        while (rowSet.moveToNext()){
            val color = if (rowSet.getString(1).toBoolean()) Color.GREEN else Color.RED
            print("#${rowSet.count - i}")
            print("提交时间:"+SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Date(rowSet.getLong(0))),color)
            print("提交结果:"+rowSet.getString(2),color)
            i++
        }
        li.setOnLongClickListener {
            workManager.cancelAllWork()
            SettingManager.setValue("UUID", "")
            print("clear work success!", Color.RED)
            true
        }
    }

    private fun print(a: String) {
        val b = TextView(this)
        b.text = a
        li.addView(b)
    }

    private fun print(a: String, c: Int) {
        val b = TextView(this)
        b.text = a
        b.setTextColor(c)
        li.addView(b)
    }

    private fun String.readText():String{
        return BufferedReader(InputStreamReader(this@MainActivity.resources.assets.open(this))).readText()
    }
}