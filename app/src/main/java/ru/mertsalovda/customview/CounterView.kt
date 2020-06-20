package ru.mertsalovda.customview

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class CounterView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val mDuration = 200L
    private val mMaxValuePaint: Paint

    private val mValuePaint: Paint
    private val mSeparatorPaint: Paint

    private var mMaxValueTextWidth = 0f
    private var maxTextHeight = 0

    private var mValueTextWidth = 0f
    private var mSeparatorTextWidth = 0f

    private val mValueBounds: Rect
    private val mValueBoundsCenter: Rect
    private val mValueBoundsUp: Rect
    private val mValueBoundsDown: Rect
    private val mMaxValueBounds: Rect
    private val mSeparatorBounds: Rect

    private var mValue = 0
    private var mNewValue = mValue
    private var mMaxValue = 0
    private var mSeparator = "/"
    private val mValueColor: Int
    private val mMaxValueColor: Int
    private val mSeparatorColor: Int
    private var mTextSize: Float
    private var mFontStyle: Int


    private val mMoveDownValueAnimator = ValueAnimator()
    private val mMoveUpValueAnimator = ValueAnimator()

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CounterView,
            0,
            R.style.AppTheme
        )
            .apply {
                mTextSize = getFloat(R.styleable.CounterView_textSize, 100f)
                mValue = getInt(R.styleable.CounterView_value, 0)
                mMaxValue = getInt(R.styleable.CounterView_maxValue, 10)
                mSeparator = getString(R.styleable.CounterView_separator) ?: "/"
                mValueColor = getColor(R.styleable.CounterView_valueColor, Color.BLACK)
                mMaxValueColor = getColor(R.styleable.CounterView_maxValueColor, Color.BLACK)
                mSeparatorColor = getColor(R.styleable.CounterView_separatorColor, Color.BLACK)
                mFontStyle = getInt(R.styleable.CounterView_textStyle, 0)
            }

        mMaxValuePaint = Paint().apply {
            textSize = mTextSize
            color = mMaxValueColor
            typeface = Typeface.create(Typeface.DEFAULT, mFontStyle)
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        mValuePaint = Paint(mMaxValuePaint).apply {
            color = mValueColor
            textAlign = Paint.Align.RIGHT
        }
        mSeparatorPaint = Paint(mMaxValuePaint).apply {
            color = mSeparatorColor
        }

        mValueBounds = Rect()
        mValueBoundsCenter = Rect()
        mValueBoundsUp = Rect()
        mValueBoundsDown = Rect()
        mMaxValueBounds = Rect()
        mSeparatorBounds = Rect()

        mMoveDownValueAnimator.apply {
            duration = mDuration
            addUpdateListener {
                val value = it.animatedValue as Int
                mValueBounds.set(
                    mValueBoundsCenter.left,
                    mValueBoundsCenter.top + value,
                    mValueBoundsCenter.right,
                    mValueBoundsCenter.bottom + value
                )
                invalidate()
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator?) {
                    if (mValue != mNewValue) {
                        mValue = mNewValue
                        mMoveDownValueAnimator.apply {
                            setIntValues(mValueBoundsUp.bottom, mValueBoundsCenter.bottom)
                            start()
                        }
                    }
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })
        }


        mMoveUpValueAnimator.apply {
            duration = mDuration
            addUpdateListener {
                val value = it.animatedValue as Int
                mValueBounds.set(
                    mValueBoundsCenter.left,
                    mValueBoundsCenter.top - value,
                    mValueBoundsCenter.right,
                    mValueBoundsCenter.bottom - value
                )
                invalidate()
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator?) {
                    if (mValue != mNewValue) {
                        mValue = mNewValue
                        mMoveUpValueAnimator.apply {
                            setIntValues(mValueBoundsUp.bottom, mValueBoundsCenter.bottom)
                            start()
                        }
                    }
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mMaxValueTextWidth = mMaxValuePaint.measureText(mMaxValue.toString())
        mValueTextWidth = mValuePaint.measureText(mMaxValue.toString())
        mSeparatorTextWidth = mSeparatorPaint.measureText(mSeparator)

        mMaxValuePaint.getTextBounds("A", 0, 1, mMaxValueBounds)
        mValuePaint.getTextBounds("A", 0, 1, mValueBounds)
        mValueBoundsCenter.set(mValueBounds)
        mSeparatorPaint.getTextBounds("A", 0, 1, mSeparatorBounds)

        maxTextHeight = intArrayOf(
            mMaxValueBounds.height(),
            mValueBounds.height(),
            mSeparatorBounds.height()
        ).max() ?: 0

        val maxHeight = maxTextHeight + paddingBottom + paddingTop

        val maxWidth =
            mMaxValueTextWidth + mValueTextWidth + mSeparatorTextWidth + paddingLeft + paddingRight

        val measuredWidth = resolveSize(maxWidth.toInt(), widthMeasureSpec)
        val measuredHeight = resolveSize(maxHeight, heightMeasureSpec)

        mValueBoundsUp.set(
            mValueBoundsCenter.left,
            mValueBoundsCenter.top - measuredHeight,
            mValueBoundsCenter.right,
            mValueBoundsCenter.bottom - measuredHeight
        )
        mValueBoundsDown.set(
            mValueBoundsCenter.left,
            mValueBoundsCenter.top + (measuredHeight * 2),
            mValueBoundsCenter.right,
            mValueBoundsCenter.bottom + (measuredHeight * 2)
        )

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        val x = canvas.width / 2f
        val y = (canvas.height / 2f) + (maxTextHeight / 2f)
        val valueY = mValueBounds.bottom.toFloat() + (canvas.height / 2f) - paddingBottom / 2f

        canvas.apply {
            drawText(mSeparator, x, y - paddingBottom / 2f, mSeparatorPaint)
            drawText(mMaxValue.toString(), x + mSeparatorBounds.width() * 1.15f, y, mMaxValuePaint)
            drawText(
                mValue.toString(),
                x - mSeparatorBounds.width() * 0.35f,
                valueY * 2f,
                mValuePaint
            )
        }
    }

    fun setValue(value: Int) {
        if (value in 0..mMaxValue) {
            if (value > mNewValue) {
                mNewValue = value
                mMoveDownValueAnimator.apply {
                    setIntValues(0, measuredHeight)
                    start()
                }
            } else {
                mNewValue = value
                mMoveUpValueAnimator.apply {
                    setIntValues(0, measuredHeight + 2)
                    start()
                }
            }
        }
    }

    fun getValue() = mValue

    fun setMaxValue(value: Int) {
        if (value >= 0) {
            mMaxValue = value
            invalidate()
        }
    }

    fun getMaxValue() = mMaxValue
}