package com.github.eliascoelho911.robok.rubikcube

import android.graphics.Color.BLUE
import android.graphics.Color.GREEN
import android.graphics.Color.MAGENTA
import android.graphics.Color.RED
import android.graphics.Color.WHITE
import android.graphics.Color.YELLOW
import org.junit.Assert.assertEquals
import org.junit.Test

abstract class RubikCubeModelParserBaseTest {
    private val rubikCube by lazy {
        RubikCube(
            frontFace = FrontFace(
                ColorList(
                    GREEN,
                    YELLOW,
                    WHITE,
                    WHITE,
                    BLUE,
                    YELLOW,
                    MAGENTA,
                    GREEN,
                    GREEN
                )
            ),
            rightFace = RightFace(
                ColorList(
                    GREEN,
                    YELLOW,
                    BLUE,
                    RED,
                    RED,
                    WHITE,
                    YELLOW,
                    GREEN,
                    MAGENTA
                )
            ),
            backFace = BackFace(
                ColorList(
                    YELLOW,
                    BLUE,
                    WHITE,
                    MAGENTA,
                    GREEN,
                    BLUE,
                    WHITE,
                    GREEN,
                    BLUE
                )
            ),
            leftFace = LeftFace(
                ColorList(
                    RED,
                    GREEN,
                    RED,
                    WHITE,
                    MAGENTA,
                    RED,
                    YELLOW,
                    BLUE,
                    WHITE
                )
            ),
            upFace = UpFace(
                ColorList(
                    BLUE,
                    RED,
                    MAGENTA,
                    YELLOW,
                    YELLOW,
                    MAGENTA,
                    YELLOW,
                    BLUE,
                    RED
                )
            ),
            downFace = DownFace(
                ColorList(
                    BLUE,
                    MAGENTA,
                    MAGENTA,
                    MAGENTA,
                    WHITE,
                    WHITE,
                    RED,
                    RED,
                    GREEN
                )
            )
        )
    }

    protected abstract val model: String
    protected abstract val modelParser: RubikCubeModelParser

    private inline fun forEachFace(
        block: (expected: String, result: String) -> Unit,
    ) {
        for (i in 0 until CUBE_FACES) {
            val startIndex = i * CUBE_FACE_CELLS
            val endIndex = startIndex + CUBE_FACE_CELLS
            val expected = model.substring(startIndex, endIndex)
            val result = modelParser.parse(rubikCube).substring(startIndex, endIndex)

            block(expected, result)
        }
    }

    @Test
    fun testFaceletsMustBeEqual() {
        forEachFace { expected, result ->
            assertEquals(expected, result)
        }
    }
}