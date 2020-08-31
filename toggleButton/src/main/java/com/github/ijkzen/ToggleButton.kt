package com.github.ijkzen

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import androidx.core.content.ContextCompat

open class ToggleButton : AbstractToggleButton {

    private var mDefaultMinRadius = 0

    private var mDefaultNormalRadius = 0

    private var mDefaultMaxRadius = 0

    private var mDefaultCircleMaxPadding = 0

    private var mDefaultCircleMinPadding = 0

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attributes: AttributeSet?) : super(context, attributes)

    override fun initAttrs(attributes: AttributeSet?) {

        val typedArray = context.obtainStyledAttributes(
            attributes,
            R.styleable.ToggleButton
        )
        mCheckedBackgroundColor = typedArray.getColor(
            R.styleable.ToggleButton_checkedBackgroundColor,
            ContextCompat.getColor(
                context,
                R.color.checkedBackgroundColor
            )
        )

        mCheckedRoundColor = typedArray.getColor(
            R.styleable.ToggleButton_checkedRoundColor,
            ContextCompat.getColor(
                context,
                R.color.checkedRoundColor
            )
        )

        mUncheckedBackgroundColor = typedArray.getColor(
            R.styleable.ToggleButton_uncheckedBackgroundColor,
            ContextCompat.getColor(
                context,
                R.color.uncheckedBackgroundColor
            )
        )

        mUncheckedRoundColor = typedArray.getColor(
            R.styleable.ToggleButton_uncheckedRoundColor,
            ContextCompat.getColor(
                context,
                R.color.uncheckedRoundColor
            )
        )

        mDisableCheckedBackgroundColor = typedArray.getColor(
            R.styleable.ToggleButton_disableCheckedBackgroundColor,
            ContextCompat.getColor(
                context,
                R.color.disableCheckedBackgroundColor
            )
        )

        mDisableCheckedRoundColor = typedArray.getColor(
            R.styleable.ToggleButton_disableCheckedRoundColor,
            ContextCompat.getColor(
                context,
                R.color.disableCheckedRoundColor
            )
        )

        mDisableUncheckedBackgroundColor = typedArray.getColor(
            R.styleable.ToggleButton_disableUncheckedBackgroundColor,
            ContextCompat.getColor(
                context,
                R.color.disableUncheckBackgroundColor
            )
        )

        mDisableUncheckedRoundColor = typedArray.getColor(
            R.styleable.ToggleButton_disableUncheckedRoundColor,
            ContextCompat.getColor(
                context,
                R.color.disableUncheckRoundColor
            )
        )

        val duration =
            typedArray.getInt(R.styleable.ToggleButton_duration, DEFAULT_DURATION.toInt())
        mDuration = if (duration < DEFAULT_DURATION) {
            DEFAULT_DURATION
        } else {
            duration.toLong()
        }

        mIsChecked = typedArray.getBoolean(R.styleable.ToggleButton_checked, false)
        mIsEnabled = typedArray.getBoolean(R.styleable.ToggleButton_enabled, true)

        typedArray.recycle()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        mDefaultMinRadius = mDefaultRoundCenterY - convertDp2Px(
            8,
            context
        )
        mDefaultNormalRadius = mDefaultRoundCenterY - convertDp2Px(
            7,
            context
        )
        mDefaultMaxRadius = mDefaultRoundCenterY - convertDp2Px(
            3,
            context
        )
    }

    override fun initDefaultSize() {
        mDefaultWidth = convertDp2Px(56, context)
        mDefaultHeight = convertDp2Px(30, context)

        mDefaultCircleMinPadding = convertDp2Px(4, context)
        mDefaultCircleMaxPadding = convertDp2Px(8, context)
    }

    override fun drawEnabledBackground(canvas: Canvas?) {
        val left = paddingStart
        val right = width - paddingEnd
        val top = paddingTop
        val bottom = height - paddingBottom

        mBackgroundPaint.color = getCurrentColor(
            mCheckedBackgroundColor,
            mUncheckedBackgroundColor,
            0
        )

        val backgroundRect = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
        val radius = (bottom - top) / 2
        canvas?.drawRoundRect(backgroundRect, radius.toFloat(), radius.toFloat(), mBackgroundPaint)
    }

    override fun drawEnabledRound(canvas: Canvas?) {
        val point = getCircleCenter()
        mRoundPaint.color = getCurrentColor(mCheckedRoundColor, mUncheckedRoundColor, 1)
        canvas?.drawCircle(point.x, point.y, getRoundRadius(), mRoundPaint)
    }

    override fun drawDisableBackground(canvas: Canvas?) {
        val left = paddingStart
        val right = width - paddingEnd
        val top = paddingTop
        val bottom = height - paddingBottom

        mBackgroundPaint.color =
            if (isChecked()) mDisableCheckedBackgroundColor else mDisableUncheckedBackgroundColor

        val backgroundRect = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
        val radius = (bottom - top) / 2
        canvas?.drawRoundRect(backgroundRect, radius.toFloat(), radius.toFloat(), mBackgroundPaint)
    }

    override fun drawDisableRound(canvas: Canvas?) {
        val point = getCircleCenter()
        mRoundPaint.color =
            if (isChecked()) mDisableCheckedRoundColor else mDisableUncheckedRoundColor
        canvas?.drawCircle(point.x, point.y, getRoundRadius(), mRoundPaint)
    }

    private fun getCircleCenter(): PointF {
        val during = mDuration * mRate
        val oneFifth = mDuration / 5.0f
        val threeFifth = oneFifth * 3
        val fourFifth = oneFifth * 4

        if (isInitStatus()) {
            return if (mIsChecked) {
                PointF(
                    width.toFloat() - mDefaultNormalRadius - mDefaultCircleMaxPadding.toFloat(),
                    mDefaultRoundCenterY.toFloat()
                )
            } else {
                PointF(
                    mDefaultNormalRadius + mDefaultCircleMaxPadding.toFloat(),
                    mDefaultRoundCenterY.toFloat()
                )
            }
        }

        return if (mIsChecked) {
            when (during) {
                in 0F..oneFifth -> {
                    val result = during / oneFifth
                    PointF(
                        (mDefaultCircleMinPadding + mDefaultNormalRadius + (mDefaultMaxRadius - mDefaultNormalRadius) * result),
                        mDefaultRoundCenterY.toFloat()
                    )
                }
                in oneFifth..fourFifth -> {
                    val initX = mDefaultMaxRadius + mDefaultCircleMinPadding
                    val targetX = width - mDefaultCircleMinPadding - mDefaultMinRadius
                    val result = (during - oneFifth) / threeFifth
                    PointF(initX + (targetX - initX) * result, mDefaultRoundCenterY.toFloat())
                }
                in fourFifth..mDuration.toFloat() -> {
                    val initX = width - mDefaultCircleMinPadding - mDefaultMinRadius
                    val targetX = width - mDefaultCircleMaxPadding - mDefaultNormalRadius
                    val result = (during - fourFifth) / oneFifth
                    PointF(initX + (targetX - initX) * result, mDefaultRoundCenterY.toFloat())
                }
                else -> {
                    PointF(
                        width.toFloat() - mDefaultNormalRadius - mDefaultCircleMaxPadding.toFloat(),
                        mDefaultRoundCenterY.toFloat()
                    )
                }
            }
        } else {
            when (during) {
                in 0F..oneFifth -> {
                    val initX =
                        width.toFloat() - mDefaultNormalRadius - mDefaultCircleMaxPadding.toFloat()
                    val targetX = width.toFloat() - mDefaultMaxRadius - mDefaultMinRadius.toFloat()
                    val result = during / oneFifth
                    PointF(
                        (initX + (targetX - initX) * result),
                        mDefaultRoundCenterY.toFloat()
                    )
                }
                in oneFifth..fourFifth -> {
                    val initX = width.toFloat() - mDefaultMaxRadius - mDefaultMinRadius.toFloat()
                    val targetX = mDefaultCircleMinPadding + mDefaultMinRadius
                    val result = (during - oneFifth) / threeFifth
                    PointF(initX + (targetX - initX) * result, mDefaultRoundCenterY.toFloat())
                }
                in fourFifth..mDuration.toFloat() -> {
                    val initX = mDefaultCircleMinPadding + mDefaultMinRadius
                    val targetX = mDefaultNormalRadius + mDefaultCircleMaxPadding
                    val result = (during - fourFifth) / oneFifth
                    PointF(initX + (targetX - initX) * result, mDefaultRoundCenterY.toFloat())
                }
                else -> {
                    PointF(
                        mDefaultNormalRadius.toFloat() + mDefaultCircleMaxPadding,
                        mDefaultRoundCenterY.toFloat()
                    )
                }
            }
        }
    }

    private fun getRoundRadius(): Float {
        val during = mDuration * mRate
        val oneFifth = mDuration / 5.0f
        val threeFifth = oneFifth * 3
        val fourFifth = oneFifth * 4
        return when (during) {
            in 0F..oneFifth -> {
                mDefaultNormalRadius.toFloat() + (mDefaultMaxRadius - mDefaultNormalRadius) * (during / oneFifth)
            }
            in oneFifth..fourFifth -> {
                val result = (during - oneFifth) / threeFifth
                mDefaultMaxRadius.toFloat() + (mDefaultMinRadius - mDefaultMaxRadius) * result
            }
            in fourFifth..mDuration.toFloat() -> {
                val result = (during - fourFifth) / oneFifth
                mDefaultMinRadius.toFloat() + (mDefaultNormalRadius - mDefaultMinRadius) * result
            }
            else -> {
                mDefaultNormalRadius.toFloat()
            }
        }
    }
}