package com.github.eliascoelho911.robok.rubikcube

import com.github.eliascoelho911.robok.rubikcube.side.Side

class RubikCube(val sides: Set<Side>) {
    enum class SidePosition {
        LEFT, FRONT, UP, DOWN, RIGHT, BOTTOM;
    }
}