package com.github.eliascoelho911.robok.ui.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.domain.constants.RubikCubeConstants.SideLineHeight
import com.github.eliascoelho911.robok.domain.RubikCube
import com.github.eliascoelho911.robok.domain.RubikCubeSide
import kotlin.math.min

private const val SidesOnHorizontal = 4
private const val SidesOnVertical = 3

class RubikCubePreview @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    var rubikCube: RubikCube? = null
        set(value) {
            field = value
            invalidate()
        }
    var boxItemSize: Int = 0
        set(value) {
            field = value
            invalidate()
        }
    var boxItemRadius: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    init {
        initAttrs(attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = SidesOnHorizontal * boxItemSize * SideLineHeight
        val desiredHeight = SidesOnVertical * boxItemSize * SideLineHeight
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> {
                widthSize
            }
            MeasureSpec.AT_MOST -> {
                min(widthSize, desiredWidth)
            }
            else -> {
                desiredWidth
            }
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> {
                heightSize
            }
            MeasureSpec.AT_MOST -> {
                min(heightSize, desiredHeight)
            }
            else -> {
                desiredHeight
            }
        }

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawSides()
    }

    private fun initAttrs(attrs: AttributeSet?) {
        internalMargin = resources.getDimensionPixelOffset(R.dimen.rubik_color_preview_internal_margin)
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.RubikCubePreview,
            0, 0
        ).apply {
            try {
                boxItemSize = getDimensionPixelSize(R.styleable.RubikCubePreview_boxItemSize, 0)
                boxItemRadius = getDimensionPixelSize(R.styleable.RubikCubePreview_boxItemRadius, 0)
            } finally {
                recycle()
            }
        }
    }

    private fun Canvas.drawSides() {
        rubikCube?.let { rubikCube ->
            val sizeOfSide = boxItemSize * SideLineHeight
            rubikCube.sides.forEach { side ->
                val x = side.position.x * sizeOfSide
                val y = side.position.y * sizeOfSide
                drawColors(x, y, side)
            }
        }
    }

    private fun Canvas.drawColors(
        parentX: Int,
        parentY: Int,
        side: RubikCubeSide,
    ) {
        side.colors.forEach { position, box ->
            val left = parentX + (boxItemSize * position.x) + internalMargin
            val right = left + boxItemSize - internalMargin
            val top = parentY + (boxItemSize * position.y) + internalMargin
            val bottom = top + boxItemSize - internalMargin
            drawRoundRect(left.toFloat(),
                top.toFloat(),
                right.toFloat(),
                bottom.toFloat(),
                boxItemRadius.toFloat(),
                boxItemRadius.toFloat(),
                boxPaint(box.androidColor(context)))
        }
    }

    private val boxPaint: (Color) -> Paint = {
        Paint().apply {
            color = it.toArgb()
        }
    }
    private var internalMargin: Int = 0
}