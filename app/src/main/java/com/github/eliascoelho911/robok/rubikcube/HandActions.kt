package com.github.eliascoelho911.robok.rubikcube

import com.github.eliascoelho911.robok.robot.bluetooth.RobotBluetoothManager

class HandActionManager(
    private val leftHand: LeftHand,
    private val rightHand: RightHand,
    private val bluetoothManager: RobotBluetoothManager
) {
    suspend fun openLeftHand() {
        openHand(leftHand)
    }

    suspend fun closeLeftHand() {
        closeHand(leftHand)
    }

    suspend fun rotateLeftHand(
        clockwise: Boolean,
        amount: Int = 1,
        rotateCube: Boolean,
        isAuto: Boolean = true
    ) {
        val handRotation = if (clockwise) HandRotation.Clockwise else HandRotation.CounterClockwise
        rotateLeftHand(handRotation, amount, rotateCube, isAuto)
    }

    suspend fun rotateLeftHand(
        rotation: HandRotation,
        amount: Int = 1,
        rotateCube: Boolean,
        isAuto: Boolean = true
    ) {
        handleHandRotation(leftHand, rotation, amount, rotateCube, isAuto)
    }

    suspend fun openRightHand() {
        openHand(rightHand)
    }

    suspend fun closeRightHand() {
        closeHand(rightHand)
    }

    suspend fun rotateRightHand(
        clockwise: Boolean,
        amount: Int = 1,
        rotateCube: Boolean,
        isAuto: Boolean = true
    ) {
        val handRotation = if (clockwise) HandRotation.Clockwise else HandRotation.CounterClockwise
        rotateRightHand(handRotation, amount, rotateCube, isAuto)
    }

    suspend fun rotateRightHand(
        rotation: HandRotation,
        amount: Int = 1,
        rotateCube: Boolean,
        isAuto: Boolean = true
    ) {
        handleHandRotation(rightHand, rotation, amount, rotateCube, isAuto)
    }

    private suspend fun handleHandRotation(
        hand: Hand,
        rotation: HandRotation,
        amount: Int,
        rotateCube: Boolean,
        isAuto: Boolean = true
    ) {
        repeat(amount) {
            rotateHand(hand, rotation, rotateCube, isAuto)
        }
    }

    private suspend fun rotateHand(
        hand: Hand,
        rotation: HandRotation,
        rotateCube: Boolean,
        isAuto: Boolean = true
    ) {
        if (isAuto) {
            val otherHand = getOtherHand(hand)

            closeHand(hand)

            if (rotateCube) {
                openHand(otherHand)
            } else {
                closeHand(otherHand)
            }

            bluetoothManager.sendRotationCommand(hand, rotation)

            if (rotateCube) closeHand(otherHand)
            openHand(hand)
            bluetoothManager.sendRotationCommand(hand, HandRotation.Middle)
            closeHand(hand)
            bluetoothManager.sendRotationCommand(hand, HandRotation.Middle)
            bluetoothManager.sendRotationCommand(otherHand, HandRotation.Middle)
        } else {
            bluetoothManager.sendRotationCommand(hand, rotation)
        }
    }

    private suspend fun closeHand(hand: Hand) {
        openOrCloseHand(hand, open = false)
    }

    private suspend fun openHand(hand: Hand) {
        openOrCloseHand(hand, open = true)
    }

    private suspend fun openOrCloseHand(hand: Hand, open: Boolean) {
        bluetoothManager.sendOpenOrCloseCommand(hand, open)
    }

    private fun getOtherHand(hand: Hand): Hand {
        return if (hand == leftHand) rightHand else leftHand
    }
}

private suspend fun RobotBluetoothManager.sendRotationCommand(
    hand: Hand,
    rotation: HandRotation
) {
    sendCommand("move:${hand.handId},${rotation.value};")
}

private suspend fun RobotBluetoothManager.sendOpenOrCloseCommand(hand: Hand, open: Boolean) {
    sendCommand("move:${hand.gripperId},${if (open) "open" else "close"};")
}