package com.github.eliascoelho911.robok.ui.analyzers

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Rect
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.github.eliascoelho911.robok.rubikcube.CUBE_CELLS_PER_LINE
import com.github.eliascoelho911.robok.util.getColorsOfGrid
import com.github.eliascoelho911.robok.util.rotate
import com.github.eliascoelho911.robok.util.toBitmap

class FaceColorsAnalyzer(
    private val cropFrame: Rect,
    private val previewFrame: Rect,
    private val faceImageCropper: FaceImageCropper,
    private val onSuccess: (colors: List<Int>) -> Unit,
) : ImageAnalysis.Analyzer {
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        runCatching {
            image.image?.toBitmap()?.let { bitmap ->
                val faceImage = faceImageCropper.crop(bitmap, cropFrame, previewFrame)
                image.close()
                faceImage.getColorsOfGrid(CUBE_CELLS_PER_LINE, CUBE_CELLS_PER_LINE)
            }
        }.onSuccess {
            it?.run { onSuccess(this) }
        }
    }
}

class FaceImageCropper {
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
        return Bitmap.createBitmap(this, leftFinal, topFinal, widthFinal, heightFinal)
            .also { recycle() }
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