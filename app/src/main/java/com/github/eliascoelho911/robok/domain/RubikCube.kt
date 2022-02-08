package com.github.eliascoelho911.robok.domain

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.github.eliascoelho911.robok.R
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

class RubikCubeSide(colors: List<RubikCubeColor> = List(NumberOfCells) { WHITE }) {
    private val _colors by lazy { colors.toMutableList() }
    val colors: List<RubikCubeColor> get() = _colors

    fun get(x: Int, y: Int): RubikCubeColor {
        validate(x, y)

        return get(indexFrom(x, y))
    }

    fun get(index: Int): RubikCubeColor {
        return colors[index]
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
enum class RubikCubeColor(@ColorRes id: Int) {
    WHITE(R.color.white), BLUE(R.color.blue_a400),
    RED(R.color.red_a700), YELLOW(R.color.yellow_a400),
    ORANGE(R.color.orange_a700), GREEN(R.color.green_a400);

    companion object {
        fun findBySimilarity(context: Context, color: Color): RubikCubeColor =
            values().mapNotNull { rubikCubeColor ->
                val similarity = color.similarityFrom(rubikCubeColor.androidColor(context))
                (similarity to rubikCubeColor).takeIf { it.first < MinSimilarity }
            }.minByOrNull { it.first }?.second ?: WHITE
    }

    val androidColor: (context: Context) -> Color = {
        Color.valueOf(ContextCompat.getColor(it, id))
    }
}

class RubikCubeBuilder {
    private val _sides = mutableListOf<RubikCubeSide>()

    fun withSide(colors: List<RubikCubeColor>): RubikCubeBuilder {
        _sides.add(RubikCubeSide(colors))
        return this
    }

    val lengthOfSides: Int get() = _sides.size

    fun build() = RubikCube(_sides)
}