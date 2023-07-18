package com.iostyle.micphone

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class AudioVisualizerView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val count = 20

    private val linePaint = Paint().apply {
        color = Color.RED
        strokeWidth = 2f
        isAntiAlias = true
    }

    private val circlePaint = Paint().apply {
        color = Color.RED
        isAntiAlias = true
    }

    private val fontPaint = Paint().apply {
        color = Color.RED
        textSize = 40f
        isAntiAlias = true
    }

    private val dataPoints: MutableList<Float> = mutableListOf()

    fun addDataPoint(value: Float) {
        if (dataPoints.size > count) dataPoints.removeFirst()
        dataPoints.add(value)
        invalidate() // 触发重绘
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        // 绘制折线
        linePaint.strokeWidth = 4f
        for (i in 0 until dataPoints.size - 1) {
            val startX = i * (width / (dataPoints.size - 1))
            val startY = height - dataPoints[i] * (height / 2f)

            val endX = (i + 1) * (width / (dataPoints.size - 1))
            val endY = height - dataPoints[i + 1] * (height / 2f)

            canvas.drawLine(startX, startY, endX, endY, linePaint)
        }

        canvas.drawLine(0f, height / 2f, width, height / 2f, linePaint)

        // 绘制数据点圆圈
        circlePaint.style = Paint.Style.FILL
        circlePaint.strokeWidth = 0f
        for (i in dataPoints.indices) {
            val x = i * (width / (dataPoints.size - 1))
            val y = height - dataPoints[i] * (height / 2f)

            canvas.drawCircle(x, y, 8f, circlePaint)

            // float保留两位小数
            canvas.drawText(String.format("%.2f", dataPoints[i]), x, y, fontPaint)
        }
    }
}
