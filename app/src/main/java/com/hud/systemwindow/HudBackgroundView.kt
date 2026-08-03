package com.hud.systemwindow

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

/**
 * Draws the misty, glowing "holographic glass" background seen in the
 * reference art: a deep-blue radial glow behind the frame plus faint
 * diagonal light streaks, with a slow real pulse animation
 * (ValueAnimator driving repaints — no fake/looping GIF, actual draw calls).
 */
class HudBackgroundView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var pulse = 0.75f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val streakPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 40f
    }

    private val animator = ValueAnimator.ofFloat(0.55f, 1f, 0.55f).apply {
        duration = 4200
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener {
            pulse = it.animatedValue as Float
            invalidate()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        animator.start()
    }

    override fun onDetachedFromWindow() {
        animator.cancel()
        super.onDetachedFromWindow()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        // base near-black backdrop
        canvas.drawColor(0xFF050B14.toInt())

        // deep blue nebula glow, centered, pulsing in intensity
        val glowAlpha = (pulse * 140).toInt().coerceIn(60, 150)
        paint.shader = RadialGradient(
            w / 2f, h * 0.42f, kotlin.math.max(w, h) * 0.75f,
            intArrayOf(
                (glowAlpha shl 24) or 0x1E5FA8,
                (glowAlpha / 2 shl 24) or 0x123A66,
                0x00050B14
            ),
            floatArrayOf(0f, 0.45f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, w, h, paint)

        // faint diagonal glass reflection streaks, top-left to bottom-right
        streakPaint.shader = LinearGradient(
            0f, 0f, w * 0.4f, h,
            0x00FFFFFF, 0x14FFFFFF, Shader.TileMode.CLAMP
        )
        canvas.drawLine(w * 0.12f, 0f, w * 0.30f, h, streakPaint)
        canvas.drawLine(w * 0.20f, 0f, w * 0.38f, h, streakPaint)
    }
}
