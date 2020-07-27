package com.github.ijkzen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorRes

abstract class AbstractToggleButton : View {

    protected var mDefaultWidth = 0

    protected var mDefaultHeight = 0

    protected val mBackgroundPaint = Paint()

    protected val mRoundPaint = Paint()

    protected var mEnableBackgroundColor = 0

    protected var mDisableBackgroundColor = 0

    protected var mEnableRoundColor = 0

    protected var mDisableRoundColor = 0

    protected var mIsEnable = false

    protected var mIsChanged = false

    protected var mTouchUpTime: Long = 0

    protected var mDefaultRoundCenterY = 0

    protected var mDuration = DEFAULT_DURATION

    companion object {
        const val DEFAULT_DURATION = 300
        const val DEFAULT_WIDTH = 45
        const val DEFAULT_HEIGHT = 26
    }

    constructor(context: Context?) : super(context) {
        initAttrs(null)
        initPaint()
    }

    constructor(context: Context?, attributes: AttributeSet?) : super(context, attributes) {
        initAttrs(attributes)
        initPaint()
    }


    abstract fun initAttrs(attributes: AttributeSet?)

    private fun initPaint() {
        mBackgroundPaint.isAntiAlias = true
        mBackgroundPaint.style = Paint.Style.FILL

        mRoundPaint.isAntiAlias = true
        mRoundPaint.style = Paint.Style.FILL
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val widthTmp = widthMode in arrayListOf<Int>(MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED)
        val heightTmp = heightMode in arrayListOf<Int>(MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED)

        initDefaultSize()
        if (widthTmp && heightTmp) {
            setMeasuredDimension(mDefaultWidth, mDefaultHeight)
        } else if (widthTmp) {
            setMeasuredDimension(mDefaultWidth, heightSize)
        } else if (heightTmp) {
            setMeasuredDimension(widthSize, mDefaultHeight)
        }
    }

    abstract fun initDefaultSize()

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mDefaultRoundCenterY = height / 2
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawBackground(canvas)
        drawRound(canvas)

        if (System.currentTimeMillis() - mTouchUpTime <= mDuration + 200) {
            invalidate()
        } else {
            mIsChanged = false
        }
    }

    open fun getCurrentColor(enableColor: Int, disableColor: Int, currentTime: Long): Int {
        if (!mIsChanged) {
            return if (mIsEnable) {
                enableColor
            } else {
                disableColor
            }
        }

        val (originColor, targetColor) = if (mIsEnable) disableColor to enableColor else enableColor to disableColor

        val rate = (currentTime - mTouchUpTime) / mDuration.toFloat()

        return if (mIsEnable) {
            when{
                rate <= 0.4 ->{
                    originColor
                }
                rate > 1 ->{
                    targetColor
                }
                else ->{
                    val alpha = (rate * 100).toInt()
                    val drawable = ColorDrawable(targetColor)
                    drawable.alpha = alpha
                    drawable.color
                }
            }
        } else {
            when {
                rate <= 0.6 ->{
                    val alpha = 100 - (rate * 100).toInt()
                    val drawable = ColorDrawable(originColor)
                    drawable.alpha = alpha
                    drawable.color
                }
                else -> {
                    targetColor
                }
            }
        }
    }

    abstract fun drawBackground(canvas: Canvas?)

    abstract fun drawRound(canvas: Canvas?)

    fun setBackgroundEnableColor(@ColorRes color: Int) {
        mEnableBackgroundColor = color
        invalidate()
    }

    fun setBackgroundDisableColor(@ColorRes color: Int) {
        mDisableBackgroundColor = color
        invalidate()
    }

    fun setRoundEnableColor(@ColorRes color: Int) {
        mEnableRoundColor = color
        invalidate()
    }

    fun setRoundDisableColor(@ColorRes color: Int) {
        mDisableRoundColor = color
        invalidate()
    }

    fun setEnable(enable: Boolean) {
        if (enable != mIsEnable) {
            mIsEnable = !mIsEnable
            mIsChanged = true
            mTouchUpTime = System.currentTimeMillis()
            invalidate()
        }
    }

    fun isEnable() = mIsEnable

    fun toggle() {
        setEnable(!mIsEnable)
    }

    fun setDuration(duration: Int) {
        mDuration = if (duration < DEFAULT_DURATION) {
            DEFAULT_DURATION
        } else {
            duration
        }
    }
}