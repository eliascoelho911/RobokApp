package com.github.eliascoelho911.robok.domain

import com.github.eliascoelho911.robok.util.Matrix
import com.github.eliascoelho911.robok.util.Position
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class SideTest {
    private val colors = mockk<Matrix<RubikCubeSideColor>>(relaxed = true)
    private val side = RubikCubeSide(mockk(), colors)

    @Before
    fun setup() {
        every { side.colors } returns colors
    }

    @Test
    fun testPegaCorCorreta() {
        side.colors[Position(0, 0)]
        verify { colors[0] }

        side.get(0, 2)
        verify { colors[2] }

        side.get(1, 2)
        verify { colors[5] }
    }
}