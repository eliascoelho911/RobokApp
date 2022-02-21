package com.github.eliascoelho911.robok.ui.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import com.github.eliascoelho911.robok.R

class BoxHighlight @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    init {
        initAttrs(attrs)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.BoxHighlight,
            0, 0
        ).apply {
            try {
                internalBoxWidth =
                    getDimensionPixelSize(R.styleable.BoxHighlight_internalBoxWidth, 0)
                internalBoxHeight =
                    getDimensionPixelSize(R.styleable.BoxHighlight_internalBoxHeight, 0)
            } finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawHorizontalRects()
        canvas.drawVerticalRects()
    }

    private fun Canvas.drawHorizontalRects() {
        val centerY = height / 2
        val height = centerY - (internalBoxHeight / 2)

        drawTopRect(height)
        drawBottomRect(height)
    }

    private fun Canvas.drawTopRect(height: Int) {
        drawRect(0f, 0f, width.toFloat(), height.toFloat(), colorPaint)
    }

    private fun Canvas.drawBottomRect(height: Int) {
        val top = (this.height - height).toFloat()
        drawRect(0f,
            top,
            width.toFloat(),
            this.height.toFloat(),
            colorPaint)
    }

    private fun Canvas.drawVerticalRects() {
        drawLeftRect()
        drawRightRect()
    }

    private fun Canvas.drawRightRect() {
        val centerX = width / 2
        val centerY = height / 2
        val startY = (centerY - (internalBoxHeight / 2)).toFloat()
        val left = (centerX + internalBoxHeight / 2).toFloat()
        drawRect(left, startY, this.width.toFloat(), startY + internalBoxHeight, colorPaint)
    }

    private fun Canvas.drawLeftRect() {
        val centerX = width / 2
        val centerY = height / 2
        val startY = (centerY - (internalBoxHeight / 2)).toFloat()
        val width = (centerX - (internalBoxWidth / 2)).toFloat()
        drawRect(0f, startY, width, startY + internalBoxHeight.toFloat(), colorPaint)
    }

    private var internalBoxWidth: Int = 0
    private var internalBoxHeight: Int = 0

    @ColorInt
    private var color: Int = Color.BLACK
    private val colorPaint
        get() = Paint().apply {
            color = color
        }
}