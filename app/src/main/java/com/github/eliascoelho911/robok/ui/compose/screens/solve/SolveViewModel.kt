package com.github.eliascoelho911.robok.ui.compose.screens.solve

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.eliascoelho911.robok.robot.Robot
import com.github.eliascoelho911.robok.robot.bluetooth.RobotBluetoothManager.ConnectionState
import com.github.eliascoelho911.robok.rubikcube.RubikCube
import com.github.eliascoelho911.robok.rubikcube.RubikCubeMoves
import com.github.eliascoelho911.robok.rubikcube.RubikCubeSolver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SolveViewModel(
    val robot: Robot,
    val rubikCubeSolver: RubikCubeSolver
) : ViewModel() {

    private val _state = MutableStateFlow<SolveState>(SolveState.Empty)
    val state = _state.asStateFlow()

    val connectionState = robot.bluetoothManager.connectionState
    val cube = robot.cube

    init {
        attemptToConnectToRobot()
        solveCubeAndUpdateMovements()
    }

    fun onNext() {
        val nextMovement = state.value.nextMovement

        runMove(nextMovement, state.value.nextMovementIndex)
    }

    fun onPrevious() {
        val previousMovement = state.value.previousMovement

        runMove(previousMovement, state.value.previousMovementIndex)
    }

    fun attemptToConnectToRobot() {
        viewModelScope.launch {
            robot.bluetoothManager.connect()
            // TODO Dar feedbacks para o usuário (conectando, pedir para ligar o bluetooth, etc)
        }
    }

    fun disconnectFromRobot() {
        robot.bluetoothManager.disconnect()
    }

    fun receiveCube() {
        if (connectionState.value != ConnectionState.CONNECTED) {
            return
        }

        viewModelScope.launch {
            val success = robot.receiveCube()
            if (success) {
                _state.update { it.copy(cubeReceived = true) }
            }
        }
    }

    fun resetSetup() {
        _state.update { it.copy(cubeReceived = false) }
    }

    override fun onCleared() {
        super.onCleared()
        disconnectFromRobot()
    }

    private fun solveCubeAndUpdateMovements() {
        viewModelScope.launch {
            val movements = rubikCubeSolver.solve(cube)

            _state.update {
                it.copy(rubikCubeMoves = movements, currentMoveIndex = 0)
            }
        }
    }

    private fun runMove(move: RubikCube.Movement, newIndex: Int) {
        if (connectionState.value != ConnectionState.CONNECTED) {
            return
        }

        viewModelScope.launch {
            robot.runMovement(move)
        }

        _state.update {
            it.copy(currentMoveIndex = newIndex)
        }
    }
}

@Immutable
data class SolveState(
    val rubikCubeMoves: RubikCubeMoves,
    val currentMoveIndex: Int = 0,
    val cubeReceived: Boolean = false,
) {
    val nextMovement: RubikCube.Movement
        get() = rubikCubeMoves[nextMovementIndex]

    val nextMovementIndex: Int
        get() = (currentMoveIndex + 1).coerceAtMost(rubikCubeMoves.size - 1)

    val previousMovement: RubikCube.Movement
        get() = rubikCubeMoves[previousMovementIndex]

    val previousMovementIndex: Int
        get() = (currentMoveIndex - 1).coerceAtLeast(0)

    companion object {
        val Empty = SolveState(emptyList())
    }
}