package ru.mertsalovda.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class IndicatorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mTextWidth: Float = 0f
    private val mTextPaint: Paint

    private var mExtDiameter: Float = 0f
    private var mInnDiameter: Float = 0f
    private var mTotalDiameter: Float = 0f

    private val mEmptySectorPaint: Paint
    private val mFillSectorPaint: Paint
    private val mInnerCirclePaint: Paint

    private val mTextBounds: Rect
    private val mTotalBounds: RectF
    private val mSectorBounds: RectF

    private var mTextSize = 100f
    private var mValue = 0
    private var mMaxValue = 4
    private var mTextColor = Color.BLUE
    private var mFillSectorColor = Color.BLUE
    private var mEmptySectorColor = Color.GRAY

    private val mSweepAngle: Float
    private var mShiftAngle: Float = 0f

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.IndicatorView,
            0,
            R.style.AppTheme
        ).apply {
            mMaxValue = getInt(R.styleable.IndicatorView_maxValue, 4)
            mValue = getInt(R.styleable.IndicatorView_startValue, 0)
            mTextColor = getColor(R.styleable.IndicatorView_textColor, Color.BLUE)
            mFillSectorColor = getColor(R.styleable.IndicatorView_fillSectorColor, Color.BLUE)
            mEmptySectorColor =
                getColor(R.styleable.IndicatorView_emptySectorColor, Color.GRAY)
            mTextSize = getFloat(R.styleable.IndicatorView_textSize, 48f)
            recycle()
        }

        mFillSectorPaint = Paint().apply {
            color = mFillSectorColor
            style = Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
        }

        mTextPaint = Paint(mFillSectorPaint).apply {
            color = mTextColor
            textSize = mTextSize
            textAlign = Paint.Align.CENTER
        }

        mEmptySectorPaint = Paint(mFillSectorPaint).apply {
            color = mEmptySectorColor
            setAlpha(0.4f)
        }

        mInnerCirclePaint = Paint(mFillSectorPaint).apply {
            color = Color.WHITE
        }

        mTotalBounds = RectF()
        mSectorBounds = RectF()
        mTextBounds = Rect()

        val angle = 360f / mMaxValue
        if (mMaxValue <= 60)
            mShiftAngle = (angle * 0.05f) / 2f
        mSweepAngle = angle - mShiftAngle * 2f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mTextWidth = mTextPaint.measureText(mMaxValue.toString())
        mTextPaint.getTextBounds("A", 0, 1, mTextBounds)

        val wh = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        mExtDiameter = wh
        mInnDiameter = wh * 0.9f
        mTotalDiameter = mExtDiameter

        val measuredWidth = resolveSize(mTotalDiameter.toInt(), widthMeasureSpec)
        val measuredHeight = resolveSize(mTotalDiameter.toInt(), heightMeasureSpec)

        mTotalBounds.set(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
        mSectorBounds.set(mTotalBounds)
//        mSectorBounds.inset(mTextWidth * 0.025f, mTextWidth * 0.025f)

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        val cx = canvas.width / 2f
        val cy = canvas.height / 2f
        canvas.apply {
            save()
            rotate(-90f, cx, cy)
            drawCircle(cx, cy, mExtDiameter / 2f, mInnerCirclePaint)

            drawSectors(this, cx, cy)

            restore()
            drawCircle(cx, cy, mInnDiameter / 2f, mInnerCirclePaint)
            drawText(mValue.toString(), cx, (mTextBounds.height() / 2f) + cy, mTextPaint)
        }
    }

    private fun drawSectors(canvas: Canvas, cx: Float, cy: Float) {
        when (mValue) {
            mMaxValue -> canvas.drawCircle(cx, cy, mExtDiameter / 2f, mFillSectorPaint)
            0 -> canvas.drawCircle(cx, cy, mExtDiameter / 2f, mEmptySectorPaint)
            else -> {
                var angle = 0f
                for (i in 1..mMaxValue) {
                    angle += mShiftAngle
                    if (i <= mValue) {
                        canvas.drawArc(mSectorBounds, angle, mSweepAngle, true, mFillSectorPaint)
                    } else {
                        canvas.drawArc(mSectorBounds, angle, mSweepAngle, true, mEmptySectorPaint)
                    }
                    angle += mSweepAngle + mShiftAngle
                }
            }
        }
    }

    fun getValue() = mValue
    fun setValue(value: Int) {
        if (value > mMaxValue) {
            mValue = mMaxValue
        } else {
            mValue = value
            invalidate()
        }
    }

    fun getMaxValue() = mMaxValue
    fun setMaxValue(value: Int) {
        mMaxValue = value
        invalidate()
    }
}