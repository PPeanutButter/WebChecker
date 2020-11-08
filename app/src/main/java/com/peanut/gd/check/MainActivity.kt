package com.peanut.gd.check

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SettingManager.init(this@MainActivity)
        floatingActionButton.setOnClickListener {
            startActivity(Intent(this, AddWebActivity::class.java))
        }
        floatingActionButton.setOnLongClickListener {
            startActivity(Intent(this, MainActivity2::class.java))
            true
        }
        sw.setOnRefreshListener {
            refresh()
            sw.isRefreshing = false
        }
        val hours = SettingManager.getValue("hours",3)
        val workManager = WorkManager.getInstance(this)
        start.setOnClickListener {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val checkRequest = PeriodicWorkRequestBuilder<CheckWorker>(hours.toLong(), TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()
            workManager.enqueue(checkRequest)
            Toast.makeText(this, "查询间隔${hours}小时", Toast.LENGTH_SHORT).show()
            SettingManager.setValue("requestID", checkRequest.id.toString())
            syncButton()
        }
        stop.setOnClickListener {
            workManager.cancelAllWork()
            SettingManager.setValue("requestID", value = "")
            syncButton()
        }
        try {
            workManager.getWorkInfoByIdLiveData(
                UUID.fromString(
                    SettingManager.getValue(
                        "requestID",
                        defaultValue = ""
                    )
                )
            ).observe(this,
                androidx.lifecycle.Observer {
                    refresh()
                })
        }catch (e:Exception){
            //invalid uuid exception
            e.printStackTrace()
        }
        syncButton()
    }

    private fun syncButton(){
        if (SettingManager.getValue("requestID", defaultValue = "") == "") {
            start.isEnabled = true
            stop.isEnabled = false
        }
        else {
            start.isEnabled = false
            stop.isEnabled = true
        }
    }

    @SuppressLint("InflateParams")
    private fun refresh(){
        panel.removeAllViews()
        val sets = SettingManager.getValue("jobs",defaultValue = emptySet<String>())
        for (set in sets){
            val view = LayoutInflater.from(this).inflate(R.layout.card,null)
            view.findViewById<TextView>(R.id.name).text = set
            view.findViewById<TextView>(R.id.url).text = SettingManager.getValue(set+"url",defaultValue = "url")
            view.findViewById<TextView>(R.id.css).text = SettingManager.getValue(set+"css",defaultValue = "url")
            view.findViewById<ConstraintLayout>(R.id.c).setOnLongClickListener {
                SettingManager.setValue(key = "jobs", value = sets.minus(set))
                Snackbar.make(panel, "已取消该监听", Snackbar.LENGTH_LONG).setAction("撤销") {
                    SettingManager.setValue(key = "jobs", value = sets)
                    refresh()
                }.show()
                refresh()
                true
            }
            view.findViewById<ConstraintLayout>(R.id.c).setOnClickListener {
                try {
                    val intent = Intent()
                    intent.action = "android.intent.action.VIEW"
                    val uri = Uri.parse(SettingManager.getValue(set+"url",defaultValue = "url"))
                    intent.data = uri
                    startActivity(intent)
                }catch (e:Exception){
                    Toast.makeText(this,e.localizedMessage,Toast.LENGTH_SHORT).show()
                }
            }
            val time = SettingManager.getValue(set + "Time", defaultValue = "0").toLong()
            val date1 = Date()
            date1.time = time
            view.findViewById<TextView>(R.id.state).text = SimpleDateFormat("HH:mm:ss", Locale.CHINA).format(date1)
            panel.addView(view)
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }
}
