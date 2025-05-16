package com.github.eliascoelho911.robok.robot.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

/**
 * Implementation of RobotBluetoothConnectionManager that uses Android's Bluetooth API
 * to connect to and communicate with the robot.
 */
class RobotBluetoothManagerImpl(private val context: Context) : RobotBluetoothManager {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothSocket: BluetoothSocket? = null
    private val _connectionState = MutableStateFlow<RobotBluetoothManager.ConnectionState>(
        RobotBluetoothManager.ConnectionState.DISCONNECTED
    )

    override val connectionState: StateFlow<RobotBluetoothManager.ConnectionState> =
        _connectionState

    companion object {
        private val ROBOT_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Standard SerialPort service UUID
        private const val ROBOT_NAME_PREFIX = "HC-05" // Robot device name prefix
    }

    override fun isBluetoothSupported(): Boolean = bluetoothAdapter != null

    override fun isBluetoothEnabled(): Boolean = bluetoothAdapter?.isEnabled == true

    override suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        if (bluetoothAdapter == null) {
            _connectionState.value = RobotBluetoothManager.ConnectionState.ERROR
            return@withContext false
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            _connectionState.value = RobotBluetoothManager.ConnectionState.PERMISSION_REQUIRED
            return@withContext false
        }

        try {
            _connectionState.value = RobotBluetoothManager.ConnectionState.CONNECTING

            // Find the robot device
            val pairedDevices = bluetoothAdapter.bondedDevices
            val robotDevice = pairedDevices.find { device ->
                device.name?.startsWith(ROBOT_NAME_PREFIX) == true
            }

            if (robotDevice == null) {
                _connectionState.value = RobotBluetoothManager.ConnectionState.NO_DEVICE
                return@withContext false
            }

            return@withContext connectToDevice(robotDevice)
        } catch (e: Exception) {
            _connectionState.value = RobotBluetoothManager.ConnectionState.ERROR
            return@withContext false
        }
    }

    private suspend fun connectToDevice(device: BluetoothDevice): Boolean =
        withContext(Dispatchers.IO) {
            try {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@withContext false
                }

                // Close any existing connection
                disconnect()

                // Create a new socket and connect
                bluetoothSocket = device.createRfcommSocketToServiceRecord(ROBOT_UUID)
                bluetoothSocket?.connect()

                if (bluetoothSocket?.isConnected == true) {
                    _connectionState.value = RobotBluetoothManager.ConnectionState.CONNECTED
                    return@withContext true
                } else {
                    _connectionState.value = RobotBluetoothManager.ConnectionState.ERROR
                    return@withContext false
                }
            } catch (e: IOException) {
                _connectionState.value = RobotBluetoothManager.ConnectionState.ERROR
                return@withContext false
            }
        }

    override fun disconnect() {
        try {
            bluetoothSocket?.close()
            bluetoothSocket = null
            _connectionState.value = RobotBluetoothManager.ConnectionState.DISCONNECTED
        } catch (e: IOException) {
            // Ignore close errors
        }
    }

    override suspend fun sendCommand(command: String): Boolean = withContext(Dispatchers.IO) {
        if (bluetoothSocket?.isConnected != true) {
            return@withContext false
        }

        try {
            val outputStream = bluetoothSocket?.outputStream
            outputStream?.write(command.toByteArray())
            outputStream?.flush()
            return@withContext true
        } catch (e: IOException) {
            _connectionState.value = RobotBluetoothManager.ConnectionState.ERROR
            disconnect()
            return@withContext false
        }
    }
}