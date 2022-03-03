package com.github.eliascoelho911.robok.rubikcube.face

import androidx.annotation.ColorInt

class Face(val position: Position, @ColorInt val colors: List<Int>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Face

        return position == other.position
    }

    override fun hashCode(): Int {
        return position.hashCode()
    }

    enum class Position {
        FRONT, RIGHT, BOTTOM, LEFT, TOP, DOWN;
    }
}