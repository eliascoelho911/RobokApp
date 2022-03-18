package com.github.eliascoelho911.robok.rubikcube

class Min2PhaseModelCreatorTest : ModelCreatorBaseTest() {
    private val modelByFace by lazy {
        listOf("up" to "FRLUULUFR",
            "right" to "BUFRRDUBL",
            "front" to "BUDDFULBB",
            "down" to "FLLLDDRRB",
            "left" to "RBRDLRUFD",
            "back" to "UFDLBFDBF")
    }
    override val model: Model = modelByFace.joinToString(separator = "") { it.second }
    override val modelCreator: ModelCreator = Min2PhaseModelCreator
}