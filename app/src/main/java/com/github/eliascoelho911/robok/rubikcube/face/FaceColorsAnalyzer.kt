package com.github.eliascoelho911.robok.rubikcube.face

import android.annotation.SuppressLint
import android.graphics.Rect
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.github.eliascoelho911.robok.rubikcube.RubikCube.Companion.FaceLineHeight
import com.github.eliascoelho911.robok.util.converters.toBitmap
import com.github.eliascoelho911.robok.util.getColorsOfGrid

class FaceColorsAnalyzer(
    private val cropFrame: Rect,
    private val previewFrame: Rect,
    private val onSuccess: (colors: List<Int>) -> Unit,
) : ImageAnalysis.Analyzer {
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        image.image?.toBitmap()?.let { bitmap ->
            val faceImage = FaceImageCropper.crop(bitmap, cropFrame, previewFrame)
            image.close()
            val colors = faceImage.getColorsOfGrid(FaceLineHeight, FaceLineHeight)
            onSuccess(colors)
        }
    }
}