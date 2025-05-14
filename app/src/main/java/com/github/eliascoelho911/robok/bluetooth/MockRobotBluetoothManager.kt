package com.github.eliascoelho911.robok.bluetooth

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Mock implementation of RobotBluetoothConnectionManager for testing and development.
 * This implementation simulates the behavior of a real robot connection without actually
 * using Bluetooth hardware or requiring a physical robot.
 */
class MockRobotBluetoothManager : RobotBluetoothManager {
    private val _connectionState =
        MutableStateFlow(RobotBluetoothManager.ConnectionState.DISCONNECTED)
    override val connectionState: StateFlow<RobotBluetoothManager.ConnectionState> =
        _connectionState

    // Simulate whether the device supports Bluetooth
    private val simulateBluetoothSupported = true

    // Simulate whether Bluetooth is enabled
    private val simulateBluetoothEnabled = true

    // Simulate connection success/failure rate
    private val simulateConnectionSuccessRate = 0.9 // 90% success rate

    // Track received commands for inspection during testing
    private val receivedCommands = mutableListOf<String>()

    override fun isBluetoothSupported(): Boolean = simulateBluetoothSupported

    override fun isBluetoothEnabled(): Boolean = simulateBluetoothEnabled

    override suspend fun connect(): Boolean {
        // Simulate connecting process
        _connectionState.value = RobotBluetoothManager.ConnectionState.CONNECTING

        // Simulate connection delay
        delay(1500)

        // Simulate random success/failure based on configured rate
        return if (Math.random() < simulateConnectionSuccessRate) {
            _connectionState.value = RobotBluetoothManager.ConnectionState.CONNECTED
            true
        } else {
            _connectionState.value = RobotBluetoothManager.ConnectionState.ERROR
            false
        }
    }

    override fun disconnect() {
        // Simulate disconnection
        if (_connectionState.value == RobotBluetoothManager.ConnectionState.CONNECTED) {
            _connectionState.value = RobotBluetoothManager.ConnectionState.DISCONNECTED
        }
    }

    override suspend fun sendCommand(command: String): Boolean {
        // Only accept commands if connected
        if (_connectionState.value != RobotBluetoothManager.ConnectionState.CONNECTED) {
            return false
        }

        // Add to received commands list for testing
        receivedCommands.add(command)

        // Simulate command processing delay
        delay(300)

        // Simulate high success rate for commands
        return Math.random() < 0.95
    }

    /**
     * Test utility to get all commands that were sent to the mock manager
     */
    fun getReceivedCommands(): List<String> = receivedCommands.toList()

    /**
     * Test utility to clear the command history
     */
    fun clearReceivedCommands() {
        receivedCommands.clear()
    }

    /**
     * Force a specific connection state (for testing scenarios)
     */
    fun setConnectionState(state: RobotBluetoothManager.ConnectionState) {
        _connectionState.value = state
    }
}