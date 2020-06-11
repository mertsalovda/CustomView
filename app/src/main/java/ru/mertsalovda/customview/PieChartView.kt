package ru.mertsalovda.customview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.acos
import kotlin.math.sqrt

class PieChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mCenterX = 0f
    private var mCenterY = 0f
    private val mMainTextPaint: Paint
    private val mSecondTextPaint: Paint
    private val mInnerCirclePaint: Paint

    private var mInnerRadius: Float = -1f
    private var mSectorRadius = -1f
    private var mSelectedRadius = -1f
    private var mTotalRadius = -1f

    private var mSelectedIndex = -1
    private var mOldSelectedIndex = -1

    private var mAllValuesSum: Float = 0f

    private val mStandartBounds: RectF
    private val mSelectedBounds: RectF
    private val mTotalBounds: RectF
    private val mMainTextBounds: Rect

    private val mBoundsToUp: RectF
    private val mBoundsToDown: RectF

    private val mGestureDetector: GestureDetector
    private val mSizeAnimator: ValueAnimator

    private val mSectors: MutableList<Sector>

    init {
        mMainTextPaint = Paint().apply {
            color = Color.BLACK
            textSize = resources.getDimensionPixelSize(R.dimen.large_text).toFloat()
            style = Paint.Style.FILL_AND_STROKE
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        mSecondTextPaint = Paint(mMainTextPaint).apply {
            textSize = resources.getDimensionPixelSize(R.dimen.medium_text).toFloat()
            color = Color.GRAY
        }
        mInnerCirclePaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        mSectors = mutableListOf(
            Sector("Facebook", 32.5f, Color.MAGENTA),
            Sector("Google", 25f, Color.CYAN),
            Sector("Youtube", 15f, Color.YELLOW),
            Sector("Dropbox", 14f, Color.BLUE),
            Sector("Other", 12.5f, Color.GREEN)
        )

        for (sector in mSectors) {
            mAllValuesSum += sector.value
        }
        for (sector in mSectors) {
            sector.calculate(mAllValuesSum)
        }

        mStandartBounds = RectF()
        mSelectedBounds = RectF()
        mTotalBounds = RectF()
        mMainTextBounds = Rect()

        mBoundsToUp = RectF()
        mBoundsToDown = RectF()

        mGestureDetector = GestureDetector(context, object :
            GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent?) = true

            override fun onSingleTapConfirmed(e: MotionEvent?) = true
        })

        mSizeAnimator = ValueAnimator().apply {
            duration = 300L
            addUpdateListener {
                val value = it.animatedValue as Float
                mBoundsToDown.set(
                    mSelectedBounds.left + value, mSelectedBounds.top + value,
                    mSelectedBounds.right - value, mSelectedBounds.bottom - value
                )
                mBoundsToUp.set(
                    mStandartBounds.left - value, mStandartBounds.top - value,
                    mStandartBounds.right + value, mStandartBounds.bottom + value
                )
                invalidate()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//      x радиус внутреннего круга | x*1,5 ширина сектора | x*1.75 выделенный сектор | x*2 view с отступами
        mInnerRadius = mMainTextPaint.measureText("100.0%")
        mMainTextPaint.getTextBounds("A", 0, 1, mMainTextBounds)

        mSectorRadius = mInnerRadius * 1.5f
        mSelectedRadius = mInnerRadius * 1.75f
        mTotalRadius = mInnerRadius * 2f

        val desiredDiameter = mInnerRadius * 4

        val measuredWidth = resolveSize(desiredDiameter.toInt(), widthMeasureSpec)
        val measuredHeight = resolveSize(desiredDiameter.toInt(), heightMeasureSpec)
        mCenterX = measuredWidth / 2f
        mCenterY = measuredHeight / 2f

        mTotalBounds.set(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
        mStandartBounds.set(mTotalBounds)
        mStandartBounds.inset(mInnerRadius * 0.5f, mInnerRadius * 0.5f)

        mSelectedBounds.set(mTotalBounds)
        mSelectedBounds.inset(mInnerRadius * 0.25f, mInnerRadius * 0.25f)

        mSizeAnimator.setFloatValues(0f, mInnerRadius * 0.25f)

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        val cx = canvas.width / 2f
        val cy = canvas.height / 2f

        canvas.save()
        canvas.rotate(-90f, cx, cy)

        var startAngle = 0f
        var drawBound: RectF
        for (i in mSectors.indices) {
            drawBound = if (i == mSelectedIndex)
                mBoundsToUp
            else if (i == mOldSelectedIndex)
                mBoundsToDown
            else
                mStandartBounds
            startAngle = mSectors[i].draw(canvas, drawBound, startAngle)
        }

        canvas.apply {
            drawCircle(cx, cy, mInnerRadius, mInnerCirclePaint)
            restore()

            if (mSelectedIndex != -1) {
                val sector = mSectors[mSelectedIndex]
                drawText("${sector.value}%", cx, cy, mMainTextPaint)
                drawText(
                    sector.name, cx, cy + mMainTextBounds.height(), mSecondTextPaint
                )
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return mGestureDetector.onTouchEvent(event) && checkSelection(event)
    }

    private fun checkSelection(event: MotionEvent?): Boolean {
        val tappedAngle = getAngle(event!!.x, event.y)
        for ((i, sector) in mSectors.withIndex()) {
            if (i != mSelectedIndex && sector.isAngleIsSector(tappedAngle)) {
                mOldSelectedIndex = mSelectedIndex
                mSelectedIndex = i
                mSizeAnimator.start()
                return true
            }
        }
        return false
    }

    private fun getAngle(touchX: Float, touchY: Float): Float {
        val x2 = touchX - mCenterX
        val y2 = touchY - mCenterY
        val d1 = sqrt((mCenterY * mCenterY).toDouble())
        val d2 = sqrt((x2 * x2 + y2 * y2).toDouble())
        return if (touchX >= mCenterX) {
            Math.toDegrees(acos((-mCenterY * y2) / (d1 * d2))).toFloat()
        } else {
            (360 - Math.toDegrees(acos((-mCenterY * y2) / (d1 * d2)))).toFloat()
        }
    }

    private class Sector constructor(
        var name: String,
        var value: Float,
        var color: Int
    ) {

        private var mPaint: Paint
        private var mAngle: Float = 0f
        private var mStartAngle: Float = 0f
        private var mEndAngle: Float = 0f

        private var mPercent: Float = 0f

        init {
            mPaint = Paint().apply {
                style = Paint.Style.FILL
                isAntiAlias = true
                color = this@Sector.color
            }
        }

        fun draw(canvas: Canvas, bounds: RectF, startAngle: Float): Float {
            mStartAngle = startAngle
            mEndAngle = startAngle + mAngle
            canvas.drawArc(bounds, startAngle, mAngle, true, mPaint)
            return mEndAngle
        }

        fun calculate(allValuesSum: Float) {
            mAngle = value / allValuesSum * 360f
            mPercent = value / allValuesSum * 100f
        }

        fun isAngleIsSector(angle: Float) = mStartAngle < angle && angle < mEndAngle
    }
}