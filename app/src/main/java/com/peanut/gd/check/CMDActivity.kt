package com.peanut.gd.check

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_cmd.*

open class CMDActivity : Activity(){
    private var onTextInputListener:TextInputListener?=null
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cmd)
        editTextTextMultiLine.requestFocus()
        editTextTextMultiLine.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onTextInputListener?.onInput(editTextTextMultiLine.text.toString())
                editTextTextMultiLine.text.clear()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    protected fun CMDActivity.printV(a: String) = print(a, Color.WHITE)
    protected fun CMDActivity.printI(a: String) = print(a, Color.GREEN)
    protected fun CMDActivity.printE(a: String) = print(a, Color.RED)
    protected fun CMDActivity.printW(a: String) = print(a, Color.YELLOW)
    protected fun CMDActivity.printD(a: String) = print(a, Color.CYAN)

    protected fun registerInputListener(func: (String) -> Unit){
        this.onTextInputListener = object :TextInputListener{
            override fun onInput(s: String) {
                func.invoke(s)
            }
        }
    }

    private fun print(a: String, c: Int) {
        li.addView(TextView(this).apply {
            this.text = a
            this.textSize = 12f
            this.setTextColor(c)
            this.setTextIsSelectable(true)
            this.typeface = Typeface.createFromAsset(this@CMDActivity.assets,"ubuntu_mono.ttf")
        }, li.childCount - 1)
    }

    fun String.toFixedLengthString(l:Int?):String{
        if (l == null)
            return this
        return if (this.length>l)
            this.substring(0,l-3)+"..."
        else {
            this+" ".repeat(l-this.length)
        }
    }
}

interface TextInputListener{
    fun onInput(s: String)
}