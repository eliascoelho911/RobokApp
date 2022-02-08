package com.github.eliascoelho911.robok

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.github.eliascoelho911.robok.domain.RubikCubeColor
import com.github.eliascoelho911.robok.domain.RubikCubeSide
import java.io.File
import java.io.FileInputStream

data class RubikPicture(val path: String, val rubikCubeSide: RubikCubeSide)

val rubikSide: Map<RubikPicture, Bitmap> by lazy {
    mutableMapOf<RubikPicture, Bitmap>().apply {
        File("src/test/res/pictures/rubik_cube").list()!!.forEach { filename ->
            val colors = filename.split("/").last().split(".").first()
            val rubikCubeSide = RubikCubeSide()
            colors.forEachIndexed { index, c ->
                val color = colorMapper[c.toString()]
                rubikCubeSide.put(index, color!!)
            }
            put(RubikPicture(filename, rubikCubeSide),
                BitmapFactory.decodeStream(FileInputStream(File("src/test/res/pictures/rubik_cube/$filename"))))
        }
    }
}

private val colorMapper = mapOf(
    "b" to RubikCubeColor.BLUE,
    "g" to RubikCubeColor.GREEN,
    "w" to RubikCubeColor.WHITE,
    "r" to RubikCubeColor.RED,
    "o" to RubikCubeColor.ORANGE,
    "y" to RubikCubeColor.YELLOW,
)