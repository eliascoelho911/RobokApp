package com.github.eliascoelho911.robok.robot

import android.util.Log
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
    companion object {
        private const val TAG = "RobokRobot"
    }
    
    var facePointingToRightHand: CompatibleWithRightHand = cube.rightFace
    var facePointingToLeftHand: CompatibleWithLeftHand = cube.downFace

    val handActionManager by lazy { HandActionManager(leftHand, rightHand, bluetoothManager) }

    suspend fun runMovement(movement: RubikCube.Movement): Boolean {
        Log.i(
            TAG,
            "Executando movimento: ${movement.notation} (clockwise: ${movement.isClockwise})"
        )
        return runCatching {
            withContext(Dispatchers.IO) {
                val face = movement.faceMovedByMovement(cube)
                Log.i(TAG, "Face a ser movida: ${face.javaClass.simpleName}")
                
                if (rotateCubeToMoveFace(face)) {
                    Log.i(TAG, "Cubo rotacionado com sucesso para mover a face")
                    handleFace(
                        face,
                        onCompatibleWithLeftHand = {
                            Log.i(
                                TAG,
                                "Rotacionando mão esquerda (clockwise: ${movement.isClockwise})"
                            )
                            handActionManager.rotateLeftHand(clockwise = movement.isClockwise)
                        },
                        onCompatibleWithRightHand = {
                            Log.i(
                                TAG,
                                "Rotacionando mão direita (clockwise: ${movement.isClockwise})"
                            )
                            handActionManager.rotateRightHand(clockwise = movement.isClockwise)
                        }
                    )
                } else {
                    Log.e(TAG, "Falha ao rotacionar o cubo para mover a face")
                }
            }
        }.also { result ->
            Log.i(
                TAG,
                "Movimento ${movement.notation} ${if (result.isSuccess) "executado com sucesso" else "falhou"}"
            )
        }.isSuccess
    }

    suspend fun receiveCube(): Boolean {
        Log.i(TAG, "Recebendo cubo...")
        return runCatching {
            withContext(Dispatchers.IO) {
                bluetoothManager.sendCommand("receive:0;")
                Log.i(TAG, "Comando para receber cubo enviado")
            }
        }.also { result ->
            if (result.isSuccess) {
                Log.i(TAG, "Cubo recebido com sucesso")
            } else {
                Log.e(TAG, "Falha ao receber cubo", result.exceptionOrNull())
            }
        }.isSuccess
    }

    private suspend fun rotateCubeToMoveFace(face: Face): Boolean {
        Log.i(TAG, "Rotacionando cubo para mover a face: ${face.javaClass.simpleName}")
        return runCatching {
            withContext(Dispatchers.IO) {
                handleFace(
                    face,
                    onCompatibleWithLeftHand = {
                        Log.i(
                            TAG,
                            "Posicionando face ${it.javaClass.simpleName} para a mão esquerda"
                        )
                        pointFaceToLeftHand(it)
                    },
                    onCompatibleWithRightHand = {
                        Log.i(
                            TAG,
                            "Posicionando face ${it.javaClass.simpleName} para a mão direita"
                        )
                        pointFaceToRightHand(it)
                    }
                )
            }
        }.also { result ->
            if (result.isFailure) {
                Log.e(TAG, "Falha ao rotacionar cubo", result.exceptionOrNull())
            }
        }.isSuccess
    }

    private suspend fun pointFaceToLeftHand(face: CompatibleWithLeftHand) {
        Log.i(TAG, "Posicionando face para mão esquerda: ${face.javaClass.simpleName}")
        Log.i(TAG, "Face atual na mão esquerda: ${facePointingToLeftHand.javaClass.simpleName}")
        
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

        if (steps == 0) {
            Log.i(TAG, "Não é necessário rotacionar, face já está na posição correta")
            return
        }

        rotateCubeWithRightHand(steps)

        Log.i(TAG, "Face ${face.javaClass.simpleName} posicionada para mão esquerda")
        facePointingToLeftHand = face
    }

    private suspend fun pointFaceToRightHand(face: CompatibleWithRightHand) {
        Log.i(TAG, "Posicionando face para mão direita: ${face.javaClass.simpleName}")
        Log.i(TAG, "Face atual na mão direita: ${facePointingToRightHand.javaClass.simpleName}")
        
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

        if (steps == 0) {
            Log.i(TAG, "Não é necessário rotacionar, face já está na posição correta")
            return
        }

        rotateCubeWithLeftHand(steps)

        Log.i(TAG, "Face ${face.javaClass.simpleName} posicionada para mão direita")
        facePointingToRightHand = face
    }

    private suspend fun rotateCubeWithLeftHand(steps: Int) {
        Log.i(TAG, "Rotacionando cubo com mão esquerda, passos: $steps")

        handActionManager.rotateLeftHand(clockwise = steps > 0, amount = abs(steps))

        Log.i(TAG, "Rotação do cubo com mão esquerda completada")
    }

    private suspend fun rotateCubeWithRightHand(steps: Int) {
        Log.i(TAG, "Rotacionando cubo com mão direita, passos: $steps")

        handActionManager.rotateRightHand(clockwise = steps > 0, amount = abs(steps))

        Log.i(TAG, "Rotação do cubo com mão direita completada")
    }

    private suspend fun handleFace(
        face: Face,
        onCompatibleWithLeftHand: suspend (CompatibleWithLeftHand) -> Unit,
        onCompatibleWithRightHand: suspend (CompatibleWithRightHand) -> Unit
    ) {
        Log.i(TAG, "Manipulando face: ${face.javaClass.simpleName}")
        when (face) {
            is CompatibleWithLeftHand -> {
                Log.i(TAG, "Face compatível com mão esquerda")
                onCompatibleWithLeftHand(face)
            }

            is CompatibleWithRightHand -> {
                Log.i(TAG, "Face compatível com mão direita")
                onCompatibleWithRightHand(face)
            }

            else -> {
                Log.e(TAG, "Tipo de face não suportado: ${face.javaClass.simpleName}")
            }
        }
        Log.i(TAG, "Manipulação de face concluída")
    }
}
