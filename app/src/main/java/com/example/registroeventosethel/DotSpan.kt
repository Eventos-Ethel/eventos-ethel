package com.example.registroeventosethel

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.LineBackgroundSpan

class DotSpan(private val radius: Float, private val color: Int) : LineBackgroundSpan {
    override fun drawBackground(
        canvas: Canvas,
        paint: Paint,
        left: Int,
        right: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        charSequence: CharSequence,
        start: Int,
        end: Int,
        lineNum: Int
    ) {
        val oldColor = paint.color
        paint.color = color
        canvas.drawCircle((left + right) / 2f, bottom + radius, radius, paint)
        paint.color = oldColor
    }
}
