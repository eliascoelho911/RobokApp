package com.github.eliascoelho911.robok.robot

interface RobotHandsProvider {
    val leftHand: LeftHand
    val rightHand: RightHand
}

object RobotHandsProviderImpl : RobotHandsProvider {
    override val leftHand: LeftHand = LeftHand
    override val rightHand: RightHand = RightHand
}