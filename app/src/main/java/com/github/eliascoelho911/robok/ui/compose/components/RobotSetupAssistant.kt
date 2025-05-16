package com.github.eliascoelho911.robok.ui.compose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.robot.bluetooth.RobotBluetoothManager.ConnectionState
import com.github.eliascoelho911.robok.ui.compose.theme.AppTheme

/**
 * Componente assistente de configuração do robô que guia o usuário pelas etapas de configuração
 *
 * @param connectionState Estado da conexão atual com o robô
 * @param cubeReceived Se o cubo foi recebido pelo robô
 * @param onConnectClick Callback quando o usuário clica para conectar o robô
 * @param onInsertCubeClick Callback quando o usuário clica para inserir o cubo
 * @param onRestartClick Callback quando o usuário clica para reiniciar a configuração
 * @param modifier Modifier a ser aplicado ao componente
 */
@Composable
fun RobotSetupAssistant(
    connectionState: ConnectionState,
    cubeReceived: Boolean,
    onConnectClick: () -> Unit,
    onInsertCubeClick: () -> Unit,
    onRestartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Calcular progresso com base nas etapas concluídas
    val isConnected = connectionState == ConnectionState.CONNECTED
    val isCompleted = isConnected && cubeReceived

    val progress = when {
        isCompleted -> 1f
        isConnected -> 0.5f
        else -> 0f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "Setup Progress"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isCompleted -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título do assistente
            Text(
                text = when {
                    isCompleted -> stringResource(R.string.robot_setup_complete)
                    else -> stringResource(R.string.robot_setup_title)
                },
                style = MaterialTheme.typography.titleLarge,
                color = when {
                    isCompleted -> MaterialTheme.colorScheme.onTertiaryContainer
                    else -> MaterialTheme.colorScheme.onErrorContainer
                },
                textAlign = TextAlign.Center
            )

            // Barra de progresso
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = when {
                    isCompleted -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.primary
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            // Somente exibe as etapas se não estiver concluído
            AnimatedVisibility(
                visible = !isCompleted,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Etapa 1: Conectar ao robô
                    SetupStep(
                        title = stringResource(R.string.robot_setup_step1),
                        isCompleted = isConnected,
                        isEnabled = true,
                        buttonText = stringResource(R.string.robot_setup_connect_action),
                        onButtonClick = onConnectClick
                    )

                    // Etapa 2: Inserir o cubo
                    SetupStep(
                        title = stringResource(R.string.robot_setup_step2),
                        isCompleted = cubeReceived,
                        isEnabled = isConnected && !cubeReceived,
                        buttonText = stringResource(R.string.insert_cube_action),
                        onButtonClick = onInsertCubeClick
                    )
                }
            }

            // Mensagem de sucesso ao concluir
            AnimatedVisibility(
                visible = isCompleted,
                enter = fadeIn() + slideInVertically { it / 2 }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(48.dp)
                    )

                    Button(
                        onClick = onRestartClick,
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        Text(text = stringResource(R.string.robot_setup_restart))
                    }
                }
            }
        }
    }
}

@Composable
private fun SetupStep(
    title: String,
    isCompleted: Boolean,
    isEnabled: Boolean,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = if (isCompleted)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onErrorContainer
            )
        }

        AnimatedVisibility(visible = !isCompleted) {
            Button(
                onClick = onButtonClick,
                enabled = isEnabled
            ) {
                Text(text = buttonText)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RobotSetupAssistantPreview_Initial() {
    AppTheme {
        RobotSetupAssistant(
            connectionState = ConnectionState.DISCONNECTED,
            cubeReceived = false,
            onConnectClick = {},
            onInsertCubeClick = {},
            onRestartClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RobotSetupAssistantPreview_Connected() {
    AppTheme {
        RobotSetupAssistant(
            connectionState = ConnectionState.CONNECTED,
            cubeReceived = false,
            onConnectClick = {},
            onInsertCubeClick = {},
            onRestartClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RobotSetupAssistantPreview_Completed() {
    AppTheme {
        RobotSetupAssistant(
            connectionState = ConnectionState.CONNECTED,
            cubeReceived = true,
            onConnectClick = {},
            onInsertCubeClick = {},
            onRestartClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}