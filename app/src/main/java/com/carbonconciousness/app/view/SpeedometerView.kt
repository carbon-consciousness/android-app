package com.carbonconciousness.app.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.carbonconciousness.app.R
import java.lang.Float.min

class SpeedometerView (context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var mStartValue: Int
    private var mCurrentValue: Int
    private var mEndValue: Int
    private var mAnimationTime: Int
    private var mStrokeWidth: Float

    private var mBackgroundPaint: Paint = Paint()
    private var mForgroundPaint: Paint = Paint()

    var percentage = 0f

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SpeedometerView,
            0, 0).apply {

            try {
                mStartValue = getInteger(R.styleable.SpeedometerView_startValue, 0)
                mCurrentValue = getInteger(R.styleable.SpeedometerView_currentValue, 0)
                mEndValue = getInteger(R.styleable.SpeedometerView_endValue, 0)
                mAnimationTime = getInteger(R.styleable.SpeedometerView_animationTime, 2000)

                mStrokeWidth = getDimension(R.styleable.SpeedometerView_strokeWidth, 30f)

                mBackgroundPaint.color = getColor(R.styleable.SpeedometerView_backgroundCircleColor, 125)
                mBackgroundPaint.style = Paint.Style.STROKE
                mBackgroundPaint.strokeWidth = mStrokeWidth
                mBackgroundPaint.strokeCap = Paint.Cap.ROUND

                mForgroundPaint.color = getColor(R.styleable.SpeedometerView_foregroundCircleColor, 255)
                mForgroundPaint.style = Paint.Style.STROKE
                mForgroundPaint.strokeWidth = mStrokeWidth - 10
                mForgroundPaint.strokeCap = Paint.Cap.ROUND
            } finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val startAngleSpeedometer = -230f
        val endAngleSpeedometer = 280f

        // Compute ponts
        var cx = width.toFloat()/ 2
        var cy = height.toFloat()/2
        var radius = width.toFloat()/ 2 - mStrokeWidth / 2
        var arcRectStartX = cx  - radius
        var arcRectStartY = cy - radius
        var arcRectStopX = cx  + radius
        var arcRectStopY = cy + radius

        // Draw background circle
        canvas?.drawArc(
            arcRectStartX,
            arcRectStartY,
            arcRectStopX,
            arcRectStopY,
            startAngleSpeedometer,
            endAngleSpeedometer,
            false,
            mBackgroundPaint
        )

        // Draw arc
        canvas?.drawArc(
            arcRectStartX,
            arcRectStartY,
            arcRectStopX,
            arcRectStopY,
            startAngleSpeedometer,
            percentage * endAngleSpeedometer,
            false,
            mForgroundPaint
        )
    }

    fun setCompletedPerentage(completedPercentage: Float) {
        var animator = ValueAnimator.ofFloat(percentage, min(completedPercentage, 1f))
        animator.setDuration((completedPercentage * mAnimationTime).toLong())
        animator.addUpdateListener { animation ->
            run {
                percentage = animation.animatedValue as Float
                invalidate()
            }
        }
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.startDelay = (completedPercentage * mAnimationTime).toLong() / 2
        animator.start()

    }
}