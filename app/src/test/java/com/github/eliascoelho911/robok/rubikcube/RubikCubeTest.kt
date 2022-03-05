package com.github.eliascoelho911.robok.rubikcube

import com.github.eliascoelho911.robok.util.createColorFrom
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [30])
@RunWith(RobolectricTestRunner::class)
class RubikCubeTest {
    @Test
    fun testDevePadronizarCoresDasFaces() {
        println(rubikCube.facesWithStandardizesColors.flatMap { face -> face.colors.map { createColorFrom(it) } }.distinct())
    }
}