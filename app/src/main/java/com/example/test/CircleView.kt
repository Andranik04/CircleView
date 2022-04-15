package com.example.test

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout

class CircleView(context: Context, attrs: AttributeSet?) :
    RelativeLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_circle, this, true)
    }

    /**
     * This method help to manipulate with circleView
     * @param target must be in interval [0, 100]
     */
    fun setTarget(target: Float) {
        assert(target >= 0f || target <= 100f) { "Target can only be in interval [0, 100]" }
        val innerLayer = findViewById<LayerView>(R.id.innerLayer)
        val outerLayer = findViewById<LayerView>(R.id.outerLayer)
        outerLayer.setTarget(target)
        innerLayer.setTarget(target)
    }

}