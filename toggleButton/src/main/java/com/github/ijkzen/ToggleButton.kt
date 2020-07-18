package com.github.ijkzen

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

open class ToggleButton : AbstractToggleButton {

    private var mDisableRoundColor = 0

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
        mEnableBackgroundColor = typedArray.getColor(
            R.styleable.ToggleButton_enableBackgroundColor,
            ContextCompat.getColor(
                context,
                R.color.enableBackgroundColor
            )
        )

        mEnableRoundColor = typedArray.getColor(
            R.styleable.ToggleButton_enableRoundColor,
            ContextCompat.getColor(
                context,
                R.color.enableRoundColor
            )
        )

        mDisableBackgroundColor = typedArray.getColor(
            R.styleable.ToggleButton_disableBackgroundColor,
            ContextCompat.getColor(
                context,
                R.color.disableBackgroundColor
            )
        )

        mDisableRoundColor = typedArray.getColor(
            R.styleable.ToggleButton_disableRoundColor,
            ContextCompat.getColor(
                context,
                R.color.disableRoundColor
            )
        )

        val duration = typedArray.getInt(R.styleable.ToggleButton_duration, DEFAULT_DURATION)
        mDuration = if (duration < DEFAULT_DURATION) {
            DEFAULT_DURATION
        } else {
            duration
        }

        mIsEnable = typedArray.getBoolean(R.styleable.ToggleButton_enable, false)

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

    override fun drawBackground(canvas: Canvas?) {
        val left = paddingStart
        val right = width - paddingEnd
        val top = paddingTop
        val bottom = height - paddingBottom

        mBackgroundPaint.color = getCurrentColor(
            mEnableBackgroundColor,
            mDisableBackgroundColor,
            System.currentTimeMillis()
        )

        val backgroundRect = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
        val radius = (bottom - top) / 2
        canvas?.drawRoundRect(backgroundRect, radius.toFloat(), radius.toFloat(), mBackgroundPaint)
    }

    override fun drawRound(canvas: Canvas?) {
        val currentTime = System.currentTimeMillis()
        val point = getCircleCenter(currentTime)
        mRoundPaint.color = getCurrentColor(mEnableRoundColor, mDisableRoundColor, currentTime)
        canvas?.drawCircle(point.x, point.y, getRoundRadius(currentTime), mRoundPaint)
    }

    private fun getCircleCenter(currentTime: Long): PointF {
        val during = currentTime - mTouchUpTime
        val oneFifth = mDuration / 5.0f
        val threeFifth = oneFifth * 3
        val fourFifth = oneFifth * 4

        return if (mIsEnable) {
            when {
                during <= 0 -> {
                    PointF(
                        mDefaultNormalRadius + mDefaultCircleMaxPadding.toFloat(),
                        mDefaultRoundCenterY.toFloat()
                    )
                }

                during in 1..oneFifth.toInt() -> {
                    val result = during / oneFifth
                    PointF(
                        (mDefaultCircleMinPadding + mDefaultNormalRadius + (mDefaultMaxRadius - mDefaultNormalRadius) * result),
                        mDefaultRoundCenterY.toFloat()
                    )
                }

                during in oneFifth.toInt() + 1..fourFifth.toInt() -> {
                    val initX = mDefaultMaxRadius + mDefaultCircleMinPadding
                    val targetX = width - mDefaultCircleMinPadding - mDefaultMinRadius
                    val result = (during - oneFifth) / threeFifth
                    PointF(initX + (targetX - initX) * result, mDefaultRoundCenterY.toFloat())
                }

                during in fourFifth.toInt() + 1..mDuration -> {
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
            when {
                during <= 0 -> {
                    PointF(
                        width.toFloat() - mDefaultNormalRadius - mDefaultCircleMaxPadding.toFloat(),
                        mDefaultRoundCenterY.toFloat()
                    )
                }

                during in 1..oneFifth.toInt() -> {
                    val initX =
                        width.toFloat() - mDefaultNormalRadius - mDefaultCircleMaxPadding.toFloat()
                    val targetX = width.toFloat() - mDefaultMaxRadius - mDefaultMinRadius.toFloat()
                    val result = during / oneFifth
                    PointF(
                        (initX + (targetX - initX) * result),
                        mDefaultRoundCenterY.toFloat()
                    )
                }

                during in oneFifth.toInt() + 1..fourFifth.toInt() -> {
                    val initX = width.toFloat() - mDefaultMaxRadius - mDefaultMinRadius.toFloat()
                    val targetX = mDefaultCircleMinPadding + mDefaultMinRadius
                    val result = (during - oneFifth) / threeFifth
                    PointF(initX + (targetX - initX) * result, mDefaultRoundCenterY.toFloat())
                }

                during in fourFifth.toInt() + 1..mDuration -> {
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

    private fun getRoundRadius(currentTime: Long): Float {
        val during = currentTime - mTouchUpTime
        val oneFifth = mDuration / 5.0f
        val threeFifth = oneFifth * 3
        val fourFifth = oneFifth * 4
        return when {
            during <= 0 -> {
                mDefaultNormalRadius.toFloat()
            }

            during in 1..oneFifth.toInt() -> {
                mDefaultNormalRadius.toFloat() + (mDefaultMaxRadius - mDefaultNormalRadius) * (during / oneFifth)
            }
            during in oneFifth.toInt() + 1..fourFifth.toInt() -> {
                val result = (during - oneFifth) / threeFifth
                mDefaultMaxRadius.toFloat() + (mDefaultMinRadius - mDefaultMaxRadius) * result
            }
            during in fourFifth.toInt() + 1..mDuration -> {
                val result = (during - fourFifth) / oneFifth
                mDefaultMinRadius.toFloat() + (mDefaultNormalRadius - mDefaultMinRadius) * result
            }

            else -> {
                mDefaultNormalRadius.toFloat()
            }
        }
    }

    fun setRoundDisableColor(@ColorRes color: Int) {
        mDisableRoundColor = color
        invalidate()
    }
}