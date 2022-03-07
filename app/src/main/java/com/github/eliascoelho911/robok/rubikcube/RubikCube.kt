package com.github.eliascoelho911.robok.rubikcube

import com.github.eliascoelho911.robok.rubikcube.face.Face
import com.github.eliascoelho911.robok.rubikcube.face.Face.Position.BOTTOM
import com.github.eliascoelho911.robok.rubikcube.face.Face.Position.DOWN
import com.github.eliascoelho911.robok.rubikcube.face.Face.Position.FRONT
import com.github.eliascoelho911.robok.rubikcube.face.Face.Position.LEFT
import com.github.eliascoelho911.robok.rubikcube.face.Face.Position.RIGHT
import com.github.eliascoelho911.robok.rubikcube.face.Face.Position.TOP
import com.github.eliascoelho911.robok.util.ColorUtil.similarityBetweenColors

private const val SimilarityLimit = 20

class RubikCube private constructor(faces: Set<Face>) {
    val kociembaValue: String by lazy {
        allColors.map { color ->
            colorsIndex.single { it.value == color }.index
        }.joinToString(separator = "")
    }
    val distinctColors by lazy { allColors.distinct() }

    private val allColors = faces.flatMap { it.colors }
    private val colorsIndex = distinctColors.withIndex()

    companion object {
        const val NumberOfFaces = 6
        const val FaceLineHeight = 3
        const val NumberOfFacelets = FaceLineHeight * FaceLineHeight
    }

    class Builder {
        private val originalFaces = mutableSetOf<Face>()

        fun withFace(face: Face) = apply {
            if (originalFaces.size + 1 > NumberOfFaces) throw IllegalArgumentException("limit of faces is $NumberOfFaces")
            originalFaces.add(face)
        }

        fun build(): RubikCube = RubikCube(originalFaces.standardizesColors())

        private fun Set<Face>.standardizesColors(): Set<Face> {
            val allColors = flatMap { it.colors }
            val colorMapper = createColorMapper(allColors)
            return map { it.standardizesColors(colorMapper) }.toSet()
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

val testRubikCube by lazy {
    RubikCube.Builder()
        .withFace(Face(FRONT,
            listOf(-16741681,
                -27604,
                -924965,
                -8173,
                -8525545,
                -1618623,
                -1631721,
                -16743734,
                -7739125)))
        .withFace(Face(RIGHT,
            listOf(-227024,
                -2303018,
                -1101519,
                -16743738,
                -1891552,
                -159954,
                -142063,
                -16677686,
                -16545326)))
        .withFace(Face(LEFT,
            listOf(-9314047,
                -7937016,
                -28646,
                -2107699,
                -2171180,
                -778212,
                -1044206,
                -1960688,
                -16741935)))
        .withFace(Face(BOTTOM,
            listOf(-16748348,
                -1257202,
                -2895154,
                -3356475,
                -32228,
                -30176,
                -996081,
                -3355195,
                -2960948)))
        .withFace(Face(TOP,
            listOf(-73198,
                -16741164,
                -8131064,
                -664560,
                -16745009,
                -5838827,
                -140785,
                -915445,
                -93389)))
        .withFace(Face(DOWN,
            listOf(-2283990,
                -10299358,
                -11020260,
                -10630123,
                -1651442,
                -163018,
                -3618616,
                -2373363,
                -29359))).build()
}