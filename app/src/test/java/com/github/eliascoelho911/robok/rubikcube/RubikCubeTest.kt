package com.github.eliascoelho911.robok.rubikcube

import android.graphics.Color.BLUE
import android.graphics.Color.GREEN
import android.graphics.Color.MAGENTA
import android.graphics.Color.RED
import android.graphics.Color.WHITE
import android.graphics.Color.YELLOW
import com.github.eliascoelho911.robok.rubikcube.RubikCube.Companion.NumberOfFacelets
import com.github.eliascoelho911.robok.rubikcube.face.Face
import com.github.eliascoelho911.robok.rubikcube.face.Position.BACK
import com.github.eliascoelho911.robok.rubikcube.face.Position.DOWN
import com.github.eliascoelho911.robok.rubikcube.face.Position.FRONT
import com.github.eliascoelho911.robok.rubikcube.face.Position.LEFT
import com.github.eliascoelho911.robok.rubikcube.face.Position.RIGHT
import com.github.eliascoelho911.robok.rubikcube.face.Position.UP
import org.junit.Assert.assertEquals
import org.junit.Test

class RubikCubeTest {
    @Test
    fun testIsValid() {
        val rubikCube by lazy {
            RubikCube(listOf(
                Face(FRONT, List(NumberOfFacelets) { GREEN }),
                Face(RIGHT, List(NumberOfFacelets) { YELLOW }),
                Face(BACK, List(NumberOfFacelets) { RED }),
                Face(LEFT, List(NumberOfFacelets) { BLUE }),
                Face(UP, List(NumberOfFacelets) { WHITE }),
                Face(DOWN, List(NumberOfFacelets) { MAGENTA })
            ))
        }

        assertEquals(true, rubikCube.isValid)
    }

    @Test
    fun testIsNotValid() {
        val rubikCube by lazy {
            RubikCube(listOf(
                Face(FRONT, List(NumberOfFacelets) { GREEN }),
                Face(RIGHT, List(NumberOfFacelets) { GREEN }),
                Face(BACK, List(NumberOfFacelets) { RED }),
                Face(LEFT, List(NumberOfFacelets) { BLUE }),
                Face(UP, List(NumberOfFacelets) { WHITE }),
                Face(DOWN, List(NumberOfFacelets) { MAGENTA })
            ))
        }

        assertEquals(false, rubikCube.isValid)
    }
}