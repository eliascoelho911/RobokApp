package com.github.eliascoelho911.robok.rubikcube

import com.github.eliascoelho911.robok.rubikcube.face.Face
import com.github.eliascoelho911.robok.rubikcube.face.Face.Position.BACK
import com.github.eliascoelho911.robok.rubikcube.face.Face.Position.DOWN
import com.github.eliascoelho911.robok.rubikcube.face.Face.Position.FRONT
import com.github.eliascoelho911.robok.rubikcube.face.Face.Position.LEFT
import com.github.eliascoelho911.robok.rubikcube.face.Face.Position.RIGHT
import com.github.eliascoelho911.robok.rubikcube.face.Face.Position.UP
import com.github.eliascoelho911.robok.rubikcube.face.FaceSorter
import com.github.eliascoelho911.robok.util.ColorUtil.similarityBetweenColors

private const val SimilarityLimit = 20

/*
Todo:
 Toda essa complexidade que criei nessa classe é necessária?
 Por exemplo: o face poderia ser uma classe contendo o modelo e a posição
 */
class RubikCube private constructor(faces: List<Face>, sorter: FaceSorter) {
    val kociembaValue: String by lazy {
        allColors.map { color ->
            colorsIndex.single { it.value == color }.index
        }.joinToString(separator = "")
    }
    val distinctColors by lazy { allColors.distinct() }

    private val allColors = sorter.sort(faces).flatMap { it.colors }
    private val colorsIndex = distinctColors.withIndex()

    companion object {
        const val NumberOfFaces = 6
        const val FaceLineHeight = 3
        const val NumberOfFacelets = FaceLineHeight * FaceLineHeight
    }

    class Builder {
        private val originalFaces = mutableSetOf<Face>()
        private lateinit var sorter: FaceSorter

        fun withFace(face: Face) = apply {
            if (originalFaces.size + 1 > NumberOfFaces) throw IllegalArgumentException("limit of faces is $NumberOfFaces")
            originalFaces.add(face)
        }

        fun withSorter(sorter: FaceSorter) = apply {
            this.sorter = sorter
        }

        fun build(): RubikCube = RubikCube(originalFaces.standardizesColors(), sorter)

        private fun Set<Face>.standardizesColors(): List<Face> {
            val allColors = flatMap { it.colors }
            val colorMapper = createColorMapper(allColors)
            return map { it.standardizesColors(colorMapper) }
        }

        private fun Face.standardizesColors(colorMapper: Map<Int, Int>): Face =
            Face(colors.map { colorMapper[it] ?: it }, position)

        private fun createColorMapper(colors: List<Int>): Map<Int, Int> {
            val referenceColors = findReferenceColors(colors)
            return colors.associateWith {
                mostSimilarReferenceColor(referenceColors, it)
            }
        }

        private fun findReferenceColors(colors: List<Int>): List<Int> =
            mutableListOf(colors.first()).apply {
                for (color in colors) {
                    if (isDifferentFromAllColors(reference = color, otherColors = this)) {
                        add(color)

                        if (size == NumberOfFaces) break
                    }
                }
            }

        private fun isDifferentFromAllColors(reference: Int, otherColors: List<Int>) =
            otherColors.map {
                similarityBetweenColors(it, reference)
            }.any { it < SimilarityLimit }.not()

        private fun mostSimilarReferenceColor(
            referenceColors: List<Int>,
            color: Int,
        ) = referenceColors.map {
            it to similarityBetweenColors(it, color)
        }.minByOrNull { it.second }!!.first
    }
}