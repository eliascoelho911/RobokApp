package com.github.eliascoelho911.robok.util

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix

fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix: Matrix = Matrix().apply { postRotate(degrees) }

    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true).also {
        recycle()
    }
}