package com.github.eliascoelho911.robok.rubikcube.side

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.annotation.ColorInt
import com.github.eliascoelho911.robok.rubikcube.RubikCube
import com.github.eliascoelho911.robok.rubikcube.RubikCubeConstants
import com.github.eliascoelho911.robok.util.getColorsOfGrid

class Side(val position: RubikCube.SidePosition, @ColorInt val colors: List<Int>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Side

        return position == other.position
    }

    override fun hashCode(): Int {
        return position.hashCode()
    }

    object Factory {
        fun createByImageCaptured(
            originalImageCaptured: Bitmap,
            cropFrame: Rect,
            previewFrame: Rect,
            sidePosition: RubikCube.SidePosition,
        ): Side {
            val sideImage = SideImageCropper.crop(originalImageCaptured,
                cropFrame, previewFrame)
            val colorsOfSide = sideImage.getColorsOfGrid(RubikCubeConstants.SideLineHeight,
                RubikCubeConstants.SideLineHeight)
            return Side(sidePosition, colorsOfSide)
        }
    }
}