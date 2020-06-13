package ru.mertsalovda.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class IndicatorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mTextWidth: Float = 0f
    private var mCenterX = 0f
    private var mCenterY = 0f
    private val mTextPaint: Paint

    private var mExtRadius: Float = 0f
    private var mInnRadius: Float = 0f
    private var mTotalRadius: Float = 0f

    private val mEmptySectorPaint: Paint
    private val mFillSectorPaint: Paint
    private val mInnerCirclePaint: Paint

    private val mTextBounds: Rect
    private val mTotalBounds: RectF
    private val mSectorBounds: RectF

    private val mMaxLength = "000"
    private var mValue = 1
    private var mMaxValue = 60

    private val mSweepAngle: Float
    private val mShiftAngle: Float

    init {
        mFillSectorPaint = Paint().apply {
            color = Color.BLUE
            style = Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
        }

        mTextPaint = Paint(mFillSectorPaint).apply {
            textSize = resources.getDimensionPixelSize(R.dimen.very_big_text).toFloat()
            textAlign = Paint.Align.CENTER
        }

        mEmptySectorPaint = Paint(mFillSectorPaint).apply {
            color = Color.GRAY
            setAlpha(0.4f)
        }

        mInnerCirclePaint = Paint(mFillSectorPaint).apply {
            color = Color.WHITE
        }

        mTotalBounds = RectF()
        mSectorBounds = RectF()
        mTextBounds = Rect()

        val angle = 360 / mMaxValue
        mShiftAngle = angle * 0.05f
        mSweepAngle = angle - mShiftAngle
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mTextWidth = mTextPaint.measureText(mMaxLength)
        mTextPaint.getTextBounds("A", 0, 1, mTextBounds)

        mInnRadius = mTextWidth * 1.1f
        mExtRadius = mTextWidth * 1.15f
        mTotalRadius = mTextWidth * 1.2f

        val desiredDiameter = mTotalRadius

        val measuredWidth = resolveSize(desiredDiameter.toInt(), widthMeasureSpec)
        val measuredHeight = resolveSize(desiredDiameter.toInt(), heightMeasureSpec)

        mCenterX = measuredWidth / 2f
        mCenterX = measuredHeight / 2f

        mTotalBounds.set(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
        mSectorBounds.set(mTotalBounds)
        mSectorBounds.inset(mTextWidth * 0.025f, mTextWidth * 0.025f)

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        val cx = canvas.width / 2f
        val cy = canvas.height / 2f
        canvas.drawCircle(cx, cy, mExtRadius / 2f, mEmptySectorPaint)
        canvas.save()
        canvas.rotate(-90f, cx, cy)
        canvas.drawCircle(cx, cy, mExtRadius / 2f, mInnerCirclePaint)

        drawSectors(canvas, cx, cy)

        canvas.restore()
        canvas.drawCircle(cx, cy, mInnRadius / 2f, mInnerCirclePaint)
        canvas.drawText(mValue.toString(), cx, (mTextBounds.height() / 2f) + cy, mTextPaint)
    }

    private fun drawSectors(canvas: Canvas, cx: Float, cy: Float) {
        when (mValue) {
            mMaxValue -> canvas.drawCircle(cx, cy, mExtRadius / 2f, mFillSectorPaint)
            0 -> canvas.drawCircle(cx, cy, mExtRadius / 2f, mEmptySectorPaint)
            else -> {
                var angle = 0f
                for (i in 1..mMaxValue) {
                    angle += mShiftAngle / 2f
                    if (i <= mValue) {
                        canvas.drawArc(mSectorBounds, angle, mSweepAngle, true, mFillSectorPaint)
                    } else {
                        canvas.drawArc(mSectorBounds, angle, mSweepAngle, true, mEmptySectorPaint)
                    }
                    angle += mSweepAngle + mShiftAngle / 2
                }
            }
        }
    }

    fun getValue() = mValue
    fun setValue(value: Int) {
        mValue = if (value > mMaxValue) {
            mMaxValue
        } else {
            value
        }
        invalidate()
    }

    fun getMaxValue() = mMaxValue
    fun setMaxValue(value: Int) {
        mMaxValue = if (value > 60) {
            60
        } else {
            value
        }
        invalidate()
    }
}