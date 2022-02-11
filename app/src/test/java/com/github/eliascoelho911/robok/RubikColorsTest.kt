package com.github.eliascoelho911.robok

import com.github.eliascoelho911.robok.analyzers.getColorsOfGrid
import com.github.eliascoelho911.robok.domain.RubikCubeSideColor
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowInstrumentation.getInstrumentation

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class RubikColorsTest : BaseTest() {
    @Test
    fun testPegaAsCoresDoCuboCorretamente() {
        rubikSide.forEach { (side, bitmap) ->
            bitmap.getColorsOfGrid(3, 3).forEachIndexed { index, color ->
                assertEquals("index: $index, file: ${side.path}",
                    side.rubikCubeSide.get(index),
                    RubikCubeSideColor.findBySimilarity(getInstrumentation().context, color))
            }
        }
    }
}