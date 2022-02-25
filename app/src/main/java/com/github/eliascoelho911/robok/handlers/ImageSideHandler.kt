package com.github.eliascoelho911.robok.handlers

import android.graphics.Bitmap
import android.graphics.Rect
import com.github.eliascoelho911.robok.util.rotate

class ImageSideHandler(private val originalImageCaptured: Bitmap) {
    fun crop(
        cropFrame: Rect,
        previewFrame: Rect,
    ): Bitmap {
        originalImageCaptured.adjustRotation().let {
            val widthFinal = cropFrame.width() * it.width / previewFrame.width()
            val heightFinal = cropFrame.height() * it.height / previewFrame.height()
            val leftFinal = cropFrame.left * it.width / previewFrame.width()
            val topFinal = cropFrame.top * it.height / previewFrame.height()
            return Bitmap.createBitmap(it, leftFinal, topFinal, widthFinal, heightFinal)
        }
    }

    private fun Bitmap.adjustRotation(): Bitmap {
        val orientationIsIncorrect = width > height
        return if (orientationIsIncorrect) {
            rotate(90f)
        } else {
            this
        }
    }
}