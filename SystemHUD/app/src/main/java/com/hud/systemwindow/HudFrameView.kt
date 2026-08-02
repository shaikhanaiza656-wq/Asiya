package com.hud.systemwindow

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

/**
 * Procedurally draws the sci-fi "system window" HUD border:
 * cut corners, glowing outline, tech circuit tick marks, dash blocks
 * and dot columns along the sides. Everything here is drawn with the
 * real android.graphics Canvas/Path/Paint APIs — no images, no fake data.
 */
class HudFrameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val accent = 0xFF7FD6FF.toInt()
    private val accentDim = 0xFF3FA9DC.toInt()

    private val corner = 46f
    private val inset = 18f
    private val strokeWidth = 3.5f

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = this@HudFrameView.strokeWidth
        color = accent
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = this@HudFrameView.strokeWidth + 2f
        color = accent
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        maskFilter = BlurMaskFilter(14f, BlurMaskFilter.Blur.NORMAL)
    }

    private val dimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = accentDim
    }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = accent
    }

    init {
        // Software layer required: hardware layer does not support BlurMaskFilter.
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        val l = inset
        val t = inset
        val r = w - inset
        val b = h - inset
        val c = corner

        val outline = Path().apply {
            moveTo(l, t + c)
            lineTo(l, b - c)
            lineTo(l + c * 0.5f, b)
            lineTo(r - c * 2.2f, b)
            lineTo(r - c * 1.6f, b - c * 0.55f)
            lineTo(r - c * 0.4f, b - c * 0.55f)
            lineTo(r, b - c)
            lineTo(r, t + c * 1.6f)
            lineTo(r - c * 0.6f, t + c * 0.6f)
            lineTo(r - c * 0.6f, t)
            lineTo(l + c * 2f, t)
            lineTo(l + c * 1.3f, t + c * 0.55f)
            lineTo(l + c * 0.4f, t + c * 0.55f)
            close()
        }

        canvas.drawPath(outline, glowPaint)
        canvas.drawPath(outline, linePaint)

        // inner thin secondary line, slightly inset, for the "double edge" look
        val innerInset = 9f
        val innerOutline = Path().apply {
            moveTo(l + innerInset, t + c)
            lineTo(l + innerInset, b - c)
            lineTo(r - innerInset, b - c)
            lineTo(r - innerInset, t + c)
            close()
        }
        canvas.drawPath(innerOutline, dimPaint)

        drawDotColumn(canvas, l + 26f, t + c + 40f, b - c - 40f)
        drawDotColumn(canvas, r - 26f, t + c + 40f, b - c - 40f)

        drawDashBlock(canvas, l + c + 10f, t + 6f, 5, true)
        drawDashBlock(canvas, r - c - 70f, b - 22f, 4, true)

        drawSquareCluster(canvas, l + 24f, b - c - 90f)
        drawSquareCluster(canvas, r - 74f, t + c + 60f)
    }

    private fun drawDotColumn(canvas: Canvas, x: Float, top: Float, bottom: Float) {
        var y = top
        var i = 0
        while (y < bottom) {
            if (i % 3 == 0) {
                canvas.drawRect(x - 3f, y - 3f, x + 3f, y + 3f, dimPaint)
            } else {
                canvas.drawCircle(x, y, 2.2f, fillPaint)
            }
            y += 22f
            i++
        }
    }

    private fun drawDashBlock(canvas: Canvas, x: Float, y: Float, count: Int, vertical: Boolean) {
        for (i in 0 until count) {
            if (vertical) {
                val dx = x + i * 9f
                canvas.drawLine(dx, y, dx, y + 16f, linePaint)
            } else {
                val dy = y + i * 9f
                canvas.drawLine(x, dy, x + 16f, dy, linePaint)
            }
        }
    }

    private fun drawSquareCluster(canvas: Canvas, x: Float, y: Float) {
        canvas.drawRect(x, y, x + 14f, y + 14f, dimPaint)
        canvas.drawRect(x + 20f, y + 4f, x + 30f, y + 24f, dimPaint)
        canvas.drawRect(x, y + 22f, x + 10f, y + 40f, dimPaint)
    }
}
