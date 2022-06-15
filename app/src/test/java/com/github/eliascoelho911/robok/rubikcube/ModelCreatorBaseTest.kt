package com.github.eliascoelho911.robok.rubikcube

import android.graphics.Color.BLUE
import android.graphics.Color.GREEN
import android.graphics.Color.MAGENTA
import android.graphics.Color.RED
import android.graphics.Color.WHITE
import android.graphics.Color.YELLOW
import com.github.eliascoelho911.robok.rubikcube.face.Face
import com.github.eliascoelho911.robok.rubikcube.face.Position.BACK
import com.github.eliascoelho911.robok.rubikcube.face.Position.DOWN
import com.github.eliascoelho911.robok.rubikcube.face.Position.FRONT
import com.github.eliascoelho911.robok.rubikcube.face.Position.LEFT
import com.github.eliascoelho911.robok.rubikcube.face.Position.RIGHT
import com.github.eliascoelho911.robok.rubikcube.face.Position.UP
import org.junit.Assert.assertEquals
import org.junit.Test

abstract class ModelCreatorBaseTest {
    private val rubikCube by lazy {
        RubikCube(listOf(
            Face(FRONT, GREEN, YELLOW, WHITE, WHITE, BLUE, YELLOW, MAGENTA, GREEN, GREEN),
            Face(RIGHT, GREEN, YELLOW, BLUE, RED, RED, WHITE, YELLOW, GREEN, MAGENTA),
            Face(BACK, YELLOW, BLUE, WHITE, MAGENTA, GREEN, BLUE, WHITE, GREEN, BLUE),
            Face(LEFT, RED, GREEN, RED, WHITE, MAGENTA, RED, YELLOW, BLUE, WHITE),
            Face(UP, BLUE, RED, MAGENTA, YELLOW, YELLOW, MAGENTA, YELLOW, BLUE, RED),
            Face(DOWN, BLUE, MAGENTA, MAGENTA, MAGENTA, WHITE, WHITE, RED, RED, GREEN)
        ))
    }

    protected abstract val model: Model
    protected abstract val modelCreator: ModelCreator

    private inline fun forEachFace(
        block: (expected: Model, result: Model) -> Unit,
    ) {
        for (i in 0 until RubikCube.NumberOfFaces) {
            val startIndex = i * RubikCube.NumberOfFacelets
            val endIndex = startIndex + RubikCube.NumberOfFacelets
            val expected = model.substring(startIndex, endIndex)
            val result = rubikCube.createModelWith(modelCreator).substring(startIndex, endIndex)

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