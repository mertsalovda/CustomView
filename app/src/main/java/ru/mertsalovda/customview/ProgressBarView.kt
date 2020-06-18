package ru.mertsalovda.customview

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min


class ProgressBarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mExtDiameter = 0f
    private var mInnDiameter = 0f

    private val mSupportPaint: Paint
    private val mScalePaint: Paint
    private val mProgressPaint: Paint
    private val mMainTextPaint: Paint
    private val mSecondTextPaint: Paint

    private val mMainTextBounds: Rect
    private val mScaleBounds: RectF
    private val mProgressBounds: RectF

    private var mValue: Int
    private var mMaxValue: Int
    private var mOldValue: Int = 0
    private var mMetric: String
    private var mMainTextSize: Float
    private var mSecondTextSize: Float
    private var mProgressColor: Int
    private var mStartProgressColor: Int
    private var mCurrentProgressColor: Int = 0
    private var mEndProgressColor: Int

    private val mProgressAnimator: ValueAnimator
    private val mColorAnimator: ValueAnimator

    private val mDuration = 2000L

    init {

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ProgressBarView,
            0,
            R.style.AppTheme
        )
            .apply {
                mMainTextSize = getFloat(R.styleable.ProgressBarView_textSize, 150f)
                mSecondTextSize = getFloat(R.styleable.ProgressBarView_metricTextSize, 40f)
                mValue = getInt(R.styleable.ProgressBarView_value, 0)
                mMaxValue = getInt(R.styleable.ProgressBarView_maxValue, 100)
                mMetric = getString(R.styleable.ProgressBarView_metric) ?: "%"
                mStartProgressColor = getColor(R.styleable.ProgressBarView_startColor, Color.BLUE)
                mEndProgressColor = getColor(R.styleable.ProgressBarView_endColor, Color.BLUE)
                mProgressColor = getColor(R.styleable.ProgressBarView_progressColor, -1)
                if (mProgressColor < 0){
                    mProgressColor = mStartProgressColor
                }
            }


        mSupportPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        mScalePaint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 5f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            isAntiAlias = true
        }
        mProgressPaint = Paint(mScalePaint).apply {
            color = mProgressColor
            strokeWidth = 30f
        }
        mMainTextPaint = Paint().apply {
            color = Color.BLACK
            textSize = mMainTextSize
            style = Paint.Style.FILL_AND_STROKE
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        mSecondTextPaint = Paint(mMainTextPaint).apply {
            textSize = mSecondTextSize
        }

        mMainTextBounds = Rect()
        mScaleBounds = RectF()
        mProgressBounds = RectF()

        mProgressAnimator = ValueAnimator().apply {
            duration = mDuration
            addUpdateListener {
                val value = it.animatedValue as Int
                mValue = value
                invalidate()
            }
        }
        mColorAnimator = ValueAnimator().apply {
            duration = mDuration
            setEvaluator(ArgbEvaluator())
            addUpdateListener {
                val position: Float = it.animatedFraction
                mProgressColor = if (mValue > mOldValue){
                    blendColors(mCurrentProgressColor, mEndProgressColor, position)
                } else{
                    blendColors(mCurrentProgressColor, mStartProgressColor, position)
                }
            }
        }
    }

    private fun blendColors(from: Int, to: Int, ratio: Float): Int {
        val inverseRatio = 1f - ratio
        val r = Color.red(to) * ratio + Color.red(from) * inverseRatio
        val g = Color.green(to) * ratio + Color.green(from) * inverseRatio
        val b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio
        return Color.rgb(r.toInt(), g.toInt(), b.toInt())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mTextWidth = mMainTextPaint.measureText(mMaxValue.toString())
        mMainTextPaint.getTextBounds("A", 0, 1, mMainTextBounds)

        val w = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        val h = MeasureSpec.getSize(heightMeasureSpec).toFloat()
        val min = min(w, h)

        val measuredWidth = resolveSize(min.toInt(), widthMeasureSpec)
        val measuredHeight = resolveSize(min.toInt(), heightMeasureSpec)

        mInnDiameter = measuredWidth * 0.8f

        val side = measuredWidth.toFloat()
        mProgressBounds.set(
            side * 0.1f,
            side * 0.1f,
            side * 0.9f,
            side * 0.9f
        )
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        val cx = canvas.width / 2f
        val cy = canvas.height / 2f

        mProgressPaint.color = mProgressColor

        canvas.apply {
            val delta = 270f / 30f
            var angle = -45f
            for (i in 0..30) {
                angle + delta * i
                save()
                rotate(angle + (delta * i), cx, cy)
                if (i == 0 || i % 5 == 0) {
                    drawLine(
                        mProgressBounds.left - 30f,
                        mProgressBounds.top + (mInnDiameter / 2f),
                        mProgressBounds.left - 60f,
                        mProgressBounds.top + (mInnDiameter / 2f),
                        mScalePaint
                    )
                } else {
                    drawLine(
                        mProgressBounds.left - 40f,
                        mProgressBounds.top + (mInnDiameter / 2f),
                        mProgressBounds.left - 50f,
                        mProgressBounds.top + (mInnDiameter / 2f),
                        mScalePaint
                    )
                }
                restore()
            }

            save()
            rotate(-225f, cx, cy)
            drawArc(mProgressBounds, 0f, 270f, false, mScalePaint)
            drawArc(mProgressBounds, 0f, getSweepAngle(mValue), false, mProgressPaint)
            restore()

            drawText(mValue.toString(), cx, (mMainTextBounds.height() / 2f) + cy, mMainTextPaint)
            drawText(mMetric, cx, mMainTextBounds.height() + cy, mSecondTextPaint)
        }

    }

    private fun getSweepAngle(value: Int): Float {
        var result: Float
        result = (270f * value) / mMaxValue
        return result
    }

    fun setValue(value: Int) {
        if (value <= mMaxValue) {
            mOldValue = mValue
            mValue = value

            mCurrentProgressColor = mProgressColor

            mProgressAnimator.setIntValues(mOldValue, mValue)
            if (value > mOldValue) {
                mColorAnimator.setIntValues(mCurrentProgressColor, mEndProgressColor)
            } else {
                mColorAnimator.setIntValues(mCurrentProgressColor, mStartProgressColor)
            }

            mProgressAnimator.start()
            mColorAnimator.start()
        }
    }
}