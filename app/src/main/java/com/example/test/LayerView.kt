package com.example.test

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import kotlin.math.cos
import kotlin.math.sin

class LayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val FULL_DEGREE = 180f
        const val MOVE_DURATION = 2000L
        const val MAX_VALUE = 100
    }

    enum class PointerType {
        CIRCLE,
        TRIANGLE
    }
    /// Private Variables
    private val rect = RectF()
    private val pointerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pointerBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pathBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val targetDegreePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var currentValue = 0f

    private var isFilled = false
    private var isPointerShow = false
    private var isGradientShow = false

    private var gradientStartColor = color(R.color.orange)
    private var gradientEndColor =  color(R.color.blue)
    private var pointerColor =  color(R.color.orange)
    private var pointerType: PointerType? = PointerType.CIRCLE

    private var strokeWidth = dpToPx(10f)
    private val pointerRadius = dpToPx(13f)
    private val pointerBackgroundRadius = dpToPx(20f)
    private var borderDistance = dpToPx(20f)
    private var circleBorderWidth = dpToPx(5f)
    private var triangleSideWidth = dpToPx(20f)

    private var alpha = 128
    init {
        initAttributeSet(context, attrs)
    }


    private fun initAttributeSet(context: Context, attrs: AttributeSet?) {
        attrs?.let {
            val typedArray =
                context.theme.obtainStyledAttributes(it, R.styleable.PointerSpeedometer, 0, 0)
            isPointerShow =
                typedArray.getBoolean(R.styleable.PointerSpeedometer_withPointer, isPointerShow)
            isFilled =
                typedArray.getBoolean(R.styleable.PointerSpeedometer_withValue, isFilled)
            pointerType =
                PointerType.values()[typedArray.getInt(
                    R.styleable.PointerSpeedometer_pointerType,
                    0
                )]
            pointerColor =
                typedArray.getColor(R.styleable.PointerSpeedometer_pointerColor, pointerColor)
            isGradientShow =
                typedArray.getBoolean(R.styleable.PointerSpeedometer_withGradient, isGradientShow)
            gradientStartColor = typedArray.getColor(
                R.styleable.PointerSpeedometer_gradientStartColor,
                gradientStartColor
            )
            gradientEndColor = typedArray.getColor(
                R.styleable.PointerSpeedometer_gradientEndColor,
                gradientEndColor
            )
            strokeWidth =
                typedArray.getDimension(R.styleable.PointerSpeedometer_strokeWidth, strokeWidth)
        }
    }

    fun setTarget(target: Float) {
        ValueAnimator.ofFloat(0f, target).apply {
            interpolator = DecelerateInterpolator()
            duration = MOVE_DURATION
            addUpdateListener { animation ->
                val currentValue = animation.animatedValue as Float
                this@LayerView.currentValue = -(currentValue / MAX_VALUE) * FULL_DEGREE
                postInvalidate()
            }
        }?.also {
            it.start()
        }
    }

    override fun onSizeChanged(xNew: Int, yNew: Int, xOld: Int, yOld: Int) {
        super.onSizeChanged(xNew, yNew, xOld, yOld)
        prepare()
    }

    private fun prepare(){
        val gradient = LinearGradient(
            0f,
            0f,
            width.toFloat(),
            width.toFloat() / 2,
            gradientStartColor,
            gradientEndColor,
            Shader.TileMode.CLAMP
        )
        initPathBackgroundPaint(gradient)
        if (isFilled)
            initTargetDegreePaint(gradient)
        if (isPointerShow)
            initPointerPaint()
    }

    private fun initTargetDegreePaint(gradient: LinearGradient) {
        targetDegreePaint.run {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.BUTT
            strokeWidth = this@LayerView.strokeWidth
            shader = gradient
        }
    }

    private fun initPathBackgroundPaint(gradient: LinearGradient) {
        pathBackgroundPaint.run {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.BUTT
            strokeWidth = this@LayerView.strokeWidth
            alpha = this@LayerView.alpha
            if (isGradientShow)
                shader = gradient
            else
                color = color(R.color.gray)
        }
    }

    private fun initPointerPaint() {
        pointerPaint.color = pointerColor
        when (pointerType) {
            PointerType.CIRCLE -> {
                pointerPaint.run {
                    style = Paint.Style.STROKE
                    strokeWidth = circleBorderWidth
                }
                pointerBackgroundPaint.color = color(android.R.color.white)
            }
            PointerType.TRIANGLE -> {
                pointerPaint.run {
                    style = Paint.Style.FILL
                }
            }
            null -> {
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        rect.set(borderDistance, borderDistance, width.toFloat() - borderDistance, width.toFloat() - borderDistance)
        canvas.run {
            // Print background
            drawArc(rect, -FULL_DEGREE, FULL_DEGREE, false, pathBackgroundPaint)

            // Fill Path
            if (isFilled){
                drawArc(rect, 0f, currentValue, false, targetDegreePaint)
            }

            // Draw Point
            if (isPointerShow) {
                drawPointer(this)
            }
        }
    }

    private fun drawPointer(canvas: Canvas){
        val alpha = -currentValue + FULL_DEGREE / 2
        val radian = (alpha * Math.PI / FULL_DEGREE).toFloat()
        val r = rect.width() / 2
        val x = sin(radian) * r + rect.width() / 2 + borderDistance
        val y = cos(radian) * r + rect.height() / 2 + borderDistance
        when (pointerType) {
            PointerType.CIRCLE -> {
                canvas.drawCircle(x, y, pointerBackgroundRadius, pointerBackgroundPaint)
                canvas.drawCircle(x, y, pointerRadius, pointerPaint)
            }
            PointerType.TRIANGLE -> {
                drawTriangle(
                    canvas,
                    FULL_DEGREE - alpha,
                    x,
                    y - triangleSideWidth / 2,
                    triangleSideWidth.toInt()
                )
            }
            null -> {}
        }
    }

    private fun drawTriangle(canvas: Canvas, alpha: Float, x: Float, y: Float, width: Int) {
        val halfWidth = width / 2
        Path().apply {
            moveTo(x, (y - halfWidth))
            lineTo(x - halfWidth, y + halfWidth)
            lineTo(x + halfWidth, y + halfWidth)
            lineTo(x, y - halfWidth)
            close()
        }.also {
            canvas.rotate(alpha, x, y)
            canvas.drawPath(it, pointerPaint)
        }
    }

}

private fun LayerView.dpToPx(dp: Float): Float = dp * context.resources.displayMetrics.density

private fun LayerView.color(value: Int): Int = ContextCompat.getColor(context, value)