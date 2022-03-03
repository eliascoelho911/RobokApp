package com.github.eliascoelho911.robok.rubikcube

import com.github.eliascoelho911.robok.rubikcube.face.Face

class RubikCube(faces: Set<Face>) {
    private val _faces = faces.toMutableSet()
    val faces: Set<Face> get() = _faces

    fun add(face: Face) {
        _faces.add(face)
    }

    companion object {
        const val NumberOfFaces = 6
        const val FaceLineHeight = 3
        const val NumberOfFacelets = FaceLineHeight * FaceLineHeight
    }
}

//val DefaultSides = setOf(createSideWithOneColor(FRONT, Color.WHITE),
//    createSideWithOneColor(LEFT, Color.RED),
//    createSideWithOneColor(RIGHT, Color.BLUE),
//    createSideWithOneColor(TOP, Color.YELLOW),
//    createSideWithOneColor(BOTTOM, Color.GREEN),
//    createSideWithOneColor(DOWN, Color.MAGENTA))
//
//private fun createSideWithOneColor(sidePosition: SidePosition, @ColorInt color: Int) =
//    Side(sidePosition, List(NumberOfColorsBySide) { color })