package com.github.eliascoelho911.robok.util

import android.graphics.Bitmap
import android.graphics.Color

fun Bitmap.getColorsOfGrid(
    amountRows: Int,
    amountColumns: Int,
): List<Color> {
    val itemWidth = width / amountColumns
    val itemHeight = height / amountRows
    val itemRelativeCenterX = itemWidth / 2
    val itemRelativeCenterY = itemHeight / 2
    val colors = mutableListOf<Color>()

    for (y in 0 until amountRows) {
        for (x in 0 until amountColumns) {
            val currentItemGlobalX = (itemWidth * x) + itemRelativeCenterX
            val currentItemGlobalY = (itemHeight * y) + itemRelativeCenterY

            val pixel = getPixel(currentItemGlobalX, currentItemGlobalY)
            val red = Color.red(pixel)
            val green = Color.green(pixel)
            val blue = Color.blue(pixel)
            colors.add(Color.valueOf(Color.rgb(red, green, blue)))
        }
    }

    return colors
}