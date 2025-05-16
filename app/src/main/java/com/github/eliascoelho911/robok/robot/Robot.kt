package com.github.eliascoelho911.robok.robot

import com.github.eliascoelho911.robok.robot.bluetooth.RobotBluetoothManager
import com.github.eliascoelho911.robok.rubikcube.BackFace
import com.github.eliascoelho911.robok.rubikcube.CompatibleWithLeftHand
import com.github.eliascoelho911.robok.rubikcube.CompatibleWithRightHand
import com.github.eliascoelho911.robok.rubikcube.DownFace
import com.github.eliascoelho911.robok.rubikcube.Face
import com.github.eliascoelho911.robok.rubikcube.FrontFace
import com.github.eliascoelho911.robok.rubikcube.HandActionManager
import com.github.eliascoelho911.robok.rubikcube.LeftFace
import com.github.eliascoelho911.robok.rubikcube.LeftHand
import com.github.eliascoelho911.robok.rubikcube.RightFace
import com.github.eliascoelho911.robok.rubikcube.RightHand
import com.github.eliascoelho911.robok.rubikcube.RubikCube
import com.github.eliascoelho911.robok.rubikcube.UpFace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs

class Robot(
    val cube: RubikCube,
    val bluetoothManager: RobotBluetoothManager,
    val leftHand: LeftHand = LeftHand,
    val rightHand: RightHand = RightHand,
) {
    var facePointingToRightHand: CompatibleWithRightHand = cube.rightFace
    var facePointingToLeftHand: CompatibleWithLeftHand = cube.downFace

    val handActionManager by lazy { HandActionManager(leftHand, rightHand, bluetoothManager) }

    suspend fun runMovement(movement: RubikCube.Movement): Boolean {
        return runCatching {
            withContext(Dispatchers.IO) {
                val face = movement.faceMovedByMovement(cube)
                if (rotateCubeToMoveFace(face)) {
                    handleFace(
                        face,
                        onCompatibleWithLeftHand = {
                            handActionManager.rotateLeftHand(clockwise = movement.isClockwise)
                        },
                        onCompatibleWithRightHand = {
                            handActionManager.rotateRightHand(clockwise = movement.isClockwise)
                        }
                    )
                }
            }
        }.isSuccess
    }

    suspend fun receiveCube(): Boolean {
        return runCatching {
            withContext(Dispatchers.IO) {
                bluetoothManager.sendCommand("receive:0;")
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

        rotateCubeWithRightHand(steps)

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

        rotateCubeWithLeftHand(steps)

        facePointingToRightHand = face
    }

    private suspend fun rotateCubeWithLeftHand(steps: Int) {
        handActionManager.openRightHand()

        repeat(abs(steps)) {
            handActionManager.rotateLeftHand(clockwise = steps > 0)
        }

        handActionManager.closeRightHand()
    }

    private suspend fun rotateCubeWithRightHand(steps: Int) {
        handActionManager.openLeftHand()

        repeat(abs(steps)) {
            handActionManager.rotateRightHand(clockwise = steps > 0)
        }

        handActionManager.closeLeftHand()
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