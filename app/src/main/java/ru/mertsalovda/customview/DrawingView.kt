package ru.mertsalovda.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val mLines = mutableListOf<Line>()
    private val mPaint: Paint

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
        if (mLines.isNotEmpty()) {
            mLines.map {
                for (i in 1 until it.coordinates.size) {
                    canvas.drawLine(
                        it.coordinates[i - 1].first,
                        it.coordinates[i - 1].second,
                        it.coordinates[i].first,
                        it.coordinates[i].second,
                        it.paint
                    )
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mLines.add(Line(Paint(mPaint)))
            }
            MotionEvent.ACTION_MOVE -> {
                val shape = mLines.last()
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
        mLines.clear()
        invalidate()
    }

    fun removeLast() {
        if (mLines.isNotEmpty()) {
            mLines.removeAt(mLines.size - 1)
            invalidate()
        }
    }

    private class Line(val paint: Paint) {
        val coordinates = mutableListOf<Pair<Float, Float>>()

        fun addCoordinate(x: Float, y: Float) {
            coordinates.add(Pair(x, y))
        }
    }
}