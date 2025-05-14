package com.github.eliascoelho911.robok.rubikcube

import kotlin.reflect.KClass

abstract class RubikCubeModelParser {
    protected abstract val faceOrder: List<KClass<out Face>>

    fun parse(rubikCube: RubikCube): String {
        val faces = rubikCube.faces
        val orderedFaces = reorderFaces(faces)
        val orderedCells = reorderCells(orderedFaces)
        val colorMapper = createColorMapper(getDistinctColors(rubikCube))
        return orderedCells.joinToString(separator = "") { face ->
            face.cells.joinToString(separator = "") { colorMapper[it.color].toString() }
        }
    }

    fun getDistinctColorsInOrder(rubikCube: RubikCube): List<Int> {
        val distinctColors = getDistinctColors(rubikCube)
        return faceOrder.map { distinctColors[it]!! }
    }

    protected open fun reorderCells(faces: List<Face>): List<Face> = faces

    protected open fun createColorMapper(distinctColors: Map<KClass<out Face>, Int>): Map<Int, String> =
        distinctColors.values.withIndex().associate { it.value to it.index.toString() }

    private fun getDistinctColors(rubikCube: RubikCube): Map<KClass<out Face>, Int> {
        return mapOf(
            UpFace::class to rubikCube.upFace.center().color,
            DownFace::class to rubikCube.downFace.center().color,
            FrontFace::class to rubikCube.frontFace.center().color,
            BackFace::class to rubikCube.backFace.center().color,
            LeftFace::class to rubikCube.leftFace.center().color,
            RightFace::class to rubikCube.rightFace.center().color
        )
    }

    private fun reorderFaces(faces: List<Face>): List<Face> {
        val classFaceMap = faces.associateBy { it::class }
        return faceOrder.map { classFaceMap[it]!! }
    }
}

open class AnimCubeModelParser : RubikCubeModelParser() {
    override val faceOrder: List<KClass<out Face>> = listOf(
        UpFace::class,
        DownFace::class,
        FrontFace::class,
        BackFace::class,
        LeftFace::class,
        RightFace::class
    )

    private val orderOfCellsMap = mapOf(
        UpFace::class to listOf(6, 7, 8, 3, 4, 5, 0, 1, 2),
        DownFace::class to listOf(0, 3, 6, 1, 4, 7, 2, 5, 8),
        FrontFace::class to listOf(0, 3, 6, 1, 4, 7, 2, 5, 8),
        BackFace::class to listOf(0, 3, 6, 1, 4, 7, 2, 5, 8),
        LeftFace::class to listOf(2, 1, 0, 5, 4, 3, 8, 7, 6),
        RightFace::class to listOf(0, 3, 6, 1, 4, 7, 2, 5, 8)
    )

    override fun reorderCells(faces: List<Face>): List<Face> {
        return faces.map { face ->
            val faceClass = face::class
            val reorderedCells = reorderFaceCells(face, faceClass)

            when (faceClass) {
                UpFace::class -> UpFace(reorderedCells)
                DownFace::class -> DownFace(reorderedCells)
                FrontFace::class -> FrontFace(reorderedCells)
                BackFace::class -> BackFace(reorderedCells)
                LeftFace::class -> LeftFace(reorderedCells)
                RightFace::class -> RightFace(reorderedCells)
                else -> throw IllegalArgumentException("Unknown face type: $faceClass")
            }
        }
    }

    private fun reorderFaceCells(face: Face, faceClass: KClass<out Face>): List<Cell> {
        val cellsArray = face.cells

        return orderOfCellsMap[faceClass]!!.mapIndexed { index, newIndex ->
            cellsArray[newIndex]
        }
    }
}

open class Min2PhaseModelParser : RubikCubeModelParser() {
    override val faceOrder: List<KClass<out Face>> = listOf(
        UpFace::class,
        RightFace::class,
        FrontFace::class,
        DownFace::class,
        LeftFace::class,
        BackFace::class
    )

    override fun createColorMapper(distinctColors: Map<KClass<out Face>, Int>): Map<Int, String> {
        val classToLetter = mapOf(
            UpFace::class to "U",
            RightFace::class to "R",
            FrontFace::class to "F",
            DownFace::class to "D",
            LeftFace::class to "L",
            BackFace::class to "B"
        )

        return distinctColors.map { (faceClass, color) ->
            color to classToLetter[faceClass]!!
        }.toMap()
    }
}