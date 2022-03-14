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
    abstract val faceOrder: List<Position>
    abstract fun reorderFacelets(faces: List<Face>): List<Face>

    fun create(faces: List<Face>): Model {
        val orderedFaces = reorderFaces(faces)
        val orderedFacelets = reorderFacelets(orderedFaces)
        val distinctColors = faces.flatMap { it.colors }.distinct()
        val colorMapper = createColorMapper(distinctColors)
        return orderedFacelets.joinToString(separator = "") { face ->
            face.colors.map { colorMapper[it] }.joinToString(separator = "")
        }
    }

    private fun createColorMapper(distinctColors: List<Int>): Map<Int, String> =
        distinctColors.withIndex().associate { it.value to it.index.toString() }

    private fun reorderFaces(faces: List<Face>): List<Face> {
        val faceOrderWithIndex = faceOrder.withIndex()
        return faces.map { face ->
            faceOrderWithIndex.single { it.value == face.position }.index to face
        }.sortedBy { it.first }.map { it.second }
    }
}

object DefaultModelCreator : ModelCreator() {
    override val faceOrder: List<Position> = listOf(UP, DOWN, FRONT, BACK, LEFT, RIGHT)

    override fun reorderFacelets(faces: List<Face>): List<Face> = faces
}

object AnimCubeModelCreator : ModelCreator() {
    override val faceOrder: List<Position> = listOf(UP, DOWN, FRONT, BACK, LEFT, RIGHT)

    private val OrderOfFaceletsUp = listOf(6, 7, 8, 3, 4, 5, 0, 1, 2)
    private val OrderOfFaceletsDown = listOf(0, 3, 6, 1, 4, 7, 2, 5, 8)
    private val OrderOfFaceletsFront = listOf(0, 3, 6, 1, 4, 7, 2, 5, 8)
    private val OrderOfFaceletsBack = listOf(0, 3, 6, 1, 4, 7, 2, 5, 8)
    private val OrderOfFaceletsLeft = listOf(2, 1, 0, 5, 4, 3, 8, 7, 6)
    private val OrderOfFaceletsRight = listOf(0, 3, 6, 1, 4, 7, 2, 5, 8)
    private val OrderOfFaceletsMap = mapOf(
        UP to OrderOfFaceletsUp,
        DOWN to OrderOfFaceletsDown,
        FRONT to OrderOfFaceletsFront,
        BACK to OrderOfFaceletsBack,
        LEFT to OrderOfFaceletsLeft,
        RIGHT to OrderOfFaceletsRight)

    override fun reorderFacelets(faces: List<Face>): List<Face> {
        return faces.map { face ->
            face.colors.mapIndexed { index, color ->
                OrderOfFaceletsMap[face.position]!![index] to color
            }.sortedBy { it.first }.map { it.second }.let { Face(face.position, it) }
        }
    }
}