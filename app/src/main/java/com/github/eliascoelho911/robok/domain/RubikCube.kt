package com.github.eliascoelho911.robok.domain

import android.graphics.Color
import com.github.eliascoelho911.robok.analyzers.similarityFrom
import com.github.eliascoelho911.robok.domain.RubikCube.Companion.NumberOfCells
import com.github.eliascoelho911.robok.domain.RubikCube.Companion.NumberOfColumnsOnTheSide
import com.github.eliascoelho911.robok.domain.RubikCube.Companion.NumberOfRowsOnTheSide
import com.github.eliascoelho911.robok.domain.RubikCubeColor.WHITE


class RubikCube(val sides: List<RubikCubeSide> = List(NumberOfSides) { RubikCubeSide() }) {
    companion object {
        const val NumberOfSides = 6
        const val NumberOfColumnsOnTheSide = 3
        const val NumberOfRowsOnTheSide = 3
        const val NumberOfCells = NumberOfColumnsOnTheSide * NumberOfRowsOnTheSide
    }
}

class RubikCubeSide(private val _colors: MutableList<RubikCubeColor> = MutableList(NumberOfCells) { WHITE }) {

    fun get(x: Int, y: Int): RubikCubeColor {
        validate(x, y)

        return get(indexFrom(x, y))
    }

    fun get(index: Int): RubikCubeColor {
        return _colors[index]
    }

    fun put(x: Int, y: Int, color: RubikCubeColor) {
        validate(x, y)

        put(indexFrom(x, y), color)
    }

    fun put(index: Int, color: RubikCubeColor) {
        _colors[index] = color
    }

    private fun indexFrom(x: Int, y: Int) = x * NumberOfColumnsOnTheSide + y

    private fun validate(x: Int, y: Int) {
        if (x >= NumberOfColumnsOnTheSide)
            throw IndexOutOfBoundsException("x: $x is greater than maximum $NumberOfColumnsOnTheSide")

        if (y >= NumberOfRowsOnTheSide)
            throw IndexOutOfBoundsException("y: $x is greater than maximum $NumberOfRowsOnTheSide")
    }
}

private const val MinSimilarity = 60f

@Suppress("unused")
enum class RubikCubeColor(r: Int, g: Int, b: Int) {
    WHITE(255, 255, 255), BLUE(7, 121, 191),
    RED(156, 20, 22), YELLOW(199, 179, 64),
    ORANGE(214, 96, 60), GREEN(32, 181, 63);

    companion object {
        fun findBySimilarity(color: Color): RubikCubeColor = values().mapNotNull { rubikCubeColor ->
            val similarity = color.similarityFrom(rubikCubeColor.androidColor)
            (similarity to rubikCubeColor).takeIf { it.first < MinSimilarity }
        }.minByOrNull { it.first }?.second ?: WHITE
    }

    val androidColor by lazy { Color.valueOf(Color.rgb(r, g, b)) }
}