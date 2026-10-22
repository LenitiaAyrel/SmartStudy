package com.example.smartstudy

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

class StudyProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(107, 79, 179)
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(50, 50, 50)
        textSize = 28f
        typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.NORMAL
        )
    }

    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(40, 40, 40)
        textSize = 36f
        typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )
    }

    private val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(210, 205, 220)
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    private var subjects: List<String> = emptyList()
    private var minutes: List<Int> = emptyList()

    fun setData(
        subjectNames: List<String>,
        studyMinutes: List<Int>
    ) {
        subjects = subjectNames
        minutes = studyMinutes
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (subjects.isEmpty()) {
            textPaint.textAlign = Paint.Align.LEFT
            textPaint.textSize = 28f

            canvas.drawText(
                "No study data yet.",
                40f,
                80f,
                textPaint
            )

            return
        }

        val chartLeft = 55f
        val chartTop = 80f
        val chartRight = width - 20f
        val chartBottom = height - 110f

        // Title
        textPaint.textAlign = Paint.Align.LEFT

        canvas.drawText(
            "Study Progress",
            chartLeft,
            40f,
            titlePaint
        )

        val maxMinutes =
            max(
                minutes.maxOrNull() ?: 1,
                1
            )

        // Y axis
        canvas.drawLine(
            chartLeft,
            chartTop,
            chartLeft,
            chartBottom,
            axisPaint
        )

        // X axis
        canvas.drawLine(
            chartLeft,
            chartBottom,
            chartRight,
            chartBottom,
            axisPaint
        )

        val availableWidth =
            chartRight - chartLeft

        val numberOfBars =
            subjects.size

        val slotWidth =
            availableWidth / numberOfBars

        val barWidth =
            slotWidth * 0.55f

        for (i in subjects.indices) {

            val value =
                minutes.getOrNull(i) ?: 0

            val barHeight =
                if (maxMinutes > 0) {
                    (value.toFloat() / maxMinutes) *
                            (chartBottom - chartTop)
                } else {
                    0f
                }

            val centerX =
                chartLeft +
                        slotWidth * i +
                        slotWidth / 2

            val left =
                centerX - barWidth / 2

            val top =
                chartBottom - barHeight

            val right =
                centerX + barWidth / 2

            // Draw bar
            canvas.drawRect(
                left,
                top,
                right,
                chartBottom,
                barPaint
            )

            // Draw value above bar
            textPaint.textSize = 24f
            textPaint.textAlign = Paint.Align.CENTER

            canvas.drawText(
                "$value",
                centerX,
                top - 8f,
                textPaint
            )

            // Draw subject name vertically
            canvas.save()

            canvas.rotate(
                -90f,
                centerX,
                chartBottom + 18f
            )

            textPaint.textSize = 21f
            textPaint.textAlign = Paint.Align.LEFT

            var label = subjects[i]

            if (label.length > 12) {
                label = label.take(11) + "…"
            }

            canvas.drawText(
                label,
                centerX,
                chartBottom + 18f,
                textPaint
            )

            canvas.restore()
        }
    }
}