package com.github.eliascoelho911.robok.rubikcube

import android.graphics.Color
import com.github.eliascoelho911.robok.rubikcube.face.Face
import com.github.eliascoelho911.robok.util.createColorFrom
import com.github.eliascoelho911.robok.util.similarityFrom

private const val SimilarityLimit = 30

class RubikCube(faces: Set<Face>) {
    private val originalFaces = faces.toMutableSet()
    val facesWithStandardizesColors: Set<Face> get() = originalFaces.standardizesColors()

    fun add(face: Face) {
        originalFaces.add(face)
    }

    private fun Set<Face>.standardizesColors(): Set<Face> {
        val allColors = flatMap { it.colors }
        val colorGroups = allColors.groupBySimilarity()
        return map { it.createWithReferenceColor(colorGroups) }.toSet().also { face ->
            val numberOfDistinctColors = face.flatMap { it.colors }.distinct().size
            assert(numberOfDistinctColors == NumberOfFaces)
        }
    }

    private fun Face.createWithReferenceColor(colorGroups: Map<Color, List<Color>>): Face {
        val referenceColors = colors.map { createColorFrom(it) }.mapNotNull { color ->
            colorGroups.filterValues { color in it }.keys.firstOrNull()
        }.map { it.toArgb() }
        return Face(position, referenceColors)
    }

    private fun List<Int>.groupBySimilarity(): Map<Color, List<Color>> {
        val groups = mutableMapOf<Color, List<Color>>()

        map { createColorFrom(it) }.run {
            for (reference in this) {
                if (reference in groups.keys || reference in groups.values.flatten()) continue
                val similarColors = filter { it !in groups.values.flatten() }
                    .filter { it !in groups.keys }
                    .map { it to reference.similarityFrom(it) }
                    .filter { it.second < SimilarityLimit }
                groups[reference] = similarColors.map { it.first }
            }
        }

        return groups
    }

    companion object {
        const val NumberOfFaces = 6
        const val FaceLineHeight = 3
        const val NumberOfFacelets = FaceLineHeight * FaceLineHeight
    }
}
