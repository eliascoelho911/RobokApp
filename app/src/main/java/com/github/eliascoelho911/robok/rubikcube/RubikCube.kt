package com.github.eliascoelho911.robok.rubikcube

import android.os.Parcelable
import com.github.eliascoelho911.robok.util.ColorUtil.similarityBetweenColors
import kotlinx.parcelize.Parcelize

const val CUBE_FACES = 6
const val CUBE_FACE_CELLS = 9
const val CUBE_CELLS_PER_LINE = 3

@Parcelize
data class RubikCube(
    val upFace: UpFace,
    val frontFace: FrontFace,
    val rightFace: RightFace,
    val backFace: BackFace,
    val leftFace: LeftFace,
    val downFace: DownFace
) : Parcelable {
    val faces: List<Face>
        get() = listOf(
            upFace,
            frontFace,
            rightFace,
            backFace,
            leftFace,
            downFace
        )
}

class RubikCubeBuilder {
    private var upFace: UpFace? = null
    private var frontFace: FrontFace? = null
    private var rightFace: RightFace? = null
    private var backFace: BackFace? = null
    private var leftFace: LeftFace? = null
    private var downFace: DownFace? = null

    private val allFaces: List<Face>
        get() = listOfNotNull(
            upFace,
            frontFace,
            rightFace,
            backFace,
            leftFace,
            downFace
        )
    private val allCells: List<Cell> get() = allFaces.flatMap { it.cells }

    fun withFace(position: Position, colors: List<Int>) = apply {
        val colorList = ColorList(colors)
        when (position) {
            Position.UP -> withUpFace(UpFace(colorList))
            Position.FRONT -> withFrontFace(FrontFace(colorList))
            Position.RIGHT -> withRightFace(RightFace(colorList))
            Position.BACK -> withBackFace(BackFace(colorList))
            Position.LEFT -> withLeftFace(LeftFace(colorList))
            Position.DOWN -> withDownFace(DownFace(colorList))
        }
    }

    fun withUpFace(upFace: UpFace) = apply { this.upFace = upFace }
    fun withFrontFace(frontFace: FrontFace) = apply { this.frontFace = frontFace }
    fun withRightFace(rightFace: RightFace) = apply { this.rightFace = rightFace }
    fun withBackFace(backFace: BackFace) = apply { this.backFace = backFace }
    fun withLeftFace(leftFace: LeftFace) = apply { this.leftFace = leftFace }
    fun withDownFace(downFace: DownFace) = apply { this.downFace = downFace }

    fun build(): RubikCube {
        require(allFaces.size == CUBE_FACES) { "allFaces requires $CUBE_FACES faces" }
        require(allCells.size == CUBE_FACES * CUBE_FACE_CELLS) { "allCells requires $CUBE_FACES faces with $CUBE_FACE_CELLS cells" }

        val topFace = upFace!!
        val frontFace = frontFace!!
        val rightFace = rightFace!!
        val backFace = backFace!!
        val leftFace = leftFace!!
        val bottomFace = downFace!!

        val cellsByColorSimilarity = groupCellsByColorSimilarity()

        return RubikCube(
            upFace = topFace.copy(cells = normalizeColors(topFace.cells, cellsByColorSimilarity)),
            frontFace = frontFace.copy(
                cells = normalizeColors(
                    frontFace.cells,
                    cellsByColorSimilarity
                )
            ),
            rightFace = rightFace.copy(
                cells = normalizeColors(
                    rightFace.cells,
                    cellsByColorSimilarity
                )
            ),
            backFace = backFace.copy(
                cells = normalizeColors(
                    backFace.cells,
                    cellsByColorSimilarity
                )
            ),
            leftFace = leftFace.copy(
                cells = normalizeColors(
                    leftFace.cells,
                    cellsByColorSimilarity
                )
            ),
            downFace = bottomFace.copy(
                cells = normalizeColors(
                    bottomFace.cells,
                    cellsByColorSimilarity
                )
            )
        ).also {
            val colors = it.faces.flatMap { it.cells }.map { it.color }
            colors.groupBy { it }
                .forEach { require(it.value.size == CUBE_FACE_CELLS) { "All faces must have the same colors" } }
        }
    }

    private fun normalizeColors(
        cells: List<Cell>,
        cellsByColorSimilarity: Map<Int, List<Cell>>
    ): List<Cell> {
        return buildList {
            for (cell in cells) {
                cellsByColorSimilarity.forEach { (groupColor, groupCells) ->
                    if (groupCells.contains(cell)) {
                        add(cell.copy(color = groupColor))
                        return@forEach
                    }
                }
            }
        }
    }

    /**
     * Cria um mapa de grupos de celulas que possuem a mesma cor.
     */
    private fun groupCellsByColorSimilarity(): Map<Int, List<Cell>> {
        val centerFaceColors = allFaces.map { it.center().color }

        return allCells.groupBy { findMostSimilarColor(centerFaceColors, it.color) }
    }
}

private fun findMostSimilarColor(
    referenceColors: Collection<Int>,
    color: Int,
): Int = referenceColors.map {
    it to similarityBetweenColors(it, color)
}.minByOrNull { it.second }!!.first