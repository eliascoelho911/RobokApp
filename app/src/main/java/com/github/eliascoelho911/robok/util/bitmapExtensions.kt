package com.github.eliascoelho911.robok.util

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix

fun Bitmap.getColorsOfGrid(
    amountRows: Int,
    amountColumns: Int,
): List<Int> {
    val itemWidth = width / amountColumns
    val itemHeight = height / amountRows
    val itemRelativeCenterX = itemWidth / 2
    val itemRelativeCenterY = itemHeight / 2
    val colors = mutableListOf<Int>()

    for (y in 0 until amountRows) {
        for (x in 0 until amountColumns) {
            val currentItemGlobalX = (itemWidth * x) + itemRelativeCenterX
            val currentItemGlobalY = (itemHeight * y) + itemRelativeCenterY

            val pixel = getPixel(currentItemGlobalX, currentItemGlobalY)
            colors.add(Color.rgb(pixel, pixel, pixel))
        }
    }

    return colors
}

fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix: Matrix = Matrix().apply { postRotate(degrees) }

    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true).also {
        recycle()
    }
}