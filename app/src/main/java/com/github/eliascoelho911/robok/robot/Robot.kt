package com.github.eliascoelho911.robok.robot

import com.github.eliascoelho911.robok.robot.bluetooth.RobotBluetoothManager
import com.github.eliascoelho911.robok.rubikcube.BackFace
import com.github.eliascoelho911.robok.rubikcube.CompatibleWithLeftHand
import com.github.eliascoelho911.robok.rubikcube.CompatibleWithRightHand
import com.github.eliascoelho911.robok.rubikcube.DELAY_BETWEEN_COMMANDS
import com.github.eliascoelho911.robok.rubikcube.DownFace
import com.github.eliascoelho911.robok.rubikcube.Face
import com.github.eliascoelho911.robok.rubikcube.FrontFace
import com.github.eliascoelho911.robok.rubikcube.Hand
import com.github.eliascoelho911.robok.rubikcube.LeftFace
import com.github.eliascoelho911.robok.rubikcube.LeftHand
import com.github.eliascoelho911.robok.rubikcube.RightFace
import com.github.eliascoelho911.robok.rubikcube.RightHand
import com.github.eliascoelho911.robok.rubikcube.RubikCube
import com.github.eliascoelho911.robok.rubikcube.UpFace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.abs

class Robot(
    val cube: RubikCube,
    val leftHand: LeftHand,
    val rightHand: RightHand,
    private val bluetoothManager: RobotBluetoothManager
) {
    var facePointingToRightHand: CompatibleWithRightHand = cube.rightFace
    var facePointingToLeftHand: CompatibleWithLeftHand = cube.downFace

    suspend fun runMovement(movement: RubikCube.Movement): Boolean {
        return runCatching {
            withContext(Dispatchers.IO) {
                val face = movement.faceMovedByMovement(cube)
                if (rotateCubeToMoveFace(face)) {
                    handleFace(
                        face,
                        onCompatibleWithLeftHand = {
                            leftHand.rotate(clockwise = movement.isClockwise)
                        },
                        onCompatibleWithRightHand = {
                            rightHand.rotate(clockwise = movement.isClockwise)
                        }
                    )
                }
            }
        }.isSuccess
    }

    private suspend fun rotateCubeToMoveFace(face: Face): Boolean {
        return runCatching {
            withContext(Dispatchers.IO) {
                handleFace(
                    face,
                    onCompatibleWithLeftHand = { pointFaceToLeftHand(it) },
                    onCompatibleWithRightHand = { pointFaceToRightHand(it) }
                )
            }
        }.isSuccess
    }

    private suspend fun pointFaceToLeftHand(face: CompatibleWithLeftHand) {
        val steps = when (face) {
            is BackFace -> {
                when (facePointingToLeftHand) {
                    is BackFace -> 0
                    is DownFace -> 1
                    is FrontFace -> 2
                    is UpFace -> -1
                }
            }

            is DownFace -> {
                when (facePointingToLeftHand) {
                    is BackFace -> -1
                    is DownFace -> 0
                    is FrontFace -> 1
                    is UpFace -> 2
                }
            }

            is FrontFace -> {
                when (facePointingToLeftHand) {
                    is BackFace -> -2
                    is DownFace -> 1
                    is FrontFace -> 0
                    is UpFace -> -1
                }
            }

            is UpFace -> {
                when (facePointingToLeftHand) {
                    is BackFace -> -1
                    is DownFace -> -2
                    is FrontFace -> -1
                    is UpFace -> 0
                }
            }
        }

        if (steps == 0) return

        rotateRightHand(steps)

        facePointingToLeftHand = face
    }

    private suspend fun pointFaceToRightHand(face: CompatibleWithRightHand) {
        val steps = when (face) {
            is BackFace -> {
                when (facePointingToRightHand) {
                    is BackFace -> 0
                    is FrontFace -> -2
                    is LeftFace -> 1
                    is RightFace -> -1
                }
            }

            is FrontFace -> {
                when (facePointingToRightHand) {
                    is BackFace -> 2
                    is FrontFace -> 0
                    is LeftFace -> -1
                    is RightFace -> 1
                }
            }

            is LeftFace -> {
                when (facePointingToRightHand) {
                    is BackFace -> -1
                    is FrontFace -> 1
                    is LeftFace -> 0
                    is RightFace -> -2
                }
            }

            is RightFace -> {
                when (facePointingToRightHand) {
                    is BackFace -> 1
                    is FrontFace -> -1
                    is LeftFace -> 2
                    is RightFace -> 0
                }
            }
        }

        if (steps == 0) return

        rotateLeftHand(steps)

        facePointingToRightHand = face
    }

    private suspend fun rotateLeftHand(steps: Int) {
        rightHand.open()

        repeat(abs(steps)) {
            leftHand.rotate(clockwise = steps > 0)
        }

        rightHand.close()
    }

    private suspend fun rotateRightHand(steps: Int) {
        leftHand.open()

        repeat(abs(steps)) {
            rightHand.rotate(clockwise = steps > 0)
        }

        leftHand.close()
    }

    private suspend fun Hand.open() {
        bluetoothManager.sendCommand("move:${this.gripperId},open;")
        delay(DELAY_BETWEEN_COMMANDS)
    }

    private suspend fun Hand.close() {
        bluetoothManager.sendCommand("move:${this.gripperId},close;")
        delay(DELAY_BETWEEN_COMMANDS)
    }

    private suspend fun Hand.rotate(clockwise: Boolean) {
        bluetoothManager.sendCommand("move:${this.handId},${if (clockwise) "cw" else "ccw"};")
        delay(DELAY_BETWEEN_COMMANDS)
    }
}

private suspend fun handleFace(
    face: Face,
    onCompatibleWithLeftHand: suspend (CompatibleWithLeftHand) -> Unit,
    onCompatibleWithRightHand: suspend (CompatibleWithRightHand) -> Unit
) {
    when (face) {
        is CompatibleWithLeftHand -> onCompatibleWithLeftHand(face)
        is CompatibleWithRightHand -> onCompatibleWithRightHand(face)
    }
}