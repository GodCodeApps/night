package com.start

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import com.night.R
import kotlinx.android.synthetic.main.activity_start_group.*

class StartGroupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_group)
        val list = arrayListOf<View>()
        for (index in 1..10) {
            val imageView = ImageView(this)
            imageView.setImageResource(R.mipmap.bg_star_head)
            list.add(imageView)
        }
        starGroup.upRoundView(list.subList(0, 3))
        starGroup.upView(iv_sun_small, list.subList(3, 6), 3.3f, -12f, 75f)
        starGroup.upView(iv_sun_big, list.subList(6, 9), 3.1f, -25f, 125f)
    }
}