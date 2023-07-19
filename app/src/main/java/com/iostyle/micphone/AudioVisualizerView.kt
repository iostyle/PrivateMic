package com.iostyle.micphone

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class AudioVisualizerView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val count = 10

    private val paint = Paint().apply {
        color = Color.RED
        strokeWidth = 5f
        isAntiAlias = true
        style = Paint.Style.STROKE
    }

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
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val dataPoints: MutableList<Float> = mutableListOf()

    fun addDataPoint(value: Float) {
        if (dataPoints.size > count) dataPoints.removeFirst()
        dataPoints.add(value)
        invalidate() // 触发重绘
    }

    private val path = Path()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
//
//        // 绘制折线
//        linePaint.strokeWidth = 4f
//        for (i in 0 until dataPoints.size - 1) {
//            val startX = i * (width / (dataPoints.size - 1))
//            val startY = height - dataPoints[i] * (height / 2f)
//
//            val endX = (i + 1) * (width / (dataPoints.size - 1))
//            val endY = height - dataPoints[i + 1] * (height / 2f)
//
//            canvas.drawLine(startX, startY, endX, endY, linePaint)
//        }

        if (dataPoints.size < 2) {
            return
        }

        path.reset()

        val firstPoint = dataPoints[0]
        path.moveTo(0f, height - firstPoint)

        val lastPointIndex = dataPoints.size - 1

        for (i in 0 until lastPointIndex) {
            val currentPoint = dataPoints[i]
            val nextPoint = dataPoints[i + 1]

            val startX = ((i - 1) * width / lastPointIndex)
            val endX = ((i) * width / lastPointIndex)
            val startY = height - currentPoint * (height / 2f)
            val endY = height - nextPoint * (height / 2f)

            val controlX1 = startX + (endX - startX) / 2
            val controlY1 = startY
            val controlX2 = startX + (endX - startX) / 2
            val controlY2 = endY

            path.cubicTo(controlX1, controlY1, controlX2, controlY2, endX, endY)
        }

        canvas.drawPath(path, paint)

        canvas.drawLine(0f, height / 2f, width, height / 2f, linePaint)

        // 绘制数据点圆圈
        circlePaint.style = Paint.Style.FILL
        circlePaint.strokeWidth = 0f
        for (i in dataPoints.indices) {
            val x = (i - 1) * (width / (dataPoints.size - 1))
            val y = height - dataPoints[i] * (height / 2f)

            canvas.drawCircle(x, y, 8f, circlePaint)

            // float保留两位小数
            canvas.drawText(String.format("%.2f", dataPoints[i]), x, y - 20, fontPaint)
        }
    }
}
