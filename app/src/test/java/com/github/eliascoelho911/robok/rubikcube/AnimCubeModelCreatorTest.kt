package com.github.eliascoelho911.robok.rubikcube

import android.graphics.Color.BLUE
import android.graphics.Color.GREEN
import android.graphics.Color.MAGENTA
import android.graphics.Color.RED
import android.graphics.Color.WHITE
import android.graphics.Color.YELLOW
import com.github.eliascoelho911.robok.rubikcube.RubikCube.Companion.NumberOfFacelets
import com.github.eliascoelho911.robok.rubikcube.RubikCube.Companion.NumberOfFaces
import com.github.eliascoelho911.robok.rubikcube.face.Face
import com.github.eliascoelho911.robok.rubikcube.face.Position.BACK
import com.github.eliascoelho911.robok.rubikcube.face.Position.DOWN
import com.github.eliascoelho911.robok.rubikcube.face.Position.FRONT
import com.github.eliascoelho911.robok.rubikcube.face.Position.LEFT
import com.github.eliascoelho911.robok.rubikcube.face.Position.RIGHT
import com.github.eliascoelho911.robok.rubikcube.face.Position.UP
import org.junit.Assert.assertEquals
import org.junit.Test

class AnimCubeModelCreatorTest {
    private val rubikCube by lazy {
        RubikCube(listOf(
            Face(FRONT, GREEN, YELLOW, WHITE, WHITE, BLUE, YELLOW, MAGENTA, GREEN, GREEN),
            Face(RIGHT, GREEN, YELLOW, BLUE, RED, RED, WHITE, YELLOW, GREEN, MAGENTA),
            Face(BACK, YELLOW, BLUE, WHITE, MAGENTA, GREEN, BLUE, WHITE, GREEN, BLUE),
            Face(LEFT, RED, GREEN, RED, WHITE, MAGENTA, RED, YELLOW, BLUE, WHITE),
            Face(UP, BLUE, RED, MAGENTA, YELLOW, YELLOW, MAGENTA, YELLOW, BLUE, RED),
            Face(DOWN, BLUE, MAGENTA, MAGENTA, MAGENTA, WHITE, WHITE, RED, RED, GREEN)
        )).apply {
            modelCreator = this@AnimCubeModelCreatorTest.modelCreator
        }
    }
    private val colorToModelMapper by lazy {
        mapOf(
            WHITE to "0",
            YELLOW to "1",
            MAGENTA to "2",
            RED to "3",
            BLUE to "4",
            GREEN to "5"
        )
    }
    private val modelCreator by lazy {
        TestAnimCubeModelCreator(colorToModelMapper)
    }
    private val modelByFace by lazy {
        listOf("up" to "143112432",
            "down" to "423203205",
            "front" to "502145015",
            "back" to "120455044",
            "left" to "353320041",
            "right" to "531135402")
    }
    private val model = modelByFace.joinToString("") { it.second }

    @Test
    fun testFaceletsMustBeEqual() {
        forEachFace { expected, result ->
            assertEquals(expected, result)
        }
    }

    @Test
    fun testFaceColorsMustMatch() {
        forEachFace { expected, result ->
            assertEquals(expected.sumOf { it.digitToInt() }, result.sumOf { it.digitToInt() })
        }
    }

    private inline fun forEachFace(
        block: (expected: Model, result: Model) -> Unit,
    ) {
        for (i in 0 until NumberOfFaces) {
            val startIndex = i * NumberOfFacelets
            val endIndex = startIndex + NumberOfFacelets
            val expected = model.substring(startIndex, endIndex)
            val result = rubikCube.model.substring(startIndex, endIndex)

            block(expected, result)
        }
    }
}

private class TestAnimCubeModelCreator(
    private val colorToModelMapper: Map<Int, String>,
) : AnimCubeModelCreator() {
    override fun createColorMapper(distinctColors: List<Int>): Map<Int, String> = colorToModelMapper
}
