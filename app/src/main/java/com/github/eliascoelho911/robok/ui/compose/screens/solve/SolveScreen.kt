package com.github.eliascoelho911.robok.ui.compose.screens.solve

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.eliascoelho911.robok.ui.compose.components.SolvePlayer

@Composable
fun SolveScreen(
    viewModel: SolveViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    val isSolved =
        state.currentMoveIndex >= state.rubikCubeMoves.size - 1 && state.rubikCubeMoves.isNotEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.height(16.dp))

        SolvePlayer(
            currentMove = getCurrentMoveNotation(state),
            currentMoveIndex = state.currentMoveIndex,
            totalMoves = state.rubikCubeMoves.size,
            isPlaying = false,
            isSolved = isSolved,
            onPreviousClick = { viewModel.onPrevious() },
            onNextClick = { viewModel.onNext() },
            modifier = Modifier
        )
    }
}

/**
 * Obtém a notação do movimento atual
 */
private fun getCurrentMoveNotation(state: SolveState): String {
    return if (state.rubikCubeMoves.isEmpty() || state.currentMoveIndex >= state.rubikCubeMoves.size) {
        ""
    } else {
        state.rubikCubeMoves[state.currentMoveIndex].notation
    }
}