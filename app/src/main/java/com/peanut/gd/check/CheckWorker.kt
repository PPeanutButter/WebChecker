package com.peanut.gd.check

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.peanut.gd.check.AddInFuns.insertTo
import org.jsoup.Jsoup
import java.lang.StringBuilder

class CheckWorker(private val context: Context, workerParams: WorkerParameters) :Worker(context, workerParams) {
    override fun doWork(): Result {
        try {
            val cursor =
                Settings.dataBase.rawQuery("select * from tasks,task where tasks.id=task.id")
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val url = cursor.getString(cursor.getColumnIndex("url"))
                val css = cursor.getString(cursor.getColumnIndex("css"))
                val attr = cursor.getString(cursor.getColumnIndex("attr"))
                val doc = Jsoup.connect(url).get()
                val list = doc.select(css)
                var mapOld = emptySet<String>()
                try {
                    mapOld = mapOld.plus(
                        Settings.dataBase.rawQuery("select item from lists where id=$id")
                            .apply { this.moveToFirst() }.getString(0).split("@#_#@")
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                //填充list
                var mapNew = emptySet<String>()
                for (j in list.indices) {
                    mapNew = if (attr != null)
                        mapNew.plus(list.elementAt(j).attr(attr))
                    else
                        mapNew.plus(list.elementAt(j).text())
                }
                val mapDiff = mapNew.minus(mapOld)
                val messages = StringBuilder()
                for (j in mapDiff.indices) {
                    messages.append(mapDiff.elementAt(j))
                    if (j != mapDiff.size - 1)
                        messages.append("\n")
                    else sendSimpleNotification(title = name, message = messages.toString())
                }
                ContentValues().apply {
                    Settings.dataBase.execSQL("delete from lists where id=$id", "更新列表")
                    this.put("id", id)
                    this.put("item", mapNew.joinToString("@#_#@"))
                    this.put("timeMillis", System.currentTimeMillis())
                }.insertTo("lists")
            }
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }

    private fun sendSimpleNotification(title: String, message: String) {
        val notificationManager =
            context.getSystemService(Activity.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            NotificationChannel(
                "AppTestNotificationId",
                "爷爷,你追的剧更新了~",
                NotificationManager.IMPORTANCE_HIGH
            )
        )
        val builder = NotificationCompat.Builder(context, "AppTestNotificationId")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        notificationManager.notify((Math.random() * 100000).toInt(), builder.build())
    }
}