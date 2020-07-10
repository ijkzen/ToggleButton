package com.github.ijkzen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

class ToggleButton : View {

    private var mDefaultWidth = 0

    private var mDefaultHeight = 0

    private val mBackgroundPaint = Paint()

    private val mForegroundPaint = Paint()

    private val mRoundPaint = Paint()

    private var mEnableBackgroundColor = 0

    private var mDisableBackgroundColor = 0

    private var mEnableRoundColor = 0

    private var mDisableRoundColor = 0

    private var mIsEnable = false

    private var mTouchUpTime: Long = 0

    private var mDefaultMinRadius = 0

    private var mDefaultNormalRadius = 0

    private var mDefaultMiddleRadius = 0

    private var mDefaultMaxRadius = 0

    private var mDefaultCircleMaxPadding = 0

    private var mDefaultCircleMinPadding = 0

    private var mDefaultRoundCenterY = 0

    private var mIsChanged = false

    private var mDuration = 300


    constructor(context: Context?) : super(context) {
        initAttrs(null)
    }


    constructor(context: Context?, attributes: AttributeSet?) : super(context, attributes) {
        initAttrs(attributes)
    }

    private fun initAttrs(attributes: AttributeSet?) {

        val typedArray = context.obtainStyledAttributes(attributes,
            R.styleable.ToggleButton
        )
        mEnableBackgroundColor = typedArray.getColor(
            R.styleable.ToggleButton_enableBackgroundColor,
            ContextCompat.getColor(context,
                R.color.enableBackgroundColor
            )
        )

        mEnableRoundColor = typedArray.getColor(
            R.styleable.ToggleButton_enableRoundColor,
            ContextCompat.getColor(context,
                R.color.enableRoundColor
            )
        )

        mDisableBackgroundColor = typedArray.getColor(
            R.styleable.ToggleButton_disableBackgroundColor,
            ContextCompat.getColor(context,
                R.color.disableBackgroundColor
            )
        )

        mDisableRoundColor = typedArray.getColor(
            R.styleable.ToggleButton_disableRoundColor,
            ContextCompat.getColor(context,
                R.color.disableRoundColor
            )
        )

        val duration = typedArray.getInt(R.styleable.ToggleButton_duration, 300)
        mDuration = if (duration < 300) {
            300
        } else {
            duration
        }

        mIsEnable = typedArray.getBoolean(R.styleable.ToggleButton_enable, false)

        typedArray.recycle()
        initPaint()
    }

    private fun initPaint() {
        mBackgroundPaint.isAntiAlias = true
        mBackgroundPaint.style = Paint.Style.FILL
        mForegroundPaint.isAntiAlias = true
        mForegroundPaint.style = Paint.Style.FILL

        mRoundPaint.isAntiAlias = true
        mRoundPaint.style = Paint.Style.FILL
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        initDefaultSize()
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mDefaultWidth, mDefaultHeight)
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mDefaultWidth, heightSize)
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, mDefaultHeight)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mDefaultRoundCenterY = height / 2

        mDefaultMinRadius = mDefaultRoundCenterY - convertDp2Px(
            8,
            context
        )
        mDefaultNormalRadius = mDefaultRoundCenterY - convertDp2Px(
            7,
            context
        )
        mDefaultMiddleRadius = mDefaultRoundCenterY - convertDp2Px(
            5,
            context
        )
        mDefaultMaxRadius = mDefaultRoundCenterY - convertDp2Px(
            3,
            context
        )
    }

    private fun initDefaultSize() {
        mDefaultWidth = convertDp2Px(56, context)
        mDefaultHeight =
            convertDp2Px(30, context)

        mDefaultCircleMinPadding =
            convertDp2Px(4, context)
        mDefaultCircleMaxPadding =
            convertDp2Px(8, context)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawBackground(canvas)
        drawForeground(canvas)
        drawRound(canvas)

        if (System.currentTimeMillis() - mTouchUpTime <= mDuration + 200) {
            invalidate()
        }
    }

    private fun drawBackground(canvas: Canvas?) {
        val left = paddingStart
        val right = width - paddingEnd
        val top = paddingTop
        val bottom = height - paddingBottom

        mBackgroundPaint.color = mDisableBackgroundColor

        val backgroundRect = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
        val radius = (bottom - top) / 2
        canvas?.drawRoundRect(backgroundRect, radius.toFloat(), radius.toFloat(), mBackgroundPaint)
    }

    private fun drawForeground(canvas: Canvas?) {
        val left = paddingStart
        val right = width - paddingEnd
        val top = paddingTop
        val bottom = height - paddingBottom

        mBackgroundPaint.color = getForegroundColor()

        val backgroundRect = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
        val radius = (bottom - top) / 2
        canvas?.drawRoundRect(backgroundRect, radius.toFloat(), radius.toFloat(), mBackgroundPaint)
    }

    private fun getForegroundColor(): Int {
        if (!mIsChanged) {
            return if (mIsEnable) {
                mEnableBackgroundColor
            } else {
                mDisableBackgroundColor
            }
        }

        val targetColor: Int = if (mIsEnable) mEnableBackgroundColor else mDisableBackgroundColor

        val tmp = (System.currentTimeMillis() - mTouchUpTime) / mDuration.toFloat()

        return when {
            tmp <= 0 -> {
                targetColor
            }
            tmp > 1 -> {
                targetColor
            }
            else -> {
                val drawable = ColorDrawable(targetColor)
                drawable.alpha = (tmp * 100).toInt()
                drawable.color
            }
        }
    }

    private fun drawRound(canvas: Canvas?) {
        val currentTime = System.currentTimeMillis()
        val point = getCircleCenter(currentTime)
        mRoundPaint.color = getRoundColor(currentTime)
        canvas?.drawCircle(point.x, point.y, getRoundRadius(currentTime), mRoundPaint)
    }

    private fun getRoundColor(currentTime: Long): Int {
        if (!mIsChanged) {
            return if (mIsEnable) {
                mEnableRoundColor
            } else {
                mDisableRoundColor
            }
        }

        val a: Int
        val b: Int
        if (mIsEnable) {
            a = mDisableRoundColor
            b = mEnableRoundColor
        } else {
            a = mEnableRoundColor
            b = mDisableRoundColor
        }

        val tmp :Float= (currentTime - mTouchUpTime) / mDuration.toFloat()

        return when {
            tmp <= 0 -> {
                a
            }
            tmp > 1 -> {
                b
            }
            else -> {
                val alpha = 100 - (tmp * 100).toInt()
                if (alpha > 50) {
                    val drawable = ColorDrawable(a)
                    drawable.alpha = alpha
                    drawable.color
                } else {
                    val drawable = ColorDrawable(b)
                    drawable.alpha = 100 - alpha
                    drawable.color
                }
            }
        }
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
        mDuration = if (duration < 300) {
            300
        } else {
            duration
        }
    }
}