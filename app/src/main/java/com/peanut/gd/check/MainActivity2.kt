package com.peanut.gd.check

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import cn.surine.ui_lib.dialog
import cn.surine.ui_lib.setting
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setting(root){
            group {
                sliderItem(title = "间隔(小时)",valueFrom = 1f,valueTo = 48f,initValue = 3f){view, value ->
                    SettingManager.setValue("hours",value.toInt())
                }
            }
        }
    }
}