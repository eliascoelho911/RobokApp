package com.github.eliascoelho911.robok.rubikcube

class Min2PhaseRubikCubeModelParserTest : RubikCubeModelParserBaseTest() {
    private val modelByFace by lazy {
        listOf(
            "up" to "FRLUULUFR",
            "right" to "BUFRRDUBL",
            "front" to "BUDDFULBB",
            "down" to "FLLLDDRRB",
            "left" to "RBRDLRUFD",
            "back" to "UFDLBFDBF"
        )
    }
    override val model: String = modelByFace.joinToString(separator = "") { it.second }
    override val modelParser: RubikCubeModelParser = Min2PhaseModelParser()
}