package com.github.eliascoelho911.robok.domain

import com.github.eliascoelho911.robok.domain.Cell.UNDEFINED
import com.github.eliascoelho911.robok.domain.RubikCube.Companion.NumberOfCells
import com.github.eliascoelho911.robok.domain.RubikCube.Companion.NumberOfColumnsOnTheSide
import com.github.eliascoelho911.robok.domain.RubikCube.Companion.NumberOfRowsOnTheSide


class RubikCube(val sides: List<Side> = List(NumberOfSides) { Side() }) {
    companion object {
        const val NumberOfSides = 6
        const val NumberOfColumnsOnTheSide = 3
        const val NumberOfRowsOnTheSide = 3
        const val NumberOfCells = NumberOfColumnsOnTheSide * NumberOfRowsOnTheSide
    }
}

class Side(private val cells: List<Cell> = List(NumberOfCells) { UNDEFINED }) {
    fun get(x: Int, y: Int): Cell {
        if (x >= NumberOfColumnsOnTheSide)
            throw IndexOutOfBoundsException("x: $x is greater than maximum $NumberOfColumnsOnTheSide")

        if (y >= NumberOfRowsOnTheSide)
            throw IndexOutOfBoundsException("y: $x is greater than maximum $NumberOfRowsOnTheSide")

        val indexOnArray = x * NumberOfColumnsOnTheSide + y
        return cells[indexOnArray]
    }
}

enum class Cell {
    UNDEFINED, COLOR_0, COLOR_1, COLOR_2, COLOR_3, COLOR_4, COLOR_5
}