package ru.mertsalovda.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class DrawingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val shapes = mutableListOf<Shape>()
    private val mPaint: Paint
    private val isDrawing = false

    init {
        mPaint = Paint().apply {
            strokeWidth = 20f
            color = Color.BLACK
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.STROKE
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measuredWidth = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val measuredHeight = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        if (shapes.isNotEmpty()) {
            shapes.map {
                for (i in 1 until it.coordinatesX.size) {
                    canvas.drawLine(
                        it.coordinatesX[i - 1],
                        it.coordinatesY[i - 1],
                        it.coordinatesX[i],
                        it.coordinatesY[i],
                        it.paint
                    )
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> shapes.add(Shape(mPaint))
            MotionEvent.ACTION_MOVE -> {
                val shape = shapes.last()
                shape.addCoordinate(event.rawX, event.rawY - 125)
                invalidate()
            }
            MotionEvent.ACTION_UP -> invalidate()
            else -> return false
        }

        return true
    }


    fun setPaint(p: Paint) {
        mPaint.set(p)
    }

    fun clear() {
        shapes.clear()
        invalidate()
    }

    fun removeLast() {
        if (shapes.isNotEmpty()) {
            shapes.removeAt(shapes.size - 1)
            invalidate()
        }
    }

    private class Shape(val paint: Paint) {
        val coordinatesX = mutableListOf<Float>()
        val coordinatesY = mutableListOf<Float>()

        fun addCoordinate(x: Float, y: Float) {
            coordinatesX.add(x)
            coordinatesY.add(y)
        }
    }
}