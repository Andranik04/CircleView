package com.example.test

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val circleView = findViewById<CircleView>(R.id.circleView)
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            circleView.setTarget(0f)
        }, 1000)
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            circleView.setTarget(100f)
        }
    }
}

