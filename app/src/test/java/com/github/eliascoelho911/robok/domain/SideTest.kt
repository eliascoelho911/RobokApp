package com.github.eliascoelho911.robok.domain

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class SideTest {
    private val cells = mockk<List<Cell>>()
    private val side = Side(cells)

    @Before
    fun setup() {
        every { cells[any()] } returns mockk()
    }

    @Test
    fun testPegaCelulaCorreta() {
        side.get(0, 0)
        verify { cells[0] }

        side.get(0, 2)
        verify { cells[2] }

        side.get(1, 2)
        verify { cells[5] }
    }
}