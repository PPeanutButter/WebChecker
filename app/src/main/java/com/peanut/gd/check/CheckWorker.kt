package com.peanut.gd.check

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.jsoup.Jsoup

class CheckWorker(private val context: Context, workerParams: WorkerParameters) :Worker(context, workerParams) {
    override fun doWork(): Result {
        val sets = SettingManager.getValue("jobs",defaultValue = emptySet<String>())
        if (sets.isEmpty())return Result.success()
        for (name in sets){
            try {
                val messages = StringBuilder()
                val url = SettingManager.getValue(name + "url", "")
                val css = SettingManager.getValue(name + "css", "")
                val attrKey = SettingManager.getValue(name + "attrKey", "")
                val attr = SettingManager.getValue(name + "attr", false)
                val doc = Jsoup.connect(url).get()
                val list = doc.select(css)
                var mapNew = emptySet<String>()
                val mapOld = SettingManager.getValue(
                    key = name + "list",
                    defaultValue = emptySet<String>()
                )
                for (j in list.indices) {
                    mapNew = if (attr)
                        mapNew.plus(list.elementAt(j).attr(attrKey))
                    else
                        mapNew.plus(list.elementAt(j).text())
                }
                val mapDiff = mapNew.minus(mapOld)
                for (j in mapDiff.indices) {
                    messages.append(mapDiff.elementAt(j))
                    if (j != mapDiff.size - 1)
                        messages.append("\n")
                    else sendSimpleNotification(title = name, message = messages.toString())
                }
                SettingManager.setValue(key = name + "list", value = mapNew)
                SettingManager.setValue(name + "Time", System.currentTimeMillis().toString())
            }catch (e:Exception) {
                e.printStackTrace()
            }
        }
        return Result.success()
    }

    private fun sendSimpleNotification(title: String, message: String) {
        val notificationManager = context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                "AppTestNotificationId",
                "网页监听通知",
                NotificationManager.IMPORTANCE_HIGH
            )
            )
        }
        val builder = NotificationCompat.Builder(context, "AppTestNotificationId")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                .bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        notificationManager.notify((Math.random()*100000).toInt(), builder.build())
    }
}