package com.github.eliascoelho911.robok.bluetooth

import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for managing Bluetooth connections with the Rubik's Cube solving robot.
 * This abstraction allows for different implementations including mock versions for testing.
 */
interface RobotBluetoothManager {
    /**
     * Current connection state as a StateFlow to observe changes
     */
    val connectionState: StateFlow<ConnectionState>

    /**
     * Check if Bluetooth is supported on the device
     */
    fun isBluetoothSupported(): Boolean

    /**
     * Check if Bluetooth is enabled on the device
     */
    fun isBluetoothEnabled(): Boolean

    /**
     * Attempt to connect to the robot
     * @return true if connection was successful, false otherwise
     */
    suspend fun connect(): Boolean

    /**
     * Disconnect from the robot
     */
    fun disconnect()

    /**
     * Send a command to the robot
     * @param command The command to send
     * @return true if command was sent successfully, false otherwise
     */
    suspend fun sendCommand(command: String): Boolean

    /**
     * Possible states of the Bluetooth connection
     */
    enum class ConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        ERROR,
        PERMISSION_REQUIRED,
        NO_DEVICE
    }
}