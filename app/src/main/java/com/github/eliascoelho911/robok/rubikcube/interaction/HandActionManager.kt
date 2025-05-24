package com.github.eliascoelho911.robok.rubikcube.interaction

import com.github.eliascoelho911.robok.robot.HandRotation
import com.github.eliascoelho911.robok.robot.LeftHand
import com.github.eliascoelho911.robok.robot.RightHand
import com.github.eliascoelho911.robok.robot.bluetooth.RobotBluetoothManager

class HandActionManager(
    leftHand: LeftHand,
    rightHand: RightHand,
    private val bluetoothManager: RobotBluetoothManager
) {
    private val cubeRotator = CubeRotator(bluetoothManager, leftHand, rightHand)
    private val faceRotator = FaceRotator(bluetoothManager, leftHand, rightHand)
    private val defaultHandMover = DefaultHandMover(bluetoothManager, leftHand, rightHand)

    suspend fun openLeftHand() {
        defaultHandMover.openLeftHand()
    }

    suspend fun closeLeftHand() {
        defaultHandMover.closeLeftHand()
    }

    suspend fun rotateLeftHand(clockwise: Boolean) {
        val handRotation = if (clockwise) HandRotation.Clockwise else HandRotation.CounterClockwise
        rotateLeftHand(handRotation)
    }

    suspend fun rotateLeftHand(rotation: HandRotation) {
        defaultHandMover.rotateLeftHand(rotation)
    }

    suspend fun rotateRightHand(clockwise: Boolean) {
        val handRotation = if (clockwise) HandRotation.Clockwise else HandRotation.CounterClockwise
        rotateRightHand(handRotation)
    }

    suspend fun rotateRightHand(rotation: HandRotation) {
        defaultHandMover.rotateRightHand(rotation)
    }

    suspend fun rotateCubeWithLeftHand(
        clockwise: Boolean,
        amount: Int = 1
    ) {
        val handRotation = if (clockwise) HandRotation.Clockwise else HandRotation.CounterClockwise
        rotateCubeWithLeftHand(handRotation, amount)
    }

    suspend fun rotateCubeWithLeftHand(
        rotation: HandRotation,
        amount: Int = 1
    ) {
        repeat(amount) {
            cubeRotator.rotateLeftHand(rotation)
        }
    }

    suspend fun rotateFaceWithLeftHand(
        clockwise: Boolean,
        amount: Int = 1
    ) {
        val handRotation = if (clockwise) HandRotation.Clockwise else HandRotation.CounterClockwise
        rotateFaceWithLeftHand(handRotation, amount)
    }

    suspend fun rotateFaceWithLeftHand(
        rotation: HandRotation,
        amount: Int = 1
    ) {
        repeat(amount) {
            faceRotator.rotateLeftHand(rotation)
        }
    }

    suspend fun openRightHand() {
        cubeRotator.openRightHand()
    }

    suspend fun closeRightHand() {
        cubeRotator.closeRightHand()
    }

    suspend fun rotateCubeWithRightHand(
        clockwise: Boolean,
        amount: Int = 1,
    ) {
        val handRotation = if (clockwise) HandRotation.Clockwise else HandRotation.CounterClockwise
        rotateCubeWithRightHand(handRotation, amount)
    }

    suspend fun rotateCubeWithRightHand(
        rotation: HandRotation,
        amount: Int = 1,
    ) {
        repeat(amount) {
            cubeRotator.rotateRightHand(rotation)
        }
    }

    suspend fun rotateFaceWithRightHand(
        clockwise: Boolean,
        amount: Int = 1
    ) {
        val handRotation = if (clockwise) HandRotation.Clockwise else HandRotation.CounterClockwise
        rotateFaceWithRightHand(handRotation, amount)
    }

    suspend fun rotateFaceWithRightHand(
        rotation: HandRotation,
        amount: Int = 1
    ) {
        repeat(amount) {
            faceRotator.rotateRightHand(rotation)
        }
    }
}