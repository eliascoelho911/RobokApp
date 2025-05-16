package com.github.eliascoelho911.robok.rubikcube

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface CompatibleWithLeftHand : Face
sealed interface CompatibleWithRightHand : Face

sealed interface Face {
    val cells: List<Cell>

    fun getCell(x: Int, y: Int): Cell {
        require(x in 1..CUBE_CELLS_PER_LINE && y in 1..CUBE_CELLS_PER_LINE)

        return cells.first { it.x == x && it.y == y }
    }

    fun center(): Cell = getCell(1, 1)
}

data class ColorList(val colors: List<Int>) {
    constructor(vararg colors: Int) : this(colors.toList())
}

private fun createCellsByColors(colors: ColorList): List<Cell> {
    return colors.colors.mapIndexed { index, color ->
        val x = (index % CUBE_CELLS_PER_LINE)
        val y = (index / CUBE_CELLS_PER_LINE)
        Cell(x, y, color)
    }.toList()
}

@Parcelize
data class UpFace(
    override val cells: List<Cell>
) : CompatibleWithLeftHand, Parcelable {
    constructor(colors: ColorList) : this(createCellsByColors(colors))
}

@Parcelize
data class DownFace(
    override val cells: List<Cell>
) : CompatibleWithLeftHand, Parcelable {
    constructor(colors: ColorList) : this(createCellsByColors(colors))
}

@Parcelize
data class RightFace(
    override val cells: List<Cell>
) : CompatibleWithRightHand, Parcelable {
    constructor(colors: ColorList) : this(createCellsByColors(colors))
}

@Parcelize
data class LeftFace(
    override val cells: List<Cell>
) : CompatibleWithRightHand, Parcelable {
    constructor(colors: ColorList) : this(createCellsByColors(colors))
}

@Parcelize
data class FrontFace(
    override val cells: List<Cell>
) : CompatibleWithLeftHand, CompatibleWithRightHand, Parcelable {
    constructor(colors: ColorList) : this(createCellsByColors(colors))
}

@Parcelize
data class BackFace(
    override val cells: List<Cell>
) : CompatibleWithLeftHand, CompatibleWithRightHand, Parcelable {
    constructor(colors: ColorList) : this(createCellsByColors(colors))
}
