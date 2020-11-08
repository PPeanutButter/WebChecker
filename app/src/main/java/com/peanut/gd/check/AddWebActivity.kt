package com.peanut.gd.check

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_web.*
import org.jsoup.Jsoup

class AddWebActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "添加网页"
        setContentView(R.layout.activity_add_web)
    }

    fun test(view: View) {
        logcat.removeAllViews()
        val url = input1.editableText.toString()
        val css = input2.editableText.toString()
        val attr = checkBox.isChecked
        val attrKey = editText3.editableText.toString()
        object : Thread() {
            override fun run() {
                try {
                    val doc = Jsoup.connect(url).get()
                    val elements = doc.select(css)
                    Handler(this@AddWebActivity.mainLooper).post {
                        val textView = TextView(this@AddWebActivity)
                        textView.text = "下面是选择的标签(测试用)："
                        textView.setTextColor(Color.RED)
                        logcat.addView(textView)
                        val textView1 = TextView(this@AddWebActivity)
                        textView1.text = elements.toString()
                        logcat.addView(textView1)
                    }
                    Handler(this@AddWebActivity.mainLooper).post {
                        val textView = TextView(this@AddWebActivity)
                        textView.text = "下面是通知的内容：==============="
                        textView.setTextColor(Color.RED)
                        logcat.addView(textView)
                    }
                    for (i in elements.indices) {
                        if (attr) {
                            Log.v("a", elements[i].attr(attrKey))
                            Handler(this@AddWebActivity.mainLooper).post {
                                val textView = TextView(this@AddWebActivity)
                                textView.text = elements[i].attr(attrKey)
                                logcat.addView(textView)
                            }
                        } else {
                            Log.v("a", elements[i].text())
                            Handler(this@AddWebActivity.mainLooper).post {
                                val textView = TextView(this@AddWebActivity)
                                textView.text = elements[i].text()
                                logcat.addView(textView)
                            }
                        }
                    }
                }catch (e:Exception){
                    Handler(this@AddWebActivity.mainLooper).post {
                        val textView = TextView(this@AddWebActivity)
                        textView.text = e.localizedMessage
                        logcat.addView(textView)
                    }
                }
            }
        }.start()
    }

    fun add(view: View) {
        val url = input1.editableText.toString()
        val css = input2.editableText.toString()
        val attr = checkBox.isChecked
        val attrKey = editText3.editableText.toString()
        val name = editText4.editableText.toString()
        SettingManager.setValue("jobs",SettingManager.getValue("jobs", emptySet<String>()).plus(name))
        SettingManager.setValue(name+"url",url)
        SettingManager.setValue(name+"css",css)
        SettingManager.setValue(name+"attr",attr)
        SettingManager.setValue(name+"attrKey",attrKey)
        this.finish()
    }
}
