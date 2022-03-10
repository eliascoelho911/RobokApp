package com.github.eliascoelho911.robok.rubikcube.face

import androidx.annotation.ColorInt
import com.github.eliascoelho911.robok.rubikcube.RubikCube.Companion.NumberOfFacelets
import com.github.eliascoelho911.robok.rubikcube.face.FaceletsSorter.AnimCubeSorters

class Face(
    @ColorInt colors: List<Int>,
    val position: Position,
) {
    val colors = position.sorter.sort(colors)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Face

        return centerColor(colors) == centerColor(other.colors)
    }

    override fun hashCode(): Int {
        return centerColor(colors).hashCode()
    }

    enum class Position(val sorter: FaceletsSorter) {
        FRONT(AnimCubeSorters.DefaultFace), RIGHT(AnimCubeSorters.DefaultFace),
        BACK(AnimCubeSorters.DefaultFace), LEFT(AnimCubeSorters.DefaultFace),
        UP(AnimCubeSorters.DefaultFace), DOWN(AnimCubeSorters.DefaultFace);
    }
}

private fun centerColor(colors: List<Int>) = colors[(NumberOfFacelets - 1) / 2]