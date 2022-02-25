package com.github.eliascoelho911.robok.domain

import android.graphics.Color
import com.github.eliascoelho911.robok.util.Matrix

class RubikCube(val sides: Set<Side>) {
    class Side(val position: SidePosition, val colors: Matrix<Color>) {
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
}

enum class SidePosition {
    LEFT, FRONT, UP, DOWN, RIGHT, BOTTOM;
}