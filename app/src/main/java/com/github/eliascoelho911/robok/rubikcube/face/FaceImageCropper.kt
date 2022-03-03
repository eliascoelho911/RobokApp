package com.github.eliascoelho911.robok.rubikcube.face

import android.graphics.Bitmap
import android.graphics.Rect
import com.github.eliascoelho911.robok.util.rotate

object FaceImageCropper {
    fun crop(
        originalImageCaptured: Bitmap,
        cropFrame: Rect,
        previewFrame: Rect,
    ): Bitmap {
        return originalImageCaptured
            .adjustRotation()
            .cropPreview(previewFrame)
            .cropFace(cropFrame, previewFrame)
    }

    private fun Bitmap.cropPreview(previewFrame: Rect): Bitmap {
        val widthFinal = previewFrame.width() * height / previewFrame.height()
        val heightFinal = previewFrame.height() * height / previewFrame.height()
        val leftFinal = (width - widthFinal) / 2
        return Bitmap.createBitmap(this, leftFinal, 0, widthFinal, heightFinal).also { recycle() }
    }

    private fun Bitmap.cropFace(faceFrame: Rect, previewFrame: Rect): Bitmap {
        val widthFinal = faceFrame.width() * height / previewFrame.height()
        val heightFinal = faceFrame.height() * height / previewFrame.height()
        val leftFinal = faceFrame.left * height / previewFrame.height()
        val topFinal = faceFrame.top * height / previewFrame.height()
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