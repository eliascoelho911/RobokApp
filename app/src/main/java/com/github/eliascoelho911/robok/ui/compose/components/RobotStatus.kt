package com.github.eliascoelho911.robok.ui.compose.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.eliascoelho911.robok.R
import com.github.eliascoelho911.robok.bluetooth.MockRobotBluetoothManager
import com.github.eliascoelho911.robok.bluetooth.RobotBluetoothManager
import com.github.eliascoelho911.robok.bluetooth.RobotBluetoothManager.ConnectionState

/**
 * A Compose component that displays the robot connection status and provides a connect/disconnect button.
 *
 * @param robotBluetoothManager The manager that handles bluetooth connection with the robot
 * @param onConnectClick Lambda to be invoked when the connect button is clicked
 * @param modifier Modifier to be applied to the component
 */
@Composable
fun RobotStatus(
    robotBluetoothManager: RobotBluetoothManager,
    onConnectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val connectionState by robotBluetoothManager.connectionState.collectAsStateWithLifecycle(
        ConnectionState.DISCONNECTED
    )

    val (text, color, icon) = when (connectionState) {
        ConnectionState.CONNECTED -> Triple(
            stringResource(R.string.connected),
            MaterialTheme.colorScheme.primary,
            R.drawable.ic_usb_24dp
        )

        ConnectionState.CONNECTING -> Triple(
            stringResource(R.string.connecting),
            MaterialTheme.colorScheme.secondary,
            R.drawable.ic_usb_24dp
        )

        ConnectionState.DISCONNECTED -> Triple(
            stringResource(R.string.disconnected),
            MaterialTheme.colorScheme.surfaceVariant,
            R.drawable.ic_usb_24dp
        )

        ConnectionState.ERROR -> Triple(
            stringResource(R.string.robot_connection_error),
            MaterialTheme.colorScheme.error,
            R.drawable.ic_usb_off_24dp
        )

        ConnectionState.PERMISSION_REQUIRED -> Triple(
            stringResource(R.string.robot_permission_required),
            MaterialTheme.colorScheme.error,
            R.drawable.ic_usb_off_24dp
        )

        ConnectionState.NO_DEVICE -> Triple(
            stringResource(R.string.robot_not_found),
            MaterialTheme.colorScheme.error,
            R.drawable.ic_usb_off_24dp
        )
    }

    Row(
        modifier = modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = color
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = onConnectClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = if (connectionState == ConnectionState.CONNECTED) {
                    stringResource(R.string.robot_disconnected)
                } else {
                    stringResource(R.string.connecting)
                },
                tint = color
            )
        }
    }
}

@Preview
@Composable
fun PreviewRobotStatus() {
    RobotStatus(
        robotBluetoothManager = MockRobotBluetoothManager(),
        onConnectClick = {}
    )
}