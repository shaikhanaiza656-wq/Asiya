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
 * Draws the frosted "glass" tint that sits on top of the REAL device
 * wallpaper (shown behind the window via android:windowShowWallpaper,
 * see themes.xml + MainActivity's window blur setup — real Android
 * APIs, not a fake/painted backdrop).
 *
 * This view is intentionally translucent: it only adds a deep-blue
 * night tint, a soft pulsing glow, and faint diagonal glass reflection
 * streaks — the same "glass panel over your wallpaper" effect shown in
 * the reference concept's "REAL TRANSPARENT GLASS LOOK" callout.
 * It never paints a fully opaque color, or the wallpaper underneath
 * would be hidden again.
 */
class HudBackgroundView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var pulse = 0.75f
    private val tintPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
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

        // Flat, semi-transparent night-blue tint over the real wallpaper —
        // NOT drawColor() at full alpha, so whatever is behind the window
        // (the device's actual wallpaper) still shows through.
        tintPaint.color = 0xB0060C16.toInt()
        canvas.drawRect(0f, 0f, w, h, tintPaint)

        // Soft pulsing blue glow, also translucent, centered like the reference.
        val glowAlpha = (pulse * 100).toInt().coerceIn(30, 110)
        glowPaint.shader = RadialGradient(
            w / 2f, h * 0.42f, kotlin.math.max(w, h) * 0.75f,
            intArrayOf(
                (glowAlpha shl 24) or 0x1E5FA8,
                (glowAlpha / 2 shl 24) or 0x123A66,
                0x00050B14
            ),
            floatArrayOf(0f, 0.45f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, w, h, glowPaint)

        // Faint diagonal glass reflection streaks, top-left to bottom-right.
        streakPaint.shader = LinearGradient(
            0f, 0f, w * 0.4f, h,
            0x00FFFFFF, 0x14FFFFFF, Shader.TileMode.CLAMP
        )
        canvas.drawLine(w * 0.12f, 0f, w * 0.30f, h, streakPaint)
        canvas.drawLine(w * 0.20f, 0f, w * 0.38f, h, streakPaint)
    }
}
