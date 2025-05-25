package com.github.eliascoelho911.robok.rubikcube.interaction

import com.github.eliascoelho911.robok.robot.Hand
import com.github.eliascoelho911.robok.robot.HandRotation
import com.github.eliascoelho911.robok.robot.LeftHand
import com.github.eliascoelho911.robok.robot.RightHand
import com.github.eliascoelho911.robok.robot.bluetooth.RobotBluetoothManager

internal abstract class HandMover {

    protected abstract val bluetoothManager: RobotBluetoothManager
    protected abstract val leftHand: LeftHand
    protected abstract val rightHand: RightHand

    suspend fun openLeftHand() {
        openHand(leftHand)
    }

    suspend fun openRightHand() {
        openHand(rightHand)
    }

    suspend fun closeLeftHand() {
        closeHand(leftHand)
    }

    suspend fun closeRightHand() {
        closeHand(rightHand)
    }

    suspend fun rotateLeftHand(rotation: HandRotation) {
        rotateHand(leftHand, rotation)
    }

    suspend fun rotateRightHand(rotation: HandRotation) {
        rotateHand(rightHand, rotation)
    }

    protected abstract suspend fun rotateHand(
        hand: Hand,
        rotation: HandRotation
    )

    protected abstract fun getPulseFromRotation(hand: Hand, rotation: HandRotation): Int

    protected suspend fun openHand(hand: Hand) {
        sendOpenOrCloseCommand(hand, open = true)
    }

    protected suspend fun closeHand(hand: Hand) {
        sendOpenOrCloseCommand(hand, open = false)
    }

    protected suspend fun resetToMiddle(hand: Hand, otherHand: Hand) {
        openHand(hand)
        sendRotationCommand(hand, HandRotation.Middle, fast = true)
        closeHand(hand)
//        sendRotationCommand(hand, HandRotation.Middle, fast = true)
//        sendRotationCommand(otherHand, HandRotation.Middle, fast = true)
    }

    protected suspend fun sendRotationCommand(hand: Hand, rotation: HandRotation, fast: Boolean) {
        val speed = if (fast) 0.1f else 0.02f
        val pulse = getPulseFromRotation(hand, rotation)
        bluetoothManager.sendCommand("rotate:${hand.handId},${pulse},${speed};")
    }

    protected suspend fun sendOpenOrCloseCommand(hand: Hand, open: Boolean) {
        bluetoothManager.sendCommand("gripper:${hand.gripperId},${if (open) "open" else "close"};")
    }

    protected fun getOtherHand(hand: Hand): Hand {
        return if (hand == leftHand) rightHand else leftHand
    }
}

internal class CubeRotator(
    override val bluetoothManager: RobotBluetoothManager,
    override val leftHand: LeftHand,
    override val rightHand: RightHand,
) : HandMover() {

    override suspend fun rotateHand(
        hand: Hand,
        rotation: HandRotation
    ) {
        val otherHand = getOtherHand(hand)

        closeHand(hand)
        openHand(otherHand)

        sendRotationCommand(hand, rotation, fast = true)

        closeHand(otherHand)
        resetToMiddle(hand, otherHand)
    }

    override fun getPulseFromRotation(hand: Hand, rotation: HandRotation): Int {
        return when (rotation) {
            HandRotation.Clockwise -> if (hand == leftHand) LEFT_HAND_CLOCKWISE else RIGHT_HAND_CLOCKWISE
            HandRotation.CounterClockwise -> if (hand == leftHand) LEFT_HAND_COUNTER_CLOCKWISE else RIGHT_HAND_COUNTER_CLOCKWISE
            HandRotation.Middle -> if (hand == leftHand) LEFT_HAND_MIDDLE else RIGHT_HAND_MIDDLE
        }
    }

    companion object {
        private const val LEFT_HAND_CLOCKWISE = 530
        private const val LEFT_HAND_COUNTER_CLOCKWISE = 130
        private const val LEFT_HAND_MIDDLE = 320

        private const val RIGHT_HAND_CLOCKWISE = 505
        private const val RIGHT_HAND_COUNTER_CLOCKWISE = 120
        private const val RIGHT_HAND_MIDDLE = 320
    }
}

internal open class FaceRotator(
    override val bluetoothManager: RobotBluetoothManager,
    override val leftHand: LeftHand,
    override val rightHand: RightHand,
) : HandMover() {
    override suspend fun rotateHand(
        hand: Hand,
        rotation: HandRotation
    ) {
        val otherHand = getOtherHand(hand)

        closeHand(hand)
        closeHand(otherHand)

        sendRotationCommand(hand, rotation, fast = false)

        resetToMiddle(hand, otherHand)
    }

    override fun getPulseFromRotation(
        hand: Hand,
        rotation: HandRotation
    ): Int {
        return when (rotation) {
            HandRotation.Clockwise -> if (hand == leftHand) LEFT_HAND_CLOCKWISE else RIGHT_HAND_CLOCKWISE
            HandRotation.CounterClockwise -> if (hand == leftHand) LEFT_HAND_COUNTER_CLOCKWISE else RIGHT_HAND_COUNTER_CLOCKWISE
            HandRotation.Middle -> if (hand == leftHand) LEFT_HAND_MIDDLE else RIGHT_HAND_MIDDLE
        }
    }

    companion object {
        private const val LEFT_HAND_CLOCKWISE = 525
        private const val LEFT_HAND_COUNTER_CLOCKWISE = 105
        private const val LEFT_HAND_MIDDLE = 320

        private const val RIGHT_HAND_CLOCKWISE = 525
        private const val RIGHT_HAND_COUNTER_CLOCKWISE = 105
        private const val RIGHT_HAND_MIDDLE = 320
    }
}

internal class DefaultHandMover(
    override val bluetoothManager: RobotBluetoothManager,
    override val leftHand: LeftHand,
    override val rightHand: RightHand
) : FaceRotator(bluetoothManager, leftHand, rightHand) {
    override suspend fun rotateHand(hand: Hand, rotation: HandRotation) {
        sendRotationCommand(hand, rotation, fast = true)
    }
}