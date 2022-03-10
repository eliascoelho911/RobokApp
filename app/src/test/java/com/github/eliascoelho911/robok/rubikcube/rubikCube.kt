package com.github.eliascoelho911.robok.rubikcube

import android.graphics.BitmapFactory
import com.github.eliascoelho911.robok.rubikcube.RubikCube.Companion.FaceLineHeight
import com.github.eliascoelho911.robok.rubikcube.face.Face
import com.github.eliascoelho911.robok.rubikcube.face.Face.Position.BACK
import com.github.eliascoelho911.robok.rubikcube.face.Face.Position.DOWN
import com.github.eliascoelho911.robok.rubikcube.face.Face.Position.FRONT
import com.github.eliascoelho911.robok.rubikcube.face.Face.Position.LEFT
import com.github.eliascoelho911.robok.rubikcube.face.Face.Position.RIGHT
import com.github.eliascoelho911.robok.rubikcube.face.Face.Position.UP
import com.github.eliascoelho911.robok.util.getColorsOfGrid
import java.io.File
import java.io.FileInputStream

private const val PathPicturesFolder = "src/test/res/pictures/rubik_cube"

val rubikCube: RubikCube by lazy {
    RubikCube(emptySet()).apply {
        File(PathPicturesFolder).list()!!.forEachIndexed { index, filename ->
            val file = File("$PathPicturesFolder/$filename")
            val colors = BitmapFactory.decodeStream(FileInputStream(file))
                .getColorsOfGrid(FaceLineHeight, FaceLineHeight)
            add(Face(positionOrder[index], colors))
            println("${positionOrder[index]}($colors)")
        }
    }
}

private val positionOrder = listOf(FRONT, RIGHT, LEFT, BACK, UP, DOWN)