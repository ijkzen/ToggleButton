package com.github.ijkzen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorRes

abstract class AbstractToggleButton : View {

    protected var mDefaultWidth = 0

    protected var mDefaultHeight = 0

    protected val mBackgroundPaint = Paint()

    protected val mRoundPaint = Paint()

    protected var mCheckedBackgroundColor = 0

    protected var mUncheckedBackgroundColor = 0

    protected var mCheckedRoundColor = 0

    protected var mUncheckedRoundColor = 0

    protected var mDisableCheckedBackgroundColor = 0

    protected var mDisableUncheckedBackgroundColor = 0

    protected var mDisableCheckedRoundColor = 0

    protected var mDisableUncheckedRoundColor = 0

    protected var mIsChecked = false

    protected var mIsChanged = false

    protected var mIsEnabled = false

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

        if (isButtonEnabled()) {
            drawEnabledBackground(canvas)
            drawEnabledRound(canvas)

            if (System.currentTimeMillis() - mTouchUpTime <= mDuration + 200) {
                invalidate()
            } else {
                mIsChanged = false
            }
        } else {
            drawDisableBackground(canvas)
            drawDisableRound(canvas)
        }
    }

    open fun getCurrentColor(
        enableColor: Int,
        disableColor: Int,
        currentTime: Long,
        flag: Int
    ): Int {
        if (!mIsChanged) {
            return if (mIsChecked) {
                enableColor
            } else {
                disableColor
            }
        }

        val (originColor, targetColor) = if (mIsChecked) disableColor to enableColor else enableColor to disableColor

        var rate = (currentTime - mTouchUpTime) / mDuration.toFloat()

        if (rate > 1.0F) {
            rate = 1.0F
        }

        val redDelta = Color.red(targetColor) - Color.red(originColor)
        val greenDelta = Color.green(targetColor) - Color.green(originColor)
        val blueDelta = Color.blue(targetColor) - Color.blue(originColor)

        return Color.argb(
            0xFF,
            (Color.red(originColor) + redDelta * rate).toInt(),
            (Color.green(originColor) + greenDelta * rate).toInt(),
            (Color.blue(originColor) + blueDelta * rate).toInt()
        )

    }

    abstract fun drawEnabledBackground(canvas: Canvas?)

    abstract fun drawEnabledRound(canvas: Canvas?)

    abstract fun drawDisableBackground(canvas: Canvas?)

    abstract fun drawDisableRound(canvas: Canvas?)

//  set button color

    fun setCheckedBackgroundColor(@ColorRes color: Int) {
        mCheckedBackgroundColor = color
        invalidate()
    }

    fun setUncheckedBackgroundColor(@ColorRes color: Int) {
        mUncheckedBackgroundColor = color
        invalidate()
    }

    fun setCheckedRoundColor(@ColorRes color: Int) {
        mCheckedRoundColor = color
        invalidate()
    }

    fun setUncheckedRoundColor(@ColorRes color: Int) {
        mUncheckedRoundColor = color
        invalidate()
    }

    fun setDisableCheckedBackgroundColor(@ColorRes color: Int) {
        mDisableCheckedBackgroundColor = color
        invalidate()
    }

    fun setDisableUncheckBackgroundColor(@ColorRes color: Int) {
        mDisableUncheckedBackgroundColor = color
        invalidate()
    }

    fun setDisableCheckedRoundColor(@ColorRes color: Int) {
        mDisableCheckedRoundColor = color
        invalidate()
    }

    fun setDisableUncheckedRoundColor(@ColorRes color: Int) {
        mDisableUncheckedRoundColor = color
        invalidate()
    }

//  set button status

    fun setChecked(checked: Boolean) {
        if (checked != mIsChecked) {
            mIsChecked = !mIsChecked
            mIsChanged = true
            mTouchUpTime = System.currentTimeMillis()
            invalidate()
        }
    }

    fun isChecked() = mIsChecked

    fun toggle() {
        if (isButtonEnabled()) {
            setChecked(!mIsChecked)
        }
    }

    fun setButtonEnabled(enabled: Boolean) {
        if (mIsEnabled != enabled) {
            mIsEnabled = enabled
            invalidate()
        }
    }

    fun isButtonEnabled() = mIsEnabled

    fun setDuration(duration: Int) {
        mDuration = if (duration < DEFAULT_DURATION) {
            DEFAULT_DURATION
        } else {
            duration
        }
    }
}