package com.github.eliascoelho911.robok.rubikcube

import android.os.Parcelable
import com.github.eliascoelho911.robok.rubikcube.face.Face
import com.github.eliascoelho911.robok.util.ColorUtil.similarityBetweenColors
import kotlinx.parcelize.Parcelize

private const val SimilarityLimit = 20

typealias Model = String

@Parcelize
class RubikCube(val faces: List<Face>) : Parcelable {
    private val allColors = faces.flatMap { it.colors }
    val distinctColors by lazy { faces.associate { it.position to it.centerColor() } }
    val isValid: Boolean
        get() {
            val allColorsWithCorrectQuantity =
                allColors.groupBy { it }.all { it.value.size == NumberOfFacelets }
            return distinctColors.size == NumberOfFaces && allColorsWithCorrectQuantity
        }

    companion object {
        const val NumberOfFaces = 6
        const val FaceLineHeight = 3
        const val NumberOfFacelets = FaceLineHeight * FaceLineHeight
    }

    fun createModelWith(modelCreator: ModelCreator) =
        modelCreator.create(this)

    class Builder {
        private val originalFaces = mutableListOf<Face>()

        fun withFace(face: Face) = apply {
            if (originalFaces.size + 1 > NumberOfFaces) throw IllegalArgumentException("limit of faces is $NumberOfFaces")
            originalFaces.add(face)
        }

        fun build(): RubikCube = RubikCube(originalFaces.standardizesColors())

        private fun List<Face>.standardizesColors(): List<Face> {
            val allColors = flatMap { it.colors }
            val colorMapper = createColorMapper(allColors)
            return map { it.standardizesColors(colorMapper) }
        }

        private fun Face.standardizesColors(colorMapper: Map<Int, Int>): Face =
            Face(position, colors.map { colorMapper[it] ?: it })

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