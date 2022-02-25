package com.github.eliascoelho911.robok.camera

import android.graphics.Bitmap
import android.graphics.Rect
import com.github.eliascoelho911.robok.util.rotate

object SideImageCropper {
    fun crop(
        originalImageCaptured: Bitmap,
        cropFrame: Rect,
        previewFrame: Rect,
    ): Bitmap {
        return originalImageCaptured
            .adjustRotation()
            .cropPreview(previewFrame)
            .cropSide(cropFrame, previewFrame)
    }

    private fun Bitmap.cropPreview(previewFrame: Rect): Bitmap {
        val widthFinal = previewFrame.width() * height / previewFrame.height()
        val heightFinal = previewFrame.height() * height / previewFrame.height()
        val leftFinal = (width - widthFinal) / 2
        return Bitmap.createBitmap(this, leftFinal, 0, widthFinal, heightFinal).also { recycle() }
    }

    private fun Bitmap.cropSide(sideFrame: Rect, previewFrame: Rect): Bitmap {
        val widthFinal = sideFrame.width() * height / previewFrame.height()
        val heightFinal = sideFrame.height() * height / previewFrame.height()
        val leftFinal = sideFrame.left * height / previewFrame.height()
        val topFinal = sideFrame.top * height / previewFrame.height()
        return Bitmap.createBitmap(this, leftFinal, topFinal, widthFinal, heightFinal).also { recycle() }
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