package com.night

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import com.start.StartGroupActivity

class MainActivity : AppCompatActivity() {
    var array = arrayOf("贪狼", "巨门", "禄存", "文曲", "廉贞", "武曲", "破军")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var startLayout = findViewById<StartLayout>(R.id.startLayout)
        for (i in 0 until array.size) {
            var inflater = LayoutInflater.from(this).inflate(R.layout.item_start, null)
            startLayout.addView(inflater)
        }
        startActivity(Intent(this, StartGroupActivity::class.java))
    }
}
