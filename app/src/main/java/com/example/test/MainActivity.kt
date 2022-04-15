package com.example.test

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val circleView = findViewById<CircleView>(R.id.circleView)
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            circleView.setTarget(30f)
        }, 1000)
    }
}

