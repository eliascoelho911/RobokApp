package com.github.eliascoelho911.robok.rubikcube

const val DELAY_BETWEEN_COMMANDS = 3000L

sealed interface Hand {
    val handId: String
    val gripperId: String
}

data object LeftHand : Hand {
    override val handId: String = "hl"
    override val gripperId: String = "gl"
}

data object RightHand : Hand {
    override val handId: String = "hr"
    override val gripperId: String = "gr"
}