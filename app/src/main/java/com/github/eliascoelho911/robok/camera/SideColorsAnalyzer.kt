package com.github.eliascoelho911.robok.camera

import android.annotation.SuppressLint
import android.graphics.Rect
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.github.eliascoelho911.robok.domain.constants.RubikCubeConstants.SideLineHeight
import com.github.eliascoelho911.robok.util.converters.toBitmap
import com.github.eliascoelho911.robok.util.getColorsOfGrid

class SideColorsAnalyzer(
    private val cropFrame: Rect,
    private val previewFrame: Rect,
    private val onSuccess: (colors: List<Int>) -> Unit,
) : ImageAnalysis.Analyzer {
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        image.image?.toBitmap()?.let { bitmap ->
            val sideImage = SideImageCropper.crop(bitmap, cropFrame, previewFrame)
            image.close()
            val colors = sideImage.getColorsOfGrid(SideLineHeight, SideLineHeight)
            onSuccess(colors)
        }
    }
}