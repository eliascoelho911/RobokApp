package com.github.eliascoelho911.robok.ui.compose.screens.solve

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.robot.Robot
import com.github.eliascoelho911.robok.robot.bluetooth.RobotBluetoothManager
import com.github.eliascoelho911.robok.robot.bluetooth.RobotBluetoothManager.ConnectionState
import com.github.eliascoelho911.robok.rubikcube.RubikCubeSolver
import com.github.eliascoelho911.robok.rubikcube.createRubikCubeStub
import com.github.eliascoelho911.robok.ui.compose.components.RobotManualControlSheet
import com.github.eliascoelho911.robok.ui.compose.components.RobotSetupAssistant
import com.github.eliascoelho911.robok.ui.compose.components.RobotStatus
import com.github.eliascoelho911.robok.ui.compose.components.SolvePlayer
import com.github.eliascoelho911.robok.ui.compose.theme.AppTheme
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolveScreen(
    viewModel: SolveViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle(ConnectionState.DISCONNECTED)
    val coroutineScope = rememberCoroutineScope()

    var showBottomSheet by remember { mutableStateOf(false) }
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val isSolved =
        state.currentMoveIndex >= state.rubikCubeMoves.size - 1 && state.rubikCubeMoves.isNotEmpty()

    // Automatically attempt to connect when the screen is first shown
    LaunchedEffect(key1 = Unit) {
        if (connectionState !in listOf(ConnectionState.CONNECTED, ConnectionState.CONNECTING)) {
            viewModel.attemptToConnectToRobot()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
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

            // Manual control FAB
            if (connectionState == ConnectionState.CONNECTED) {
                FloatingActionButton(
                    onClick = {
                        showBottomSheet = true
                        coroutineScope.launch {
                            modalBottomSheetState.expand()
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 60.dp) // Position below the connection status
                        .size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SettingsRemote,
                        contentDescription = stringResource(R.string.manual_control_desc)
                    )
                }
            }

            // Modal Bottom Sheet
            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        coroutineScope.launch {
                            modalBottomSheetState.hide()
                        }
                        showBottomSheet = false
                    },
                    sheetState = modalBottomSheetState,
                ) {
                    RobotManualControlSheet(
                        robot = viewModel.robot,
                        onDismiss = {
                            coroutineScope.launch {
                                modalBottomSheetState.hide()
                            }
                            showBottomSheet = false
                        }
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Spacer(modifier = Modifier.height(16.dp))

                RobotSetupAssistant(
                    connectionState = connectionState,
                    cubeReceived = state.cubeReceived,
                    onConnectClick = {
                        if (connectionState == ConnectionState.CONNECTED) {
                            viewModel.disconnectFromRobot()
                        } else {
                            viewModel.attemptToConnectToRobot()
                        }
                    },
                    onInsertCubeClick = {
                        viewModel.receiveCube()
                    },
                    onRestartClick = {
                        viewModel.resetSetup()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                SolvePlayer(
                    currentMove = getCurrentMoveNotation(state),
                    nextMove = getNextMoveNotation(state),
                    currentMoveIndex = state.currentMoveIndex + 1,
                    totalMoves = state.rubikCubeMoves.size,
                    isPlaying = false,
                    isSolved = isSolved,
                    isEnabled = state.cubeReceived,
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
    return if (state.rubikCubeMoves.isEmpty() || state.currentMoveIndex >= state.rubikCubeMoves.size || state.currentMoveIndex == -1) {
        ""
    } else {
        state.rubikCubeMoves[state.currentMoveIndex].notation
    }
}

private fun getNextMoveNotation(state: SolveState): String {
    return if (state.rubikCubeMoves.isEmpty() || state.currentMoveIndex >= state.rubikCubeMoves.size - 1) {
        ""
    } else {
        state.rubikCubeMoves[state.currentMoveIndex + 1].notation
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
