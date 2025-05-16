package com.github.eliascoelho911.robok.rubikcube

import com.github.eliascoelho911.robok.robot.bluetooth.RobotBluetoothManager
import kotlinx.coroutines.delay

const val DELAY_BETWEEN_COMMANDS = 3000L

class HandActionManager(
    private val leftHand: LeftHand,
    private val rightHand: RightHand,
    private val bluetoothManager: RobotBluetoothManager
) {
    suspend fun openLeftHand() {
        bluetoothManager.sendCommand("move:${leftHand.gripperId},open;")
        delay(DELAY_BETWEEN_COMMANDS)
    }

    suspend fun closeLeftHand() {
        bluetoothManager.sendCommand("move:${leftHand.gripperId},close;")
        delay(DELAY_BETWEEN_COMMANDS)
    }

    suspend fun rotateLeftHand(clockwise: Boolean) {
        val handRotation = if (clockwise) HandRotation.Clockwise else HandRotation.CounterClockwise
        rotateLeftHand(handRotation)
    }

    suspend fun rotateLeftHand(rotation: HandRotation) {
        bluetoothManager.sendCommand("move:${leftHand.handId},${rotation.value};")
        delay(DELAY_BETWEEN_COMMANDS)
    }

    suspend fun openRightHand() {
        bluetoothManager.sendCommand("move:${rightHand.gripperId},open;")
        delay(DELAY_BETWEEN_COMMANDS)
    }

    suspend fun closeRightHand() {
        bluetoothManager.sendCommand("move:${rightHand.gripperId},close;")
        delay(DELAY_BETWEEN_COMMANDS)
    }

    suspend fun rotateRightHand(clockwise: Boolean) {
        val handRotation = if (clockwise) HandRotation.Clockwise else HandRotation.CounterClockwise
        rotateRightHand(handRotation)
    }

    suspend fun rotateRightHand(rotation: HandRotation) {
        bluetoothManager.sendCommand("move:${rightHand.handId},${rotation.value};")
        delay(DELAY_BETWEEN_COMMANDS)
    }
}