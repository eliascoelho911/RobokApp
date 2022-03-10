package com.github.eliascoelho911.robok.rubikcube.face

import com.github.eliascoelho911.robok.rubikcube.face.Face.Position
import com.github.eliascoelho911.robok.rubikcube.face.Face.Position.BACK
import com.github.eliascoelho911.robok.rubikcube.face.Face.Position.DOWN
import com.github.eliascoelho911.robok.rubikcube.face.Face.Position.FRONT
import com.github.eliascoelho911.robok.rubikcube.face.Face.Position.LEFT
import com.github.eliascoelho911.robok.rubikcube.face.Face.Position.RIGHT
import com.github.eliascoelho911.robok.rubikcube.face.Face.Position.UP

class FaceSorter private constructor(private vararg val order: Position) {
    private val orderWithIndex = order.withIndex()

    fun sort(faces: List<Face>): List<Face> {
        return faces.map { face ->
            orderWithIndex.single { it.value == face.position }.index to face
        }.sortedBy { it.first }.map { it.second }
    }

    companion object {
        val AnimCube = FaceSorter(UP, DOWN, FRONT, BACK, LEFT, RIGHT)
    }
}