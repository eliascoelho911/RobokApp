package com.github.eliascoelho911.robok.rubikcube.face

import androidx.annotation.ColorInt

class FaceletsSorter private constructor(private val indexMapper: List<Int>) {
    @ColorInt
    fun sort(@ColorInt colors: List<Int>): List<Int> =
        colors.mapIndexed { index, color ->
            indexMapper[index] to color
        }.sortedBy { it.first }.map { it.second }

    object AnimCubeSorters {
        val DefaultFace = FaceletsSorter(listOf(0, 1, 2, 3, 4, 5, 6, 7, 8))
        val UpFace = FaceletsSorter(listOf(6, 7, 8, 3, 4, 5, 0, 1, 2))
        val DownFace = FaceletsSorter(UpToDown)
        val FrontFace = FaceletsSorter(listOf(0, 1, 2, 3, 4, 5, 6, 7, 8))
        val BackFace = FaceletsSorter(UpToDown)
        val LeftFace = FaceletsSorter(listOf(2, 1, 0, 5, 4, 3, 8, 7, 6))
        val RightFace = FaceletsSorter(listOf(2, 5, 8, 1, 4, 7, 0, 3, 6))
    }
}

private val UpToDown = listOf(0, 3, 6, 2, 4, 7, 3, 5, 8)