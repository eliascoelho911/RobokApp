package com.github.eliascoelho911.robok.ui.compose.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.robot.Robot
import com.github.eliascoelho911.robok.rubikcube.HandRotation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Composable de BottomSheet para controle manual do robô
 */
@Composable
fun RobotManualControlSheet(
    robot: Robot,
    onDismiss: () -> Unit
) {
    var isOperationInProgress by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.manual_control_title),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Controles da mão esquerda
        HandControl(
            title = stringResource(R.string.left_hand),
            isOperationInProgress = isOperationInProgress,
            onOpenClick = {
                executeRobotOperation(
                    coroutineScope = coroutineScope,
                    updateProgressState = { inProgress -> isOperationInProgress = inProgress }
                ) {
                    robot.handActionManager.openLeftHand()
                }
            },
            onCloseClick = {
                executeRobotOperation(
                    coroutineScope = coroutineScope,
                    updateProgressState = { inProgress -> isOperationInProgress = inProgress }
                ) {
                    robot.handActionManager.closeLeftHand()
                }
            },
            onRotateClockwiseClick = {
                executeRobotOperation(
                    coroutineScope = coroutineScope,
                    updateProgressState = { inProgress -> isOperationInProgress = inProgress }
                ) {
                    robot.handActionManager.rotateLeftHand(clockwise = true, isAuto = false)
                }
            },
            onRotateToMiddleClick = {
                executeRobotOperation(
                    coroutineScope = coroutineScope,
                    updateProgressState = { inProgress -> isOperationInProgress = inProgress }
                ) {
                    robot.handActionManager.rotateLeftHand(HandRotation.Middle, isAuto = false)
                }
            },
            onRotateCounterClockwiseClick = {
                executeRobotOperation(
                    coroutineScope = coroutineScope,
                    updateProgressState = { inProgress -> isOperationInProgress = inProgress }
                ) {
                    robot.handActionManager.rotateLeftHand(clockwise = false, isAuto = false)
                }
            }
        )

        // Controles da mão direita
        HandControl(
            title = stringResource(R.string.right_hand),
            isOperationInProgress = isOperationInProgress,
            onOpenClick = {
                executeRobotOperation(
                    coroutineScope = coroutineScope,
                    updateProgressState = { inProgress -> isOperationInProgress = inProgress }
                ) {
                    robot.handActionManager.openRightHand()
                }
            },
            onCloseClick = {
                executeRobotOperation(
                    coroutineScope = coroutineScope,
                    updateProgressState = { inProgress -> isOperationInProgress = inProgress }
                ) {
                    robot.handActionManager.closeRightHand()
                }
            },
            onRotateClockwiseClick = {
                executeRobotOperation(
                    coroutineScope = coroutineScope,
                    updateProgressState = { inProgress -> isOperationInProgress = inProgress }
                ) {
                    robot.handActionManager.rotateRightHand(clockwise = true, isAuto = false)
                }
            },
            onRotateToMiddleClick = {
                executeRobotOperation(
                    coroutineScope = coroutineScope,
                    updateProgressState = { inProgress -> isOperationInProgress = inProgress }
                ) {
                    robot.handActionManager.rotateRightHand(HandRotation.Middle, isAuto = false)
                }
            },
            onRotateCounterClockwiseClick = {
                executeRobotOperation(
                    coroutineScope = coroutineScope,
                    updateProgressState = { inProgress -> isOperationInProgress = inProgress }
                ) {
                    robot.handActionManager.rotateRightHand(clockwise = false, isAuto = false)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.close_controls))
        }
    }
}

/**
 * Composable reutilizável para controlar uma mão do robô
 */
@Composable
fun HandControl(
    title: String,
    isOperationInProgress: Boolean,
    onOpenClick: () -> Unit,
    onCloseClick: () -> Unit,
    onRotateClockwiseClick: () -> Unit,
    onRotateToMiddleClick: () -> Unit,
    onRotateCounterClockwiseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Controles de abrir/fechar
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onOpenClick,
                enabled = !isOperationInProgress,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.open))
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onCloseClick,
                enabled = !isOperationInProgress,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.close))
            }
        }

        // Controles de rotação
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Button(
                onClick = onRotateClockwiseClick,
                enabled = !isOperationInProgress,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.rotate_cw))
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onRotateToMiddleClick,
                enabled = !isOperationInProgress,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.rotate_middle))
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onRotateCounterClockwiseClick,
                enabled = !isOperationInProgress,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.rotate_ccw))
            }
        }
    }
}

/**
 * Função de extensão para executar operações do robô em uma coroutine com tratamento de estado
 */
private fun executeRobotOperation(
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    updateProgressState: (Boolean) -> Unit,
    operation: suspend () -> Unit
) {
    updateProgressState(true)
    coroutineScope.launch {
        withContext(Dispatchers.IO) {
            operation()
        }
        updateProgressState(false)
    }
}
