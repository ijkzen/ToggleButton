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

        mEnableBackgroundColor = typedArray.getColor(
            R.styleable.SlideToggle_enableBackgroundColor,
            ContextCompat.getColor(context, R.color.enableBackgroundColor)
        )

        mDisableBackgroundColor = typedArray.getColor(
            R.styleable.SlideToggle_disableBackgroundColor,
            ContextCompat.getColor(context, R.color.disableBackgroundColor)
        )

        mEnableRoundColor = typedArray.getColor(
            R.styleable.SlideToggle_enableRoundColor,
            ContextCompat.getColor(context, R.color.enableRoundColor)
        )

        mDisableRoundColor = typedArray.getColor(
            R.styleable.SlideToggle_disableRoundColor,
            ContextCompat.getColor(context, R.color.disableRoundColor)
        )

        val duration = typedArray.getInt(R.styleable.SlideToggle_duration, DEFAULT_DURATION)
        mDuration = if (duration < DEFAULT_DURATION) {
            DEFAULT_DURATION
        } else {
            duration
        }

        mIsEnable = typedArray.getBoolean(R.styleable.SlideToggle_enable, false)

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

    override fun drawBackground(canvas: Canvas?) {
        mBackgroundPaint.color =
            getCurrentColor(
                mEnableBackgroundColor,
                mDisableBackgroundColor,
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

    override fun drawRound(canvas: Canvas?) {
        val x = getRoundX()
        mRoundPaint.color =
            getCurrentColor(mEnableRoundColor, mDisableRoundColor, System.currentTimeMillis(), 1)
        mRoundPaint.setShadowLayer(5f, 0F, 3F, Color.GRAY)
        canvas?.drawCircle(x, mDefaultRoundCenterY.toFloat(), mRadius.toFloat(), mRoundPaint)
    }

    private fun getRoundX(): Float {
        return if (mIsChanged) {

            val rate = (System.currentTimeMillis() - mTouchUpTime) / mDuration.toFloat()

            val (originX, targetX) = if (mIsEnable) {
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
            if (mIsEnable) {
                mEndRoundX.toFloat()
            } else {
                mStartRoundX.toFloat()
            }
        }
    }
}