package com.hud.systemwindow

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.random.Random

/**
 * Draws the reference-style night scene BUNDLED into the app itself
 * (deep sky gradient, twinkling stars, a distant city skyline silhouette,
 * a lone standing silhouette, faint glass reflection streaks) — all real
 * android.graphics Canvas/Path/Paint drawing, no bitmap asset, no fake
 * placeholder.
 *
 * This intentionally does NOT read the device's live wallpaper: the
 * requirement is that the app always looks the same as the reference art
 * regardless of whose phone it's on, so every element here is generated
 * with a fixed random seed — identical on every launch, every device.
 */
class HudBackgroundView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var pulse = 0.75f

    private val skyPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val starPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val buildingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF040A12.toInt()
        style = Paint.Style.FILL
    }
    private val windowLitPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x66FFD98A
    }
    private val silhouettePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF02060B.toInt()
        style = Paint.Style.FILL
    }
    private val streakPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 40f
    }

    // fixed seed -> identical star field / skyline every run, every device
    private val rng = Random(7331)
    private val stars = (0 until 90).map {
        Triple(rng.nextFloat(), rng.nextFloat() * 0.75f, 1.2f + rng.nextFloat() * 2.2f)
    }
    private val buildingHeights = (0 until 14).map { 0.08f + rng.nextFloat() * 0.30f }
    private val litWindows = buildingHeights.indices.map { rng.nextFloat() > 0.5f }

    private val animator = ValueAnimator.ofFloat(0.5f, 1f, 0.5f).apply {
        duration = 3800
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

        // 1) Night sky vertical gradient
        skyPaint.shader = LinearGradient(
            0f, 0f, 0f, h,
            intArrayOf(0xFF050B14.toInt(), 0xFF0A1826.toInt(), 0xFF0E2233.toInt()),
            floatArrayOf(0f, 0.55f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, w, h, skyPaint)

        // 2) Pulsing blue nebula glow, centered like the reference
        val glowAlpha = (pulse * 130).toInt().coerceIn(50, 140)
        glowPaint.shader = RadialGradient(
            w / 2f, h * 0.4f, kotlin.math.max(w, h) * 0.75f,
            intArrayOf(
                (glowAlpha shl 24) or 0x1E5FA8,
                (glowAlpha / 2 shl 24) or 0x123A66,
                0x00050B14
            ),
            floatArrayOf(0f, 0.45f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, w, h, glowPaint)

        // 3) Stars, twinkling with the same pulse
        for ((fx, fy, r) in stars) {
            val twinkle = (0.4f + 0.6f * ((pulse + fx) % 1f))
            starPaint.color = (( (120 + (120 * twinkle)).toInt().coerceIn(0,255) ) shl 24) or 0xFFFFFF
            canvas.drawCircle(fx * w, fy * h, r, starPaint)
        }

        // 4) Distant city skyline silhouette across the bottom
        val baseY = h * 0.82f
        val bw = w / buildingHeights.size
        buildingHeights.forEachIndexed { i, hf ->
            val bx = i * bw
            val bh = h * hf
            canvas.drawRect(bx, baseY - bh, bx + bw * 0.86f, h, buildingPaint)
            if (litWindows[i]) {
                var wy = baseY - bh + 10f
                while (wy < h - 14f) {
                    canvas.drawRect(bx + bw * 0.3f, wy, bx + bw * 0.42f, wy + 6f, windowLitPaint)
                    wy += 16f
                }
            }
        }

        // 5) Lone standing silhouette, lower-center (head + shoulders + body),
        // matching the reference's small figure looking up at the frame.
        val figX = w * 0.52f
        val figBase = h * 0.98f
        val figH = h * 0.16f
        val headR = figH * 0.16f
        val figure = Path().apply {
            addCircle(figX, figBase - figH, headR, Path.Direction.CW)
            moveTo(figX - figH * 0.22f, figBase - figH + headR * 0.6f)
            lineTo(figX + figH * 0.22f, figBase - figH + headR * 0.6f)
            lineTo(figX + figH * 0.30f, figBase)
            lineTo(figX - figH * 0.30f, figBase)
            close()
        }
        canvas.drawPath(figure, silhouettePaint)

        // 6) Faint diagonal glass reflection streaks (the frosted-glass panel feel)
        streakPaint.shader = LinearGradient(
            0f, 0f, w * 0.4f, h,
            0x00FFFFFF, 0x14FFFFFF, Shader.TileMode.CLAMP
        )
        canvas.drawLine(w * 0.12f, 0f, w * 0.30f, h, streakPaint)
        canvas.drawLine(w * 0.20f, 0f, w * 0.38f, h, streakPaint)
    }
}
