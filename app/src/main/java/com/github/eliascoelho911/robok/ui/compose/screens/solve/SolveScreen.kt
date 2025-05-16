package com.github.eliascoelho911.robok.ui.compose.screens.solve

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.eliascoelho911.robok.robot.Robot
import com.github.eliascoelho911.robok.robot.bluetooth.RobotBluetoothManager
import com.github.eliascoelho911.robok.robot.bluetooth.RobotBluetoothManager.ConnectionState
import com.github.eliascoelho911.robok.rubikcube.BackFace
import com.github.eliascoelho911.robok.rubikcube.Cell
import com.github.eliascoelho911.robok.rubikcube.DownFace
import com.github.eliascoelho911.robok.rubikcube.FrontFace
import com.github.eliascoelho911.robok.rubikcube.LeftFace
import com.github.eliascoelho911.robok.rubikcube.RightFace
import com.github.eliascoelho911.robok.rubikcube.RubikCube
import com.github.eliascoelho911.robok.rubikcube.RubikCubeSolver
import com.github.eliascoelho911.robok.rubikcube.UpFace
import com.github.eliascoelho911.robok.ui.compose.components.RobotStatus
import com.github.eliascoelho911.robok.ui.compose.components.SolvePlayer
import com.github.eliascoelho911.robok.ui.compose.theme.AppTheme
import org.koin.compose.koinInject

@Composable
fun SolveScreen(
    viewModel: SolveViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle(ConnectionState.DISCONNECTED)

    val isSolved =
        state.currentMoveIndex >= state.rubikCubeMoves.size - 1 && state.rubikCubeMoves.isNotEmpty()

    // Automatically attempt to connect when the screen is first shown
    LaunchedEffect(key1 = Unit) {
        if (connectionState == ConnectionState.DISCONNECTED) {
            viewModel.attemptToConnectToRobot()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Robot status displayed at the top
            RobotStatus(
                robotBluetoothManager = viewModel.robot.bluetoothManager,
                onConnectClick = {
                    if (connectionState == ConnectionState.CONNECTED) {
                        viewModel.disconnectFromRobot()
                    } else {
                        viewModel.attemptToConnectToRobot()
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .imePadding()
                    .padding(bottom = 16.dp)
            )

            Column(
                modifier = Modifier.fillMaxSize(),
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

@Composable
@Preview
fun SolveScreenPreview() {
    val activity = LocalActivity.current
    if (activity is ComponentActivity) {
        activity.enableEdgeToEdge()
    }
    val rubikCube = remember { createRubikCubeStub() }
    val bluetoothManager = koinInject<RobotBluetoothManager>()
    val solver = koinInject<RubikCubeSolver>()
    val robot = remember(rubikCube) {
        Robot(
            cube = rubikCube,
            bluetoothManager = bluetoothManager
        )
    }
    val viewModel: SolveViewModel = viewModel {
        SolveViewModel(robot, solver)
    }

    AppTheme {
        SolveScreen(viewModel = viewModel)
    }
}

private fun createRubikCubeStub() = RubikCube(
    upFace = UpFace(
        cells = listOf(
            Cell(x = 0, y = 0, color = -1320180),
            Cell(x = 1, y = 0, color = -3796710),
            Cell(x = 2, y = 0, color = -33758),
            Cell(x = 0, y = 1, color = -16747558),
            Cell(x = 1, y = 1, color = -4666656),
            Cell(x = 2, y = 1, color = -33758),
            Cell(x = 0, y = 2, color = -1320180),
            Cell(x = 1, y = 2, color = -3796710),
            Cell(x = 2, y = 2, color = -4666656)
        )
    ),
    frontFace = FrontFace(
        cells = listOf(
            Cell(x = 0, y = 0, color = -33758),
            Cell(x = 1, y = 0, color = -1320180),
            Cell(x = 2, y = 0, color = -3796710),
            Cell(x = 0, y = 1, color = -4666656),
            Cell(x = 1, y = 1, color = -33758),
            Cell(x = 2, y = 1, color = -4666656),
            Cell(x = 0, y = 2, color = -4666656),
            Cell(x = 1, y = 2, color = -33758),
            Cell(x = 2, y = 2, color = -3796710)
        )
    ),
    rightFace = RightFace(
        cells = listOf(
            Cell(x = 0, y = 0, color = -16747558),
            Cell(x = 1, y = 0, color = -9374135),
            Cell(x = 2, y = 0, color = -9374135),
            Cell(x = 0, y = 1, color = -33758),
            Cell(x = 1, y = 1, color = -9374135),
            Cell(x = 2, y = 1, color = -9374135),
            Cell(x = 0, y = 2, color = -16747558),
            Cell(x = 1, y = 2, color = -9374135),
            Cell(x = 2, y = 2, color = -9374135)
        )
    ),
    backFace = BackFace(
        cells = listOf(
            Cell(x = 0, y = 0, color = -4666656),
            Cell(x = 1, y = 0, color = -4666656),
            Cell(x = 2, y = 0, color = -3796710),
            Cell(x = 0, y = 1, color = -4666656),
            Cell(x = 1, y = 1, color = -3796710),
            Cell(x = 2, y = 1, color = -1320180),
            Cell(x = 0, y = 2, color = -4666656),
            Cell(x = 1, y = 2, color = -1320180),
            Cell(x = 2, y = 2, color = -1320180)
        )
    ),
    leftFace = LeftFace(
        cells = listOf(
            Cell(x = 0, y = 0, color = -9374135),
            Cell(x = 1, y = 0, color = -3796710),
            Cell(x = 2, y = 0, color = -9374135),
            Cell(x = 0, y = 1, color = -16747558),
            Cell(x = 1, y = 1, color = -16747558),
            Cell(x = 2, y = 1, color = -16747558),
            Cell(x = 0, y = 2, color = -16747558),
            Cell(x = 1, y = 2, color = -16747558),
            Cell(x = 2, y = 2, color = -16747558)
        )
    ),
    downFace = DownFace(
        cells = listOf(
            Cell(x = 0, y = 0, color = -33758),
            Cell(x = 1, y = 0, color = -1320180),
            Cell(x = 2, y = 0, color = -1320180),
            Cell(x = 0, y = 1, color = -33758),
            Cell(x = 1, y = 1, color = -1320180),
            Cell(x = 2, y = 1, color = -3796710),
            Cell(x = 0, y = 2, color = -33758),
            Cell(x = 1, y = 2, color = -9374135),
            Cell(x = 2, y = 2, color = -3796710)
        )
    )
)