package com.github.eliascoelho911.robok.domain

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.annotation.ColorInt
import com.github.eliascoelho911.robok.camera.SideImageCropper
import com.github.eliascoelho911.robok.domain.constants.RubikCubeConstants.SideLineHeight
import com.github.eliascoelho911.robok.util.getColorsOfGrid

class RubikCube(val sides: Set<Side>) {
    class Side(val position: SidePosition, @ColorInt val colors: List<Int>) {
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
                sidePosition: SidePosition,
            ): Side {
                val sideImage = SideImageCropper.crop(originalImageCaptured,
                    cropFrame, previewFrame)
                val colorsOfSide = sideImage.getColorsOfGrid(SideLineHeight, SideLineHeight)
                return Side(sidePosition, colorsOfSide)
            }
        }
    }

    enum class SidePosition {
        LEFT, FRONT, UP, DOWN, RIGHT, BOTTOM;
    }
}