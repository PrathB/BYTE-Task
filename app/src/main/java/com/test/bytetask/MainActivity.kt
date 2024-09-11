package com.test.bytetask

import android.graphics.BlurMaskFilter
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val byteLogo: TextView = findViewById(R.id.byte_logo)
        byteLogo.setShadowLayer(
            15f,
            0f,
            0f,
            Color.parseColor("#66FF00")
        )
    }
}