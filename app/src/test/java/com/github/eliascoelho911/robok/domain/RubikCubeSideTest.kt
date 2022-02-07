package com.github.eliascoelho911.robok.domain

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class RubikCubeSideTest {
    private val colors = mockk<List<RubikCubeColor>>()
    private val side = RubikCubeSide(colors)

    @Before
    fun setup() {
        every { colors[any()] } returns mockk()
    }

    @Test
    fun testPegaCorCorreta() {
        side.get(0, 0)
        verify { colors[0] }

        side.get(0, 2)
        verify { colors[2] }

        side.get(1, 2)
        verify { colors[5] }
    }
}