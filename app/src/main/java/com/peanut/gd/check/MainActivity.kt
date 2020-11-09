package com.peanut.gd.check

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : CMDActivity() {
    private val workManager = WorkManager.getInstance(this)

    private fun populateCMD(cmd: String) {
        printV("$ $cmd")
        val cmds = cmd.split(" ")
        try {
            when (cmds[0].toLowerCase(Locale.CHINA)) {
                "a", "add" -> a()
                "i", "interval" -> i(cmds[1])
                "r", "restart" -> r()
                "re", "remove" -> re(cmds[1].toInt())
                "l", "list" -> l()
                "s", "shutdown" -> s()
                "st", "start" -> st()
                "o", "open" -> o(cmds[1])
                else -> printE("Unknown Command:${cmds[0]}")
            }
        } catch (e: Exception) {
            printE("Error->" + e.localizedMessage)
        }
    }

    private fun a() = startActivity(Intent(this, AddWebActivity::class.java))

    private fun i(a: String) {
        SettingManager.setValue("hours", a.toInt())
        printI("Interval is ${a}h now.")
        printW("However, set interval require restart service.")
    }

    private fun r() {
        s()
        st()
    }

    private fun s() {
        workManager.cancelAllWork()
        SettingManager.setValue("requestID", value = "")
    }

    private fun st() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val checkRequest = PeriodicWorkRequestBuilder<CheckWorker>(
            SettingManager.getValue("hours", 6).toLong(),
            TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()
        workManager.enqueue(checkRequest)
        SettingManager.setValue("requestID", checkRequest.id.toString())
    }

    private fun re(id: Int) {
        printI(Settings.dataBase.execSQL("DELETE FROM tasks WHERE id=$id;", "删除任务"))
    }

    private fun l() {
        val cursor =
            Settings.dataBase.rawQuery("select tasks.id,task.name,tasks.MD5 from tasks,task where tasks.id=task.id")
        printI("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
        printI("|ID   |Name                           |MD5              |")
        printI("|-------------------------------------------------------|")
        while (cursor.moveToNext())
            printI(
                "|${cursor.getInt(0).toString().toFixedLengthString(5)}|${
                    cursor.getString(1).toFixedLengthString(31)
                }|${cursor.getString(2).toFixedLengthString(17)}|"
            )
        printI("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
    }

    private fun o(id:String){
        try {
            val url = Settings.dataBase.rawQuery("select url from task where task.id=$id").apply { this.moveToNext() }.getString(0)
            val intent = Intent()
            intent.action = "android.intent.action.VIEW"
            val uri = Uri.parse(url)
            intent.data = uri
            startActivity(intent)
        }catch (e:Exception){
            printE("open error->"+e.localizedMessage)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SettingManager.init(this)
        Settings.init(this)
        printHelp()
        registerInputListener {
            populateCMD(it)
        }
    }

    private fun printHelp() {
        printV("Usage:")
        printV("    <command> <parameters>")
        printV("")
        printV("Commands:")
        printV("    a,add           Add a new Tasks.")
        printV("    i,interval      Set interval of Task(Hours,Integer).")
        printV("    l,list          Lists current queue tasks.")
        printV("    r,restart       Restart periodic check-service.")
        printV("    re,remove       Remove a old Tasks.")
        printV("    o,open          Open a task`s url.")
        printV("    s,shutdown      Shutdown periodic check-service.")
        printV("    st,start        Start periodic check-service.")
        printV("")
    }
}
