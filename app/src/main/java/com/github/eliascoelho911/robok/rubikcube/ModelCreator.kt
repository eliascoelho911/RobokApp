package com.github.eliascoelho911.robok.rubikcube

import com.github.eliascoelho911.robok.rubikcube.face.Face
import com.github.eliascoelho911.robok.rubikcube.face.Position
import com.github.eliascoelho911.robok.rubikcube.face.Position.BACK
import com.github.eliascoelho911.robok.rubikcube.face.Position.DOWN
import com.github.eliascoelho911.robok.rubikcube.face.Position.FRONT
import com.github.eliascoelho911.robok.rubikcube.face.Position.LEFT
import com.github.eliascoelho911.robok.rubikcube.face.Position.RIGHT
import com.github.eliascoelho911.robok.rubikcube.face.Position.UP

abstract class ModelCreator {
    protected abstract val faceOrder: List<Position>

    fun create(rubikCube: RubikCube): Model {
        val faces = rubikCube.faces
        val orderedFaces = reorderFaces(faces)
        val orderedFacelets = reorderFacelets(orderedFaces)
        val colorMapper = createColorMapper(rubikCube.distinctColors)
        return orderedFacelets.joinToString(separator = "") { face ->
            face.colors.map { colorMapper[it] }.joinToString(separator = "")
        }
    }

    protected open fun reorderFacelets(faces: List<Face>): List<Face> = faces

    protected open fun createColorMapper(distinctColors: Map<Position, Int>): Map<Int, String> =
        distinctColors.values.withIndex().associate { it.value to it.index.toString() }

    private fun reorderFaces(faces: List<Face>): List<Face> {
        val faceOrderWithIndex = faceOrder.withIndex()
        return faces.map { face ->
            faceOrderWithIndex.single { it.value == face.position }.index to face
        }.sortedBy { it.first }.map { it.second }
    }
}

class DefaultModelCreator : ModelCreator() {
    override val faceOrder: List<Position> = listOf(UP, DOWN, FRONT, BACK, LEFT, RIGHT)
}

open class AnimCubeModelCreator : ModelCreator() {
    override val faceOrder: List<Position> = listOf(UP, DOWN, FRONT, BACK, LEFT, RIGHT)

    private val orderOfFaceletsUp = listOf(6, 7, 8, 3, 4, 5, 0, 1, 2)
    private val orderOfFaceletsDown = listOf(0, 3, 6, 1, 4, 7, 2, 5, 8)
    private val orderOfFaceletsFront = listOf(0, 3, 6, 1, 4, 7, 2, 5, 8)
    private val orderOfFaceletsBack = listOf(0, 3, 6, 1, 4, 7, 2, 5, 8)
    private val orderOfFaceletsLeft = listOf(2, 1, 0, 5, 4, 3, 8, 7, 6)
    private val orderOfFaceletsRight = listOf(0, 3, 6, 1, 4, 7, 2, 5, 8)
    private val orderOfFaceletsMap = mapOf(
        UP to orderOfFaceletsUp,
        DOWN to orderOfFaceletsDown,
        FRONT to orderOfFaceletsFront,
        BACK to orderOfFaceletsBack,
        LEFT to orderOfFaceletsLeft,
        RIGHT to orderOfFaceletsRight)

    override fun reorderFacelets(faces: List<Face>): List<Face> {
        return faces.map { face ->
            face.colors.mapIndexed { index, color ->
                orderOfFaceletsMap[face.position]!![index] to color
            }.sortedBy { it.first }.map { it.second }.let { Face(face.position, it) }
        }
    }
}

class Min2PhaseModelCreator : ModelCreator() {
    override val faceOrder: List<Position> = listOf(UP, RIGHT, FRONT, DOWN, LEFT, BACK)

    override fun createColorMapper(distinctColors: Map<Position, Int>): Map<Int, String> {
        val positionToValue = Position.values().associateWith { it.name.first().toString() }
        return distinctColors.map { it.value to positionToValue[it.key]!! }.toMap()
    }
}