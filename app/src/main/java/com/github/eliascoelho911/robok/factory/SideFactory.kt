package com.github.eliascoelho911.robok.factory

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import com.github.eliascoelho911.robok.domain.RubikCube
import com.github.eliascoelho911.robok.domain.SidePosition
import com.github.eliascoelho911.robok.domain.constants.RubikCubeConstants.SideLineHeight
import com.github.eliascoelho911.robok.handlers.ImageSideHandler

object SideFactory {

    fun createByImageCaptured(
        originalImageCaptured: Bitmap,
        cropFrame: Rect,
        previewFrame: Rect,
        sidePosition: SidePosition,
    ): RubikCube.Side {
        val sideImage = ImageSideHandler(originalImageCaptured).crop(cropFrame, previewFrame)
        val colorsOfSide = sideImage.getColorsOfSide()
        return RubikCube.Side(sidePosition, colorsOfSide)
    }

    private fun Bitmap.getColorsOfSide(): List<Int> {
        val itemWidth = width / SideLineHeight
        val itemHeight = height / SideLineHeight
        val itemRelativeCenterX = itemWidth / 2
        val itemRelativeCenterY = itemHeight / 2
        val colors = mutableListOf<Int>()

        for (y in 0 until SideLineHeight) {
            for (x in 0 until SideLineHeight) {
                val currentItemGlobalX = (itemWidth * x) + itemRelativeCenterX
                val currentItemGlobalY = (itemHeight * y) + itemRelativeCenterY

                val pixel = getPixel(currentItemGlobalX, currentItemGlobalY)
                colors.add(pixel)
            }
        }

        return colors
    }
}