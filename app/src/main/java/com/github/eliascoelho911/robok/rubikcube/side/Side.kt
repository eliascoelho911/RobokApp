package com.github.eliascoelho911.robok.rubikcube.side

import androidx.annotation.ColorInt

class Side(val position: SidePosition, @ColorInt val colors: List<Int>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Side

        return position == other.position
    }

    override fun hashCode(): Int {
        return position.hashCode()
    }
}

enum class SidePosition {
    FRONT, RIGHT, BOTTOM, LEFT, TOP, DOWN;
}