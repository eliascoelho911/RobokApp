package com.github.eliascoelho911.robok.domain

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.util.similarityFrom
import com.github.eliascoelho911.robok.util.Matrix

class RubikCube(sides: Set<RubikCubeSide>) {
    private val _sides = sides.toMutableSet()
    val sides: Set<RubikCubeSide> get() = _sides

    fun addSide(side: RubikCubeSide) {
        _sides.add(side)
    }
}

class RubikCubeSide(val position: SidePosition, val colors: Matrix<RubikCubeSideColor>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RubikCubeSide

        return position == other.position
    }

    override fun hashCode(): Int {
        return position.hashCode()
    }
}

enum class SidePosition(private val order: Int) {
    LEFT(order = 0), FRONT(order = 1),
    UP(order = 2), DOWN(order = 3),
    RIGHT(order = 4), BOTTOM(order = 5);

    fun next(): SidePosition = values().single { it.order == this.order + 1 }

    companion object {
        fun first() = values().first()
    }
}

private const val MinSimilarity = 60f

@Suppress("unused")
enum class RubikCubeSideColor(@ColorRes val colorId: Int) {
    WHITE(R.color.white), BLUE(R.color.blue_a400),
    RED(R.color.red_9D1519), YELLOW(R.color.yellow_a400),
    ORANGE(R.color.orange_E99D4B), GREEN(R.color.green_a400);

    companion object {
        fun findBySimilarity(context: Context, color: Color): RubikCubeSideColor =
            values().mapNotNull { rubikCubeColor ->
                val similarity = color.similarityFrom(rubikCubeColor.androidColor(context))
                (similarity to rubikCubeColor).takeIf { it.first < MinSimilarity }
            }.minByOrNull { it.first }?.second ?: WHITE
    }

    val androidColor: (context: Context) -> Color = {
        Color.valueOf(ContextCompat.getColor(it, colorId))
    }
}