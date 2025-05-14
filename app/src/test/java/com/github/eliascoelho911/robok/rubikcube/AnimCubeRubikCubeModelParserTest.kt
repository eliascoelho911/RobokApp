package com.github.eliascoelho911.robok.rubikcube

import android.graphics.Color.BLUE
import android.graphics.Color.GREEN
import android.graphics.Color.MAGENTA
import android.graphics.Color.RED
import android.graphics.Color.WHITE
import android.graphics.Color.YELLOW
import kotlin.reflect.KClass

class AnimCubeRubikCubeModelParserTest : RubikCubeModelParserBaseTest() {
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
    private val modelByFace by lazy {
        listOf("up" to "143112432",
            "down" to "423203205",
            "front" to "502145015",
            "back" to "120455044",
            "left" to "353320041",
            "right" to "531135402")
    }
    override val modelParser by lazy {
        TestAnimCubeModelCreator(colorToModelMapper)
    }
    override val model = modelByFace.joinToString("") { it.second }
}

class TestAnimCubeModelCreator(
    private val colorToModelMapper: Map<Int, String>,
) : AnimCubeModelParser() {
    override fun createColorMapper(distinctColors: Map<KClass<out Face>, Int>): Map<Int, String> =
        colorToModelMapper
}
