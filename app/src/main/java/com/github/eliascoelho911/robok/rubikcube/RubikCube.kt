package com.github.eliascoelho911.robok.rubikcube

import com.github.eliascoelho911.robok.rubikcube.side.Side

class RubikCube(sides: Set<Side>) {
    private val _sides = sides.toMutableSet()
    val sides: Set<Side> get() = _sides

    fun add(side: Side) {
        _sides.add(side)
    }
}