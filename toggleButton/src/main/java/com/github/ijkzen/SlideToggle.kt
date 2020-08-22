package com.github.ijkzen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.util.AttributeSet
import androidx.core.content.ContextCompat

open class SlideToggle : AbstractToggleButton {

    private var mRadius = 0
    private var mStartRoundX: Int = 0
    private var mEndRoundX = 0

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attributes: AttributeSet?) : super(context, attributes)


    override fun initAttrs(attributes: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attributes, R.styleable.SlideToggle)

        mCheckedBackgroundColor = typedArray.getColor(
            R.styleable.SlideToggle_checkedBackgroundColor,
            ContextCompat.getColor(context, R.color.checkedBackgroundColor)
        )

        mUncheckedBackgroundColor = typedArray.getColor(
            R.styleable.SlideToggle_uncheckedBackgroundColor,
            ContextCompat.getColor(context, R.color.uncheckedBackgroundColor)
        )

        mCheckedRoundColor = typedArray.getColor(
            R.styleable.SlideToggle_checkedRoundColor,
            ContextCompat.getColor(context, R.color.checkedRoundColor)
        )

        mUncheckedRoundColor = typedArray.getColor(
            R.styleable.SlideToggle_uncheckedRoundColor,
            ContextCompat.getColor(context, R.color.uncheckedRoundColor)
        )

        mDisableCheckedBackgroundColor = typedArray.getColor(
            R.styleable.SlideToggle_disableCheckedBackgroundColor,
            ContextCompat.getColor(context, R.color.disableCheckedBackgroundColor)
        )

        mDisableUncheckedBackgroundColor = typedArray.getColor(
            R.styleable.SlideToggle_disableUncheckedBackgroundColor,
            ContextCompat.getColor(context, R.color.disableUncheckBackgroundColor)
        )

        mDisableCheckedRoundColor = typedArray.getColor(
            R.styleable.SlideToggle_disableCheckedRoundColor,
            ContextCompat.getColor(context, R.color.disableCheckedRoundColor)
        )

        mDisableUncheckedRoundColor = typedArray.getColor(
            R.styleable.SlideToggle_disableUncheckedRoundColor,
            ContextCompat.getColor(context, R.color.disableUncheckRoundColor)
        )

        val duration = typedArray.getInt(R.styleable.SlideToggle_duration, DEFAULT_DURATION)
        mDuration = if (duration < DEFAULT_DURATION) {
            DEFAULT_DURATION
        } else {
            duration
        }

        mIsChecked = typedArray.getBoolean(R.styleable.SlideToggle_checked, false)
        mIsEnabled = typedArray.getBoolean(R.styleable.SlideToggle_enabled, true)

        typedArray.recycle()
    }

    override fun initDefaultSize() {
        mDefaultWidth = convertDp2Px(DEFAULT_WIDTH + 4, context)
        mDefaultHeight = convertDp2Px(DEFAULT_HEIGHT + 4, context)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val padding = convertDp2Px(2, context)
        mRadius = (height - paddingTop - paddingBottom - 2 * padding) / 2
        mStartRoundX = paddingStart + mRadius + padding
        mEndRoundX = width - paddingEnd - padding - mRadius
    }

    override fun drawEnabledBackground(canvas: Canvas?) {
        mBackgroundPaint.color =
            getCurrentColor(
                mCheckedBackgroundColor,
                mUncheckedBackgroundColor,
                System.currentTimeMillis(),
                0
            )

        val padding = convertDp2Px(2, context)
        val left = paddingStart + padding
        val right = width - paddingEnd - padding
        val realHeight = height - paddingBottom - paddingTop
        val top = paddingTop + realHeight / 4
        val bottom = top + realHeight / 2

        val backgroundRect = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
        val radius = (bottom - top) / 2
        canvas?.drawRoundRect(backgroundRect, radius.toFloat(), radius.toFloat(), mBackgroundPaint)
    }

    override fun drawEnabledRound(canvas: Canvas?) {
        val x = getRoundX()
        mRoundPaint.color =
            getCurrentColor(mCheckedRoundColor, mUncheckedRoundColor, System.currentTimeMillis(), 1)
        mRoundPaint.setShadowLayer(5f, 0F, 3F, Color.GRAY)
        canvas?.drawCircle(x, mDefaultRoundCenterY.toFloat(), mRadius.toFloat(), mRoundPaint)
    }

    override fun drawDisableBackground(canvas: Canvas?) {
        mBackgroundPaint.color =
            if (isChecked()) mDisableCheckedBackgroundColor else mDisableUncheckedBackgroundColor

        val padding = convertDp2Px(2, context)
        val left = paddingStart + padding
        val right = width - paddingEnd - padding
        val realHeight = height - paddingBottom - paddingTop
        val top = paddingTop + realHeight / 4
        val bottom = top + realHeight / 2

        val backgroundRect = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
        val radius = (bottom - top) / 2
        canvas?.drawRoundRect(backgroundRect, radius.toFloat(), radius.toFloat(), mBackgroundPaint)
    }

    override fun drawDisableRound(canvas: Canvas?) {
        val x = getRoundX()
        mRoundPaint.color =
            if (isChecked()) mDisableCheckedRoundColor else mDisableUncheckedRoundColor
        mRoundPaint.setShadowLayer(5f, 0F, 3F, Color.GRAY)
        canvas?.drawCircle(x, mDefaultRoundCenterY.toFloat(), mRadius.toFloat(), mRoundPaint)
    }

    private fun getRoundX(): Float {
        return if (mIsChanged) {

            val rate = (System.currentTimeMillis() - mTouchUpTime) / mDuration.toFloat()

            val (originX, targetX) = if (mIsChecked) {
                mStartRoundX to mEndRoundX
            } else {
                mEndRoundX to mStartRoundX
            }

            when {
                rate <= 0 -> {
                    mStartRoundX.toFloat()
                }
                rate < 1f -> {
                    originX + (targetX - originX) * rate
                }
                else -> {
                    targetX.toFloat()
                }
            }

        } else {
            if (mIsChecked) {
                mEndRoundX.toFloat()
            } else {
                mStartRoundX.toFloat()
            }
        }
    }
}