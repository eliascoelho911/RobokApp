package com.github.eliascoelho911.robok.ui.compose.screens.solve

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.eliascoelho911.robok.robot.Robot
import com.github.eliascoelho911.robok.rubikcube.RubikCube
import com.github.eliascoelho911.robok.rubikcube.RubikCubeMoves
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SolveViewModel(
    private val robot: Robot
) : ViewModel() {

    private val _state = MutableStateFlow<SolveState>(SolveState.Empty)
    val state = _state.asStateFlow()

    fun onNext() {
        val nextMovement = state.value.nextMovement

        runMove(nextMovement, state.value.nextMovementIndex)
    }

    fun onPrevious() {
        val previousMovement = state.value.previousMovement

        runMove(previousMovement, state.value.previousMovementIndex)
    }

    private fun runMove(move: RubikCube.Movement, newIndex: Int) {
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