package com.night

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    var array = arrayOf("贪狼", "巨门", "禄存", "文曲", "廉贞", "武曲", "破军")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       var  huaban =findViewById<StartLayout>(R.id.huaban)
        for (i in 0 until array.size) {
            var inflater = LayoutInflater.from(this).inflate(R.layout.item_start, null)
            var tv = inflater.findViewById<TextView>(R.id.tv_name)
            tv.text = array[i]
            huaban.addView(inflater)
        }
    }
}
