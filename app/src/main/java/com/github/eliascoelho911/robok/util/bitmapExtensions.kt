package com.github.eliascoelho911.robok.util

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Matrix
import java.io.File
import java.io.FileOutputStream
import java.util.Date

fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix: Matrix = Matrix().apply { postRotate(degrees) }

    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true).also { recycle() }
}

fun Bitmap.saveOnStorage(context: Context) {
    val dir = ContextWrapper(context).getDir("pictures", Context.MODE_PRIVATE)
    val file = File(dir, "${Date().time}.jpg")
    val out = FileOutputStream(file)
    compress(Bitmap.CompressFormat.JPEG, 100, out)
}

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

            val color = getPixel(currentItemGlobalX, currentItemGlobalY)
            colors.add(color)
        }
    }

    return colors
}